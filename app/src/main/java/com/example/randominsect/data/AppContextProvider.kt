package com.example.randominsect.data

import android.app.Application
import android.content.Context

object AppContextProvider {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        // Always store applicationContext to prevent leaking Activities/Views
        applicationContext = context.applicationContext
    }

    fun get(): Context {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("AppContextProvider must be initialized in Application.onCreate()")
        }
        return applicationContext
    }
}
