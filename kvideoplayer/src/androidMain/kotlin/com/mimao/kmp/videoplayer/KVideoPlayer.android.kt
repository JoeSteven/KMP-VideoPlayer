package com.mimao.kmp.videoplayer

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

actual class KVideoPlayer(
    private val playerView: PlayerView,
) {
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

    private var countingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var currentDataSource: Any? = null
    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> if (_status.value !is KPlayerStatus.Error) _status.value = KPlayerStatus.Idle
                Player.STATE_BUFFERING -> _status.value = KPlayerStatus.Buffering
                Player.STATE_READY -> {
                    _status.value = KPlayerStatus.Ready
                    emitDuration()
                }

                Player.STATE_ENDED -> _status.value = KPlayerStatus.Ended
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) _status.value = KPlayerStatus.Playing
            countingJob?.cancel()
            if (isPlaying) {
                countingJob = scope.launch {
                    while (true) {
                        delay(1000)
                        _currentTime.value = playerView.player?.currentPosition ?: 0
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _status.value = KPlayerStatus.Error(error)
        }
    }

    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        currentDataSource = dataSource
        _status.value = KPlayerStatus.Preparing
        playerView.apply {
            useController = false
            player?.release()
            player = ExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(listener)
                    setMediaItem(MediaItem.fromUri(dataSource as String))
                    this.playWhenReady = playWhenReady
                    prepare()
                    emitDuration()
                }
        }
    }

    actual fun play() {
        if (_status.value is KPlayerStatus.Error) {
            currentDataSource?.let {
                prepare(dataSource = it, playWhenReady = true)
            }
        } else {
            playerView.player?.playWhenReady = true
            playerView.onResume()
        }
    }

    actual fun pause() {
        playerView.player?.playWhenReady = false
        playerView.onPause()
        _status.value = KPlayerStatus.Paused
    }

    actual fun stop() {
        playerView.player?.stop()
        _status.value = KPlayerStatus.Paused
    }

    actual fun release() {
        countingJob?.cancel()
        playerView.player?.release()
        _status.value = KPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        playerView.player?.seekTo(position)
        playerView.player?.currentPosition?.let {
            _currentTime.value = it
        }
    }

    actual fun setMute(mute: Boolean) {
        playerView.player?.volume = if (mute) 0f else _volume.value
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        volume.coerceIn(0f, 1f).let {
            playerView.player?.volume = it
            _volume.value = it
        }
    }

    actual fun setRepeat(isRepeat: Boolean) {
        playerView.player?.repeatMode = if (isRepeat) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
        _isRepeated.value = isRepeat
    }

    private fun emitDuration() {
        _duration.value = playerView.player?.duration ?: 0
    }
}