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
import kotlinx.android.synthetic.main.fragment_drink_detail.*
import kotlinx.android.synthetic.main.fragment_drink_detail.view.*
import timber.log.Timber

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drink_detail, container, false)

        view.btnClose.setOnClickListener {
            activity?.finish()
        }

        view.btnDelete.setOnClickListener {
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

        view.btnEdit.setOnClickListener {
            context?.let {
                drinkObj?.run {
                    startActivityForResult(
                        AddDrinkActivity.getLaunchIntent(
                            it,
                            AddDrinkActivity.CFG_ACTION_EDIT,
                            id
                        ),
                        AddDrinkActivity.CFG_ACTION_EDIT
                    )
                    // This didn't do anything...hmmm
                    //drinkViewModel.get(id).observe(viewLifecycleOwner, Observer {
                    //    if (null != it) {
                    //        updateUI(it)
                    //    }
                    //})
                    //activity?.finish()
                }
            }

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

    private fun updateUI(d: Drink?) {
        drinkObj = d
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

    override fun onPositiveClick(df: DialogFragment) {
        val d = drinkObj ?: return
        Timber.d("Deleting \"${d.name}\"")
        drinkViewModel.remove(d)
        activity?.finish()
    }

    override fun onNegativeClick(df: DialogFragment) {
        Timber.d("Delete cancelled")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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