package com.example.andrea.tictactoemultiplayer.views

import android.content.Context
import android.graphics.Color
import android.widget.TableLayout
import android.widget.TableRow

/**
 * Created by andrea on 18/12/2017.
 */
class BoardView(context: Context) : TableLayout(context) {

    companion object {
        val ROWS = 3
        val COLUMNS = 3
    }

    interface BoardListener {
        fun onCellClick(row: Int, column: Int, cell: CellView)
    }

    private lateinit var mListener: BoardListener

    private lateinit var mBoard: Array<Array<CellView.CellInfo.CellStatus>>

    init {
        createCells()
    }

    private fun createCells() {
        // add CellView to table layout
        for (r in 0 until ROWS) {
            val tableRow = TableRow(context)
            for (c in 0 until COLUMNS) {
                val cell = CellView(context)
                cell.text = "$r,$c"

                val info = CellView.CellInfo(r, c)
                cell.setCellInfo(info)

                cell.setOnClickListener {
                    mListener.onCellClick(r, c, cell)
                }

                tableRow.addView(cell)
            }
            addView(tableRow)
        }
        // create board array
        mBoard = arrayOf(
                arrayOf(CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE),
                arrayOf(CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE),
                arrayOf(CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE, CellView.CellInfo.CellStatus.FREE)
        )

        setBackgroundColor(Color.RED)
    }

    fun setListener(listener: BoardListener) {
        mListener = listener
    }

    fun setCellStatus(row: Int, column: Int, status: CellView.CellInfo.CellStatus) {
        mBoard[row][column] = status
    }

}
