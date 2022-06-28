package com.mimao.kmp.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberKVideoPlayer(): KVideoPlayer {
    return remember() {
        KVideoPlayer()
    }
}