package com.example.andrea.tictactoemultiplayer.models

/**
 * Created by andrea on 18/12/2017.
 */
data class Room(val userHost: User,
                val userGuest: User,
                var id: String = "")
