package mohsin.code.drinksrecipe.repository



import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mohsin.code.drinksrecipe.api.ApiInterface
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.model.Drinks
import mohsin.code.drinksrecipe.model.SearchQuery
import mohsin.code.drinksrecipe.room.DrinkDatabase
import mohsin.code.drinksrecipe.util.MyUtil

class DrinkRepository(
    private val apiInterface: ApiInterface,
    private val drinkDatabase: DrinkDatabase
) {

    suspend fun searchByName(name: String): List<Drink> {
        return apiInterface.getDrinksByName(name).drinks
    }

    suspend fun searchByAlphabet(alphabet: String): List<Drink> {
        return apiInterface.getDrinksByAlphabet(alphabet).drinks
    }

    fun getDrinksAll(): LiveData<List<Drink>>{
        return drinkDatabase.drinkDao().getDrinks()
    }

    // Expose favorite drinks as LiveData
    fun getFavoriteDrinks(): LiveData<List<Drink>> {
        return drinkDatabase.drinkDao().getAllFavoriteDrinks()
    }

    // Method to update the favorite status of a drink
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean) {
        drinkDatabase.drinkDao().updateFavoriteStatus(id, isFavorite)
    }

    suspend fun saveLastSearch(query: String, searchType: String) {
        val searchQuery = SearchQuery(query = query, searchType = searchType)
        drinkDatabase.drinkDao().insertSearchQuery(searchQuery)
    }

    suspend fun getLastSearch(): SearchQuery? {
        return drinkDatabase.drinkDao().getLastSearchQuery()
    }
}