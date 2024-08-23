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

    // Check if the database is empty
    private suspend fun isDatabaseEmpty(): Boolean {
        return drinkDatabase.drinkDao().getDrinkCount() == 0
    }

    // Search drinks by name
    suspend fun searchByName(name: String): List<Drink> {
        return if (MyUtil.isInternetAvailable(context)) {
            try {
                // Fetch data from API when online
                val response = apiInterface.getDrinksByName(name)
                response.drinks?.let { drinks ->
                    drinkDatabase.drinkDao().insertDrink(drinks) // Insert into RoomDB
                    return drinks // Return fetched data from API
                } ?: emptyList()
            } catch (e: Exception) {
                // If API fails, fetch from RoomDB
                drinkDatabase.drinkDao().getDrinksByName(name) ?: emptyList()
            }
        } else {
            // If offline, fetch data from RoomDB
            drinkDatabase.drinkDao().getDrinksByName(name) ?: emptyList()
        }
    }

    // Search drinks by starting alphabet
    suspend fun searchByAlphabet(alphabet: String): List<Drink> {
        return if (MyUtil.isInternetAvailable(context)) {
            try {
                // Fetch data from API when online
                val response = apiInterface.getDrinksByAlphabet(alphabet)
                response.drinks?.let { drinks ->
                    drinkDatabase.drinkDao().insertDrink(drinks) // Insert into RoomDB
                    return drinks // Return fetched data from API
                } ?: emptyList()
            } catch (e: Exception) {
                // If API fails, fetch from RoomDB
                drinkDatabase.drinkDao().getDrinksByAlphabet(alphabet) ?: emptyList()
            }
        } else {
            // If offline, fetch data from RoomDB
            drinkDatabase.drinkDao().getDrinksByAlphabet(alphabet) ?: emptyList()
        }
    }

    // Get all drinks from RoomDB
    fun getDrinksAll(): LiveData<List<Drink>> {
        return drinkDatabase.drinkDao().getDrinks()
    }

    // Get all favorite drinks
    fun getFavoriteDrinks(): LiveData<List<Drink>> {
        return drinkDatabase.drinkDao().getAllFavoriteDrinks()
    }

    // Update the favorite status of a drink
    suspend fun updateFavoriteStatus(idDrink: String, isFavorite: Boolean) {
        drinkDatabase.drinkDao().updateFavoriteStatus(idDrink, isFavorite)
    }
}