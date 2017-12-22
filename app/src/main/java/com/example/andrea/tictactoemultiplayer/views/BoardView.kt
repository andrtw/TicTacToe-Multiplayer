package com.example.andrea.tictactoemultiplayer.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.TableLayout
import android.widget.TableRow
import com.example.andrea.tictactoemultiplayer.models.Cell

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

    private var mBoard: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        createCells()
        setBackgroundColor(Color.RED)
    }

    private fun createCells() {
        // create the board
        for (r in 0 until ROWS) {
            val row = mutableListOf<Cell>()
            for (c in 0 until COLUMNS) {
                row.add(Cell(r, c))
            }
            mBoard.add(row)
        }
        updateBoard(mBoard)
    }

    /**
     * Sets a new board
     */
    fun updateBoard(board: MutableList<MutableList<Cell>>) {
        mBoard = board
        populateBoard()
    }

    /**
     * Updates the UI based on the board
     */
    private fun populateBoard() {
        removeAllViews()
        mBoard.forEach {
            val tableRow = TableRow(context)
            it.forEach {
                val r = it.row
                val c = it.column
                val status = it.status
                val cell = CellView(context)
                cell.text = when (status) {
                    CellView.CellInfo.CellStatus.FREE -> ""
                    CellView.CellInfo.CellStatus.CROSS -> "X"
                    CellView.CellInfo.CellStatus.NOUGHT -> "O"
                }

                val info = CellView.CellInfo(r, c, status)
                cell.setCellInfo(info)

                cell.setOnClickListener {
                    mListener.onCellClick(r, c, cell)
                }

                tableRow.addView(cell)
            }
            addView(tableRow)
        }
    }

    fun setListener(listener: BoardListener) {
        mListener = listener
    }

    fun updateCell(row: Int, column: Int, status: CellView.CellInfo.CellStatus) {
        mBoard[row][column].status = status
    }

    fun isBoardComplete() = mBoard.all {
        // row
        it.all {
            // cell
            it.isA(CellView.CellInfo.CellStatus.CROSS) || it.isA(CellView.CellInfo.CellStatus.NOUGHT)
        }
    }

    fun checkForWinner(whatToCheck: CellView.CellInfo.CellStatus): List<Cell> {
        val winnerCells = mutableListOf<Cell>()

        if (whatToCheck == CellView.CellInfo.CellStatus.FREE) {
            return winnerCells
        }

        val row0 = mBoard[0]
        val row1 = mBoard[1]
        val row2 = mBoard[2]

        // 0th horizontal
        if (row0[0].isA(whatToCheck) && row0[1].isA(whatToCheck) && row0[2].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 0, whatToCheck))
            winnerCells.add(Cell(0, 1, whatToCheck))
            winnerCells.add(Cell(0, 2, whatToCheck))
        }
        // 1st horizontal
        else if (row1[0].isA(whatToCheck) && row1[1].isA(whatToCheck) && row1[2].isA(whatToCheck)) {
            winnerCells.add(Cell(1, 0, whatToCheck))
            winnerCells.add(Cell(1, 1, whatToCheck))
            winnerCells.add(Cell(1, 2, whatToCheck))
        }
        // 2nd horizontal
        else if (row2[0].isA(whatToCheck) && row2[1].isA(whatToCheck) && row2[2].isA(whatToCheck)) {
            winnerCells.add(Cell(2, 0, whatToCheck))
            winnerCells.add(Cell(2, 1, whatToCheck))
            winnerCells.add(Cell(2, 2, whatToCheck))
        }

        // 0th column
        else if (row0[0].isA(whatToCheck) && row1[0].isA(whatToCheck) && row2[0].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 0, whatToCheck))
            winnerCells.add(Cell(1, 0, whatToCheck))
            winnerCells.add(Cell(2, 0, whatToCheck))
        }
        // 1st column
        else if (row0[1].isA(whatToCheck) && row1[1].isA(whatToCheck) && row2[1].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 1, whatToCheck))
            winnerCells.add(Cell(1, 1, whatToCheck))
            winnerCells.add(Cell(2, 1, whatToCheck))
        }
        // 2nd column
        else if (row0[2].isA(whatToCheck) && row1[2].isA(whatToCheck) && row2[2].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 2, whatToCheck))
            winnerCells.add(Cell(1, 2, whatToCheck))
            winnerCells.add(Cell(2, 2, whatToCheck))
        }

        // diagonal top-left bottom-right
        else if (row0[0].isA(whatToCheck) && row1[1].isA(whatToCheck) && row2[2].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 0, whatToCheck))
            winnerCells.add(Cell(1, 1, whatToCheck))
            winnerCells.add(Cell(2, 2, whatToCheck))
        }
        // diagonal top-right bottom-left
        else if (row0[2].isA(whatToCheck) && row1[1].isA(whatToCheck) && row2[0].isA(whatToCheck)) {
            winnerCells.add(Cell(0, 2, whatToCheck))
            winnerCells.add(Cell(1, 1, whatToCheck))
            winnerCells.add(Cell(2, 0, whatToCheck))
        }

        return winnerCells
    }

    fun colorCells(cells: List<Cell>, background: Drawable) {
        cells.forEach {
            val cellView = (getChildAt(it.row) as TableRow).getChildAt(it.column) as CellView
            cellView.background = background
        }
    }

}
