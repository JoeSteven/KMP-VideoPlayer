package com.mimao.kmp.videoplayer

import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.*

actual class KVideoPlayer {
    private val player = if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }.mediaPlayer()

    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var errorCallback: OnPlayerError? = null
    actual fun setDataSource(dataSource: Any) {
        stateCallback?.invoke(KPlayerState.Idle)
        player.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
                stateCallback?.invoke(KPlayerState.Buffering)
            }

            override fun playing(mediaPlayer: MediaPlayer?) {
                stateCallback?.invoke(KPlayerState.Playing)
            }

            override fun paused(mediaPlayer: MediaPlayer?) {
                stateCallback?.invoke(KPlayerState.Paused)
            }

            override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                stateCallback?.invoke(KPlayerState.Ready)
            }

            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                progressCallback?.invoke(newTime)
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                errorCallback?.invoke(Error("Failed to load media ${mediaPlayer?.media()?.info()?.mrl()}"))
            }
        })
        stateCallback?.invoke(KPlayerState.Preparing)
        player.media().prepare(dataSource.toString())
    }

    actual fun play() {
        player.controls().play()

    }

    actual fun pause() {
        player.controls().pause()
    }

    actual fun stop() {
        player.controls().stop()
    }

    actual fun release() {
        player.release()
        progressCallback = null
        stateCallback = null
    }

    actual fun seekTo(position: Long) {
        player.controls()?.setTime(position)
    }

    actual fun setMute(mute: Boolean) {
        player.media()
    }

    actual fun setVolume(volume: Float) {
        player.audio().setVolume(volume.toInt())
    }

    actual fun duration(): Long {
        return player.status().length()
    }

    actual fun currentPosition(): Long {
        return player.status().time()
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
}

private fun Any.mediaPlayer(): MediaPlayer {
    return when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> mediaPlayer()
        else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
    }
}

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}