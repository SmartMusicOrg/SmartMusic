
package com.example.smartmusicfirst

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.ui.views.LogInScreen

class LogInActivity : ComponentActivity(), SpotifyConnectionListener, SpotifyAuthConnectionListener {

    private val spotifyAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            SpotifyAuthConnection.onActivityResult(result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogInScreen(::onLoginButtonClick)
        }
    }

    private fun onLoginButtonClick() {
        SpotifyAuthConnection.initAuthConnection(this, this, spotifyAuthLauncher)
    }

    override fun onSpotifyConnected() {
    }

    override fun onSpotifyAuthSuccess(accessToken: String) {
        com.example.smartmusicfirst.accessToken = accessToken
        SpotifyConnection.connect(this, this)
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Close the login activity
    }

    override fun onSpotifyAuthFailure(error: Throwable) {
        Log.e(TAG, "Spotify authentication failed: ${error.message}", error)
        setContent {
            LogInScreen(::onLoginButtonClick, error.message ?: "Unknown error")
        }
    }

    companion object {
        private const val TAG = "LogInActivity"
    }
}

@Preview
@Composable
fun LogInScreenPreview() {
    LogInScreen({})
}