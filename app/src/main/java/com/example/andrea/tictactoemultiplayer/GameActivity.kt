package com.example.andrea.tictactoemultiplayer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.andrea.tictactoemultiplayer.views.BoardView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ROOM_ID = "room_id"
    }

    // database
    private val mDb = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        window.fullscreen()

        // TODO retrieve room id
//        val roomId = intent?.extras?.getString(EXTRA_ROOM_ID) ?: throw RuntimeException("Null room key")

        addBoard()
    }

    /**
     * Listen for changes in the room (users moves)
     */
    private fun listenForRoom() {

    }

    private fun addBoard() {
        val boardView = BoardView(this)
        boardView.setListener(object : BoardView.BoardListener {
            override fun onCellClick(cell: Button) {
                /*
                TODO
                - update database
                - swap turn
                - check for winner
                 */

                val info = cell.tag as BoardView.CellInfo

                // do nothing if cell already has a cross or a nought
                if (info.status != BoardView.CellInfo.CellStatus.FREE) {
                    return
                }


            }
        })

        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID

        boardView.layoutParams = params

        gameContainer.addView(boardView)
    }
}
