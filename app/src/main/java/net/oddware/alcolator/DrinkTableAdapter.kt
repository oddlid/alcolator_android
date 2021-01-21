package net.oddware.alcolator

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sortabletableview.recyclerview.TableDataAdapter

class DrinkTableAdapter(
    ctx: Context?,
    drinks: List<Drink>,
) : TableDataAdapter<Drink, DrinkTableAdapter.ViewHolder>(ctx, drinks) {
    companion object {
        const val PAD_TOP = 16
        const val PAD_LEFT = 2
        const val PAD_RIGHT = 2
        const val PAD_BOTTOM = 16
        const val TEXT_SIZE: Float = 16.0F
    }

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
        init {
            textView.setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
            textView.textSize = TEXT_SIZE
        }
    }

    override fun onCreateCellViewHolder(
        columnIndex: Int,
        parent: ViewGroup?,
        viewType: Int
    ): ViewHolder {
        //Timber.d("Column index: $columnIndex")
        return ViewHolder(TextView(context))
    }

    override fun onBindCellViewHolder(cellViewHolder: ViewHolder?, rowIndex: Int, colIndex: Int) {
        //Timber.d("rowIndex: $rowIndex, colIndex: $colIndex")
        val d = getRowData(rowIndex)
        //Timber.d("Data: $data")
        when (colIndex) {
            0 -> cellViewHolder?.textView?.text = d.name
            1 -> {
                cellViewHolder?.textView?.text = d.volumeML.toString()
                cellViewHolder?.textView?.gravity = Gravity.END
            }
            2 -> {
                cellViewHolder?.textView?.text = String.format("%.2f", d.price)
                cellViewHolder?.textView?.gravity = Gravity.END
            }
            3 -> {
                cellViewHolder?.textView?.text = String.format("%.1f", d.alcPct)
                cellViewHolder?.textView?.gravity = Gravity.END
            }
            4 -> {
                cellViewHolder?.textView?.text = String.format("%.1f", d.alcML())
                cellViewHolder?.textView?.gravity = Gravity.END
            }
            5 -> {
                cellViewHolder?.textView?.text = String.format("%.2f", d.pricePerAlcML())
                cellViewHolder?.textView?.gravity = Gravity.END
            }
        }
    }
}