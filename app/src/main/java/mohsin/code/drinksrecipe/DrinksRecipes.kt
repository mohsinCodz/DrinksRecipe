package mohsin.code.drinksrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mohsin.code.drinksrecipe.adapters.ItemAdapter
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModel
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModelFactory
import java.util.Locale

class DrinksRecipes : Fragment() {

    private var mList:MutableList<Drink> = mutableListOf()
    private lateinit var adapter: ItemAdapter

    private lateinit var drinksViewModel: DrinksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drinks_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Drinks Recipes"


        // Initialize the DrinkRepository
        val repository = (requireActivity().application as MyApplication).drinkRepository

        // Initialize the adapter and set it to the fragment's adapter property
        adapter = ItemAdapter(mList, onItemClick = { drink ->
            val bundle = bundleOf(
                "strDrink " to drink.strDrink,
                "strDrink " to drink.strDrinkThumb,
                "strDrink " to drink.strInstructions,
                "strDrink " to drink.strAlcoholic
            )
        }, selectFav = {id,isSelected->
            drinksViewModel.insertFav(id,isSelected)
        } )

        val rvItem: RecyclerView = view.findViewById(R.id.rvItem)
        rvItem.layoutManager = LinearLayoutManager(context)
        rvItem.adapter = adapter


        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)

        // Set up SearchView
        val searchView: SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val selectedSearchType = when (radioGroup.checkedRadioButtonId) {
                    R.id.rbByName -> "name"
                    R.id.rbByAlphabet -> "alphabet"
                    else -> "name"
                }
                val it = ""
                drinksViewModel.saveLastSearch(it, selectedSearchType)
                drinksViewModel.performSearch(it, selectedSearchType)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterText(newText)
                return true
            }
        })

        // Initialize the ViewModel
        drinksViewModel = ViewModelProvider(
            this,
            DrinksViewModelFactory(repository)
        ).get(DrinksViewModel::class.java)



        repository.getDrinksAll().observe(viewLifecycleOwner){ drinks->
            drinksViewModel.drinks.postValue(drinks)
        }

        // Observe drinks from the repository
        drinksViewModel.drinks.observe(viewLifecycleOwner) { drinks ->
            mList.clear()
            mList.addAll(drinks)
            adapter.setFilteredList(drinks) // or adapter.notifyDataSetChanged() if you want to refresh the entire list
        }
    }

    private fun filterText(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Drink>()
            for (i in mList) {
                if (i.strDrink.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))
                ) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }
}