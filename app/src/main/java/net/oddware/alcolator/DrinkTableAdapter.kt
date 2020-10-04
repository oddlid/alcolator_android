package net.oddware.alcolator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.codecrafters.tableview.TableDataAdapter

class DrinkTableAdapter(
    private val ctx: Context,
    private val drinks: List<Drink>
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
        var renderedView = View(context)

        when (columnIndex) {
            0 -> renderedView = renderCell(drink.tag)
            1 -> renderedView = renderCell(drink.name)
            2 -> renderedView = renderCell(drink.volumeML.toString())
            3 -> renderedView = renderCell(drink.waterML().toString())
            4 -> renderedView = renderCell(drink.alcML().toString())
            5 -> renderedView = renderCell(drink.pricePerDrinkML().toString())
            6 -> renderedView = renderCell(drink.pricePerAlcML().toString())
        }
        return renderedView
    }

    private fun renderCell(txt: String): View {
        //val tv = TextView(context)
        //tv.text = txt
        //tv.setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
        //tv.textSize = TEXT_SIZE
        //return tv
        return TextView(context).apply {
            text = txt
            setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
            textSize = TEXT_SIZE
        }
    }
}