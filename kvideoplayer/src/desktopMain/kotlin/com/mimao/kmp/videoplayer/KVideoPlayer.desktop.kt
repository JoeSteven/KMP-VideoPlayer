package com.mimao.kmp.videoplayer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import uk.co.caprica.vlcj.media.*
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.MediaPlayerComponent
import java.util.Locale

actual class KVideoPlayer(
    component: MediaPlayerComponent
) {
    private val player = component.mediaPlayer()

    private val _status = MutableStateFlow<KPlayerStatus>(KPlayerStatus.Idle)
    actual val status: Flow<KPlayerStatus>
        get() = _status

    private val _volume = MutableStateFlow(1f)
    actual val volume: Flow<Float>
        get() = _volume

    private val _isMute = MutableStateFlow(false)
    actual val isMute: Flow<Boolean>
        get() = _isMute

    private val _currentTime = MutableStateFlow(0L)
    actual val currentTime: Flow<Long>
        get() = _currentTime

    private val _duration = MutableStateFlow(0L)
    actual val duration: Flow<Long>
        get() = _duration

    private val _isRepeated = MutableStateFlow(false)
    actual val isRepeated: Flow<Boolean>
        get() = _isRepeated

    private val eventAdapter = object : MediaPlayerEventAdapter() {
        override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
            _status.value = KPlayerStatus.Buffering
        }

        override fun playing(mediaPlayer: MediaPlayer?) {
            _status.value = KPlayerStatus.Playing
        }

        override fun paused(mediaPlayer: MediaPlayer?) {
            _status.value = KPlayerStatus.Paused
        }

        /**
         * Waiting for this event may be more reliable than using playing(MediaPlayer) or videoOutput(MediaPlayer, int)
         * in some cases (logo and marquee already mentioned, also setting audio tracks, sub-title tracks and so on).
         */
        override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
            _status.value = KPlayerStatus.Playing
            _duration.value = player.status().length()
        }

        override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
            _currentTime.value = newTime
        }

        override fun finished(mediaPlayer: MediaPlayer?) {
            _status.value = KPlayerStatus.Ended
        }

        override fun error(mediaPlayer: MediaPlayer?) {
            _status.value = KPlayerStatus.Error(
                Error(
                    "Failed to load media ${
                        mediaPlayer?.media()?.info()?.mrl()
                    }"
                )
            )
        }
    }

    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        _status.value = KPlayerStatus.Preparing
        player.events().addMediaPlayerEventListener(eventAdapter)
        if (playWhenReady) {
            player.media().play(dataSource.toString())
        } else {
            player.media().prepare(dataSource.toString())
        }
        _duration.value = player.status().length()
        _status.value = KPlayerStatus.Ready
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
        _status.value = KPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        player.controls()?.setTime(position)
    }

    actual fun setMute(mute: Boolean) {
        player.audio().setVolume(if (mute) 0 else _volume.value.toVLCVolume())
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        volume.coerceIn(0f, 1f).let {
            player.audio().setVolume(it.toVLCVolume())
            _volume.value = it
        }
    }

    actual fun setRepeat(isRepeat: Boolean) {
        player.controls().repeat = isRepeat
        _isRepeated.value = isRepeat
    }

    private fun Float.toVLCVolume() = (this * 200).toInt()
}

private fun Any.mediaPlayer(): MediaPlayer {
    return when (this) {
        is CallbackMediaPlayerComponent -> mediaPlayer()
        is EmbeddedMediaPlayerComponent -> mediaPlayer()
        else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
    }
}

fun defaultComponent(): MediaPlayerComponent = if (isMacOS()) {
    CallbackMediaPlayerComponent()
} else {
    EmbeddedMediaPlayerComponent()
}

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
    return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
}