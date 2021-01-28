package net.oddware.alcolator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.oddware.alcolator.databinding.FragmentDrinkDetailBinding
import timber.log.Timber
import java.util.*

class DrinkDetailFragment(private val drinkID: Int = DrinkDetailActivity.INVALID_ID) :
    Fragment(),
    DeleteOneDialog.DeleteOneDialogListener {

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

    private var drinkObj: Drink? = null
    private lateinit var drinkViewModel: DrinkViewModel
    private var _binding: FragmentDrinkDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrinkDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnClose.setOnClickListener {
            //activity?.finish()
            Timber.d("TODO: Remove this button, or make it pop the backstack")
        }

        binding.btnDelete.setOnClickListener {
            Timber.d("Delete clicked, checking for Fragment Manager...")
            val fm = activity?.supportFragmentManager ?: return@setOnClickListener
            Timber.d("We have a Fragment Manager...")
            val dod = DeleteOneDialog()
            dod.arguments = Bundle().apply {
                putString(DeleteOneDialog.DRINK_NAME, drinkObj?.name)
            }
            dod.listener = this
            dod.show(fm, "ConfirmDeleteOne")
        }

        binding.btnEdit.setOnClickListener {
            Timber.d("TODO: Update navigation for EDIT button")
            //context?.let {
            //    drinkObj?.run {
            //        startActivityForResult(
            //            AddDrinkActivity.getLaunchIntent(
            //                it,
            //                AddDrinkActivity.CFG_ACTION_EDIT,
            //                id
            //            ),
            //            AddDrinkActivity.CFG_ACTION_EDIT
            //        )
            //        // This didn't do anything...hmmm
            //        //drinkViewModel.get(id).observe(viewLifecycleOwner, Observer {
            //        //    if (null != it) {
            //        //        updateUI(it)
            //        //    }
            //        //})
            //        //activity?.finish()
            //    }
            //}

            // Didn't work at all
            //view.detailRow0.background = getBorders(
            //    Color.TRANSPARENT,
            //    Color.RED,
            //    0,
            //    0,
            //    0,
            //    2
            //)
        }

        Timber.d("DrinkDetailFragment created")
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        drinkViewModel = ViewModelProvider(this).get(DrinkViewModel::class.java)

        if (DrinkDetailActivity.INVALID_ID != drinkID) {
            Timber.d("Loading drink with id $drinkID")
            drinkViewModel.get(drinkID).observe(viewLifecycleOwner, {
                if (null != it) {
                    updateUI(it)
                }
            })
        } else {
            Timber.e("Got invalid drink ID (${DrinkDetailActivity.INVALID_ID})")
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

    override fun onPositiveClick(df: DialogFragment) {
        val d = drinkObj ?: return
        Timber.d("Deleting \"${d.name}\"")
        drinkViewModel.remove(d)
        Timber.d("TODO: update navigation for delete confirmation - don't close activity!")
        //activity?.finish()
    }

    override fun onNegativeClick(df: DialogFragment) {
        Timber.d("Delete cancelled")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("TODO: See if we can replace onActivityResult with something else in new navigation")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AddDrinkActivity.CFG_ACTION_EDIT -> {
                if (Activity.RESULT_OK == resultCode) {
                    data?.run {
                        val drinkID = getIntExtra(
                            AddDrinkActivity.DRINK_CFG_ID,
                            DrinkDetailActivity.INVALID_ID
                        )
                        if (DrinkDetailActivity.INVALID_ID != drinkID) {
                            drinkViewModel.get(drinkID).observe(viewLifecycleOwner, {
                                if (null != it) {
                                    updateUI(it)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}