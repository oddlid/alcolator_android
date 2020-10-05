package net.oddware.alcolator

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteOneDialog : DialogFragment() {
    companion object {
        const val DRINK_NAME = "net.oddware.alcolator.DRINK_NAME"
    }

    interface DeleteOneDialogListener {
        fun onPositiveClick(df: DialogFragment)
        fun onNegativeClick(df: DialogFragment)
    }

    var listener: DeleteOneDialogListener? = null
    private var drinkName: String? = null

    // This should be used when instantiated from an Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DeleteOneDialogListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drinkName = arguments?.getString(DRINK_NAME)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        if (null == drinkName) {
            drinkName = "UNDEFINED"
        }

        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setMessage(getString(R.string.dlgDelOne_msgTxt, drinkName))
            setPositiveButton(R.string.dlgDelOne_btnOkTxt) { dlgInterface, _ ->
                listener?.onPositiveClick(this@DeleteOneDialog)
                dlgInterface.dismiss()
            }
            setNegativeButton(R.string.dlgDelOne_btnCancelTxt) { dlgInterface, _ ->
                listener?.onNegativeClick(this@DeleteOneDialog)
                dlgInterface.cancel()
            }
        }

        return builder.create()
    }
}