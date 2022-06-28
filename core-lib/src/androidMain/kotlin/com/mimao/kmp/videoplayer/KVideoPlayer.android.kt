package com.mimao.kmp.videoplayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual class KVideoPlayer private constructor(
    context: Context,
) {
    private val player = StyledPlayerView(context)
    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var countingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    actual fun setDataSource(dataSource: Any) {
        player.apply {
            player = ExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(object : Player.Listener{
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when(playbackState) {
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
                                    while(true) {
                                        delay(1000)
                                        progressCallback?.invoke(player.currentPosition)
                                    }
                                }
                            }
                        }
                    })

                    ProgressiveMediaSource.Factory(
                        // TODO set cache size
                        CacheDataSource.Factory()
                    ).createMediaSource(MediaItem.fromUri(dataSource as String)).also {
                        setMediaSource(it)
                    }
                    stateCallback?.invoke(KPlayerState.Preparing)
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
        progressCallback = null
        stateCallback = null
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

    actual fun registerStateCallback(callback: OnPlayerStateChanged) {
        stateCallback = callback
    }

    actual fun unregisterStateCallback(callback: OnPlayerStateChanged) {
        stateCallback = null
    }

    actual fun registerProgressCallback(callback: OnProgressChanged) {
        progressCallback = callback
    }

    actual fun unregisterProgressCallback(callback: OnProgressChanged) {
        progressCallback = null
    }
}