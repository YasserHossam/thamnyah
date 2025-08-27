package com.thmanyah.task

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import kotlinx.coroutines.runBlocking
import java.util.Locale
import kotlinx.coroutines.flow.first
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.thmanyah.task.data.local.LanguagePreferences
import com.thmanyah.task.data.local.LocaleManager
import com.thmanyah.task.domain.model.Language
import com.thmanyah.task.presentation.navigation.ThamanyaNavigation
import com.thmanyah.task.presentation.ui.theme.ThamanyaTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var languagePreferences: LanguagePreferences
    
    @Inject
    lateinit var localeManager: LocaleManager
    
    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val languagePrefs = LanguagePreferences(newBase)
            val language = runBlocking { 
                languagePrefs.selectedLanguage.first() 
            }
            val localeManager = LocaleManager()
            val updatedContext = localeManager.setLocale(newBase, language)
            super.attachBaseContext(updatedContext)
        } else {
            super.attachBaseContext(newBase)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Switch to main theme after a brief delay
        setTheme(R.style.Theme_Thamanya)
        
        enableEdgeToEdge()
        
        setContent {
            ThamanyaTheme {
                val selectedLanguage by languagePreferences.selectedLanguage.collectAsState(initial = Language.ENGLISH)
                val layoutDirection = if (selectedLanguage == Language.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr
                val coroutineScope = rememberCoroutineScope()
                
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ThamanyaNavigation(
                            currentLanguage = selectedLanguage,
                            onLanguageToggle = {
                                coroutineScope.launch {
                                    val newLanguage = if (selectedLanguage == Language.ENGLISH) {
                                        Language.ARABIC
                                    } else {
                                        Language.ENGLISH
                                    }
                                    languagePreferences.setLanguage(newLanguage)
                                    // Recreate activity to apply new locale
                                    recreate()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}