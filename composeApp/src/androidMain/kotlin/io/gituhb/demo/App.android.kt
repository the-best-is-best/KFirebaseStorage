package io.gituhb.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.firebase_core.AndroidKFirebaseCore
import io.github.kdownloadfile.AndroidKDownloadFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        AndroidKDownloadFile.init(this)
        AndroidKFirebaseCore.initialization(this)

        setContent { App() }
    }
}
