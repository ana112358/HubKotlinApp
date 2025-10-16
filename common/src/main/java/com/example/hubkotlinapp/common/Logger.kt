package com.example.hubkotlinapp.common

import android.util.Log
import com.example.hubkotlinapp.common.BuildConfig

object Logger {

    // Define uma TAG global para todos os logs do hub
    private const val HUB_TAG = "MeuAppHub"

    // Controla se os logs detalhados devem ser exibidos.
    // Usaremos o BuildConfig do módulo para decidir.
    private val isDebugMode: Boolean = BuildConfig.DEBUG

    fun v(tag: String, message: String) {
        if (isDebugMode) {
            Log.v("$HUB_TAG/$tag", message)
        }
    }

    fun d(tag: String, message: String) {
        if (isDebugMode) {
            Log.d("$HUB_TAG/$tag", message)
        }
    }

    fun i(tag: String, message: String) {
        Log.i("$HUB_TAG/$tag", message)
    }

    fun w(tag: String, message: String, error: Throwable? = null) {
        Log.w("$HUB_TAG/$tag", message, error)
    }

    fun e(tag: String, message: String, error: Throwable? = null) {
        Log.e("$HUB_TAG/$tag", message, error)
    }
}
