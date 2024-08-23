package mohsin.code.drinksrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mohsin.code.drinksrecipe.adapters.ItemAdapter
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModel
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModelFactory
import java.util.Locale

class DrinksRecipes : Fragment() {

    private var mList: MutableList<Drink> = mutableListOf()
    private lateinit var adapter: ItemAdapter
    private lateinit var drinksViewModel: DrinksViewModel

    private var isSearchActive = false // Flag to track search state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drinks_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Drinks Recipes"

        // Initialize the repository and ViewModel
        val repository = (requireActivity().application as MyApplication).drinkRepository
        drinksViewModel = ViewModelProvider(
            this,
            DrinksViewModelFactory(repository)
        ).get(DrinksViewModel::class.java)

        // Initialize adapter
        adapter = ItemAdapter(mList, onItemClick = { drink ->
            val bundle = bundleOf(
                "strDrink" to drink.strDrink,
                "strDrinkThumb" to drink.strDrinkThumb,
                "strInstructions" to drink.strInstructions,
                "strAlcoholic" to drink.strAlcoholic
            )
            // Navigate to detail screen
        }, selectFav = { idDrink, isSelected ->
            drinksViewModel.insertFav(idDrink, isSelected)
        })

        val rvItem: RecyclerView = view.findViewById(R.id.rvItem)
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = adapter

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val searchView: SearchView = view.findViewById(R.id.searchView)

        // SearchView logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!isSearchActive) { // Only perform search if not already active
                    isSearchActive = true
                    query?.let { drinksViewModel.performSearch(it, getSearchType(radioGroup)) }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterText(newText)
                return true
            }
        })

        // Observe and update UI with drinks data from ViewModel
        drinksViewModel.drinks.observe(viewLifecycleOwner) { drinks ->
            // Only update if search is not active (initial load or clearing search)
            if (!isSearchActive) {
                mList.clear()
                mList.addAll(drinks)
                adapter.notifyDataSetChanged() // Update adapter with full data
            }
        }

        // Handle database data and update ViewModel
        repository.getDrinksAll().observe(viewLifecycleOwner) { drinks ->
            drinksViewModel.drinks.postValue(drinks)
        }

        // Observe error messages
        drinksViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSearchType(radioGroup: RadioGroup): String {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.rbByName -> "name"
            R.id.rbByAlphabet -> "alphabet"
            else -> "name"
        }
    }

    // Filter list locally as the user types in SearchView
    private fun filterText(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Drink>()
            for (drink in mList) {
                if (drink.strDrink.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
                    filteredList.add(drink)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList) // Update adapter with filtered data
            }
        }
    }
}