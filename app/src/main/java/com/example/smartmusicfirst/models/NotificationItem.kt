package com.example.smartmusicfirst.models

data class NotificationItem(
    //   todo change to this:
    //    @StringRes val title: Int,
    val title: String,
    //  todo change to this:
    //    @StringRes val description: Int? = null
    val description: String = "",
    var isAlreadyRead: Boolean = true,
    val onClick: () -> Unit
)
