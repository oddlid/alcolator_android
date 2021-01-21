package net.oddware.alcolator

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import timber.log.Timber
import java.util.*

open class DrinkArrayAdapter(
    private val ctx: Context,
    private val resource: Int,
    private val textViewResourceId: Int,
    private val drinks: MutableList<Drink>
) : ArrayAdapter<Drink>(ctx, resource, textViewResourceId, drinks) {

    /*
    This class mostly repeats what its superclass does, but separates out setting the text, so that
    we get custom text from the Drink item.
     */

    private val mInflater = LayoutInflater.from(ctx)
    private var mDropDownInflater: LayoutInflater? = null
    private var mResource = resource
    private var mDropDownResource = resource

    private fun createTextViewFromResource(
        inflater: LayoutInflater,
        convertView: View?,
        parent: ViewGroup,
        resId: Int
    ): TextView {
        val txtView: TextView
        val view: View = convertView ?: inflater.inflate(resId, parent, false)

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

        return txtView
    }

    private fun setDrinkText(txtView: TextView, drink: Drink?) {
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
    }

    // TODO: think through if this is how we want it
    // We could subclass and override from call site instead
    //override fun isEnabled(position: Int): Boolean {
    //    // Since we insert a header at position 0 before using this adapter, we will not enable
    //    // the item at this position.
    //    // This will make item #0 appear as selected when loading the list, but it will not be
    //    // possible to select that item after picking another selection.
    //    // Maybe this is not what we really want later. Could be just as well to check for position 0
    //    // in the listener and clear UI when selecting item #0 instead....
    //    return position != 0
    //}

    override fun setDropDownViewResource(resId: Int) {
        super.setDropDownViewResource(resId) // need to let the parent set its own mDropDownResource
        mDropDownResource = resId
    }

    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        super.setDropDownViewTheme(theme) // make sure the parent sets its own internal variables
        // just repeat what super does, so we get a hold of our own dropDownInflater
        if (null == theme) {
            mDropDownInflater = null
        } else if (mInflater.context.theme == theme) {
            mDropDownInflater = mInflater
        } else {
            mDropDownInflater = LayoutInflater.from(ContextThemeWrapper(ctx, theme))
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val txtView = createTextViewFromResource(mInflater, convertView, parent, mResource)
        setDrinkText(txtView, getItem(position))
        return txtView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val txtView = createTextViewFromResource(
            mDropDownInflater ?: mInflater,
            convertView,
            parent,
            mDropDownResource,
        )
        setDrinkText(txtView, getItem(position))
        return txtView
    }
}