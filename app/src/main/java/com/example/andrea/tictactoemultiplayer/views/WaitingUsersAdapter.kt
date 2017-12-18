package com.example.andrea.tictactoemultiplayer.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.andrea.tictactoemultiplayer.R
import com.example.andrea.tictactoemultiplayer.models.User
import kotlinx.android.synthetic.main.item_waiting_user.view.*

/**
 * Created by andrea on 18/12/2017.
 */
class WaitingUsersAdapter(private val waitingUsers: List<User>,
                          private val listener: WaitingUsersListener) : RecyclerView.Adapter<WaitingUsersAdapter.ViewHolder>() {

    interface WaitingUsersListener {
        fun onWaitingUserClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_waiting_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = waitingUsers[position]
        holder.waitingUser.text = user.username
        holder.itemView.setOnClickListener {
            listener.onWaitingUserClick(user)
        }
    }

    override fun getItemCount() = waitingUsers.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val waitingUser: TextView = view.waitingUserTV
    }

}