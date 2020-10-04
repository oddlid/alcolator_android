package net.oddware.alcolator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrinkViewModel(application: Application) : AndroidViewModel(application) {
    private val drinkRepo: DrinkRepo = DrinkRepo.getInstance(application)
    val drinks = drinkRepo.drinks

    fun add(drink: Drink) = viewModelScope.launch(Dispatchers.IO) {
        drinkRepo.add(drink)
    }

    fun clear() = viewModelScope.launch(Dispatchers.IO) {
        drinkRepo.clear()
    }

    fun update(drink: Drink) = viewModelScope.launch(Dispatchers.IO) {
        drinkRepo.update(drink)
    }

    fun remove(drink: Drink) = viewModelScope.launch(Dispatchers.IO) {
        drinkRepo.remove(drink)
    }

    fun get(index: Int): Drink? {
        return drinkRepo.get(index)
    }
}