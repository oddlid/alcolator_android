/*
package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sortabletableview.recyclerview.model.TableColumnWeightModel
import com.sortabletableview.recyclerview.toolkit.SimpleTableHeaderAdapter
import kotlinx.android.synthetic.main.fragment_drink_list.*
import kotlinx.android.synthetic.main.fragment_drink_list.view.*
import timber.log.Timber

class DrinkListFragment : Fragment() {
    companion object {
        class NameComparator : Comparator<Drink> {
            override fun compare(d0: Drink?, d1: Drink?): Int {
                return d0?.name!!.compareTo(d1!!.name)
            }

        }
    }

    private lateinit var drinkViewModel: DrinkViewModel

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
        val tblColWeightModel = TableColumnWeightModel(tblHdrs.size)
        with(tblColWeightModel) {
            setColumnWeight(0, 2) // name
            setColumnWeight(1, 1) // volume
            setColumnWeight(2, 1) // price
            setColumnWeight(3, 1) // percentage
            setColumnWeight(4, 1) // ml alcohol
            setColumnWeight(5, 1) // price per ml alcohol
        }
        view.tblDrinks.columnModel = tblColWeightModel

        view.tblDrinks.addDataClickListener { rowIndex, data ->
            Timber.d("rowIndex: $rowIndex, drink: $data")
            val drink = data as? Drink ?: return@addDataClickListener
            context?.let {
                startActivityForResult(
                    DrinkDetailActivity.getLaunchIntent(
                        it,
                        drink.id
                    ),
                    DrinkDetailActivity.DRINK_ACTION_VIEW
                )
            }
        }

        view.tblDrinks.setColumnComparator(0, NameComparator())

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

    //private inline fun List<Drink>.toStringArrayList(): List<Array<String>> {
    //    val lst = mutableListOf<Array<String>>()
    //    for (d in this) {
    //        lst.add(arrayOf(
    //            d.name,
    //            d.volumeML.toString(),
    //            d.price.toString(),
    //            d.alcPct.toString(),
    //            d.alcML().toString(),
    //            d.pricePerAlcML().toString()
    //        ))
    //    }
    //    return lst
    //}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.drinks.observe(viewLifecycleOwner, Observer { drinks ->
            if (null == drinks) {
                Timber.d("Observer got a null list")
            } else {
                Timber.d("Observer got a list with something in it: ${drinks}")
                tblDrinks.dataAdapter = DrinkTableAdapter(context, drinks)
            }
        })
    }
}
*/
