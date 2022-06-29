package com.mimao.kmp.videoplayer
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemPlaybackStalledNotification
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVFoundation.volume
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.darwin.NSObjectProtocol

actual class KVideoPlayer(
    private val playerController: AVPlayerViewController
) {
    private var player: AVPlayer? = null
    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var errorCallback: OnPlayerError? = null
    private var observer: NSObjectProtocol? = null

    actual fun setDataSource(dataSource: Any, playWhenReady: Boolean) {
        stateCallback?.invoke(KPlayerState.Preparing)
        val url = NSURL(string = dataSource.toString())
        this.player = AVPlayer(uRL = url)
        observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemPlaybackStalledNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                errorCallback?.invoke(Error("Failed to play media"))
            }
        )
        playerController.player = player
        stateCallback?.invoke(KPlayerState.Ready)
        if (playWhenReady) play()
    }

    actual fun play() {
        player?.play()
        stateCallback?.invoke(KPlayerState.Playing)
    }

    actual fun pause() {
        player?.pause()
        stateCallback?.invoke(KPlayerState.Paused)
    }

    actual fun stop() {
        player?.run {
            pause()
            seekTo(0)
        }
    }

    actual fun release() {
        stop()
        unRegisterCallback(state = true, progress = true, error = true)
        observer?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
    }

    actual fun seekTo(position: Long) {
        player?.seekToTime(time = cValue{value = position})
    }

    actual fun setMute(mute: Boolean) {
        player?.volume = if (mute) 0f else 1f
    }

    actual fun setVolume(volume: Float) {
        player?.volume = volume
    }

    actual fun duration(): Long {
        return player?.currentItem()?.duration?.useContents {
            value
        } ?: 0
    }

    actual fun currentPosition(): Long {
        return  player?.currentTime()?.useContents {
            value
        } ?: 0
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
