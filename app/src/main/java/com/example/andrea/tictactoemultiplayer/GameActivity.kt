package com.example.andrea.tictactoemultiplayer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import com.example.andrea.tictactoemultiplayer.models.User
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
            HOST -> GUEST
            GUEST -> HOST
        }
    }

    enum class UserType {
        HOST, GUEST
    }

    // board view
    private lateinit var mBoardView: BoardView

    // users types
    private lateinit var mUserType: UserType
    private lateinit var mOtherUserType: UserType

    // turn
    private var mTurn = Turn.HOST

    // database
    private val mDb = FirebaseDatabase.getInstance()

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
     * Listen for changes in the room (users moves)
     */
    private fun listenForRoom() {
        mRoomListener = mRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mTurn = Turn.valueOf(dataSnapshot.child("turn").getValue(String::class.java)!!)

                youTV.text = dataSnapshot.child(mUserType.name).getValue(User::class.java)!!.username
                otherPlayerTV.text = dataSnapshot.child(mOtherUserType.name).getValue(User::class.java)!!.username
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
        /*
        TODO
        - update database
        - swap turn
        - check for winner
         */

        val info = cell.getCellInfo()

        // do nothing if cell already has a cross or a nought
        if (info.status != CellView.CellInfo.CellStatus.FREE) {
            return
        }

//        mBoardView.setCellStatus(row, column, CellView.CellInfo.CellStatus.CROSS)

//        mTurn.swap()

//        saveDataToDatabase()

        if (canClickCell()) {
            Logger.d("Host clicked on $row, $column")
        }
    }

    private fun saveDataToDatabase() {

    }

    private fun canClickCell() = (mUserType == UserType.HOST && mTurn == Turn.HOST) || (mUserType == UserType.GUEST && mTurn == Turn.GUEST)
}
