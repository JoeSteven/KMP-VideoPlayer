package com.mimao.kmp.videoplayer

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

actual class KVideoPlayer(
    private val playerView: StyledPlayerView,
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

    private var countingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> _status.value = KPlayerStatus.Idle
                Player.STATE_BUFFERING -> _status.value = KPlayerStatus.Buffering
                Player.STATE_READY -> _status.value = KPlayerStatus.Ready
                Player.STATE_ENDED -> _status.value = KPlayerStatus.Ended
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _status.value = if (isPlaying) KPlayerStatus.Playing else KPlayerStatus.Paused
            countingJob?.cancel()
            if (isPlaying) {
                countingJob = scope.launch {
                    while (true) {
                        delay(1000)
                        _currentTime.value = playerView.player?.currentPosition ?:0
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _status.value = KPlayerStatus.Error(error)
        }
    }

    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
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
                }
        }
    }

    actual fun play() {
        playerView.player?.playWhenReady = true
        playerView.onResume()
    }

    actual fun pause() {
        playerView.player?.playWhenReady = false
        playerView.onPause()
    }

    actual fun stop() {
        playerView.player?.stop()
    }

    actual fun release() {
        countingJob?.cancel()
        playerView.player?.release()
        _status.value = KPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        playerView.player?.seekTo(position)
    }

    actual fun setMute(mute: Boolean) {
        playerView.player?.volume = if (mute) 0f else _volume.value
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        playerView.player?.volume = volume
        _volume.value = volume
    }

    actual fun setRepeat(isRepeat: Boolean) {
        playerView.player?.repeatMode = if (isRepeat) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }

}