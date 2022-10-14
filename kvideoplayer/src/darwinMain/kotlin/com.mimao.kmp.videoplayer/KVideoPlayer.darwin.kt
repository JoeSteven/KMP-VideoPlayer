package com.mimao.kmp.videoplayer

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import platform.AVFoundation.*
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.Foundation.*
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

actual class KVideoPlayer() : IOSPlayerObserverProtocol, NSObject() {
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

    private var player: AVPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var repeatJob: Job? = null
    private var timeObserver: Any? = null
    private var playWhenReady = false

    internal var onPlayerCreated: (AVPlayer) -> Unit = {}
    actual fun prepare(dataSource: Any, playWhenReady: Boolean) {
        this.playWhenReady = playWhenReady
        _status.value = KPlayerStatus.Preparing
        val url = NSURL(string = dataSource.toString())
        this.player = AVPlayer(uRL = url)

        player?.currentItem?.addObserver(
            observer = this,
            forKeyPath = "status",
            options = NSKeyValueObservingOptionNew,
            context = null
        )

        timeObserver = player?.addPeriodicTimeObserverForInterval(
            interval = CMTimeMake(1, 1),
            queue = dispatch_get_main_queue(),
            usingBlock = {
                _currentTime.value = it.milliseconds()
                if (_currentTime.value == _duration.value) {
                    _status.value = KPlayerStatus.Ended
                }
            }
        )
        player?.let(onPlayerCreated)
    }

    actual fun play() {
        player?.play()
        _status.value = KPlayerStatus.Playing
    }

    actual fun pause() {
        player?.pause()
        _status.value = KPlayerStatus.Paused
    }

    actual fun stop() {
        player?.run {
            pause()
            seekTo(0)
        }
        repeatJob?.cancel()
    }

    actual fun release() {
        stop()
        player?.removeObserver(this, forKeyPath = "status")
        timeObserver?.let {
            player?.removeTimeObserver(it)
        }
        _status.value = KPlayerStatus.Released
    }

    actual fun seekTo(position: Long) {
        // TODO FIXME: seekTo is not working properly
        player?.seekToTime(
            time = CMTimeMake(
                value = position,
                timescale = 1000
            )
        )
    }

    actual fun setMute(mute: Boolean) {
        player?.volume = if (mute) 0f else _volume.value
        _isMute.value = mute
    }

    actual fun setVolume(volume: Float) {
        volume.coerceIn(0f, 1f).let {
            player?.volume = it
            _volume.value = it
        }
    }

    actual fun setRepeat(isRepeat: Boolean) {
        _isRepeated.value = isRepeat
        repeatJob?.cancel()
        if (isRepeat) {
            scope.launch {
                _status.collect {
                    if (it == KPlayerStatus.Ended) {
                        seekTo(0)
                        play()
                    }
                }
            }
        }
    }

    private fun CValue<CMTime>?.milliseconds(): Long {
        return this?.let {
            CMTimeGetSeconds(this).toLong() * 1000
        } ?: 0L
    }

    override fun observeValueForKeyPath(
        keyPath: String?,
        ofObject: Any?,
        change: Map<Any?, *>?,
        context: COpaquePointer?
    ) {
        (change?.get("new") as NSNumber?)?.let {
            when (it.longValue) {
                AVPlayerItemStatusReadyToPlay -> {
                    _status.value = KPlayerStatus.Ready
                    _duration.value = player?.currentItem?.duration.milliseconds()
                    if (playWhenReady) {
                        play()
                    }
                }

                AVPlayerItemStatusFailed -> {
                    _status.value = KPlayerStatus.Error(Error("Failed to play media"))
                }
                else -> {}
            }
        } ?: println("unknown status $keyPath")
    }
}

