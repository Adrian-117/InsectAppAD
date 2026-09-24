package com.example.randominsect.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randominsect.data.api.InsectApi
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _generatedInsect = MutableStateFlow<InsectEntity?>(null)
    val generatedInsect: StateFlow<InsectEntity?> = _generatedInsect

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun generateRandomInsect() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val insect = InsectApi.getRandomInsect()
                _generatedInsect.value = insect
            } catch (e: Exception) {
                android.util.Log.e("insectapp", "Error generating insect", e)
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
