package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.models.NotificationItem
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme

@Composable
fun NotificationsScreen(
    notificationsItemsList: List<NotificationItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        items(notificationsItemsList.size) { index ->
            NotificationItemView(notificationsItemsList[index])
            HorizontalDivider()
        }
    }
}

@Composable
fun NotificationItemView(notificationItem: NotificationItem) {
    ListItem(
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.notifications_icon),
                contentDescription = null
            )
        },
        trailingContent = {
            //todo change to enable when feature completes:
            IconButton(
                enabled = false,
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.more_vert),
                        contentDescription = "click for more options"
                    )
                },
                onClick = { /*TODO*/ })
        },
        headlineContent = { Text(text = notificationItem.title) },
        supportingContent = { Text(text = notificationItem.description) },
        tonalElevation = if (notificationItem.isAlreadyRead) 0.dp else dimensionResource(id = R.dimen.elevation_large),
        modifier = Modifier.clickable { notificationItem.onClick }
    )
}

@Preview
@Composable
fun NotificationsScreenPreview() {
    SmartMusicFirstTheme {
        NotificationsScreen(
            notificationsItemsList = listOf(
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { },
                    isAlreadyRead = false
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { },
                    isAlreadyRead = false
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun NotificationsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        NotificationsScreen(
            notificationsItemsList = listOf(
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { },
                    isAlreadyRead = false
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { },
                    isAlreadyRead = false
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                ),
                NotificationItem(
                    title = "Notification Title",
                    description = "Notification Description",
                    onClick = { }
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
