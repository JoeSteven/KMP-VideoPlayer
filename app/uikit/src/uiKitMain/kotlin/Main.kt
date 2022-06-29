import kotlinx.cinterop.ObjCObjectBase
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skiko.GenericSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind
import org.jetbrains.skiko.SkikoUIView
import org.jetbrains.skiko.SkikoView
import org.jetbrains.skiko.SkikoViewController
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

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
        window!!.rootViewController = SkikoViewController(
            SkikoUIView(
                SkiaLayer().apply {
                    gesturesToListen = SkikoGestureEventKind.values()
                    skikoView = GenericSkikoView(this, object : SkikoView {
                        val paint = Paint().apply { color = Color.RED }
                        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
                            canvas.clear(Color.CYAN)
                            val ts = nanoTime / 5_000_000
                            canvas.drawCircle( (ts % width).toFloat(), (ts % height).toFloat(), 20f, paint )
                        }
                    })
                }
            )
        )
        window!!.makeKeyAndVisible()
        return true
    }
}
