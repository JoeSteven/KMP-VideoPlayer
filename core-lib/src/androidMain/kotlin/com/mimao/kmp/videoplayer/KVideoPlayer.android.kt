package com.mimao.kmp.videoplayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual class KVideoPlayer(
    context: Context,
) {
    private val player = StyledPlayerView(context).apply {
        useController = false
    }
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
                        progressCallback?.invoke(player.player?.currentPosition ?:0)
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
        player.apply {
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
        player.player?.playWhenReady = true
        player.onResume()
    }

    actual fun pause() {
        player.player?.playWhenReady = false
        player.onPause()
    }

    actual fun stop() {
        player.player?.stop()
    }

    actual fun release() {
        countingJob?.cancel()
        player.player?.release()
        unRegisterCallback(state = true, progress = true, error = true)
    }

    actual fun seekTo(position: Long) {
        player.player?.seekTo(position)
    }

    actual fun setMute(mute: Boolean) {
        player.player?.volume = if (mute) 0f else 1f
    }

    actual fun setVolume(volume: Float) {
        player.player?.volume = volume
    }

    actual fun duration(): Long {
        return player.player?.duration ?: -1
    }

    actual fun currentPosition(): Long {
        return player.player?.currentPosition ?: -1
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
        AndroidView(
            factory = {
                player
            },
            modifier = modifier
        )
    }
}