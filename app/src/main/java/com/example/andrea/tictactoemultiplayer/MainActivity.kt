package com.example.andrea.tictactoemultiplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.andrea.tictactoemultiplayer.views.UsernameDialog

class MainActivity : AppCompatActivity(), UsernameDialog.UsernameDialogListener {

    // username dialog
    private lateinit var mUsernameDialog: UsernameDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.fullscreen()

        showUsernameDialog()
    }

    private fun showUsernameDialog() {
        mUsernameDialog = UsernameDialog()
        mUsernameDialog.show(supportFragmentManager, "username_dialog")
    }

    override fun onExitClick() {
        finish()
    }

    override fun onPlayClick(username: String, dialog: DialogFragment) {

    }
}
