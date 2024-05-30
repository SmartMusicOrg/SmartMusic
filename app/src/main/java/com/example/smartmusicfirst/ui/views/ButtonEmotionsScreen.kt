package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.ButtonEmotionViewModel


@Composable
fun SimpleButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    buttonEmotionViewModel: ButtonEmotionViewModel = viewModel()
) {
    OutlinedButton(
        onClick = { buttonEmotionViewModel.onEmotionButtonClicked(text) },
        border = ButtonDefaults.outlinedButtonBorder.copy(width = dimensionResource(id = R.dimen.border_width_medium)),
        elevation = ButtonDefaults.elevatedButtonElevation(dimensionResource(id = R.dimen.elevation_large)),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier.width(300.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_extra_small))
        )
    }
    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
}

@Composable
fun MySimpleAppContainer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SimpleButton("Happy", color = Color(0xCF6D997C))
        SimpleButton("Excited", color = Color(0xCFCCB874))
        SimpleButton("Angry", color = Color(0xCFCC7474))
        SimpleButton("Optimistic", color = Color(0xCF748DCC))
        SimpleButton("Sad", color = Color(0xCF969696))
        SimpleButton("Energized", color = Color(0xCF74C6CC))
    }
}

@Preview
@Composable
fun ButtonEmotionsScreenPreview() {
    SmartMusicFirstTheme {
        MySimpleAppContainer(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun ButtonEmotionsScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        MySimpleAppContainer(modifier = Modifier.fillMaxSize())
    }
}
