package mohsin.code.drinksrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mohsin.code.drinksrecipe.adapters.ItemAdapter
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModel
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModelFactory

class FavoriteRecipes : Fragment() {

    private lateinit var adapter: ItemAdapter
    private lateinit var drinksViewModel: DrinksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Favorite Drinks"

        val repository = (requireActivity().application as MyApplication).drinkRepository

        // Initialize the ViewModel
        drinksViewModel = ViewModelProvider(this, DrinksViewModelFactory(repository)).get(
            DrinksViewModel::class.java
        )

        // Initialize the adapter and set it to the fragment's adapter property
        adapter = ItemAdapter(emptyList(), onItemClick = { drink ->
            val bundle = bundleOf(
                "strDrink " to drink.strDrink,
                "strDrink " to drink.strDrinkThumb,
                "strDrink " to drink.strInstructions,
                "strDrink " to drink.strAlcoholic
            )
        }, selectFav = { idDrink, isSelected ->
            drinksViewModel.insertFav(idDrink, isSelected)
        })

        val rvFavorites: RecyclerView = view.findViewById(R.id.rvFavorites)
        rvFavorites.layoutManager = LinearLayoutManager(context)
        rvFavorites.adapter = adapter

        // Observe favorite drinks using the ViewModel
        drinksViewModel.favoriteDrinks.observe(viewLifecycleOwner) { favoriteDrinks ->
            adapter.setFilteredList(favoriteDrinks)
        }
    }
}