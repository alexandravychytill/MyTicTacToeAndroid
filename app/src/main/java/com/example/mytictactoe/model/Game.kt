package com.example.mytictactoe.model

data class Game(
    val id: String,
    var player1Id: String,
    var player2Id: String,
    var blockMoveForPlayerId: String,
    var winnningPlayerId: String,
    var isGameActiv: Boolean,
    var moves: List<Move>
){
    constructor(): this("", "", "", "", "", false, emptyList())
}

data class Move(
    val id: Int? = null,
    var isPlayer1: Boolean? = null,
    var boardIndex: Int? = null,
    var indicator: String? = null
) {
    constructor(): this(null, null, null, null)
}