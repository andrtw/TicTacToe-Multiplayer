package com.example.andrea.tictactoemultiplayer.views

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import com.example.andrea.tictactoemultiplayer.R

/**
 * Created by andrea on 19/12/2017.
 */
class CellView(context: Context) : TextView(context) {

    init {
        textSize = 22f
        setTextColor(resources.getColor(R.color.text_dark))
        gravity = Gravity.CENTER
        background = resources.getDrawable(R.drawable.cell_background)

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