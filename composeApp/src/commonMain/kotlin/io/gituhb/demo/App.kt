package io.gituhb.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.firebase_storage.KFirebaseStorage
import io.github.kdownloadfile.openFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import io.gituhb.demo.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun App() = AppTheme {
    val firebaseStorage = KFirebaseStorage()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedButton(
            onClick = {
                scope.launch {
                    val image = FileKit.openFilePicker(type = FileKitType.Image)
                    if (image != null) {
                        val res = firebaseStorage.uploadFile(
                            "test/${image.name}",
                            image.readBytes(),
                        )
                        res.fold(
                            onSuccess = {
                                println("Upload successful: $it")
                            },
                            onFailure = {
                                println("Error: ${it.message}")
                            }
                        )
                    }
                }
            }
        ) {
            Text("Upload Image")
        }
        ElevatedButton(
            onClick = {
                scope.launch {
                    val res = firebaseStorage.getFileBytes("test/IMG_0002.jpeg")

                    res.fold(
                        onSuccess = {
                            val file = PlatformFile(FileKit.filesDir, "IMG_0002.jpeg")

                            try {
                                file.write(it)
                                openFile(file.path)
                            } catch (e: Exception) {
                                println("Error opening file: ${e.message}")
                            }
                        },
                        onFailure = {
                            println("Error: ${it.message}")
                        }
                    )

                }
            }
        ) {
            Text("Download Image by bytes")
        }


        ElevatedButton(
            onClick = {
                scope.launch {
                    firebaseStorage.deleteFile(
                        "test/IMG_0002.jpeg",
                    )
                }
            }
        ) {
            Text("Delete Image")
        }


    }
}
