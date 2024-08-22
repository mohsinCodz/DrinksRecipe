package mohsin.code.drinksrecipe.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import mohsin.code.drinksrecipe.model.Drink
import mohsin.code.drinksrecipe.model.SearchQuery


@Database(entities = [Drink::class, SearchQuery::class], version = 1, exportSchema = false)
abstract class DrinkDatabase : RoomDatabase() {
    abstract fun drinkDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: DrinkDatabase? = null

        fun getDatabase(context: Context): DrinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrinkDatabase::class.java,
                    "drink_database"
                )
//                    .addMigrations(MIGRATION_1_2) // Add migration if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }
//
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Add migration code if necessary
//                // For example:
//                // database.execSQL("ALTER TABLE drinks ADD COLUMN new_column_name INTEGER DEFAULT 0 NOT NULL")
//            }
//        }
    }
}