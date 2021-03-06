package com.mimao.kmp.videoplayer.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mimao.kmp.videoplayer.KVideoPlayer

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    val context = LocalContext.current
    return remember {
        val view = StyledPlayerView(context)
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