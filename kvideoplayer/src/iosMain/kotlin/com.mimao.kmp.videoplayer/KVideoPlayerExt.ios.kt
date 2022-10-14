package com.mimao.kmp.videoplayer
import platform.AVKit.AVPlayerViewController

fun createKVideoPlayerWithController(controller: AVPlayerViewController): KVideoPlayer {
    return KVideoPlayer().apply {
        onPlayerCreated = {
            controller.player = it
        }
    }
}