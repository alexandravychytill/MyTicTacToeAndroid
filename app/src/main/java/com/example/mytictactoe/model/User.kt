package com.example.mytictactoe.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.util.*
import android.provider.Settings

data class User( val deviceId: String? = null, val id: String? = null ){
    constructor(): this(null)
}

class UserViewModel(context: Context) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    private val contextT = context
    var currentUser: User? = null
        set(value) {
            field = value
            saveUser()
        }

    init {
        retrieveUser()
        if (currentUser == null) {
            currentUser = User(getDeviceId(),generateUserId())
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
                val user = Gson().fromJson(userJson, User::class.java)
                if (user.deviceId == getDeviceId()) {
                    user
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun getDeviceId(): String {
        return Settings.Secure.getString(contextT.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun generateUserId(): String {
        // Generate a unique ID for the user
        // Implementation is up to you.
        return "user-${System.currentTimeMillis()}"
    }
}


/*
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
*/
