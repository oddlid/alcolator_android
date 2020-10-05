package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_drink_detail.*
import kotlinx.android.synthetic.main.fragment_drink_detail.view.*
import timber.log.Timber

class DrinkDetailFragment(private val drinkID: Int = DrinkDetailActivity.INVALID_ID) : Fragment() {

    private var drinkObj: Drink? = null
    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drink_detail, container, false)

        view.btnClose.setOnClickListener {
            activity?.finish()
        }

        Timber.d("DrinkDetailFragment created")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        if (DrinkDetailActivity.INVALID_ID != drinkID) {
            Timber.d("Loading drink with id $drinkID")
            //drinkObj = drinkViewModel.get(drinkID)
            //updateUI(drinkObj)
            drinkViewModel.get(drinkID).observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    updateUI(it)
                }
            })
        } else {
            Timber.e("Got invalid drink ID (${DrinkDetailActivity.INVALID_ID})")
        }
    }

    private fun updateUI(d: Drink?) {
        tvIDVal.text = d?.id.toString()
        tvTagVal.text = d?.tag
        tvNameVal.text = d?.name
        tvVolVal.text = d?.volumeML.toString()
        tvPctVal.text = d?.alcPct.toString()
        tvPriceVal.text = d?.price.toString()
        tvPricePerMLAlcVal.text = d?.pricePerAlcML().toString()
        tvPricePerMLDrinkVal.text = d?.pricePerDrinkML().toString()
        tvVolWaterVal.text = d?.waterML().toString()
        tvVolAlcVal.text = d?.alcML().toString()
    }
}