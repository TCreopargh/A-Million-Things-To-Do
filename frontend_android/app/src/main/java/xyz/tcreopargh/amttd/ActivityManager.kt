package xyz.tcreopargh.amttd

import android.app.Activity

/**
 * @author TCreopargh
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