package mohsin.code.drinksrecipe.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.model.SearchQuery

@Dao
interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(searchQuery: SearchQuery)

    @Query("SELECT * FROM search_queries WHERE id = 1 LIMIT 1")
    suspend fun getLastSearchQuery(): SearchQuery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drinks: List<Drink>)

    @Query("SELECT * FROM drinks WHERE id = :id")
    suspend fun getDrinkById(id: Int): Drink?

    @Query("SELECT * FROM drinks")
    fun getDrinks(): LiveData<List<Drink>>

    @Query("SELECT COUNT(*) FROM drinks")
    suspend fun getDrinkCount(): Int

    @Query("DELETE FROM drinks")
    suspend fun deleteAllDrinks()

    @Update
    suspend fun updateDrink(drink: Drink)

    @Query("UPDATE drinks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM drinks WHERE isFavorite = 1")
    fun getAllFavoriteDrinks(): LiveData<List<Drink>>
}