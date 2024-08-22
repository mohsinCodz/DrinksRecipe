package mohsin.code.drinksrecipe.api

import mohsin.code.drinksrecipe.model.Drinks
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("search.php?s=margarita")
    suspend fun getDrinksByName(@Query("s") name: String): Drinks

    @GET("search.php?f=a")
    suspend fun getDrinksByAlphabet(@Query("f") alphabet: String): Drinks
}