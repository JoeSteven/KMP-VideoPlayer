package com.mimao.kmp.videoplayer.sample
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.mimao.kmp.videoplayer.KVideoPlayer
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import platform.AVKit.AVPlayerViewController
import platform.CoreGraphics.CGImageCreate

@Composable
actual fun rememberVideoPlayerState(): VideoPlayerState {
    return remember {
        val player = KVideoPlayer(AVPlayerViewController())
        VideoPlayerState(
            player = player,
            content = {
                IosVideoRender(it,player)
            }
        )
    }

}

@Composable
fun IosVideoRender(modifier: Modifier, player: KVideoPlayer) {
    val frame by player.frame.collectAsState(byteArrayOf())
    Canvas(
        modifier = modifier,
        onDraw =  {

            if (frame.isNotEmpty()) {
                val image = Image.makeFromEncoded(frame)
//                Codec.makeFromData(Data.makeFromBytes(frame)).let {
//                    val bitmap = Bitmap().apply {
//
//                    }
//                    it.readPixels(bitmap)
                val bitmap = Bitmap().apply {
                    allocPixels(image.imageInfo)
                }
                    drawImage(
                        image = bitmap.asComposeImageBitmap(),
                    )
//                }
            }
        }
    )
}

private var bitmapCache: Bitmap? = null

private fun recycleBitmap(codec: Codec): Bitmap {
    return bitmapCache?.let {
        if (codec.width == bitmapCache?.width && codec.height == bitmapCache?.height) {
            it.apply { allocPixels(codec.imageInfo) }
        } else null
    } ?: Bitmap().apply { allocPixels(codec.imageInfo) }
        .also {
            bitmapCache = it
        }
}