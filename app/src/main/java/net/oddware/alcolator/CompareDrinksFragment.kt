package net.oddware.alcolator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.oddware.alcolator.databinding.FragmentCompareDrinksAlt1Binding
import timber.log.Timber
import java.util.*

class CompareDrinksFragment : Fragment() {
    private var drinkAdapter: DrinkArrayAdapter? = null
    private lateinit var drinkViewModel: DrinkViewModel
    private lateinit var drink1ItemSelectedListener: AdapterView.OnItemSelectedListener
    private lateinit var drink2ItemSelectedListener: AdapterView.OnItemSelectedListener
    private var _binding: FragmentCompareDrinksAlt1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompareDrinksAlt1Binding.inflate(inflater, container, false)
        val view = binding.root

        drink1ItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View?,
                position: Int,
                id: Long
            ) {
                if (0 == position) {
                    displayDrink1(null)
                } else {
                    drinkAdapter?.let {
                        val d1 = it.getItem(position)
                        displayDrink1(it.getItem(position))
                        val d2pos = binding.spnCmpDrink2Chooser.selectedItemPosition
                        if (0 != d2pos) {
                            val d2 = it.getItem(d2pos)
                            displayDrink2(d1, d2)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing selected for Drink #1 spinner")
            }
        }
        binding.spnCmpDrink1Chooser.onItemSelectedListener = drink1ItemSelectedListener

        drink2ItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View?,
                position: Int,
                id: Long
            ) {
                if (0 == position) {
                    displayDrink2(null, null)
                } else {
                    drinkAdapter?.let {
                        val d1pos = binding.spnCmpDrink1Chooser.selectedItemPosition
                        if (0 != d1pos) {
                            val d1 = it.getItem(d1pos)
                            val d2 = it.getItem(position)
                            displayDrink2(d1, d2)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing selected for Drink #2 spinner")
            }
        }
        binding.spnCmpDrink2Chooser.onItemSelectedListener = drink2ItemSelectedListener

        binding.etCmpDrink1AmountVal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Timber.d("Amount: $s, length: $count")
            }

            override fun afterTextChanged(s: Editable?) {
                //Timber.d("Text was changed: ${s.toString()}")
                s.toString().toIntOrNull() ?: return
                drinkAdapter?.let { da ->
                    val d1pos = binding.spnCmpDrink1Chooser.selectedItemPosition
                    val d2pos = binding.spnCmpDrink2Chooser.selectedItemPosition

                    if (d1pos > 0) {
                        val d1 = da.getItem(d1pos)
                        displayDrink1(d1)
                        if (d2pos > 0) {
                            val d2 = da.getItem(d2pos)
                            displayDrink2(d1, d2)
                        }
                    }
                }
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


                //view?.let {
                //    it.spnCmpDrink1Chooser.adapter = drinkAdapter
                //    it.spnCmpDrink2Chooser.adapter = drinkAdapter
                //}
                binding.spnCmpDrink1Chooser.adapter = drinkAdapter
                binding.spnCmpDrink2Chooser.adapter = drinkAdapter
            }
        })
    }

    private inline fun fmtdbl(d: Double): String = String.format(Locale.US, "%.2f", d)

    private fun getD1Amount(): Int {
        return binding.etCmpDrink1AmountVal.text.toString().toIntOrNull() ?: 1
    }

    private fun displayDrink1(drink: Drink?) {
        if (null == drink) {
            binding.tvCmpDrink1PctVal.text = null
            binding.tvCmpDrink1VolVal.text = null
            binding.tvCmpDrink1AlcVolVal.text = null
            binding.tvCmpDrink1PriceVal.text = null
            //binding.tvCmpDrink1PricePMLAVal.text = null
            binding.spnCmpDrink2Chooser.setSelection(0)
        } else {
            val amount = getD1Amount()
            binding.tvCmpDrink1PctVal.text = fmtdbl(drink.alcPct)
            binding.tvCmpDrink1VolVal.text = (drink.volumeML * amount).toString()
            binding.tvCmpDrink1AlcVolVal.text = fmtdbl(drink.alcML() * amount)
            binding.tvCmpDrink1PriceVal.text = fmtdbl(drink.price * amount)
            //v.tvCmpDrink1PricePMLAVal.text = fmtdbl(drink.pricePerAlcML())
        }
    }

    private fun displayDrink2(d1: Drink?, d2: Drink?) {
        if (null == d1 || null == d2) {
            binding.tvCmpDrink2PctVal.text = null
            binding.tvCmpDrink2AmountVal.text = null
            binding.tvCmpDrink2VolVal.text = null
            binding.tvCmpDrink2AlcVolVal.text = null
            binding.tvCmpDrink2PriceVal.text = null
            //binding.tvCmpDrink2PricePMLAVal.text = null
            return
        }
        val mres = d1.matchDrink(d2)
        val amount = getD1Amount()
        binding.tvCmpDrink2PctVal.text = fmtdbl(d2.alcPct)
        binding.tvCmpDrink2AmountVal.text = String.format(
            Locale.US,
            "%.2f X %d ml",
            mres.times * amount,
            d2.volumeML
        )
        binding.tvCmpDrink2VolVal.text = fmtdbl(mres.ml * amount)
        binding.tvCmpDrink2AlcVolVal.text = fmtdbl(d2.alcML() * mres.times * amount)
        binding.tvCmpDrink2PriceVal.text = fmtdbl(mres.times * d2.price * amount)
        //binding.tvCmpDrink2PricePMLAVal.text = fmtdbl(d2.pricePerAlcML())
    }
}