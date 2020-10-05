package net.oddware.alcolator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class DrinkDetailActivity : AppCompatActivity() {
    companion object {
        const val DRINK_ID = "net.oddware.alcolator.DRINK_ID"
        const val DRINK_ACTION_VIEW = 0x0DD
        const val INVALID_ID = -1

        @JvmStatic
        fun getLaunchIntent(ctx: Context, drinkID: Int = INVALID_ID): Intent {
            return with(Intent(ctx, DrinkDetailActivity::class.java)) {
                putExtra(DRINK_ID, drinkID)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_detail)
        title = "Drink details"

        val drinkID = intent.getIntExtra(DRINK_ID, INVALID_ID)
        Timber.d("Creating DrinkDetailFragment with drinkID $drinkID")
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flDrinkDetail, DrinkDetailFragment(drinkID))
            commit()
        }

    }
}