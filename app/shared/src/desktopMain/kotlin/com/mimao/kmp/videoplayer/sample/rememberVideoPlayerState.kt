package com.mimao.kmp.videoplayer.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.mimao.kmp.videoplayer.KVideoPlayer
import com.mimao.kmp.videoplayer.defaultComponent
import java.awt.Component

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    return remember {
        val component = defaultComponent()
        VideoPlayerState(
            player = KVideoPlayer(component),
            content = {
                SwingPanel(
                    background = Color.Transparent,
                    factory = {
                        component as Component
                    },
                    modifier = it
                )
            }
        )
    }
}