package com.example.andrea.tictactoemultiplayer.models

/**
 * Created by andrea on 20/12/2017.
 */
data class Winner(var who: Who = Who.NONE,
                  var cells: List<Cell> = emptyList()) {

    enum class Who {
        HOST, GUEST, TIE, NONE
    }

}