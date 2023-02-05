package com.example.mytictactoe

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.mytictactoe.model.Move
import com.example.mytictactoe.model.User
import com.example.mytictactoe.model.UserViewModel
import kotlin.collections.ArrayList

class TicTacToe : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var fireBaseService: FireBaseService
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        supportActionBar?.hide()
        var quitButton = findViewById<Button>(R.id.end_button)
        fireBaseService = FireBaseService()
        userViewModel = UserViewModel(this)
        if (userViewModel.currentUser == null) {
            userViewModel.currentUser = User()
        }

        // start the game
        fireBaseService.startGame(userViewModel.currentUser!!.id)

        // quit game button
        quitButton.setOnClickListener {
            fireBaseService.endTheGame(fireBaseService.getGame())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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

    fun checkForGameBoardStatus(): Boolean{
        return fireBaseService.getGame().blockMoveForPlayerId != currentUser.id
    }


    fun processPlayerMove(position: Int, isPlayer1: Boolean , indicator: String){

       // println(game.toString())
      //  println(isSquareOccupied(game.moves, position))

      //  if (isSquareOccupied(game.moves, position)) {
      //      return
       // }

        fireBaseService.getGame().moves[position].isPlayer1 = isPlayer1
        fireBaseService.getGame().moves[position].boardIndex = position
        fireBaseService.getGame().moves[position].indicator = indicator

        fireBaseService.updateGame()

        println(fireBaseService.getGame().toString())
       // fireBaseService.getGame().blockMoveForPlayerId = "player2"

       /* val win = checkForWinCondition(true, game.moves)
        if (win) {
            alertItem = null
            alertItem = AlertContent.WINNER
            Log.d("Game", "You have won!")
            return
        }

        val draw = checkForDraw(game.moves)
        if (draw) {
            alertItem = null
            alertItem = AlertContent.NO_WINNER
            return
        }
        */
    }

    fun isSquareOccupied(moves: List<Move?>, index: Int): Boolean {
        return moves.any { it?.boardIndex == index }
    }

    fun updateButtonView(){
        val buttonList = listOf<Button>(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9)
        )

        for (i in 0 until buttonList.size) {
            if (fireBaseService.getGame().moves[i].id == i) {
                buttonList[i].isEnabled = false
            }
        }

    }

    fun buClick(view: View) {
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

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    var isPlayer1 = true

    private fun playGame(cellID: Int, buSelected: Button) {
        if (isPlayer1) {
            buSelected.text = "✕"
            buSelected.setTextColor((Color.parseColor("#FFFFFF")))
            processPlayerMove(cellID, isPlayer1, "✕")
            isPlayer1 = false
        }
        else {
            buSelected.text = "◯"
            buSelected.setTextColor(Color.parseColor("#FFFFFF"))
            processPlayerMove(cellID, isPlayer1, "◯")
            isPlayer1 = true
        }
        buSelected.isEnabled = false

      //  WhoIsTheWinner()
    }

    private fun WhoIsTheWinner(): Int {
        var winner = -1

        //column1
        //row1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winner = 1
        }

        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winner = 2
        }

        //row2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winner = 1
        }

        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winner = 2
        }

        //row3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winner = 1
        }

        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winner = 2
        }

        //column2
        //1,5,9
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winner = 1
        }

        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winner = 2
        }

        //3,6,9
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winner = 1
        }

        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winner = 2
        }

        //1,4,7
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winner = 1
        }

        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winner = 2
        }

        //1,5,9
        if (player1.contains(1) && player1.contains(5) && player1.contains(9)) {
            winner = 1
        }

        if (player2.contains(1) && player2.contains(5) && player2.contains(9)) {
            winner = 2

        }

        //3,5,7
        if (player1.contains(3) && player1.contains(5) && player1.contains(7)) {
            winner = 1
        }

        if (player2.contains(3) && player2.contains(5) && player2.contains(7)) {
            winner = 2
        }

        val builder = AlertDialog.Builder(this@TicTacToe)
        builder.setTitle("Winner")
        if (winner != -1) {
            if (winner == 1) {
                //Toast.makeText(this, "Player 1 won the game.", Toast.LENGTH_SHORT).show()
                builder.setTitle("You won!")
                builder.setMessage("The Game is over")
                builder.setPositiveButton("Rematch"){dialog, which -> finish()}
                builder.setNegativeButton("Quit"){dialog, which -> finish()}
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else
            {
                builder.setMessage("Other player won the game.")
                builder.setPositiveButton("Rematch"){dialog, which -> finish()}
                builder.setNegativeButton("Quit"){dialog, which -> finish()}
                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
            return winner
        }
        return 0
    }
}