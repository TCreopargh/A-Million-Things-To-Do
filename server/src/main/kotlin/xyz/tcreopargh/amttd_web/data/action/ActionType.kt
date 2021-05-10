package xyz.tcreopargh.amttd_web.data.action

import java.io.Serializable

/**
 * @author TCreopargh
 */
enum class ActionType : Serializable {
    COMMENT,
    STATUS_CHANGED,
    TASK_COMPLETED,
    TASK_UNCOMPLETED;
}