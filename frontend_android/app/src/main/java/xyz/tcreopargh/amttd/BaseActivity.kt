package xyz.tcreopargh.amttd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author TCreopargh
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.removeActivity(this)
    }
}
