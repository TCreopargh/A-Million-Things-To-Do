package xyz.tcreopargh.amttd_web.common.data.action

import java.io.Serializable

/**
 * @author TCreopargh
 */
enum class ActionType : Serializable {
    COMMENT,
    STATUS_CHANGED,
    DEADLINE_CHANGED,
    TITLE_CHANGED,
    DESCRIPTION_CHANGED,
    TASK_COMPLETED,
    TASK_UNCOMPLETED,
    TASK_ADDED,
    TASK_EDITED,
    TASK_REMOVED;
}