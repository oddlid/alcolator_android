package net.oddware.alcolator

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Drink::class],
    version = 1,
    exportSchema = false
)
abstract class DrinkDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: DrinkDatabase? = null

        fun get(ctx: Context): DrinkDatabase {
            val tmpInst = INSTANCE
            if (null != tmpInst) {
                return tmpInst
            }
            synchronized(this) {
                val inst = Room.databaseBuilder(
                    ctx.applicationContext,
                    DrinkDatabase::class.java,
                    "alcodb"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                return inst
            }
        }
    }

    abstract fun drinkDao(): DrinkDao
}