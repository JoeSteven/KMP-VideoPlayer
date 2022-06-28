package com.mimao.kmp.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
actual fun rememberKVideoPlayer(): KVideoPlayer {
    val context = LocalContext.current
    return remember {
        KVideoPlayer(StyledPlayerView(context))
    }
}