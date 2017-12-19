package com.example.andrea.tictactoemultiplayer.models

import com.example.andrea.tictactoemultiplayer.views.CellView

/**
 * Created by andrea on 19/12/2017.
 */
data class Cell(val row: Int,
                val column: Int,
                val status: CellView.CellInfo.CellStatus = CellView.CellInfo.CellStatus.FREE)
