package com.mimao.kmp.videoplayer.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun App() {
    val (player, videoLayout )= rememberVideoPlayerState()
    LaunchedEffect(player) {
        player.setDataSource("https://www.w3schools.com/html/movie.mp4")
    }
    Column {
        videoLayout.invoke(Modifier.fillMaxWidth().aspectRatio(0.6f))
    }
}


// expect fun rememberVideoPlayerState(): VideoPlayerState
//
//
// data class VideoPlayerState(
//     val player: KVideoPlayer,
//     val content: @Composable (modifier: Modifier) -> Unit
// )