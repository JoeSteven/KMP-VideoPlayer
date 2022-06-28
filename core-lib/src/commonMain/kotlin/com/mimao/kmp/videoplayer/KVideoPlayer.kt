package com.mimao.kmp.videoplayer


expect class KVideoPlayer{
    fun setDataSource(dataSource: Any)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun setMute(mute: Boolean)
    fun setVolume(volume: Float)
    fun duration(): Long
    fun currentPosition(): Long
    fun registerStateCallback(callback: OnPlayerStateChanged)
    fun unregisterStateCallback(callback: OnPlayerStateChanged)
    fun registerProgressCallback(callback: OnProgressChanged)
    fun unregisterProgressCallback(callback: OnProgressChanged)
}

enum class KPlayerState{
    Idle,
    Preparing,
    Ready,
    Buffering,
    Playing,
    Paused,
}


typealias OnPlayerStateChanged = (KPlayerState) -> Unit
typealias OnProgressChanged = (Long) -> Unit