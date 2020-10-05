package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import kotlinx.android.synthetic.main.fragment_drink_list.*
import kotlinx.android.synthetic.main.fragment_drink_list.view.*
import timber.log.Timber

class DrinkListFragment : Fragment() {
    //companion object {
    //    @JvmStatic
    //    //val TBL_HDRS = arrayOf("Pub", "Name", "Vol", "H2O", "Alc", "$ drink", "$ alc")
    //    val TBL_HDRS = arrayOf("Name", "ML", "#", "%", "Alc", "# Alc")
    //}

    //private class DrinkClickListener(private val ctx: Context, private val frag: Fragment) : TableDataClickListener<Drink> {
    //    override fun onDataClicked(rowIndex: Int, clickedData: Drink?) {
    //        clickedData.let {drink ->
    //            if (drink != null) {
    //                frag.startActivityForResult(
    //                    DrinkDetailActivity.getLaunchIntent(
    //                        ctx,
    //                        drink.id
    //                    ),
    //                    DrinkDetailActivity.DRINK_ACTION_VIEW
    //                )
    //            }
    //        }
    //    }
    //}

    private lateinit var drinkViewModel: DrinkViewModel
    //private lateinit var drinkTableAdapter: DrinkTableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drink_list, container, false)
        val tblHdrs = resources.getStringArray(R.array.colHdrs)
        view.tblDrinks.columnCount = tblHdrs.size // didn't work to set this in xml
        view.tblDrinks.headerAdapter = SimpleTableHeaderAdapter(
            view.context,
            *tblHdrs // use kotlin spread operator: *
        )
        //val tblColWeightModel = TableColumnWeightModel(tblHdrs.size)
        //with(tblColWeightModel) {
        //    setColumnWeight(0, 1) // tag
        //    setColumnWeight(1, 2) // name
        //    setColumnWeight(2, 2) // volume
        //    setColumnWeight(3, 2) // water
        //    setColumnWeight(4, 2) // alcohol
        //    setColumnWeight(5, 2) // price per ml drink
        //    setColumnWeight(6, 2) // price per ml alcohol
        //}
        //view.tblDrinks.columnModel = tblColWeightModel

        //view.tblDrinks.addDataClickListener(DrinkClickListener(context!!, this) as Nothing)

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

        drinkViewModel.drinks.observe(viewLifecycleOwner, Observer { drinks ->
            if (null == drinks) {
                Timber.d("Observer got a null list")
            } else {
                Timber.d("Observer got a list with something in it: ${drinks}")
                tblDrinks.dataAdapter = DrinkTableAdapter(context?.applicationContext, drinks, this)
            }
        })
    }
}