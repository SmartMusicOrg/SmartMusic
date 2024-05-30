package com.example.smartmusicfirst.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.smartmusicfirst.data.Routs

data class NavigationItem(
    val route: String,
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val hasNew: Boolean,
    val badgeCount: Int? = null,
    @StringRes val iconTitle: Int? = null
)
