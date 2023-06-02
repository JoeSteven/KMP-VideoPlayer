package com.mimao.kmp.videoplayer.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.mimao.kmp.videoplayer.KVideoPlayer

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    val context = LocalContext.current
    return remember {
        val view = PlayerView(context)
        VideoPlayerState(
            player = KVideoPlayer(view),
            content = {
                AndroidView(
                    factory = {
                        view
                    },
                    modifier = it
                )
            }
        )
    }
}
