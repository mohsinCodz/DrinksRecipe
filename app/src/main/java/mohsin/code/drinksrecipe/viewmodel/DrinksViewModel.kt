package mohsin.code.drinksrecipe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.model.SearchQuery
import mohsin.code.drinksrecipe.repository.DrinkRepository
import retrofit2.HttpException
import java.io.IOException

class DrinksViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

    val drinks: MutableLiveData<List<Drink>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData() // For error messages

    // LiveData for favorite drinks
    val favoriteDrinks: LiveData<List<Drink>> = drinkRepository.getFavoriteDrinks()

    fun insertFav(id: Int, isSelected: Boolean) = viewModelScope.launch {
        drinkRepository.updateFavoriteStatus(id, isSelected)
    }

    // Perform search with error handling
    fun performSearch(query: String, searchType: String) {
        viewModelScope.launch {
            try {
                val results = when (searchType) {
                    "name" -> drinkRepository.searchByName(query)
                    "alphabet" -> drinkRepository.searchByAlphabet(query)
                    else -> emptyList()
                }

                val filteredResults = results.filter { drink ->
                    when (searchType) {
                        "name" -> drink.strDrink.lowercase().contains(query.lowercase())
                        "alphabet" -> drink.strDrink.lowercase().startsWith(query.lowercase())
                        else -> false
                    }
                }

                drinks.postValue(filteredResults)

            } catch (e: IOException) {
                errorMessage.postValue("No internet connection.")
            } catch (e: HttpException) {
                errorMessage.postValue("Something went wrong! Please try again.")
            }
        }
    }

    fun fetchDataByName(name: String) = viewModelScope.launch {
        try {
            drinkRepository.searchByName(name) // Fetch and store in DB
        } catch (e: Exception) {
            // Handle errors if any
        }
    }

    fun fetchDataByAlphabet(alphabet: String) = viewModelScope.launch {
        try {
            drinkRepository.searchByAlphabet(alphabet) // Fetch and store in DB
        } catch (e: Exception) {
            // Handle errors if any
        }
    }
}