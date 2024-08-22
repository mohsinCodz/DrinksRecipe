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
    private val drinkDatabase: DrinkDatabase,
    private val context: Context
) {

    private suspend fun isDatabaseEmpty(): Boolean {
        return drinkDatabase.drinkDao().getDrinkCount() == 0
    }

    suspend fun searchByName(name: String): List<Drink> {
        return if (MyUtil.isInternetAvailable(context)) {
            if (isDatabaseEmpty()) {
                // Fetch data from API and update the database if it's empty
                val drinks = apiInterface.getDrinksByName(name).drinks
                drinkDatabase.drinkDao().insertDrink(drinks) // Insert into RoomDB
                drinks
            } else {
                // Fetch from RoomDB if the database is not empty
                drinkDatabase.drinkDao().getDrinks().value?.filter { it.strDrink.contains(name, ignoreCase = true) } ?: emptyList()
            }
        } else {
            // Fetch from RoomDB if offline
            drinkDatabase.drinkDao().getDrinks().value?.filter { it.strDrink.contains(name, ignoreCase = true) } ?: emptyList()
        }
    }

    suspend fun searchByAlphabet(alphabet: String): List<Drink> {
        return if (MyUtil.isInternetAvailable(context)) {
            if (isDatabaseEmpty()) {
                // Fetch data from API and update the database if it's empty
                val drinks = apiInterface.getDrinksByAlphabet(alphabet).drinks
                drinkDatabase.drinkDao().insertDrink(drinks) // Insert into RoomDB
                drinks
            } else {
                // Fetch from RoomDB if the database is not empty
                drinkDatabase.drinkDao().getDrinks().value?.filter { it.strDrink.startsWith(alphabet, ignoreCase = true) } ?: emptyList()
            }
        } else {
            // Fetch from RoomDB if offline
            drinkDatabase.drinkDao().getDrinks().value?.filter { it.strDrink.startsWith(alphabet, ignoreCase = true) } ?: emptyList()
        }
    }

    fun getDrinksAll(): LiveData<List<Drink>> {
        return drinkDatabase.drinkDao().getDrinks() // Get data from RoomDB
    }

    // Expose favorite drinks as LiveData
    fun getFavoriteDrinks(): LiveData<List<Drink>> {
        return drinkDatabase.drinkDao().getAllFavoriteDrinks()
    }

    // Method to update the favorite status of a drink
    suspend fun updateFavoriteStatus(idDrink: String, isFavorite: Boolean) {
        drinkDatabase.drinkDao().updateFavoriteStatus(idDrink, isFavorite)
    }

}