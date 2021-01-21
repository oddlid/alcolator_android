package net.oddware.alcolator

import android.content.Context
import timber.log.Timber

class DrinkRepo(private val drinkDao: DrinkDao) {
    companion object {
        @Volatile
        private var INSTANCE: DrinkRepo? = null

        fun getInstance(ctx: Context): DrinkRepo {
            val tmpInst = INSTANCE
            if (null != tmpInst) {
                return tmpInst
            }
            synchronized(this) {
                val inst = DrinkRepo(DrinkDatabase.get(ctx).drinkDao())
                INSTANCE = inst
                return inst
            }
        }
    }

    val drinks = drinkDao.getDrinks()
    val tags = drinkDao.getTags()

    //private inline fun LiveData<List<Drink>>.get(index: Int): Drink? {
    //    return value?.get(index)
    //}

    suspend fun add(drink: Drink) {
        Timber.d("Adding drink to database")
        drinkDao.insert(drink)
    }

    suspend fun import(drinks: List<Drink>) {
        Timber.d("Importing list of drinks to database")
        drinkDao.importDrinks(drinks)
    }

    suspend fun clear() {
        Timber.d("Deleting all drinks from database")
        drinkDao.deleteAll()
    }

    suspend fun update(drink: Drink) {
        Timber.d("Updating drink in database")
        drinkDao.update(drink)
    }

    suspend fun remove(drink: Drink) {
        Timber.d("Deleting drink from database")
        drinkDao.delete(drink)
    }

    suspend fun get(id: Int): Drink? {
        Timber.d("Finding drink in database")
        return drinkDao.get(id)
    }
}