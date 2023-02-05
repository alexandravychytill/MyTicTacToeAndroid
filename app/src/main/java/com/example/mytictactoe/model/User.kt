package com.example.mytictactoe.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.util.*

class User {
    var id: String = UUID.randomUUID().toString()
}

class UserViewModel(context: Context) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    var currentUser: User? = null
        set(value) {
            field = value
            saveUser()
        }

    init {
        retrieveUser()
        if (currentUser == null) {
            saveUser()
        }
    }

    private fun saveUser() {
        currentUser?.let {
            with(sharedPreferences.edit()) {
                putString("user", Gson().toJson(it))
                apply()
            }
        }
    }

    private fun retrieveUser() {
        val userJson = sharedPreferences.getString("user", "")
        if (userJson != null) {
            currentUser = if (userJson.isNotEmpty()) {
                Gson().fromJson(userJson, User::class.java)
            } else {
                null
            }
        }
    }
}

