package net.oddware.alcolator

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteAllDialog : DialogFragment() {
    interface DeleteAllDialogListener {
        fun onPositiveClick(df: DialogFragment)
        fun onNegativeClick(df: DialogFragment)
    }

    var listener: DeleteAllDialogListener? = null

    // This should be used when instantiated from an Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DeleteAllDialogListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setMessage(R.string.dlgDel_msgTxt)
            setPositiveButton(R.string.dlgDel_btnOkTxt) { dlgInterface, _ ->
                listener?.onPositiveClick(this@DeleteAllDialog)
                dlgInterface.dismiss()
            }
            setNegativeButton(R.string.dlgDel_btnCancelTxt) { dlgInterface, _ ->
                listener?.onNegativeClick(this@DeleteAllDialog)
                dlgInterface.cancel()
            }
        }
        return builder.create()
    }
}