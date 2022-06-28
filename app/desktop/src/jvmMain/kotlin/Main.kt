import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mimao.kmp.videoplayer.sample.VideoPlayer

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            VideoPlayer()
        }
    }
}