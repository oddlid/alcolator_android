package net.oddware.alcolator

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DrinkDao {
    @Query("SELECT * from drinks")
    fun getDrinks(): LiveData<List<Drink>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(drink: Drink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun importDrinks(drinks: List<Drink>)

    @Query("DELETE FROM drinks")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(vararg drinks: Drink)

    @Update
    suspend fun update(drink: Drink)

    @Query("SELECT * FROM drinks WHERE id == :id")
    suspend fun get(id: Int): Drink
}