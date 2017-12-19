package com.example.andrea.tictactoemultiplayer.views

import android.content.Context
import android.content.res.Configuration
import android.widget.Button
import android.widget.TableRow
import com.example.andrea.tictactoemultiplayer.R

/**
 * Created by andrea on 19/12/2017.
 */
class CellView(context: Context) : Button(context) {

    init {
        setTextColor(resources.getColor(R.color.text_dark))

        setSize()
    }

    private fun setSize() {
        val cellSize = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> resources.displayMetrics.widthPixels / BoardView.COLUMNS
            Configuration.ORIENTATION_LANDSCAPE -> resources.displayMetrics.heightPixels / BoardView.ROWS
            else -> resources.displayMetrics.widthPixels / BoardView.COLUMNS
        }

        val params = TableRow.LayoutParams(cellSize, cellSize)
        layoutParams = params
    }

    fun setCellInfo(info: CellInfo) {
        tag = info
    }

    fun getCellInfo() = tag as CellInfo

    class CellInfo(val row: Int, val column: Int, val status: CellStatus = CellStatus.FREE) {
        enum class CellStatus {
            CROSS, NOUGHT, FREE
        }
    }

}