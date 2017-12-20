package com.example.andrea.tictactoemultiplayer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.andrea.tictactoemultiplayer.models.Cell
import com.example.andrea.tictactoemultiplayer.models.User
import com.example.andrea.tictactoemultiplayer.models.Winner
import com.example.andrea.tictactoemultiplayer.utils.Logger
import com.example.andrea.tictactoemultiplayer.views.BoardView
import com.example.andrea.tictactoemultiplayer.views.CellView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity(), BoardView.BoardListener {

    companion object {
        val EXTRA_ROOM_ID = "room_id"
        val EXTRA_USER_TYPE = "user_type"
    }

    enum class Turn {
        HOST, GUEST;

        fun swap() = when (this) {
            Turn.HOST -> Turn.GUEST
            Turn.GUEST -> Turn.HOST
        }
    }

    enum class UserType {
        /**
         * Host is crosses
         */
        HOST,
        /**
         * Guest is noughts
         */
        GUEST
    }

    // board view
    private lateinit var mBoardView: BoardView

    // users types
    private lateinit var mUserType: UserType
    private lateinit var mOtherUserType: UserType
    private var mOtherUsername = ""

    // turn
    private var mTurn = Turn.HOST

    // game ended
    private var mWinner = Winner()

    // database
    private val mDb = FirebaseDatabase.getInstance()

    // waiting users
    private val mWaitingRef = mDb.getReference("waiting")

    // room where host and guest are playing
    private lateinit var mRoomRef: DatabaseReference
    private lateinit var mRoomListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        window.fullscreen()

        // retrieve room id
        val roomId = intent?.extras?.getString(EXTRA_ROOM_ID) ?: throw RuntimeException("Null room key")
        mRoomRef = mDb.getReference("rooms").child(roomId)

        // retrieve user type (host or guest)
        mUserType = intent.extras.get(EXTRA_USER_TYPE) as UserType? ?: throw RuntimeException("Null user type")
        mOtherUserType = when (mUserType) {
            UserType.HOST -> UserType.GUEST
            UserType.GUEST -> UserType.HOST
        }

        listenForRoom()
        addBoard()
    }

    /**
     * Returns the Firebase node for the user type
     */
    fun getUserFirebaseNode(userType: UserType) = when (userType) {
        UserType.HOST -> "userHost"
        UserType.GUEST -> "userGuest"
    }

    /**
     * Listen for changes in the room (users moves)
     */
    private fun listenForRoom() {
        mRoomListener = mRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    mOtherUsername = dataSnapshot.child(getUserFirebaseNode(mOtherUserType)).getValue(User::class.java)!!.username

                    // cells
                    val board: MutableList<MutableList<Cell>> = mutableListOf()
                    val rows = dataSnapshot.child("cells").value as ArrayList<*>
                    rows.forEach {
                        val boardRow = mutableListOf<Cell>()
                        val row = it as ArrayList<*>
                        row.forEach {
                            val cell = it as HashMap<*, *>
                            boardRow.add(Cell(
                                    (cell["row"].toString()).toInt(),
                                    (cell["column"].toString()).toInt(),
                                    CellView.CellInfo.CellStatus.valueOf(cell["status"].toString())
                            ))
                        }
                        board.add(boardRow)
                    }

                    mBoardView.updateBoard(board)

                    // turn
                    mTurn = Turn.valueOf(dataSnapshot.child("turn").getValue(String::class.java)!!)
                    if (hasTurn(mUserType)) {
                        infoTV.text = getString(R.string.your_turn)
                    } else if (hasTurn(mOtherUserType)) {
                        infoTV.text = getString(R.string.other_player_turn, mOtherUsername)
                    }

                    // winner
                    mWinner = dataSnapshot.child("winner").getValue(Winner::class.java)!!
                    if (mWinner.who != Winner.Who.NONE) {
                        if (hasTurn(mUserType)) {
                            infoTV.text = getString(R.string.you_win)
                            mBoardView.colorCells(mWinner.cells, resources.getDrawable(R.drawable.cell_win_background))
                        } else if (hasTurn(mOtherUserType)) {
                            infoTV.text = getString(R.string.other_player_win, mOtherUsername)
                            mBoardView.colorCells(mWinner.cells, resources.getDrawable(R.drawable.cell_win_background))
                        }

                        val bgResId = if (hasWon()) R.drawable.cell_win_background else R.drawable.cell_lose_background
                        mBoardView.colorCells(mWinner.cells, resources.getDrawable(bgResId))
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@GameActivity, getString(R.string.player_quit, mOtherUsername), Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Logger.e("listenForRoom onCancelled: ${databaseError.toException()}")
            }
        })
    }

    private fun addBoard() {
        mBoardView = BoardView(this)
        mBoardView.setListener(this)

        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT)
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID

        mBoardView.layoutParams = params

        gameContainer.addView(mBoardView)
    }

    override fun onCellClick(row: Int, column: Int, cell: CellView) {
        val info = cell.getCellInfo()

        // do nothing if cell already has a cross or a nought
        if (info.status != CellView.CellInfo.CellStatus.FREE) {
            return
        }
        // do nothing if it's not user's turn
        if (!hasTurn(mUserType)) {
            return
        }
        // do nothing if someone already won
        if (mWinner.who != Winner.Who.NONE) {
            return
        }

        // select the cell status based on the turn
        val currentStatus = when (mTurn) {
            Turn.HOST -> CellView.CellInfo.CellStatus.CROSS
            Turn.GUEST -> CellView.CellInfo.CellStatus.NOUGHT
        }

        // update the just clicked cell
        mBoardView.updateCell(info.row, info.column, currentStatus)

        // check for winner
        val winnerCells = mBoardView.checkForWinner(currentStatus)
        if (winnerCells.isNotEmpty()) {
            // get the winner cells
            mWinner.cells = winnerCells
            mWinner.who = when (currentStatus) {
                CellView.CellInfo.CellStatus.CROSS -> Winner.Who.HOST
                CellView.CellInfo.CellStatus.NOUGHT -> Winner.Who.GUEST
                else -> Winner.Who.NONE
            }
            // set background to winner cells
            var bgColor = resources.getDrawable(R.drawable.cell_background)
            if (hasTurn(mUserType)) {
                bgColor = resources.getDrawable(R.drawable.cell_win_background)
            } else if (hasTurn(mOtherUserType)) {
                bgColor = resources.getDrawable(R.drawable.cell_lose_background)
            }
            mBoardView.colorCells(winnerCells, bgColor)
        } else {
            mWinner.who = Winner.Who.NONE
        }

        // only swap the turn if nobody won
        if (mWinner.who == Winner.Who.NONE) {
            mTurn = mTurn.swap()
        }

        saveDataToDatabase(Cell(info.row, info.column, currentStatus))
    }

    private fun saveDataToDatabase(updatedCell: Cell) {
        /// turn
        mRoomRef.child("turn").setValue(mTurn)

        // clicked cell
        mRoomRef
                .child("cells")
                .child(updatedCell.row.toString())
                .child(updatedCell.column.toString())
                .updateChildren(mapOf("status" to updatedCell.status))

        // winner
        mRoomRef.child("winner").setValue(mWinner)
    }

    /**
     * Returns whether the user can click or not
     */
    private fun hasTurn(userType: UserType) =
            (userType == UserType.HOST && mTurn == Turn.HOST) || (userType == UserType.GUEST && mTurn == Turn.GUEST)

    private fun hasWon() =
            (mWinner.who == Winner.Who.HOST && mUserType == UserType.HOST) ||
                    (mWinner.who == Winner.Who.GUEST && mUserType == UserType.GUEST)

    override fun onDestroy() {
        super.onDestroy()

        // remove room
        mRoomRef.removeEventListener(mRoomListener)
        mRoomRef.removeValue()

        // add back to waiting
        mWaitingRef.child(CurrentUser.user!!.id).setValue(CurrentUser.user)
    }
}
