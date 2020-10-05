package net.oddware.alcolator

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sortabletableview.recyclerview.TableDataAdapter
import timber.log.Timber

class DrinkTableAdapter(
    ctx: Context?,
    drinks: List<Drink>,
) : TableDataAdapter<Drink, DrinkTableAdapter.ViewHolder>(ctx, drinks) {
    companion object {
        const val PAD_TOP = 1
        const val PAD_LEFT = 1
        const val PAD_RIGHT = 1
        const val PAD_BOTTOM = 1
        const val TEXT_SIZE: Float = 12.0F
    }

    //init {
    //    Timber.d("Got context: $ctx")
    //    Timber.d("Got fragment: $frag")
    //}

    inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
    }

    override fun onCreateCellViewHolder(
        columnIndex: Int,
        parent: ViewGroup?,
        viewType: Int
    ): ViewHolder {
        Timber.d("Column index: $columnIndex")
        val vh = ViewHolder(TextView(context))
        return vh
    }

    override fun onBindCellViewHolder(cellViewHolder: ViewHolder?, rowIndex: Int, colIndex: Int) {
        Timber.d("rowIndex: $rowIndex, colIndex: $colIndex")
        val d = getRowData(rowIndex)
        //Timber.d("Data: $data")
        when (colIndex) {
            0 -> cellViewHolder?.textView?.text = d.name
            1 -> cellViewHolder?.textView?.text = d.volumeML.toString()
            2 -> cellViewHolder?.textView?.text = d.price.toString()
            3 -> cellViewHolder?.textView?.text = d.alcPct.toString()
            4 -> cellViewHolder?.textView?.text = d.alcML().toString()
            5 -> cellViewHolder?.textView?.text = d.pricePerAlcML().toString()
        }
    }

    //override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup?): View {
    //    val drink = getRowData(rowIndex)

    //    return when (columnIndex) {
    //        //0 -> renderCell(drink.name)
    //        0 -> renderButton(drink)
    //        1 -> renderCell(drink.volumeML.toString())
    //        2 -> renderCell(drink.price.toString())
    //        3 -> renderCell(drink.alcPct.toString())
    //        4 -> renderCell(drink.alcML().toString())
    //        5 -> renderCell(drink.pricePerAlcML().toString())
    //        else -> return View(context)
    //    }
    //}

    //private fun renderCell(txt: String): View {
    //    return TextView(ctx).apply {
    //        text = txt
    //        setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
    //        textSize = TEXT_SIZE
    //    }
    //}

    //private fun renderButton(d: Drink): View {
    //    return Button(ctx).apply {
    //        text = d.name
    //        setPadding(PAD_LEFT, PAD_TOP, PAD_RIGHT, PAD_BOTTOM)
    //        textSize = TEXT_SIZE
    //        setOnClickListener { _ ->
    //            Timber.d("Button for \"$text\" was clicked...")
    //            ctx?.let {
    //                Timber.d("Creating DrinkDetailActivity with drinkID ${d.id}")
    //                //frag.startActivityForResult(
    //                //    DrinkDetailActivity.getLaunchIntent(
    //                //        it,
    //                //        d.id
    //                //    ),
    //                //    DrinkDetailActivity.DRINK_ACTION_VIEW
    //                //)
    //                //frag.startActivity(
    //                //    DrinkDetailActivity.getLaunchIntent(
    //                //        it,
    //                //        d.id
    //                //    )
    //                //)
    //                //it.startActivity(
    //                //    DrinkDetailActivity.getLaunchIntent(
    //                //        it,
    //                //        d.id
    //                //    )
    //                //)
    //                val intent = Intent(it, DrinkDetailActivity::class.java).apply {
    //                    putExtra(DrinkDetailActivity.DRINK_ID, d.id)
    //                }
    //                it.startActivity(intent)
    //            }
    //        }
    //    }
    //}
}