package net.oddware.alcolator

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.oddware.alcolator.databinding.FragmentAddDrinkBinding
import timber.log.Timber

class AddDrinkFragment : Fragment() {
    //companion object {
    //    const val REQ_ADD = 0xADD
    //}

    var cfgAction = AddDrinkActivity.CFG_ACTION_ADD
    var drinkID = AddDrinkActivity.INVALID_IDX
    private var drinkObj: Drink? = null
    private lateinit var drinkViewModel: DrinkViewModel
    private var _binding: FragmentAddDrinkBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDrinkBinding.inflate(inflater, container, false)
        val view = binding.root

        if (AddDrinkActivity.CFG_ACTION_ADD == cfgAction) {
            drinkObj = Drink(id = drinkID)
        }

        binding.btnSave.setOnClickListener {
            drinkObj?.run {
                //id = drinkID
                tag = binding.etTag.text.toString().trim()
                name = binding.etName.text.toString().trim()

                val dVolumeML = binding.etVol.text.toString().trim()
                volumeML = if (dVolumeML.isEmpty()) {
                    0
                } else {
                    dVolumeML.toInt()
                }

                val dAlcPct = binding.etPct.text.toString().trim().replace(",", ".")
                alcPct = if (dAlcPct.isEmpty()) {
                    0.0
                } else {
                    dAlcPct.toDouble()
                }

                val dPrice = binding.etPrice.text.toString().trim().replace(",", ".")
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

        binding.btnCancel.setOnClickListener {
            activity?.finish()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        if (AddDrinkActivity.CFG_ACTION_EDIT == cfgAction) {
            drinkViewModel.get(drinkID).observe(viewLifecycleOwner, {
                if (null != it) {
                    updateUI(it)
                }
            })
        }
    }

    private fun updateUI(drink: Drink?) {
        drinkObj = drink
        binding.etTag.setText(drink?.tag)
        binding.etName.setText(drink?.name)
        binding.etVol.setText(drink?.volumeML.toString())
        binding.etPct.setText(drink?.alcPct.toString())
        binding.etPrice.setText(drink?.price.toString())
    }
}