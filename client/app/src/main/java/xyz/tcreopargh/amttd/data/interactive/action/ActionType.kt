package xyz.tcreopargh.amttd.data.interactive.action

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