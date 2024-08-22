package mohsin.code.drinksrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.repository.DrinkRepository

class DrinksViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

    // LiveData for observing drinks
    val drinks: MutableLiveData<List<Drink>> = MutableLiveData()



    // LiveData for observing favorite drinks
    val favoriteDrinks: LiveData<List<Drink>> = drinkRepository.getFavoriteDrinks()

    init {
        drinkRepository.getDrinksAll()
    }

    fun insertFav(id: Int, isSelected: Boolean) = viewModelScope.launch {
        drinkRepository.updateFavoriteStatus(id, isSelected)
    }


    // Perform search with filtering logic
    fun performSearch(query: String, searchType: String) {
        viewModelScope.launch {
            val results = when (searchType) {
                "name" -> drinkRepository.searchByName(query)
                "alphabet" -> drinkRepository.searchByAlphabet(query)
                else -> emptyList()
            }

            // Filter results that match the query
            val filteredResults = results.filter { drink ->
                when (searchType) {
                    "name" -> drink.strDrink.lowercase().contains(query.lowercase())
                    "alphabet" -> drink.strDrink.lowercase().startsWith(query.lowercase())
                    else -> false
                }
            }

            // Post only the filtered data to LiveData
            drinks.postValue(filteredResults)
        }
    }
}