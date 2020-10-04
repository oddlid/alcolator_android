package net.oddware.alcolator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DrinkListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_list)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flDrinkList, DrinkListFragment())
            commit()
        }
    }
}