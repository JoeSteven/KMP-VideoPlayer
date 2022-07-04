package com.mimao.kmp.videoplayer
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.AVFoundation.*
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGImageRef
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.*
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.accessibilityFrame
import platform.darwin.NSObjectProtocol
import platform.darwin.UInt8

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

    private var _frame = MutableStateFlow(byteArrayOf())
    val frame:Flow<ByteArray> = _frame

    private var player: AVPlayer? = null
    private var observer: NSObjectProtocol? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var countingJob: Job? = null
    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        _status.value =KPlayerStatus.Preparing
        val url = NSURL(string = dataSource.toString())
        this.player = AVPlayer(uRL = url)
        observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemPlaybackStalledNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
            usingBlock = {
                _status.value = KPlayerStatus.Error(Error("Failed to play media"))
            }
        )
        playerController.player = player
        player?.currentItem?.asset?.let {asset ->
            player?.currentItem?.videoComposition = AVVideoComposition.videoCompositionWithAsset(
                asset = asset,
                applyingCIFiltersWithHandler = {
//                    it?.accessibilityFrame?.getBytes()?.let { bytes ->
//                        _frame.value = bytes
//                    }
//                    val provider = CGImageGetDataProvider(image)
//                    val data = CFDataGetBytePtr(CGDataProviderCopyData(provider))

                    it?.finishWithImage(it.sourceImage, null)
                }
            )
        }
        if (playWhenReady){
            play()
        }
        _duration.value = player?.currentItem?.duration.milliseconds()
        _status.value = KPlayerStatus.Ready
    }

    actual fun play() {
        player?.play()
        _status.value = KPlayerStatus.Playing
        countingJob?.cancel()
        countingJob = scope.launch {
            while (true) {
                delay(1000)
                _currentTime.value = player?.currentTime().milliseconds()
                if (_duration.value == 0L) {
                    _duration.value = player?.currentItem?.duration.milliseconds()
                }
            }
        }
    }

    actual fun pause() {
        player?.pause()
        countingJob?.cancel()
        _status.value = KPlayerStatus.Paused
    }

    actual fun stop() {
        player?.run {
            pause()
            seekTo(0)
        }
        countingJob?.cancel()
    }

    actual fun release() {
        stop()
        observer?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
        _status.value = KPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        player?.seekToTime(time = CMTimeMake(
            value = position,
            timescale = 1000
        ))
    }

    actual fun setMute(mute: Boolean) {
        player?.volume =if (mute) 0f else _volume.value
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        volume.coerceIn(0f, 1f).let {
            player?.volume = it
            _volume.value = it
        }
    }

    actual fun setRepeat(isRepeat: Boolean) {
        // todo
    }

    private fun CValue<CMTime>?.milliseconds(): Long {
        return this?.let {
            CMTimeGetSeconds(this).toLong() * 1000
        } ?: 0L
    }
}
