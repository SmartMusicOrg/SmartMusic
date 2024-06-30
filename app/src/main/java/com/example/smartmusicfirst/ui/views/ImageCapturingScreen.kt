package com.example.smartmusicfirst.ui.views

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.ai.ChatGptApi
import com.example.smartmusicfirst.ui.components.LoadingPage
import com.example.smartmusicfirst.ui.theme.SmartMusicFirstTheme
import com.example.smartmusicfirst.viewModels.ImageCapturingViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

@Composable
fun ImageCapturingScreen(
    modifier: Modifier = Modifier,
    title: String = "Capture Image",
    message: String = "Tap a button below to capture an image or select from gallery.",
    imageCapturingViewModel: ImageCapturingViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = imageCapturingViewModel.uiState.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            uiState.value.imageUri?.let {
                imageCapturingViewModel.loadImage(it)
                imageCapturingViewModel.showToast("Image captured successfully!")
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageCapturingViewModel.setImageUri(it)
            imageCapturingViewModel.loadImage(it)
            imageCapturingViewModel.showToast("Image selected from gallery!")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storagePermissionGranted =
            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

        if (cameraPermissionGranted && storagePermissionGranted) {
            val uri = createImageFile(context)
            imageCapturingViewModel.setImageUri(uri)
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun ensurePermissionsAndCaptureImage() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            val uri = createImageFile(context)
            imageCapturingViewModel.setImageUri(uri)
            cameraLauncher.launch(uri)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                    vertical = dimensionResource(id = R.dimen.padding_extra_large)
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )



            uiState.value.imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add_a_photo), // Use your placeholder vector drawable here
                        contentDescription = "Placeholder",
                        modifier = Modifier.size(128.dp)
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_medium))
                )
            }

            Button(
                onClick = { ensurePermissionsAndCaptureImage() },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.photo_camera), // Use your camera vector drawable here
                    contentDescription = "Capture Image",
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
                Text(
                    text = "Capture",
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gallery), // Use your gallery vector drawable here
                    contentDescription = "Select from Gallery",
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
                Text(
                    text = "Gallery",
                    color = Color.White,
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small))
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))

            Button(
                enabled = uiState.value.canUseSubmit,
                onClick = {
                    val properties =
                        Properties().apply { load(context.resources.openRawResource(R.raw.gemini)) }
                    imageCapturingViewModel.searchSong(
                        aiApiKey = properties.getProperty("chat_gpt_access_token") ?: "",
                        aiModel = ChatGptApi
                    )
                }, modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search), // Use your search vector drawable here
                    contentDescription = "Search",
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
                Text(text = stringResource(id = R.string.search), color = Color.White)
            }
        }
        if(uiState.value.isLoading) {
            LoadingPage(hint = uiState.value.userHint)
        }
    }
}

fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
        ?: throw IOException("Failed to create new MediaStore record.")
}

@Preview
@Composable
fun ImageCapturingScreenPreview() {
    SmartMusicFirstTheme {
        ImageCapturingScreen()
    }
}

@Preview
@Composable
fun ImageCapturingScreenDarkPreview() {
    SmartMusicFirstTheme(darkTheme = true) {
        ImageCapturingScreen()
    }
}
