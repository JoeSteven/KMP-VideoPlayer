package com.mimao.kmp.videoplayer

import kotlinx.coroutines.flow.Flow

expect class KVideoPlayer{
    val status: Flow<KPlayerStatus>
    val volume: Flow<Float>
    val isMute: Flow<Boolean>
    val currentTime: Flow<Long>
    val duration: Flow<Long>
    fun prepare(dataSource: Any, playWhenReady: Boolean = true)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun setMute(mute: Boolean)
    fun setVolume(volume: Float)
    fun setRepeat(isRepeat: Boolean)
}

sealed interface KPlayerStatus{
    object Idle: KPlayerStatus
    object Preparing: KPlayerStatus
    object Ready: KPlayerStatus
    object Buffering : KPlayerStatus
    object Playing: KPlayerStatus
    object Paused: KPlayerStatus
    object Ended: KPlayerStatus
    data class Error(val error: Throwable): KPlayerStatus
    object Released: KPlayerStatus
}

typealias OnPlayerError = (error: Throwable) -> Unit
typealias OnPlayerStateChanged = (KPlayerStatus) -> Unit
typealias OnProgressChanged = (Long) -> Unit