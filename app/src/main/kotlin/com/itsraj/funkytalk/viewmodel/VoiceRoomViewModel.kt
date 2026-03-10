package com.itsraj.funkytalk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itsraj.funkytalk.data.model.ParticipantWithProfile
import com.itsraj.funkytalk.data.model.VoiceRoom
import com.itsraj.funkytalk.data.repository.VoiceRoomRepository
import io.agora.rtc2.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoiceRoomViewModel(
    application: Application,
    private val repository: VoiceRoomRepository
) : AndroidViewModel(application) {

    private val _rooms = MutableStateFlow<List<VoiceRoom>>(emptyList())
    val rooms: StateFlow<List<VoiceRoom>> = _rooms

    private val _currentRoomId = MutableStateFlow<String?>(null)
    val currentRoomId: StateFlow<String?> = _currentRoomId

    private val _participants = MutableStateFlow<List<ParticipantWithProfile>>(emptyList())
    val participants: StateFlow<List<ParticipantWithProfile>> = _participants

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    private val _isJoined = MutableStateFlow(false)
    val isJoined: StateFlow<Boolean> = _isJoined

    private val appId = "7aed853cb4f141028bf82a0c8bfef3a6"
    private var rtcEngine: RtcEngine? = null

    init {
        fetchRooms()
        setupAgora()
        observeRealtimeChanges()
    }

    fun fetchRooms() {
        viewModelScope.launch {
            _rooms.value = repository.getActiveVoiceRooms()
        }
    }

    private fun observeRealtimeChanges() {
        viewModelScope.launch {
            repository.observeParticipantChanges().collect {
                // Refetch rooms to update counts on Home
                fetchRooms()
                // Refetch participants for the active room
                _currentRoomId.value?.let { fetchParticipants(it) }
            }
        }
    }

    fun fetchParticipants(roomId: String) {
        viewModelScope.launch {
            _participants.value = repository.getParticipants(roomId)
        }
    }

    private fun setupAgora() {
        try {
            val config = RtcEngineConfig()
            config.mContext = getApplication<Application>().applicationContext
            config.mAppId = appId
            config.mEventHandler = object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    _isJoined.value = true
                }

                override fun onLeaveChannel(stats: RtcStats?) {
                    _isJoined.value = false
                }
            }
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            rtcEngine?.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createRoom(title: String, language: String, hostId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val room = repository.createRoom(title, language, hostId)
            if (room != null) {
                _currentRoomId.value = room.id
                rtcEngine?.joinChannel(null, room.id, null, 0)
                onSuccess()
            }
        }
    }

    fun joinRoom(roomId: String, userId: String) {
        viewModelScope.launch {
            _currentRoomId.value = roomId
            repository.joinRoom(roomId, userId)
            fetchParticipants(roomId)
            rtcEngine?.joinChannel(null, roomId, null, 0)
        }
    }

    fun leaveRoom(userId: String) {
        val roomId = _currentRoomId.value ?: return
        viewModelScope.launch {
            repository.leaveRoom(roomId, userId)
            rtcEngine?.leaveChannel()
            _currentRoomId.value = null
            _participants.value = emptyList()
        }
    }

    fun raiseHand(userId: String) {
        val roomId = _currentRoomId.value ?: return
        viewModelScope.launch {
            repository.updateParticipantRole(roomId, userId, "speaker")
        }
    }

    fun toggleMute() {
        val newMuteState = !_isMuted.value
        _isMuted.value = newMuteState
        rtcEngine?.muteLocalAudioStream(newMuteState)
    }

    override fun onCleared() {
        super.onCleared()
        RtcEngine.destroy()
        rtcEngine = null
    }
}
