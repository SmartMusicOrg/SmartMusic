package com.example.smartmusicfirst.models

data class SettingsOnOffItem(
//   todo change to this:
//    @StringRes val title: Int,
    val title: String,
    var isChecked: Boolean,
//   todo change to this:
//    @StringRes val description: Int? = null
    val description: String = ""
)
