package com.mimao.kmp.videoplayer

expect class KVideoPlayer{
    fun setDataSource(dataSource: Any, playWhenReady: Boolean = true)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun setMute(mute: Boolean)
    fun setVolume(volume: Float)
    fun duration(): Long
    fun currentPosition(): Long

    fun registerCallback(
        state: OnPlayerStateChanged? = null,
        progress: OnProgressChanged? = null,
        error: OnPlayerError? = null,
    )

    fun unRegisterCallback(
        state: Boolean = false,
        progress: Boolean = false,
        error: Boolean = false,
    )
}

enum class KPlayerState{
    Idle,
    Preparing,
    Ready,
    Buffering,
    Playing,
    Paused,
}

typealias OnPlayerError = (error: Throwable) -> Unit
typealias OnPlayerStateChanged = (KPlayerState) -> Unit
typealias OnProgressChanged = (Long) -> Unit