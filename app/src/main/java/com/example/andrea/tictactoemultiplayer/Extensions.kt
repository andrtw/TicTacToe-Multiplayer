package com.example.andrea.tictactoemultiplayer

import android.view.Window
import android.view.WindowManager

/**
 * Created by andrea on 18/12/2017.
 */

/**
 * Make activity go fullscreen
 */
fun Window.fullscreen() {
    this.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}