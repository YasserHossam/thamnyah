package com.thmanyah.task.data.local

import android.content.Context
import android.content.res.Configuration
import com.thmanyah.task.domain.model.Language
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor() {
    
    fun setLocale(context: Context, language: Language): Context {
        val locale = when (language) {
            Language.ENGLISH -> Locale.ENGLISH
            Language.ARABIC -> Locale("ar")
        }
        
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
    
    fun getCurrentLocale(context: Context): Locale {
        return context.resources.configuration.locales.get(0)
    }
}