package mohsin.code.drinksrecipe

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import mohsin.code.drinksrecipe.api.ApiInterface
import mohsin.code.drinksrecipe.api.ApiUtilities
import mohsin.code.drinksrecipe.repository.DrinkRepository
import mohsin.code.drinksrecipe.room.DrinkDatabase
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModel
import mohsin.code.drinksrecipe.viewmodel.DrinksViewModelFactory

class MyApplication: Application() {

    // Initialize the DrinkRepository here
    lateinit var drinkRepository: DrinkRepository

    override fun onCreate() {
        super.onCreate()

        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)

        // Initialize the DrinkDatabase
        val database = DrinkDatabase.getDatabase(applicationContext)


        drinkRepository = DrinkRepository(apiInterface, database)

    }
}