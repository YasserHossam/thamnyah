package com.thmanyah.task.di

import android.content.Context
import com.thmanyah.task.data.local.LanguagePreferences
import com.thmanyah.task.data.local.LocaleManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    
    @Provides
    @Singleton
    fun provideLanguagePreferences(
        @ApplicationContext context: Context
    ): LanguagePreferences = LanguagePreferences(context)
    
    @Provides
    @Singleton
    fun provideLocaleManager(): LocaleManager = LocaleManager()
}