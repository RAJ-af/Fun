package com.itsraj.funkytalk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsraj.funkytalk.data.model.Announcement
import com.itsraj.funkytalk.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _latestAnnouncement = MutableStateFlow<Announcement?>(null)
    val latestAnnouncement: StateFlow<Announcement?> = _latestAnnouncement

    init {
        fetchAnnouncements()
        fetchLatestAnnouncement()
    }

    fun fetchAnnouncements() {
        viewModelScope.launch {
            _announcements.value = repository.getAnnouncements()
        }
    }

    fun fetchLatestAnnouncement() {
        viewModelScope.launch {
            _latestAnnouncement.value = repository.getLatestAnnouncement()
        }
    }
}
