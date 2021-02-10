package net.oddware.alcolator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddDrinkActivity : AppCompatActivity() {
    companion object {
        const val DRINK_CFG_ACTION = "net.oddware.alcolator.DRINK_CFG_ACTION"
        const val DRINK_CFG_ID = "net.oddware.alcolator.DRINK_CFG_ID"
        const val CFG_ACTION_ADD = 0xADD
        const val CFG_ACTION_EDIT = 0xEDD
        const val INVALID_IDX = -1

        //@JvmStatic
        //fun getLaunchIntent(ctx: Context, action: Int, index: Int = INVALID_IDX): Intent {
        //    return with(Intent(ctx, AddDrinkActivity::class.java)) {
        //        putExtra(DRINK_CFG_ACTION, action)
        //        putExtra(DRINK_CFG_ID, index)
        //    }
        //}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drink)

        val fragAdd = EditDrinkFragment()
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flAddDrink, fragAdd)
            commit()
        }

        //with(intent) {
        //    fragAdd.cfgAction = getIntExtra(DRINK_CFG_ACTION, INVALID_IDX)
        //    fragAdd.drinkID = getIntExtra(DRINK_CFG_ID, INVALID_IDX)

        //}
    }
}