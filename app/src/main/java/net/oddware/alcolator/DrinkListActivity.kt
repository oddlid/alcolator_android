package net.oddware.alcolator

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.oddware.alcolator.databinding.ActivityDrinkListBinding
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*
TODO: 2021-01-22 01:35
  The drink list gets fucked up when rotating, if a filter is applied before rotation.
  It will then only have filtered values as its entire list, so clearing the filter or selecting
  another tag will not work. Don't yet know how to fix this.
 */

class DrinkListActivity :
    AppCompatActivity(),
    DeleteAllDialog.DeleteAllDialogListener {

    companion object {
        const val REQ_CREATE_FILE = 1
        const val REQ_OPEN_FILE = 2
        const val MIME_TYPE = "application/json"
        const val TAG_DRINK_LIST = "DrinkListFrag"
        const val TAG_COMPARE_DRINKS = "CompareDrinksFrag"
        const val TAG_TABS = "TabsFrag"

        @JvmStatic
        fun getCurrentDateISO8601String(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }

        @JvmStatic
        val json = Json { prettyPrint = true }
    }

    private lateinit var drinkViewModel: DrinkViewModel
    private lateinit var cResolver: ContentResolver
    private lateinit var binding: ActivityDrinkListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(supportFragmentManager.beginTransaction()) {
            addToBackStack(TAG_DRINK_LIST)
            replace(R.id.flDrinkList, DrinkListFragment())
            commit()
        }

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)
        cResolver = applicationContext.contentResolver

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bNavItemDrinkList -> {
                    with(supportFragmentManager.beginTransaction()) {
                        addToBackStack(TAG_DRINK_LIST)
                        replace(R.id.flDrinkList, DrinkListFragment())
                        commit()
                    }
                    true
                }
                R.id.bNavItemCompareDrinks -> {
                    with(supportFragmentManager.beginTransaction()) {
                        addToBackStack(TAG_COMPARE_DRINKS)
                        replace(R.id.flDrinkList, CompareDrinksFragment())
                        commit()
                    }
                    true
                }
                R.id.bNavItemTabList -> {
                    with(supportFragmentManager.beginTransaction()) {
                        addToBackStack(TAG_TABS)
                        replace(R.id.flDrinkList, TabListFragment())
                        commit()
                    }
                    true
                }
                else -> true
            }
        }
        //bottomNav.selectedItemId = R.id.bNavItemDrinkList

        /*
        TODO: Find out how to keep the shown list (drinklist or tablist) after rotation. As it is now,
              rotation will cause the drinklist to be loaded, but the selection stays on tablist if
              that was the current page before rotation.
         */
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> {
                // After adding the bottom nav, this is now very important, so that we don't try
                // to delete the drinklist if we're on the tab page.
                // TODO: We should rather switch the action of the delete menu item to delete tabs if
                //  that's the current page
                Timber.d("Current page: ${binding.bottomNav.selectedItemId}")

                when (binding.bottomNav.selectedItemId) {
                    R.id.bNavItemDrinkList -> {
                        val drinkListFrag =
                            supportFragmentManager.findFragmentById(R.id.flDrinkList)
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
                    R.id.bNavItemTabList -> {
                        Timber.d("Request to delete all tabs")
                    }
                }
            }
            R.id.action_backup -> {
                createFile()
            }
            R.id.action_backup_restore -> {
                openFile()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQ_CREATE_FILE -> {
                    //Timber.d("Create file: $data")
                    data?.data?.also { uri ->
                        //Timber.d("Uri: $uri")
                        if (writeBackup(uri)) {
                            showMessage("Success writing backup :)")
                        } else {
                            showMessage("Error writing backup :'(")
                        }
                    }
                }
                REQ_OPEN_FILE -> {
                    Timber.d("Open file: $data")
                    data?.data?.also { uri ->
                        Timber.d("Uri: $uri")
                        if (restoreBackup(uri)) {
                            showMessage("Success restoring from backup")
                        } else {
                            showMessage("Error restoring from backup")
                        }
                    }
                }
            }
        }
    }

    override fun onPositiveClick(df: DialogFragment) {
        Timber.d("Deleting all drinks from DB...")
        drinkViewModel.clear()
    }

    override fun onNegativeClick(df: DialogFragment) {
        Timber.d("Delete all cancelled")
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE
            putExtra(
                Intent.EXTRA_TITLE,
                String.format("%s_alcolator_data.json", getCurrentDateISO8601String())
            )
        }
        startActivityForResult(intent, REQ_CREATE_FILE)
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE
        }
        startActivityForResult(intent, REQ_OPEN_FILE)
    }

    @Throws(IOException::class)
    private fun writeBackup(uri: Uri): Boolean {
        val drinks = drinkViewModel.drinks.value ?: return false
        val jsonData = json.encodeToString(drinks)
        cResolver.openOutputStream(uri)?.use { os ->
            BufferedWriter(OutputStreamWriter(os)).use { writer ->
                writer.write(jsonData)
            }
        }

        return true
    }

    @Throws(IOException::class)
    private fun readBackup(uri: Uri): String {
        val sb = StringBuilder()
        cResolver.openInputStream(uri)?.use { ips ->
            BufferedReader(InputStreamReader(ips)).use { reader ->
                var line: String? = reader.readLine()
                while (null != line) {
                    sb.append(line)
                    line = reader.readLine()
                }
            }
        }
        return sb.toString()
    }

    private fun restoreBackup(uri: Uri): Boolean {
        val jsonData = readBackup(uri)
        if (jsonData.isEmpty()) {
            Timber.d("Got empty string from readBackup(), quitting")
            return false
        }

        val drinkList = json.decodeFromString<List<Drink>>(jsonData)
        if (drinkList.isEmpty()) {
            Timber.e("Error parsing JSON to list of Drink. List is empty.")
            return false
        }
        drinkViewModel.clear()
        drinkViewModel.import(drinkList)

        return true
    }

    private fun showMessage(msg: String) {
        Snackbar.make(
            findViewById(R.id.flDrinkList),
            msg,
            Snackbar.LENGTH_LONG
        ).show()
    }

}