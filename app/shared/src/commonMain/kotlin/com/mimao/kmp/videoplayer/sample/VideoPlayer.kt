package com.mimao.kmp.videoplayer.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mimao.kmp.videoplayer.KVideoPlayer

@Composable
expect fun rememberVideoPlayerState(): VideoPlayerState

data class VideoPlayerState(
    val player: KVideoPlayer,
    val content: @Composable (Modifier) -> Unit
)