package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import kotlinx.android.synthetic.main.fragment_drink_list.view.*
import timber.log.Timber

class DrinkListFragment : Fragment() {
    companion object {
        @JvmStatic
        val TBL_HDRS =
            arrayOf("Tag/Pub", "Name", "Volume", "Water", "Alcohol", "$ ml/drink", "$ ml/alc")
    }

    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drink_list, container, false)
        view.tblDrinks.columnCount = TBL_HDRS.size // didn't work to set this in xml
        view.tblDrinks.headerAdapter = SimpleTableHeaderAdapter(
            view.context,
            *TBL_HDRS // use kotlin spread operator: *
        )

        view.fabAddDrink.setOnClickListener {
            context?.let {
                startActivityForResult(
                    AddDrinkActivity.getLaunchIntent(
                        it,
                        AddDrinkActivity.CFG_ACTION_ADD,
                        0
                    ),
                    AddDrinkActivity.CFG_ACTION_ADD
                )
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        //tblDrinks.dataAdapter = DrinkTableAdapter(context, drinkViewModel.drinks.value)

        if (null == drinkViewModel.drinks.value) {
            Timber.d("Drink list is empty/null")
        }
    }
}