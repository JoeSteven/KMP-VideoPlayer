import androidx.compose.ui.window.Window
import com.mimao.kmp.videoplayer.createKVideoPlayerWithView
import platform.AVKit.AVPlayerView
import platform.AppKit.*
import platform.Foundation.NSMakeRect
import platform.darwin.NSObject

fun main() {
    val app = NSApplication.sharedApplication()
    app.delegate = object : NSObject(), NSApplicationDelegateProtocol {
        override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean {
            return true
        }
    }
    val window = object : NSWindow(
        contentRect = NSMakeRect(0.0, 0.0, 640.0, 780.0),
        styleMask = NSWindowStyleMaskTitled or
                NSWindowStyleMaskMiniaturizable or
                NSWindowStyleMaskClosable or
                NSWindowStyleMaskResizable,
        backing = NSBackingStoreBuffered,
        defer = false
    ){
        override fun canBecomeKeyWindow(): Boolean {
            return true
        }

        override fun canBecomeMainWindow(): Boolean {
            return true
        }
    }

    val playerView = AVPlayerView(NSMakeRect(0.0, 0.0, 640.0, 480.0))

    createKVideoPlayerWithView(playerView).apply {
        prepare("https://www.w3schools.com/html/movie.mp4")
        setRepeat(false)
        setVolume(1f)
    }
    window.contentView!!.addSubview(playerView)
    window.makeFirstResponder(playerView)
    window.makeKeyAndOrderFront(app)
    app.run()
}

