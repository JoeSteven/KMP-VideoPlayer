package com.mimao.kmp.videoplayer

actual class KVideoPlayer {
    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var errorCallback: OnPlayerError? = null
    actual fun setDataSource(dataSource: Any, playWhenReady: Boolean) {
        TODO("not implemented")
    }

    actual fun play() {
        TODO("not implemented")

    }

    actual fun pause() {
        TODO("not implemented")
    }

    actual fun stop() {
        TODO("not implemented")
    }

    actual fun release() {
        TODO("not implemented")
    }

    actual fun seekTo(position: Long) {
        TODO("not implemented")
    }

    actual fun setMute(mute: Boolean) {
        TODO("not implemented")
    }

    actual fun setVolume(volume: Float) {
        TODO("not implemented")
    }

    actual fun duration(): Long {
        TODO("not implemented")
    }

    actual fun currentPosition(): Long {
        TODO("not implemented")
    }


    actual fun registerCallback(
        state: OnPlayerStateChanged?,
        progress: OnProgressChanged?,
        error: OnPlayerError?,
    ) {
        state?.let { this.stateCallback = it }
        progress?.let { this.progressCallback = it }
        error?.let { this.errorCallback = it }
    }

    actual fun unRegisterCallback(
        state: Boolean,
        progress: Boolean,
        error: Boolean,
    ) {
        if (state) this.stateCallback = null
        if (progress) this.progressCallback = null
        if (error) this.errorCallback = null
    }

    @Composable
    actual fun Content(modifier: Modifier) {
        TODO("not implemented")
    }
}
