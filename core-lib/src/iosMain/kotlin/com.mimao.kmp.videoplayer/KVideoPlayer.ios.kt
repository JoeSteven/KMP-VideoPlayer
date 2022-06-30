package com.mimao.kmp.videoplayer
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

// TODO NOT IMPLEMENTED YET
actual class KVideoPlayer(
    private val playerController: AVPlayerViewController
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

    private var player: AVPlayer? = null
    private var stateCallback: OnPlayerStateChanged? = null
    private var progressCallback: OnProgressChanged? = null
    private var errorCallback: OnPlayerError? = null
    private var observer: NSObjectProtocol? = null

    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        stateCallback?.invoke(KPlayerStatus.Preparing)
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
        stateCallback?.invoke(KPlayerStatus.Ready)
        if (playWhenReady) play()
    }

    actual fun play() {
        player?.play()
        stateCallback?.invoke(KPlayerStatus.Playing)
    }

    actual fun pause() {
        player?.pause()
        stateCallback?.invoke(KPlayerStatus.Paused)
    }

    actual fun stop() {
        player?.run {
            pause()
            seekTo(0)
        }
    }

    actual fun release() {
        stop()
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

//    actual fun duration(): Long {
//        return player?.currentItem()?.duration?.useContents {
//            value
//        } ?: 0
//    }
//
//    actual fun currentPosition(): Long {
//        return  player?.currentTime()?.useContents {
//            value
//        } ?: 0
//    }

    actual fun setRepeat(isRepeat: Boolean) {
        TODO()
    }
}
