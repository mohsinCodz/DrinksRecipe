package mohsin.code.drinksrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.model.SearchQuery
import mohsin.code.drinksrecipe.repository.DrinkRepository

class DrinksViewModel(private val drinkRepository: DrinkRepository) : ViewModel() {

    // LiveData for observing favorite drinks
    val drinks: MutableLiveData<List<Drink>> = MutableLiveData()
//        drinkRepository.getDrinksAll() as MutableLiveData<List<Drink>>

    val favoriteDrinks: LiveData<List<Drink>> = drinkRepository.getFavoriteDrinks()
    private val _lastSearchQuery = MutableLiveData<SearchQuery?>()


    // Initialization block
    init {
        loadLastSearchQuery()
    }


    fun insertFav(id: Int, isSelected: Boolean) = viewModelScope.launch() {
        drinkRepository.updateFavoriteStatus(id = id, isSelected)
    }

    private fun loadLastSearchQuery() {
        viewModelScope.launch {
            val searchQuery = drinkRepository.getLastSearch()
            _lastSearchQuery.postValue(searchQuery)
        }
    }

    fun performSearch(query: String, searchType: String) {
        viewModelScope.launch {
            val results = when (searchType) {
                "name" -> drinkRepository.searchByName(query)
                "alphabet" -> drinkRepository.searchByAlphabet(query)
                else -> emptyList()
            }
            drinks.postValue(results)
        }
    }

    fun saveLastSearch(query: String, searchType: String) {
        viewModelScope.launch {
            drinkRepository.saveLastSearch(query, searchType)
            _lastSearchQuery.postValue(SearchQuery(query = query, searchType = searchType))
        }
    }
}