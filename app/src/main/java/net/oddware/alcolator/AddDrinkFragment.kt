package net.oddware.alcolator

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_drink.*
import kotlinx.android.synthetic.main.fragment_add_drink.view.*
import timber.log.Timber

class AddDrinkFragment : Fragment() {
    //companion object {
    //    const val REQ_ADD = 0xADD
    //}

    var cfgAction = AddDrinkActivity.CFG_ACTION_ADD
    var drinkID = AddDrinkActivity.INVALID_IDX
    private var drinkObj: Drink? = null
    private lateinit var drinkViewModel: DrinkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_drink, container, false)

        if (AddDrinkActivity.CFG_ACTION_ADD == cfgAction) {
            drinkObj = Drink(id = drinkID)
        }

        view.btnSave.setOnClickListener {
            drinkObj?.run {
                //id = drinkID
                tag = view.etTag.text.toString().trim()
                name = view.etName.text.toString().trim()

                val dVolumeML = view.etVol.text.toString().trim()
                volumeML = if (dVolumeML.isEmpty()) {
                    0
                } else {
                    dVolumeML.toInt()
                }

                val dAlcPct = view.etPct.text.toString().trim().replace(",", ".")
                alcPct = if (dAlcPct.isEmpty()) {
                    0.0
                } else {
                    dAlcPct.toDouble()
                }

                val dPrice = view.etPrice.text.toString().trim().replace(",", ".")
                price = if (dPrice.isEmpty()) {
                    0.0
                } else {
                    dPrice.toDouble()
                }
            }
            activity?.setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(AddDrinkActivity.DRINK_CFG_ID, drinkID)
                }
            )

            when (cfgAction) {
                AddDrinkActivity.CFG_ACTION_ADD -> {
                    Timber.d("Saving new drink")
                    drinkObj?.run { drinkViewModel.add(this) }
                }
                AddDrinkActivity.CFG_ACTION_EDIT -> {
                    Timber.d("Saving existing drink")
                    drinkObj?.run { drinkViewModel.update(this) }
                }
                else -> Timber.e("Invalid code for save")
            }

            activity?.finish()
        }

        view.btnCancel.setOnClickListener {
            activity?.finish()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        if (AddDrinkActivity.CFG_ACTION_EDIT == cfgAction) {
            drinkViewModel.get(drinkID).observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    updateUI(it)
                }
            })
        }
    }

    private fun updateUI(drink: Drink?) {
        etTag.setText(drink?.tag)
        etName.setText(drink?.name)
        etVol.setText(drink?.volumeML.toString())
        etPct.setText(drink?.alcPct.toString())
        etPrice.setText(drink?.price.toString())
    }
}