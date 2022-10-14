package com.mimao.kmp.videoplayer
import platform.AVKit.AVPlayerView

fun createKVideoPlayerWithView(view: AVPlayerView): KVideoPlayer {
    return KVideoPlayer().apply {
        onPlayerCreated = {
            view.player = it
        }
    }
}