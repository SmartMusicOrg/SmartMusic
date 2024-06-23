package com.example.smartmusicfirst

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.ui.views.LogInScreen
import kotlinx.coroutines.launch

class LogInActivity : ComponentActivity(), SpotifyAuthConnectionListener {

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

    override fun onSpotifyAuthSuccess(accessToken: String) {
        lifecycleScope.launch {
            try {
                SpotifyWebApi.init(this@LogInActivity, accessToken)
                startActivity(Intent(this@LogInActivity, MainActivity::class.java))
                finish() // Close the login activity
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to connect to Spotify: ${e.message}", e)
                setContent {
                    LogInScreen(::onLoginButtonClick, e.message ?: "Unknown error")
                }
            }
        }
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