package com.example.andrea.tictactoemultiplayer

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.example.andrea.tictactoemultiplayer.models.Room
import com.example.andrea.tictactoemultiplayer.models.User
import com.example.andrea.tictactoemultiplayer.utils.Logger
import com.example.andrea.tictactoemultiplayer.views.UsernameDialog
import com.example.andrea.tictactoemultiplayer.views.WaitingUsersAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        UsernameDialog.UsernameDialogListener,
        WaitingUsersAdapter.WaitingUsersListener {

    companion object {
        private val SKIP_TO_GAME = true
    }

    // username dialog
    private lateinit var mUsernameDialog: UsernameDialog

    // database
    private val mDb = FirebaseDatabase.getInstance()

    // all online users
    private val mOnlineRef = mDb.getReference("online")
    private val mOnlineUsers = mutableListOf<User>()
    private lateinit var mOnlineUsersListener: ValueEventListener

    // waiting users (those who are not playing)
    private val mWaitingRef = mDb.getReference("waiting")
    private val mWaitingUsers = mutableListOf<User>()
    private lateinit var mWaitingUsersListener: ValueEventListener

    // rooms
    private val mRoomsRef = mDb.getReference("rooms")

    // current user
    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.fullscreen()

        if (SKIP_TO_GAME) {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }

        showUsernameDialog()

        listenForOnlineUsers()
        listenForWaitingUsers()
    }

    /**
     * Show the dialog that requests the user to input the username
     */
    private fun showUsernameDialog() {
        mUsernameDialog = UsernameDialog()
        mUsernameDialog.show(supportFragmentManager, "username_dialog")
    }

    /**
     * Exit clicked
     */
    override fun onExitClick() {
        finish()
    }

    /**
     * Play clicked
     */
    override fun onPlayClick(username: String, dialog: DialogFragment) {
        // make sure it's alphanumeric
        if (!username.matches(Regex("^[a-zA-Z0-9]+\$"))) {
            mUsernameDialog.showError(UsernameDialog.Error.USERNAME_NOT_VALID)
            return
        }

        // make sure it's not already used
        val usernameTaken = mOnlineUsers.any { it.username == username }
        if (usernameTaken) {
            mUsernameDialog.showError(UsernameDialog.Error.USERNAME_TAKEN)
            return
        }

        mUser = User(username)
        mUser!!.id = mOnlineRef.push().key
        // add to online
        mOnlineRef.child(mUser!!.id).setValue(mUser)
        // add to waiting
        mWaitingRef.child(mUser!!.id).setValue(mUser)

        dialog.dismiss()
    }

    /**
     * Set listener for online users
     */
    private fun listenForOnlineUsers() {
        mOnlineUsersListener = mOnlineRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mOnlineUsers.clear()

                dataSnapshot.children.mapNotNullTo(mOnlineUsers) {
                    it.getValue(User::class.java)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Logger.e("listenForOnlineUsers onCancelled: ${databaseError.toException()}")
            }
        })
    }

    /**
     * Set listener for waiting users
     */
    private fun listenForWaitingUsers() {
        mWaitingUsersListener = mWaitingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mWaitingUsers.clear()

                dataSnapshot.children.mapNotNullTo(mWaitingUsers) {
                    it.getValue(User::class.java)
                }
                mWaitingUsers.remove(mUser)

                setupWaitingUsersList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Logger.e("listenForWaitingUsers onCancelled: ${databaseError.toException()}")
            }
        })
    }

    /**
     * Setup the list of the waiting users
     */
    private fun setupWaitingUsersList() {
        // no users available to play
        if (mWaitingUsers.isEmpty()) {
            noUsersOnlineTV.visibility = View.VISIBLE
            return
        }

        // show waiting users
        val layoutManager = GridLayoutManager(this, 3)
        usersRV.layoutManager = layoutManager

        val adapter = WaitingUsersAdapter(mWaitingUsers, this)
        usersRV.adapter = adapter
    }

    /**
     * Click on a waiting user
     */
    override fun onWaitingUserClick(user: User) {
        // add users to the same room
        val room = Room(mUser!!, user)
        room.id = mRoomsRef.push().key
        mRoomsRef.child(room.id).setValue(room)

        // remove users from waiting (since they are now playing)
        mWaitingRef.child(mUser!!.id).removeValue()
        mWaitingRef.child(user.id).removeValue()

        // TODO start GameActivity passing the room key
//        val i = Intent(this, GameActivity::class.java)
//        i.putExtra(GameActivity.EXTRA_ROOM_ID, room.id)
//        startActivity(i)
    }

    override fun onDestroy() {
        super.onDestroy()

        // remove listener for online users
        mOnlineRef.removeEventListener(mOnlineUsersListener)

        // remove listener for waiting users
        mWaitingRef.removeEventListener(mWaitingUsersListener)

        // TODO delete user form database
        if (mUser != null) {
            mOnlineRef.child(mUser!!.id).removeValue()
            mWaitingRef.child(mUser!!.id).removeValue()
        }
    }
}
