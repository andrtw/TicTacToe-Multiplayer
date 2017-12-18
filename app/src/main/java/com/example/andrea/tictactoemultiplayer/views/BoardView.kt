package com.example.andrea.tictactoemultiplayer.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import com.example.andrea.tictactoemultiplayer.R

/**
 * Created by andrea on 18/12/2017.
 */
class BoardView(context: Context) : TableLayout(context) {

    companion object {
        val ROWS = 3
        val COLUMNS = 3
    }

    interface BoardListener {
        fun onCellClick(cell: Button)
    }

    private lateinit var mListener: BoardListener

    init {
        createCells()
    }

    private fun createCells() {
        val cellSize = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> resources.displayMetrics.widthPixels / COLUMNS
            Configuration.ORIENTATION_LANDSCAPE -> resources.displayMetrics.heightPixels / ROWS
            else -> resources.displayMetrics.widthPixels / COLUMNS
        }

        for (r in 0 until ROWS) {
            val tableRow = TableRow(context)
            for (c in 0 until COLUMNS) {
                val btn = Button(context)
                btn.text = "$r,$c"
                btn.setTextColor(resources.getColor(R.color.text_dark))

                val params = TableRow.LayoutParams(cellSize, cellSize)
                btn.layoutParams = params

                val info = CellInfo(r, c, CellInfo.CellStatus.FREE)
                btn.tag = info

                btn.setOnClickListener {
                    mListener.onCellClick(btn)
                }

                tableRow.addView(btn)
            }
            addView(tableRow)
        }

        setBackgroundColor(Color.RED)
    }

    fun setListener(listener: BoardListener) {
        mListener = listener
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        createCells()
    }

    data class CellInfo(val row: Int, val column: Int, val status: CellStatus) {
        enum class CellStatus {
            CROSS, NOUGHT, FREE
        }
    }

}