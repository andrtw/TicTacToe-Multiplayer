package com.example.andrea.tictactoemultiplayer.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.EditText
import com.example.andrea.tictactoemultiplayer.R
import kotlinx.android.synthetic.main.dialog_username.view.*

/**
 * Created by andrea on 18/12/2017.
 */
class UsernameDialog : DialogFragment() {

    interface UsernameDialogListener {
        fun onExitClick()
        fun onPlayClick(username: String, dialog: DialogFragment)
    }

    enum class Error {
        USERNAME_NOT_VALID,
        USERNAME_TAKEN
    }

    private lateinit var mListener: UsernameDialogListener

    private lateinit var mUsernameET: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_username, null)

        mUsernameET = view.usernameET

        // exit button
        view.exitBtn.setOnClickListener {
            mListener.onExitClick()
        }
        // play button
        view.playBtn.setOnClickListener {
            val username = view.usernameET.text.toString()
            mListener.onPlayClick(username, this)
        }

        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        // handle on back pressed
        builder.setOnKeyListener { dialogInterface, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mListener.onExitClick()
            }
            true
        }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return dialog
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = context as UsernameDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement UsernameDialogListener")
        }
    }

    fun showError(error: Error) {
        when (error) {
            Error.USERNAME_NOT_VALID -> mUsernameET.error = getString(R.string.dialog_username_error_username_not_valid)
            Error.USERNAME_TAKEN -> mUsernameET.error = getString(R.string.dialog_username_error_username_taken)
        }
    }

}