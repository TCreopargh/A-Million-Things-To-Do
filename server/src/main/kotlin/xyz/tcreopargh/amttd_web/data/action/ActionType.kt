package xyz.tcreopargh.amttd_web.data.action

import com.google.gson.reflect.TypeToken

/**
 * @author TCreopargh
 */
enum class ActionType {
    COMMENT,
    STATUS_CHANGED,
    TASK_COMPLETED,
    TASK_UNCOMPLETED;
}