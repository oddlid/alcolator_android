package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_compare_drinks.view.*
import timber.log.Timber
import java.util.*

class CompareDrinksFragment : Fragment() {
    private var drinkAdapter: DrinkArrayAdapter? = null
    private lateinit var drinkViewModel: DrinkViewModel
    private lateinit var drink1ItemSelectedListener: AdapterView.OnItemSelectedListener
    private lateinit var drink2ItemSelectedListener: AdapterView.OnItemSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compare_drinks, container, false)

        drink1ItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View?,
                position: Int,
                id: Long
            ) {
                if (0 == position) {
                    displayDrink1(view, null)
                } else {
                    drinkAdapter?.let {
                        val d1 = it.getItem(position)
                        displayDrink1(view, it.getItem(position))
                        val d2pos = view.spnCmpDrink2Chooser.selectedItemPosition
                        if (0 != d2pos) {
                            val d2 = it.getItem(d2pos)
                            displayDrink2(view, d1, d2)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing selected for Drink #1 spinner")
            }
        }
        view.spnCmpDrink1Chooser.onItemSelectedListener = drink1ItemSelectedListener

        drink2ItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View?,
                position: Int,
                id: Long
            ) {
                if (0 == position) {
                    displayDrink2(view, null, null)
                } else {
                    drinkAdapter?.let {
                        val d1pos = view.spnCmpDrink1Chooser.selectedItemPosition
                        if (0 != d1pos) {
                            val d1 = it.getItem(d1pos)
                            val d2 = it.getItem(position)
                            displayDrink2(view, d1, d2)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing selected for Drink #2 spinner")
            }
        }
        view.spnCmpDrink2Chooser.onItemSelectedListener = drink2ItemSelectedListener

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.drinks.observe(viewLifecycleOwner, { drinks ->
            if (null != drinks) {
                // Insert inactive header selection
                val mdrinks = drinks.toMutableList()
                context?.let { ctx ->
                    mdrinks.add(
                        0,
                        Drink(
                            name = ctx.getString(R.string.cmpDrinkSpnHdr),
                            tag = "--"
                        ) // bogus header
                    )
                    // Create adapter
                    drinkAdapter = DrinkArrayAdapter(
                        ctx,
                        android.R.layout.simple_spinner_item,
                        0,
                        mdrinks
                    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                }

                // If we would like to override a method in DrinkArrayAdapter, we could assign it
                // like this instead of the above:
                //drinkAdapter = object : DrinkArrayAdapter(
                //    requireContext(),
                //    android.R.layout.simple_spinner_item,
                //    0,
                //    mdrinks
                //) {
                //    override fun isEnabled(position: Int): Boolean {
                //        return position != 0
                //    }
                //}.also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }


                view?.let {
                    it.spnCmpDrink1Chooser.adapter = drinkAdapter
                    it.spnCmpDrink2Chooser.adapter = drinkAdapter
                }
            }
        })
    }

    private inline fun fmtdbl(d: Double): String = String.format(Locale.US, "%.2f", d)

    private fun displayDrink1(v: View, drink: Drink?) {
        if (null == drink) {
            v.tvCmpDrink1PctVal.text = null
            v.tvCmpDrink1VolVal.text = null
            v.tvCmpDrink1AlcVolVal.text = null
            v.tvCmpDrink1PriceVal.text = null
            v.tvCmpDrink1PricePMLAVal.text = null
        } else {
            v.tvCmpDrink1PctVal.text = fmtdbl(drink.alcPct)
            v.tvCmpDrink1VolVal.text = drink.volumeML.toString()
            v.tvCmpDrink1AlcVolVal.text = fmtdbl(drink.alcML())
            v.tvCmpDrink1PriceVal.text = fmtdbl(drink.price)
            v.tvCmpDrink1PricePMLAVal.text = fmtdbl(drink.pricePerAlcML())
        }
    }

    private fun displayDrink2(v: View, d1: Drink?, d2: Drink?) {
        if (null == d1 || null == d2) {
            v.tvCmpDrink2PctVal.text = null
            v.tvCmpDrink2AmountVal.text = null
            v.tvCmpDrink2VolVal.text = null
            v.tvCmpDrink2AlcVolVal.text = null
            v.tvCmpDrink2PriceVal.text = null
            v.tvCmpDrink2PricePMLAVal.text = null
            return
        }
        val mres = d1.matchDrink(d2)
        v.tvCmpDrink2PctVal.text = fmtdbl(d2.alcPct)
        v.tvCmpDrink2AmountVal.text = String.format(
            Locale.US,
            "%.2f X %d ml",
            mres.times,
            d2.volumeML
        )
        v.tvCmpDrink2VolVal.text = fmtdbl(mres.ml)
        v.tvCmpDrink2AlcVolVal.text = fmtdbl(d2.alcML() * mres.times)
        v.tvCmpDrink2PriceVal.text = fmtdbl(mres.times * d2.price)
        v.tvCmpDrink2PricePMLAVal.text = fmtdbl(d2.pricePerAlcML())
    }
}