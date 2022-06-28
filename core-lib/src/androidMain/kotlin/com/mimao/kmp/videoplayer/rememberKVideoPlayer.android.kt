package com.mimao.kmp.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberKVideoPlayer(): KVideoPlayer {
    val context = LocalContext.current
    return remember {
        KVideoPlayer(context)
    }
}