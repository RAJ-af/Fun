package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itsraj.funkytalk.data.repository.VoiceRoomRepository
import android.app.Application

class VoiceRoomViewModelFactory(
    private val application: Application,
    private val repository: VoiceRoomRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoiceRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoiceRoomViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
