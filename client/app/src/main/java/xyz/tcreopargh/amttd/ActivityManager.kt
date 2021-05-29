package xyz.tcreopargh.amttd

import android.app.Activity

/**
 * @author TCreopargh
 *
 * Manages any activity and allows to exit the app by finishing all activities at once.
 *
 * All activities must extend [BaseActivity] class for this to have any effect.
 */
object ActivityManager {
    private val activities = mutableSetOf<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }
}