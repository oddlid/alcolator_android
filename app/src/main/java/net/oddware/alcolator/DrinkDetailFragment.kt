package net.oddware.alcolator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import net.oddware.alcolator.databinding.FragmentDrinkDetailBinding
import timber.log.Timber
import java.util.*

class DrinkDetailFragment() : Fragment() {

    //companion object {
    //    // This didn't work at all
    //    fun getBorders(
    //        bgColor: Int,
    //        borderColor: Int,
    //        left: Int,
    //        top: Int,
    //        right: Int,
    //        bottom: Int
    //    ): LayerDrawable {
    //        val borderColorDrawable = ColorDrawable(borderColor)
    //        val bgColorDrawable = ColorDrawable(bgColor)
    //        val drawables = arrayOf(borderColorDrawable, bgColorDrawable)
    //        val layerDrawable = LayerDrawable(drawables)

    //        layerDrawable.setLayerInset(
    //            1, // Index of the drawable to adjust [background color layer]
    //            left,     // Number of pixels to add to the left bound [left border]
    //            top,      // Number of pixels to add to the top bound [top border]
    //            right,    // Number of pixels to add to the right bound [right border]
    //            bottom    // Number of pixels to add to the bottom bound [bottom border]
    //        )

    //        return layerDrawable
    //    }
    //}

    companion object {
        const val INVALID_ID = -1
    }

    private var drinkObj: Drink? = null
    private val drinkViewModel: DrinkViewModel by activityViewModels()
    private var _binding: FragmentDrinkDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: DrinkDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrinkDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnDelete.setOnClickListener {
            drinkObj?.run {
                val navCtl = Navigation.findNavController(view)
                val action =
                    DrinkDetailFragmentDirections.actionDrinkDetailFragmentToDeleteOneDialog()
                action.deleteItemName = name

                // Important to set up listening BEFORE navigate()!
                navCtl.currentBackStackEntry?.savedStateHandle?.let { hnd ->
                    hnd.getLiveData<String>(DeleteOneDialog.DELETE_ITEM_ACTION_KEY)
                        .observe(viewLifecycleOwner, {
                            if (DeleteOneDialog.DELETE_ITEM_ACTION_VAL_OK == it) {
                                drinkViewModel.remove(this)
                                val action2 =
                                    DeleteOneDialogDirections.actionDeleteOneDialogToDrinkListFragment()
                                navCtl.navigate(action2) // Works well!
                            }
                            hnd.remove<String>(DeleteOneDialog.DELETE_ITEM_ACTION_KEY)
                        })
                }

                navCtl.navigate(action)
            }
        }

        binding.btnEdit.setOnClickListener {
            drinkObj?.run {
                val action =
                    DrinkDetailFragmentDirections.actionDrinkDetailFragmentToEditDrinkFragment()
                action.loadAction = EditDrinkFragment.ACTION_EDIT
                action.drinkID = id
                Navigation.findNavController(view).navigate(action)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (INVALID_ID != args.drinkID) {
            Timber.d("Loading drink with ID ${args.drinkID}")
            drinkViewModel.get(args.drinkID).observe(viewLifecycleOwner, { drink ->
                if (null != drink) {
                    updateUI(drink)
                }
            })
        } else {
            Timber.e("Got invalid drink ID: $INVALID_ID")
        }
    }

    private inline fun fmtdbl(d: Double?): String? {
        return d?.let {
            String.format(Locale.US, "%.2f", d)
        }
    }

    private fun updateUI(d: Drink?) {
        drinkObj = d
        binding.tvIDVal.text = d?.id.toString()
        binding.tvTagVal.text = d?.tag
        binding.tvNameVal.text = d?.name
        binding.tvVolVal.text = d?.volumeML.toString()
        binding.tvPctVal.text = fmtdbl(d?.alcPct)
        binding.tvPriceVal.text = fmtdbl(d?.price)
        binding.tvPricePerMLAlcVal.text = fmtdbl(d?.pricePerAlcML())
        binding.tvPricePerMLDrinkVal.text = fmtdbl(d?.pricePerDrinkML())
        binding.tvVolWaterVal.text = fmtdbl(d?.waterML())
        binding.tvVolAlcVal.text = fmtdbl(d?.alcML())
    }
}