package com.mimao.kmp.videoplayer.sample

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mimao.kmp.videoplayer.KVideoPlayer
import platform.AVKit.AVPlayerViewController

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    return remember {
        val player = KVideoPlayer(AVPlayerViewController())
        VideoPlayerState(
            player = player,
            content = {
                IosVideoRender(it, player)
            }
        )
    }

}

@Composable
fun IosVideoRender(modifier: Modifier, player: KVideoPlayer) {
    Text(modifier = modifier, text = "haven't support compose yet")
}