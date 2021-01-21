package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_compare_drinks.view.*
import timber.log.Timber

class CompareDrinksFragment : Fragment() {
    private var drinkAdapter: DrinkArrayAdapter? = null
    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compare_drinks, container, false)

        //drinkAdapter?.let {
        //    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //    view.spnCmpDrink1Chooser.adapter = drinkAdapter
        //    view.spnCmpDrink2Chooser.adapter = drinkAdapter
        //}

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        drinkViewModel.drinks.observe(viewLifecycleOwner, { drinks ->
            if (null != drinks) {
                // assign adapter
                Timber.d("Assigning DrinkArrayAdapter...")
                drinkAdapter = context?.let { ctx ->
                    DrinkArrayAdapter(
                        ctx,
                        android.R.layout.simple_spinner_item,
                        0,
                        drinks.toMutableList()
                    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                }
                view?.let {
                    it.spnCmpDrink1Chooser.adapter = drinkAdapter
                    it.spnCmpDrink2Chooser.adapter = drinkAdapter
                }
            }
        })
    }
}