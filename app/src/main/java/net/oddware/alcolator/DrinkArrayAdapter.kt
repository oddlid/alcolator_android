package net.oddware.alcolator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import timber.log.Timber
import java.util.*

class DrinkArrayAdapter(
    private val ctx: Context,
    private val resource: Int,
    private val textViewResourceId: Int,
    private val drinks: MutableList<Drink>
) : ArrayAdapter<Drink>(ctx, resource, textViewResourceId, drinks) {

    // This seems to only set the text for the selected item.
    // TODO: find a way to do the same for items in list when clicking dropdown
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // The code here is mostly just translated from ArrayAdapter Java, and customized
        // at the end to get the desired text from a Drink object
        val txtView: TextView
        val view: View = convertView ?: LayoutInflater.from(ctx).inflate(resource, parent, false)

        try {
            if (0 == textViewResourceId) {
                txtView = view as TextView
            } else {
                txtView = view.findViewById(textViewResourceId)

                if (null == txtView) {
                    throw RuntimeException(
                        "Failed to find view with ID ${
                            ctx.resources.getResourceName(
                                textViewResourceId
                            )
                        } in item layout"
                    )
                }
            }
        } catch (e: ClassCastException) {
            Timber.e("You must supply a resource ID for a TextView")
            throw IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e)
        }

        val drink = getItem(position)
        if (null != drink) {
            txtView.text = String.format(
                Locale.US,
                "%s @ %s",
                drink.name,
                drink.tag
            )
        } else {
            txtView.text = "NULL DRINK!"
        }

        return view
    }
}