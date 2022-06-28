package com.mimao.kmp.videoplayer

actual class `KVideoPlayer.android` {
    actual fun play() {
    }

    actual fun pause() {
    }

    actual fun stop() {
    }

    actual fun release() {
    }

    actual fun seekTo(position: Long) {
    }

    actual fun setMute(mute: Boolean) {
    }

    actual fun setVolume(volume: Float) {
    }

    actual fun duration(): Long {
        TODO("Not yet implemented")
    }

    actual fun currentPosition(): Long {
        TODO("Not yet implemented")
    }

    actual fun registerStateCallback(callback: KPlayerStateCallback) {
    }

    actual fun unregisterStateCallback(callback: KPlayerStateCallback) {
    }

    actual fun registerProgressCallback(callback: KPlayerProgressCallback) {
    }

    actual fun unregisterProgressCallback(callback: KPlayerProgressCallback) {
    }

    actual companion object {
        actual fun from(dataSource: Any): `KVideoPlayer.android` {
            TODO("Not yet implemented")
        }
    }

    actual enum class State {
        Idle, Preparing, Ready, Playing, Paused, Stopped, Released
    }
}