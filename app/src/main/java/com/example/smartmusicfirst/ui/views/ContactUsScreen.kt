package com.example.smartmusicfirst.ui.views

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.ContactUsViewModel

@Composable
fun ContactUsScreen(
    modifier: Modifier = Modifier,
    contactUsViewModel: ContactUsViewModel = viewModel()
) {
    val uiState by contactUsViewModel.uiState.collectAsState()
    val ctx = LocalContext.current
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
        TextField(
            value = uiState.emailSubject,
            onValueChange = { contactUsViewModel.updateSubject(it) },
            placeholder = { Text(text = "Enter email subject") },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
        TextField(
            value = uiState.emailBody,
            onValueChange = { contactUsViewModel.updateBody(it) },
            placeholder = { Text(text = "Enter email body") },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth()
                .weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = false,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
        Button(onClick = {
            val i = Intent(Intent.ACTION_SEND)
            val emailAddress = arrayOf(uiState.receivingAddress)
            i.putExtra(Intent.EXTRA_EMAIL, emailAddress)
            i.putExtra(Intent.EXTRA_SUBJECT, uiState.emailSubject)
            i.putExtra(Intent.EXTRA_TEXT, uiState.emailBody)
            i.setType("message/rfc822")
            ctx.startActivity(Intent.createChooser(i, "Choose an Email client : "))

        }) {
            Text(
                text = "Send Email",
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@Preview
@Composable
fun ContactUsScreenPreview() {
    SmartMusicFirstTheme {
        ContactUsScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ContactUsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        ContactUsScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}
