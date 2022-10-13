import androidx.compose.ui.window.Window
import com.mimao.kmp.videoplayer.sample.App
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
    NSApplication.sharedApplication()
    Window("ComposeKmpVideoPlayer") {
        App()
    }
    NSApp?.run()
}

