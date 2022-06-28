package com.mimao.kmp.videoplayer.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mimao.kmp.videoplayer.rememberKVideoPlayer

@Composable
fun VideoPlayer() {
    val player = rememberKVideoPlayer().apply {
        setDataSource(dataSource = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
    }
    Column {
        player.Content(modifier = Modifier.fillMaxWidth().aspectRatio(0.6f))
    }
}