package net.oddware.alcolator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import net.oddware.alcolator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val appBarConfig = AppBarConfiguration(
        topLevelDestinationIds = setOf(
            R.id.drinkListFragment,
            R.id.compareDrinksFragment,
            R.id.tabListFragment
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupNav()
    }

    private fun setupNav() {
        val navCtl = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navCtl)

        setupActionBarWithNavController(navCtl, appBarConfig)
    }

    /* This did nothing. onSupportNavigateUp was the solution.
    override fun onNavigateUp(): Boolean {
        val navCtl = findNavController(R.id.nav_host_fragment)
        return navCtl.navigateUp(appBarConfig) || super.onNavigateUp()
    }

     */

    override fun onSupportNavigateUp(): Boolean {
        val navCtl = findNavController(R.id.nav_host_fragment)
        return navCtl.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}