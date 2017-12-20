package com.example.andrea.tictactoemultiplayer.models

import com.example.andrea.tictactoemultiplayer.GameActivity

/**
 * Created by andrea on 18/12/2017.
 */
data class Room(val userHost: User,
                val userGuest: User,
                val cells: List<List<Cell>>,
                val turn: GameActivity.Turn = GameActivity.Turn.HOST,
                val winner: Winner = Winner())
