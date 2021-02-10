package net.oddware.alcolator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.oddware.alcolator.databinding.FragmentCompareDrinksAlt1Binding
import timber.log.Timber
import java.util.*

class CompareDrinksFragment : Fragment() {
    companion object {
        const val KEY_D1_POS = "Drink1SelectionPosition"
        const val KEY_D2_POS = "Drink2SelectionPosition"
    }

    private lateinit var drinkAdapter: DrinkArrayAdapter

    //private lateinit var drinkViewModel: DrinkViewModel
    private val drinkViewModel: DrinkViewModel by activityViewModels()
    private lateinit var drink1ItemSelectedListener: AdapterView.OnItemSelectedListener
    private lateinit var drink2ItemSelectedListener: AdapterView.OnItemSelectedListener
    private var _binding: FragmentCompareDrinksAlt1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var d1SelectedPos = 0
    private var d2SelectedPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompareDrinksAlt1Binding.inflate(inflater, container, false)
        val view = binding.root

        drinkAdapter = object : DrinkArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            0,
            mutableListOf(
                Drink(
                    name = requireContext().getString(R.string.cmpDrinkSpnHdr),
                    tag = "--"
                )
            )
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }.also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spnCmpDrink1Chooser.adapter = drinkAdapter
        binding.spnCmpDrink2Chooser.adapter = drinkAdapter

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
                    d1SelectedPos = position
                    drinkAdapter?.let {
                        val d1 = it.getItem(position)
                        displayDrink1(it.getItem(position))
                        val d2pos = binding.spnCmpDrink2Chooser.selectedItemPosition
                        if (0 != d2pos) {
                            d2SelectedPos = d2pos
                            val d2 = it.getItem(d2SelectedPos)
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
                    d2SelectedPos = position
                    drinkAdapter?.let {
                        val d1pos = binding.spnCmpDrink1Chooser.selectedItemPosition
                        if (0 != d1pos) {
                            d1SelectedPos = d1pos
                            val d1 = it.getItem(d1SelectedPos)
                            val d2 = it.getItem(d2SelectedPos)
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
                        d1SelectedPos = d1pos
                        val d1 = da.getItem(d1SelectedPos)
                        displayDrink1(d1)
                        if (d2pos > 0) {
                            d2SelectedPos = d2pos
                            val d2 = da.getItem(d2SelectedPos)
                            displayDrink2(d1, d2)
                        }
                    }
                }
            }
        })

        val manTw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calcManual()
            }
        }

        with(binding) {
            etCmpDrinkManD1PctVal.addTextChangedListener(manTw)
            etCmpDrinkManD2PctVal.addTextChangedListener(manTw)
            etCmpDrinkManD1VolVal.addTextChangedListener(manTw)
            etCmpDrinkManD2VolVal.addTextChangedListener(manTw)
            etCmpDrinkManD1PriceVal.addTextChangedListener(manTw)
            etCmpDrinkManD2PriceVal.addTextChangedListener(manTw)
        }

        binding.btnCmpDrinkSwap.setOnClickListener {
            with(binding) {
                val d1pct = etCmpDrinkManD1PctVal.text
                etCmpDrinkManD1PctVal.text = etCmpDrinkManD2PctVal.text
                etCmpDrinkManD2PctVal.text = d1pct
                val d1vol = etCmpDrinkManD1VolVal.text
                etCmpDrinkManD1VolVal.text = etCmpDrinkManD2VolVal.text
                etCmpDrinkManD2VolVal.text = d1vol
                val d1price = etCmpDrinkManD1PriceVal.text
                etCmpDrinkManD1PriceVal.text = etCmpDrinkManD2PriceVal.text
                etCmpDrinkManD2PriceVal.text = d1price
            }
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save for restoring after orientation change
        //Timber.d("Saving d1SelectedPos: $d1SelectedPos")
        outState.putInt(KEY_D1_POS, d1SelectedPos)
        //Timber.d("Saving d2SelectedPos: $d2SelectedPos")
        outState.putInt(KEY_D2_POS, d2SelectedPos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Relevant on orientation change
        d1SelectedPos = savedInstanceState?.getInt(KEY_D1_POS, 0) ?: 0
        d2SelectedPos = savedInstanceState?.getInt(KEY_D2_POS, 0) ?: 0

        //if (null != savedInstanceState) {
        //    Timber.d("savedInstanceState is NOT null, retrieving selected positions")
        //    val d1pos = savedInstanceState.getInt(KEY_D1_POS)
        //    val d2pos = savedInstanceState.getInt(KEY_D2_POS)
        //    Timber.d("Got selected positions from savedInstanceState: d1pos = $d1pos, d2pos = $d2pos")
        //    d1SelectedPos = d1pos
        //    d2SelectedPos = d2pos
        //} else {
        //    Timber.d("savedInstanceState is null, selected positions defaulting to 0")
        //}

        drinkViewModel.drinks.observe(viewLifecycleOwner, { drinks ->
            if (null != drinks) {
                val mdrinks = drinks.toMutableList()
                mdrinks.add(
                    0,
                    Drink(name = requireContext().getString(R.string.cmpDrinkSpnHdr), tag = "--")
                )
                //Timber.d("Updating drinks in adapter...")
                drinkAdapter.update(mdrinks)
                //Timber.d("Drink #1 selected position: $d1SelectedPos")
                //Timber.d("Drink #2 selected position: $d2SelectedPos")
                if (0 != d1SelectedPos) {
                    binding.spnCmpDrink1Chooser.setSelection(d1SelectedPos)
                }
                if (0 != d2SelectedPos) {
                    binding.spnCmpDrink2Chooser.setSelection(d2SelectedPos)
                }
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

    /*
    private fun clearManual() {
        with(binding) {
            etCmpDrinkManD1PctVal.text = null
            etCmpDrinkManD2PctVal.text = null
            etCmpDrinkManD1VolVal.text = null
            etCmpDrinkManD2VolVal.text = null
            etCmpDrinkManD1PriceVal.text = null
            etCmpDrinkManD2PriceVal.text = null
            tvCmpDrinkManAlcVal.text = null
            tvCmpDrinkManVolVal.text = null
            tvCmpDrinkManPriceVal.text = null
            etCmpDrinkManD1PctVal.requestFocus()
        }
    }

     */

    private fun calcManual() {
        val d1pct = binding.etCmpDrinkManD1PctVal.text.toString().toDoubleOrNull() ?: 0.0
        val d2pct = binding.etCmpDrinkManD2PctVal.text.toString().toDoubleOrNull() ?: 0.0
        val d1vol = binding.etCmpDrinkManD1VolVal.text.toString().toIntOrNull() ?: 0
        val d2vol = binding.etCmpDrinkManD2VolVal.text.toString().toIntOrNull() ?: 0
        val d1price = binding.etCmpDrinkManD1PriceVal.text.toString().toDoubleOrNull() ?: 0.0
        val d2price = binding.etCmpDrinkManD2PriceVal.text.toString().toDoubleOrNull() ?: 0.0

        val d1 = Drink(alcPct = d1pct, volumeML = d1vol, price = d1price)
        val d2 = Drink(alcPct = d2pct, volumeML = d2vol, price = d2price)
        val mres = d1.matchDrink(d2)

        with(binding) {
            tvCmpDrinkManVolVal.text = fmtdbl(mres.ml)
            tvCmpDrinkManAlcVal.text = fmtdbl(d2.alcML() * mres.times)
            tvCmpDrinkManPriceVal.text = fmtdbl(d2.price * mres.times)
        }
    }
}