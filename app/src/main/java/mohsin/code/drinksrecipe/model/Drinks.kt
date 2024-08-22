package mohsin.code.drinksrecipe.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Drinks (
    @SerializedName("drinks")
    val drinks: List<Drink>
)

@Entity(tableName = "drinks")
data class Drink(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // or use Long if you prefer

    @SerializedName("strDrink")
    val strDrink: String,
    @SerializedName("strDrinkThumb")
    val strDrinkThumb: String,
    @SerializedName("strInstructions")
    val strInstructions: String,
    @SerializedName("strAlcoholic")
    val strAlcoholic : String,

    // This field tracks if the drink is marked as a favorite
    var isFavorite: Boolean = false
)

@Entity(tableName = "search_queries")
data class SearchQuery(
    @PrimaryKey(autoGenerate = false) // Only one entry for the last search
    val id: Int = 1,
    val query: String,
    val searchType: String // This can be "name" or "alphabet"
)