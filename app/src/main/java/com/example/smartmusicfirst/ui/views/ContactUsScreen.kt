package com.example.smartmusicfirst.ui.views

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
import androidx.compose.ui.res.stringResource
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
            placeholder = { Text(text = stringResource(id = R.string.enter_email_subject)) },
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
            placeholder = { Text(text = stringResource(id = R.string.enter_email_body)) },
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth()
                .weight(1f),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = false,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
        Button(onClick = { contactUsViewModel.sendButtonClicked(ctx) }){
            Text(
                text = stringResource(id = R.string.send_email),
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
