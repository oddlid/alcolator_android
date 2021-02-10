package net.oddware.alcolator

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import timber.log.Timber

class DeleteOneDialog : DialogFragment() {
    companion object {
        const val DELETE_ITEM_ACTION_KEY = "net.oddware.alcolator.DeleteOneDialog.DELETE_ACTION"
        const val DELETE_ITEM_ACTION_VAL_OK = "net.oddware.alcolator.DeleteOneDialog.DELETE_OK"
        const val DELETE_ITEM_ACTION_VAL_CANCEL =
            "net.oddware.alcolator.DeleteOneDialog.DELETE_CANCEL"
    }

    private val args: DeleteOneDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(activity)
        with(builder) {
            setMessage(getString(R.string.dlgDelOne_msgTxt, args.deleteItemName))
            setPositiveButton(R.string.dlgDelOne_btnOkTxt) { dlgInterface, _ ->
                //listener?.onPositiveClick(this@DeleteOneDialog)
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    DELETE_ITEM_ACTION_KEY,
                    DELETE_ITEM_ACTION_VAL_OK
                ) ?: run {
                    Timber.d("Did not find nav controller")
                }
                dlgInterface.dismiss()
            }
            setNegativeButton(R.string.dlgDelOne_btnCancelTxt) { dlgInterface, _ ->
                //listener?.onNegativeClick(this@DeleteOneDialog)
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    DELETE_ITEM_ACTION_KEY,
                    DELETE_ITEM_ACTION_VAL_CANCEL
                ) ?: run {
                    Timber.d("Did not find nav controller")
                }
                dlgInterface.cancel()
            }
        }

        return builder.create()
    }
}