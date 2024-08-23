package mohsin.code.drinksrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.repository.DrinkRepository
import retrofit2.HttpException
import java.io.IOException

class DrinksViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

    val drinks: MutableLiveData<List<Drink>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData() // For error messages

    // LiveData for favorite drinks
    val favoriteDrinks: LiveData<List<Drink>> = drinkRepository.getFavoriteDrinks()

    fun insertFav(idDrink: String, isSelected: Boolean) = viewModelScope.launch {
        drinkRepository.updateFavoriteStatus(idDrink, isSelected)
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
}