package com.example.mytictactoe

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mytictactoe.model.Game
import com.example.mytictactoe.model.Move
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FireBaseService {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val ref = db.collection("Game")
    private lateinit var game: Game
    val gameLiveData = MutableLiveData<Game>()

    fun getGame(): Game {
        return game
    }

    fun startGame(userId: String){
        ref.whereEqualTo("player2Id", "")
         //   .whereNotEqualTo("player1Id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val gameData = querySnapshot.documents.first()
                    game = gameData.toObject(Game::class.java)!!
                    game.player2Id = userId
                    game.blockMoveForPlayerId = userId
                    ref.document(gameData.id).set(game)
                        .addOnSuccessListener {
                            listenForChanges()
                        }
                        .addOnFailureListener { error ->
                            Log.d(TAG, "Error updating game: $error")
                        }
                } else {
                    game = createOnlineGame(userId)
                }
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Error starting game: $error")
                game = createOnlineGame(userId)
            }
    }

    fun createOnlineGame(userId: String): Game {
        db.collection("Game")
        game = Game(UUID.randomUUID().toString(), userId, "", userId, "", listOf(Move(0), Move(1), Move(2), Move(3), Move(4), Move(5), Move(6), Move(7), Move(8)))
        val ref = FirebaseFirestore.getInstance().collection("Game")
        ref.document(game.id).set(game)
        listenForChanges()
        return game
    }

    fun updateGame(){
        try {
            ref.document(game.id).set(game).addOnSuccessListener {
                Log.d("Firebase", "Game successfully updated")
            }.addOnFailureListener {
                Log.e("Firebase", "Error updating online game", it)
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating online game", e)
        }
    }
    fun listenForChanges(){
        val gameRef = ref.document(game.id)
        gameRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                gameLiveData.value = game

            } else {
                Log.d(TAG, "Current data: null")
                game = Game()
                gameLiveData.value = game
            }
        }
    }

    fun endTheGame(game: Game){
        ref.whereEqualTo("id", game.id)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error deleting online game: ${exception.localizedMessage}")
            }
    }
}