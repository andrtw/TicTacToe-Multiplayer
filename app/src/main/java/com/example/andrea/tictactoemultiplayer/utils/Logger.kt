package com.example.andrea.tictactoemultiplayer.utils

import android.util.Log

/**
 * Created by andrea on 18/12/2017.
 */

private const val TAG = "TicTacToeMultiplayer"

/**
 * Log messages to Logcat
 */
object Logger {

    /**
     * Debug
     */
    fun d(message: String) {
        Log.d(TAG, message)
    }

    /**
     * Error
     */
    fun e(message: String) {
        Log.e(TAG, message)
    }

}