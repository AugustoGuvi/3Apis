package com.augusto.libris

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.augusto.libris.di.AppContainer
import com.augusto.libris.ui.view.LibraryApp
import com.augusto.libris.ui.theme.LibrisTheme

class LibraryApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibrisTheme {
                LibraryApp()
            }
        }
    }
}
