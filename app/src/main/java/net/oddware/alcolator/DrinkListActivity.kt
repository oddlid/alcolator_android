package net.oddware.alcolator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import timber.log.Timber

class DrinkListActivity :
    AppCompatActivity(),
    DeleteAllDialog.DeleteAllDialogListener {

    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_list)

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.flDrinkList, DrinkListFragment())
            commit()
        }

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> {
                val drinkListFrag = supportFragmentManager.findFragmentById(R.id.flDrinkList)
                if (null != drinkListFrag) {
                    if (drinkListFrag is DrinkListFragment) {
                        if (drinkListFrag.listCount > 0) {
                            DeleteAllDialog().show(
                                supportFragmentManager,
                                "ConfirmDeleteAll"
                            )
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPositiveClick(df: DialogFragment) {
        Timber.d("Deleting all drinks from DB...")
        drinkViewModel.clear()
    }

    override fun onNegativeClick(df: DialogFragment) {
        Timber.d("Delete all cancelled")
    }
}