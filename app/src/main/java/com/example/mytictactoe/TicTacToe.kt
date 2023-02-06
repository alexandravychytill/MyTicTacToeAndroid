package com.example.mytictactoe

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.mytictactoe.model.Game
import com.example.mytictactoe.model.Move
import com.example.mytictactoe.model.User
import com.example.mytictactoe.model.UserViewModel

class TicTacToe : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    lateinit var fireBaseService: FireBaseService
    private lateinit var currentUser: User
    private var hasGameStarted: Boolean = false
    private var onCreateCalled = false

    private val winPatterns: Set<Set<Int>> = setOf(
        setOf(0, 1, 2), setOf(3, 4, 5), setOf(6, 7, 8),
        setOf(0, 3, 6), setOf(1, 4, 7), setOf(2, 5, 8),
        setOf(0, 4, 8), setOf(2, 4, 6)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        supportActionBar?.hide()
        var quitButton = findViewById<Button>(R.id.end_button)
        fireBaseService = FireBaseService()
        onCreateCalled = true

        val buttonList = listOf<Button>(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9)
        )

        userViewModel = UserViewModel(this)
        currentUser = userViewModel.currentUser!!

        // start the game
        currentUser.id?.let { fireBaseService.startGame(it) }

        quitButton.setOnClickListener {
            fireBaseService.endTheGame(fireBaseService.getGame())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        fireBaseService.gameLiveData.observe(this, Observer { game ->
            println("FUnktionnierttt!!!!")
            findViewById<Button>(R.id.button1).text = game.moves[0].indicator
            quitButton.text = game.player2Id
            updateButtonView(game, buttonList)
        })
    }

    fun checkIfGameIsOver() {
        if (fireBaseService.getGame() != null) {
            if (fireBaseService.getGame().winnningPlayerId == "0") {
                //  AlertContext.DRAW
            } else if (fireBaseService.getGame().winnningPlayerId != "") {
                if (fireBaseService.getGame().winnningPlayerId == currentUser.id) {
                    // alertItem = AlertContext.YOU_WIN
                } else {
                    // alertItem = AlertContext.YOU_LOSE
                }
            }
        }
    }

    fun resetGame() {
        fireBaseService.getGame().moves =
            listOf(Move(0), Move(1), Move(2), Move(3), Move(4), Move(5), Move(6), Move(7), Move(8))
        fireBaseService.getGame().blockMoveForPlayerId = "player2"
    }

    fun checkForGameBoardStatus(): Boolean {
        return fireBaseService.getGame().blockMoveForPlayerId != currentUser.id
    }

    fun processPlayerMove(position: Int, isPlayer1: Boolean, indicator: String): Boolean {
        val builder = AlertDialog.Builder(this@TicTacToe)

        if (isButtonOccupied(fireBaseService.getGame().moves, position)) {
            return false
        }

        // update game with new move changes
        fireBaseService.getGame().isGameActiv = true
        fireBaseService.getGame().moves[position].isPlayer1 = isPlayer1
        fireBaseService.getGame().moves[position].boardIndex = position
        fireBaseService.getGame().moves[position].indicator = indicator
        fireBaseService.getGame().blockMoveForPlayerId = currentUser.id.toString()
        fireBaseService.updateGame()

        if (checkWinCondition(isPlayerOne(), fireBaseService.getGame().moves)) {
            fireBaseService.getGame().winnningPlayerId = currentUser.id.toString()
            fireBaseService.updateGame()
            // Alert
            builder.setTitle("You won!")
            builder.setMessage("The Game is over")
            builder.setPositiveButton("Rematch") { dialog, which -> resetGame() }
            builder.setNegativeButton("Quit") { dialog, which ->
                fireBaseService.endTheGame(
                    fireBaseService.getGame()
                )
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
/*
         if(checkForDraw(fireBaseService.getGame().moves)){
            fireBaseService.getGame().winnningPlayerId = "0"
            fireBaseService.updateGame()
            // Alert
            builder.setTitle("Draw")
            builder.setMessage("The Game is over")
            builder.setPositiveButton("Rematch"){dialog, which -> resetGame()}
            builder.setNegativeButton("Quit"){dialog, which -> fireBaseService.endTheGame(fireBaseService.getGame())}
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        */
        return true
    }

    fun isPlayerOne(): Boolean {
        return fireBaseService.getGame().player1Id == currentUser.id ?: false
    }

    fun isButtonOccupied(moves: List<Move?>, index: Int): Boolean {
        return moves.any { it?.boardIndex == index }
    }

    fun updateButtonView(game: Game, buttonList: List<Button>){
            for (move in fireBaseService.getGame().moves) {
                for (button in buttonList) {
                    if (button.id == move.id) {
                        if (move.id != null) {
                            button.text = move.indicator
                            break
                        }
                    }
                }
            }
    }

    fun buttonClicked(view: View) {
        val buSelected = view as Button
        var cellID = 0
        when (buSelected.id) {
            R.id.button1 -> cellID = 0
            R.id.button2 -> cellID = 1
            R.id.button3 -> cellID = 2
            R.id.button4 -> cellID = 3
            R.id.button5 -> cellID = 4
            R.id.button6 -> cellID = 5
            R.id.button7 -> cellID = 6
            R.id.button8 -> cellID = 7
            R.id.button9 -> cellID = 8
        }
        playGame(cellID, buSelected)

    }

    private fun playGame(cellID: Int, buSelected: Button) {
        if(checkForGameBoardStatus()) {
            if (processPlayerMove(cellID, fireBaseService.getGame().player1Id == currentUser.id, "✕")) {
                if (fireBaseService.getGame().player1Id == currentUser.id) {
                    buSelected.text = "✕"
                    buSelected.setTextColor((Color.parseColor("#FFFFFF")))
                } else {
                    buSelected.text = "◯"
                    buSelected.setTextColor(Color.parseColor("#FFFFFF"))
                }
                buSelected.isEnabled = false

            }
        }
    }

    fun checkWinCondition(player: Boolean, moves: List<Move?>): Boolean {
        // remove all nils from the array and filter moves of the player only
        val playerMoves = moves.filterNotNull().filter { it.isPlayer1 == player }
        val playerPositions = playerMoves.map { it.boardIndex }.toSet()

        // go through winPatterns [0,1,2] and check if my playerPositions match with winPattern we have a win
        for (pattern in winPatterns) {
            if (playerPositions.containsAll(pattern)){
                return true
            }
        }
        return false
    }

    fun checkForDraw(moves: List<Move?>): Boolean {
        return moves.filterNotNull().count() == 9
    }
}

