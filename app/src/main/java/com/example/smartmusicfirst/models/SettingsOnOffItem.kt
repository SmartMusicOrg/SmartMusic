package com.example.smartmusicfirst.models

import androidx.annotation.StringRes

data class SettingsOnOffItem(
    @StringRes val title: Int,
    var isChecked: Boolean,
    @StringRes val description: Int? = null,
)
