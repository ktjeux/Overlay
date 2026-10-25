package fr.atelierreferences

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.IBinder
import android.view.*
import android.widget.*

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private var imageView: ImageView? = null
    private var params: WindowManager.LayoutParams? = null
    private var drawingMode = false
    private var opacity = 0.55f

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uriString = intent?.getStringExtra("image_uri") ?: return START_NOT_STICKY
        removeOverlay()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xCC202124.toInt())
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun btn(label: String, action: () -> Unit) = Button(this).apply {
            text = label
            textSize = 10f
            minWidth = 0
            setPadding(dp(3), 0, dp(3), 0)
            setOnClickListener { action() }
        }

        bar.addView(btn("Dessin") {
            drawingMode = !drawingMode
            updateTouchMode()
            Toast.makeText(this, if (drawingMode) "Mode dessin activé" else "Mode référence activé", Toast.LENGTH_SHORT).show()
        })
        bar.addView(btn("−") { resize(-32) })
        bar.addView(btn("+") { resize(32) })
        bar.addView(btn("Opacité") {
            opacity = if (opacity > 0.5f) 0.3f else 0.65f
            imageView?.alpha = opacity
        })
        bar.addView(btn("Fermer") { removeOverlay() })
        container.addView(bar)

        imageView = ImageView(this).apply {
            setImageURI(Uri.parse(uriString))
            scaleType = ImageView.ScaleType.FIT_CENTER
            alpha = opacity
            contentDescription = "Image de référence"
        }
        container.addView(imageView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(460)
        ))
        root = container

        params = WindowManager.LayoutParams(
            dp(340), dp(520),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = dp(12)
            y = dp(90)
        }

        bar.setOnTouchListener(object : View.OnTouchListener {
            var sx = 0
            var sy = 0
            var tx = 0f
            var ty = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val p = params ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        sx = p.x; sy = p.y; tx = event.rawX; ty = event.rawY
                        return false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        p.x = sx - (event.rawX - tx).toInt()
                        p.y = sy + (event.rawY - ty).toInt()
                        try { wm.updateViewLayout(root, p) } catch (_: Exception) {}
                        return true
                    }
                }
                return false
            }
        })

        wm.addView(container, params)
        return START_NOT_STICKY
    }

    private fun updateTouchMode() {
        val p = params ?: return
        val view = root ?: return
        p.flags = if (drawingMode) {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        } else {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        try { wm.updateViewLayout(view, p) } catch (_: Exception) {}
    }

    private fun resize(delta: Int) {
        val p = params ?: return
        val view = root ?: return
        p.width = (p.width + dp(delta)).coerceIn(dp(220), dp(700))
        try { wm.updateViewLayout(view, p) } catch (_: Exception) {}
    }

    private fun removeOverlay() {
        try { root?.let { wm.removeView(it) } } catch (_: Exception) {}
        root = null
        imageView = null
        params = null
        stopSelf()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        try { root?.let { wm.removeView(it) } } catch (_: Exception) {}
        root = null
        super.onDestroy()
    }
}
