package com.example.andrea.tictactoemultiplayer.models

import com.example.andrea.tictactoemultiplayer.views.CellView

/**
 * Created by andrea on 19/12/2017.
 */
data class Cell(val row: Int = -1,
                val column: Int = -1,
                var status: CellView.CellInfo.CellStatus = CellView.CellInfo.CellStatus.FREE) {

    /**
     * Returns if the cell's status is equal to checkStatus
     */
    fun isA(checkStatus: CellView.CellInfo.CellStatus) = status == checkStatus

}