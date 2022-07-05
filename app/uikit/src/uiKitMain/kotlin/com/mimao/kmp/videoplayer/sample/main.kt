package com.mimao.kmp.videoplayer.sample

import androidx.compose.material.Text
import androidx.compose.ui.window.Application
import com.mimao.kmp.videoplayer.KVideoPlayer
import kotlinx.cinterop.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skiko.*
import platform.AVFoundation.AVPlayerLayer
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSStringFromClass
import platform.UIKit.*

fun main() {
    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = VideoApp()
            window!!.makeKeyAndVisible()
        return true
    }
}

private fun composeApp() = Application("KMP Video Player") {
    App()
}

private fun SkikoApp() = SkikoViewController(
    SkikoUIView(
        SkiaLayer().apply {
            gesturesToListen = SkikoGestureEventKind.values()
            skikoView = GenericSkikoView(this, object : SkikoView {
                val paint = Paint().apply { color = Color.RED }
                override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                    canvas.clear(Color.CYAN)
                    val ts = nanoTime / 5_000_000
                    canvas.drawCircle((ts % width).toFloat(), (ts % height).toFloat(), 20f, paint)
                }
            })
        }
    )
)

private fun VideoApp() = AVPlayerViewController().apply {
    val kPlayer = KVideoPlayer(this)
    kPlayer.apply {
        prepare("https://www.w3schools.com/html/movie.mp4")
        setRepeat(true)
        setVolume(1f)
    }
}