package com.itsraj.funkytalk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    private val _isJoined = MutableStateFlow(false)
    val isJoined: StateFlow<Boolean> = _isJoined

    private val appId = "7aed853cb4f141028bf82a0c8bfef3a6"
    private var rtcEngine: RtcEngine? = null

    init {
        fetchRooms()
        setupAgora()
    }

    fun fetchRooms() {
        viewModelScope.launch {
            _rooms.value = repository.getActiveVoiceRooms()
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

    fun joinRoom(roomId: String) {
        rtcEngine?.joinChannel(null, roomId, null, 0)
    }

    fun leaveRoom() {
        rtcEngine?.leaveChannel()
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
