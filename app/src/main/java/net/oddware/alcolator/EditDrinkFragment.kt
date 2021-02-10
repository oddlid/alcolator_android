package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import net.oddware.alcolator.databinding.FragmentEditDrinkBinding
import timber.log.Timber

class EditDrinkFragment : Fragment() {
    companion object {
        const val ACTION_ADD = 0xADD
        const val ACTION_EDIT = 0xED1
    }

    private var drinkObj: Drink? = null

    private val drinkViewModel: DrinkViewModel by activityViewModels()
    private var _binding: FragmentEditDrinkBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: EditDrinkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDrinkBinding.inflate(inflater, container, false)
        val view = binding.root

        if (ACTION_ADD == args.loadAction) {
            drinkObj = Drink()
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

            when (args.loadAction) {
                ACTION_ADD -> {
                    Timber.d("Saving new drink")
                    drinkObj?.run { drinkViewModel.add(this) }
                }
                ACTION_EDIT -> {
                    Timber.d("Saving existing drink")
                    drinkObj?.run { drinkViewModel.update(this) }
                }
                else -> Timber.e("Invalid code for save")
            }

            Navigation.findNavController(view)
                .navigateUp() // weird that this works fine here, but not from DrinkDetailFragment...
            //Navigation.findNavController(view).popBackStack() // does not work any better
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ACTION_EDIT == args.loadAction) {
            drinkViewModel.get(args.drinkID).observe(viewLifecycleOwner, { drink ->
                if (null != drink) {
                    updateUI(drink)
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