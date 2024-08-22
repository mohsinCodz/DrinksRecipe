package mohsin.code.drinksrecipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mohsin.code.drinksrecipe.R
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.repository.DrinkRepository

class ItemAdapter(
    private var items: List<Drink>,
   val selectFav:(String,Boolean)->Unit,
    private val onItemClick: (Drink) -> Unit
): RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_count, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setFilteredList(filteredList: List<Drink>) {
        this.items = filteredList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drink = items[position]

        holder.strDrinkName.text = drink.strDrink
        holder.strDrinkDescription.text = drink.strInstructions
        holder.cbAlcohol.isChecked = drink.strAlcoholic == "Alcoholic"
        holder.cbAlcohol.isEnabled = false

        holder.cbFavorite.setOnCheckedChangeListener(null)
        holder.cbFavorite.isChecked = drink.isFavorite

        // Set a listener for when the favorite checkbox is checked/unchecked
        holder.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
            drink.isFavorite = isChecked // Update the local data model
            selectFav(drink.idDrink, isChecked) // Update the database or whatever persistence mechanism you use
        }
        // Load the drink thumbnail into the ImageView using Glide
        Glide.with(holder.itemView.context)
            .load(drink.strDrinkThumb) // Assuming drink.drinkThumb is the URL of the image
            .into(holder.drinkThumb)

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val drinkThumb: ImageView = itemView.findViewById(R.id.ivDrinkThumb)
        val strDrinkName: TextView = itemView.findViewById(R.id.tvDrinkName)
        val strDrinkDescription: TextView = itemView.findViewById(R.id.tvDrinkDescription)
        val cbFavorite: CheckBox = itemView.findViewById(R.id.cbFavorite)
        val cbAlcohol: CheckBox = itemView.findViewById(R.id.cbAlcohol)
    }
}