package com.mimao.kmp.videoplayer.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimao.kmp.videoplayer.KPlayerStatus

@Composable
fun App() {
    val (player, videoLayout) = rememberVideoPlayerState()
    LaunchedEffect(player) {
        player.apply {
            prepare("https://video.twimg.com/amplify_video/1589178626284847104/vid/1280x720/7ctjE5yWg6XaDE_T.mp4?tag=14")
        }
    }
    val status by player.status.collectAsState(KPlayerStatus.Idle)
    val volume by player.volume.collectAsState(1f)
    val isMuted by player.isMute.collectAsState(false)
    val duration by player.duration.collectAsState(0L)
    val currentTime by player.currentTime.collectAsState(0L)
    val isRepeated by player.isRepeated.collectAsState(false)

    println("status: $status, $volume, $isMuted, $currentTime, $duration")
    var seek:Float by remember { mutableStateOf(0f) }
    var seeking: Boolean by remember {  mutableStateOf(false) }
    Column {
        videoLayout.invoke(Modifier.fillMaxWidth().height(300.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = if (duration > 0 && !seeking) currentTime / duration.toFloat() else seek,
                modifier = Modifier.weight(1f),
                onValueChange = {
                    seek = it
                    seeking = true
                },
                onValueChangeFinished = {
                    player.seekTo((duration * seek).toLong())
                    seeking = false
                }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (status is KPlayerStatus.Error) {
                Button(onClick = {
                    player.play()
                }) {
                    Text("Reload")
                }
            } else if (status is KPlayerStatus.Buffering) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    when (status) {
                        KPlayerStatus.Playing -> player.pause()
                        else -> player.play()
                    }
                }) {
                    when (status) {
                        KPlayerStatus.Playing -> Text("Pause")
                        else -> Text("Play")
                    }
                }
            }

            Button(onClick = {
                player.setRepeat(!isRepeated)
            }) {
                Text("Mode")
            }

            if (status is KPlayerStatus.Error) {
                Text("Error: ${(status as KPlayerStatus.Error).error}")
            }

            if (isMuted) {
                Text("Volume: Muted!")
            } else {
                Text("Volume: ${volume * 100}%")
            }

            Text(if (isRepeated) "Repeat: On" else "Repeat: Off")

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
    }
}