package net.oddware.alcolator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.codecrafters.tableview.TableDataAdapter
import timber.log.Timber

class DrinkTableAdapter(
    private val ctx: Context?,
    private val drinks: List<Drink>,
    private val frag: Fragment
) : TableDataAdapter<Drink>(ctx, drinks) {
    companion object {
        const val PAD_TOP = 1
        const val PAD_LEFT = 1
        const val PAD_RIGHT = 1
        const val PAD_BOTTOM = 1
        const val TEXT_SIZE: Float = 12.0F
    }

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View {
        val drink = getRowData(rowIndex)

        return when (columnIndex) {
            //0 -> renderCell(drink.tag)
            //1 -> renderCell(drink.name)
            //2 -> renderCell(drink.volumeML.toString())
            //3 -> renderCell(drink.waterML().toString())
            //4 -> renderCell(drink.alcML().toString())
            //5 -> renderCell(drink.pricePerDrinkML().toString())
            //6 -> renderCell(drink.pricePerAlcML().toString())
            // New variant
            //0 -> renderCell(drink.name)
            0 -> renderButton(drink)
            1 -> renderCell(drink.volumeML.toString())
            2 -> renderCell(drink.price.toString())
            3 -> renderCell(drink.alcPct.toString())
            4 -> renderCell(drink.alcML().toString())
            5 -> renderCell(drink.pricePerAlcML().toString())
            else -> return View(context)
        }
    }

    private fun renderCell(txt: String): View {
        return TextView(ctx).apply {
            text = txt
            setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
            textSize = TEXT_SIZE
        }
    }

    private fun renderButton(d: Drink): View {
        return Button(ctx).apply {
            text = d.name
            setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
            textSize = TEXT_SIZE
            setOnClickListener { _ ->
                Timber.d("Button for \"$text\" was clicked...")
                ctx?.let {
                    Timber.d("Creating DrinkDetailActivity with drinkID ${d.id}")
                    frag.startActivityForResult(
                        DrinkDetailActivity.getLaunchIntent(
                            it,
                            d.id
                        ),
                        DrinkDetailActivity.DRINK_ACTION_VIEW
                    )
                }
            }
        }
    }
}