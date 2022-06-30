package com.mimao.kmp.videoplayer.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    val (player, videoLayout )= rememberVideoPlayerState()
    LaunchedEffect(player) {
        player.prepare("https://www.w3schools.com/html/movie.mp4")
    }
    Box {
        videoLayout.invoke(Modifier.fillMaxWidth().height(300.dp))
        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
           Button(onClick = {
               player.pause()
           }) {
               Text("Pause")
           }

            Button(onClick = {
                player.play()
            }) {
                Text("Play")
            }

            Button(onClick = {
                player.setMute(true)
            }) {
                Text("Mute")
            }

            Button(onClick = {
                player.setMute(false)
            }) {
                Text("unMute")
            }
        }
    }
}