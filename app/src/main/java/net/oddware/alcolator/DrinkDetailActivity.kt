package net.oddware.alcolator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_drink_detail)

        val drinkID = intent.getIntExtra(DRINK_ID, INVALID_ID)
        Timber.d("Creating DrinkDetailFragment with drinkID $drinkID")
        val fragDetail = DrinkDetailFragment(drinkID)
        //with(intent) {
        //    fragDetail.drinkID = getIntExtra(DRINK_ID, INVALID_ID)
        //}
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flDrinkDetail, fragDetail)
            commit()
        }

    }
}