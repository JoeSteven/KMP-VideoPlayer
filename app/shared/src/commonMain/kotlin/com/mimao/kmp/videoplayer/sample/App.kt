package com.mimao.kmp.videoplayer.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimao.kmp.videoplayer.KPlayerStatus

@Composable
fun App() {
    val (player, videoLayout )= rememberVideoPlayerState()
    LaunchedEffect(player) {
        player.apply {
            prepare("https://www.w3schools.com/html/movie.mp4")
            setRepeat(true)
        }
    }
    val status by player.status.collectAsState(KPlayerStatus.Idle)
    val volume by player.volume.collectAsState(1f)
    val isMuted by player.isMute.collectAsState(false)
    val duration by player.duration.collectAsState(0L)
    val currentTime by player.currentTime.collectAsState(0L)

    println("status: $status, $volume, $isMuted, $currentTime, $duration")
    Column {
        videoLayout.invoke(Modifier.fillMaxWidth().height(300.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (status is KPlayerStatus.Error) {
                Button(onClick = {
                    player.prepare("https://www.w3schools.com/html/movie.mp4")
                }) {
                    Text("Reload")
                }
            } else  if (status is KPlayerStatus.Buffering){
                CircularProgressIndicator()
            } else  {
                Button(onClick = {
                    when(status) {
                        KPlayerStatus.Playing -> player.pause()
                        else -> player.play()
                    }
                }) {
                    when(status) {
                        KPlayerStatus.Playing -> Text("Pause")
                        else -> Text("Play")
                    }
                }
            }

            if (status is KPlayerStatus.Error) {
                Text("Error: ${(status as KPlayerStatus.Error).error}")
            } else {
                LinearProgressIndicator(
                    progress = currentTime/duration.toFloat(),
                    modifier = Modifier.weight(1f),
                )
            }

            if (isMuted) {
                Text("Volume: Muted!")
            } else {
                Text("Volume: ${volume* 100}%")
            }

        }
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                player.setMute(!isMuted)
            }) {
                Text(if (isMuted) "unMute" else "Mute")
            }
            Button(onClick = {
                player.setVolume(volume - 0.1f)
            }) {
                Text("volume -10%")
            }

            Button(onClick = {
                player.setVolume(volume + 0.1f)
            }) {
                Text("volume +10%")
            }
        }
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                player.seekTo(0)
            }) {
                Text("start")
            }

            Button(onClick = {
                player.seekTo(duration/2)
            }) {
                Text("middle")
            }

            Button(onClick = {
                player.seekTo(duration)
            }) {
                Text("end")
            }
        }
    }
}