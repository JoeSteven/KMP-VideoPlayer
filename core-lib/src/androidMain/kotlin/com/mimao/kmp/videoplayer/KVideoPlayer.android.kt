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
import kotlinx.coroutines.launch

actual class KVideoPlayer(
    private val playerView: StyledPlayerView,
) {
    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var errorCallback: OnPlayerError? = null
    private var countingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> stateCallback?.invoke(KPlayerState.Idle)
                Player.STATE_BUFFERING -> stateCallback?.invoke(KPlayerState.Buffering)
                Player.STATE_READY -> stateCallback?.invoke(KPlayerState.Ready)
                else -> {}
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            stateCallback?.invoke(if (isPlaying) KPlayerState.Playing else KPlayerState.Paused)
            countingJob?.cancel()
            if (isPlaying) {
                countingJob = scope.launch {
                    while (true) {
                        delay(1000)
                        progressCallback?.invoke(playerView.player?.currentPosition ?:0)
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            error.printStackTrace()
            errorCallback?.invoke(error)
        }
    }

    actual fun setDataSource(dataSource: Any, playWhenReady: Boolean) {
        playerView.apply {
            useController = false
            player?.release()
            player = ExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(listener)
                    setMediaItem(MediaItem.fromUri(dataSource as String))
                    stateCallback?.invoke(KPlayerState.Preparing)
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
        unRegisterCallback(state = true, progress = true, error = true)
    }

    actual fun seekTo(position: Long) {
        playerView.player?.seekTo(position)
    }

    actual fun setMute(mute: Boolean) {
        playerView.player?.volume = if (mute) 0f else 1f
    }

    actual fun setVolume(volume: Float) {
        playerView.player?.volume = volume
    }

    actual fun duration(): Long {
        return playerView.player?.duration ?: -1
    }

    actual fun currentPosition(): Long {
        return playerView.player?.currentPosition ?: -1
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