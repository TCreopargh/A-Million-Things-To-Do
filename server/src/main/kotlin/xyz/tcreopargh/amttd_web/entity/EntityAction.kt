package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.annotation.ExcludeToStringProcessor
import xyz.tcreopargh.amttd_web.common.data.TodoStatus
import xyz.tcreopargh.amttd_web.common.data.action.ActionType
import xyz.tcreopargh.amttd_web.common.data.action.IAction
import java.util.*
import javax.persistence.*

/**
 * @author TCreopargh
 *
 * An action represents an edition to a to-do entry.
 *
 * When a to-do list is edited, an action is generated at the same time.
 *
 * (Actually the action comes first, the server decides what to do according to the action.)
 *
 * A special type of action is comment, which the user can add by themselves.
 *
 * This is a generic class containing all possible fields for all actions. You should probably use derived classes
 * which you can get from [xyz.tcreopargh.amttd_web.common.data.action.ActionGeneric]
 */
@Entity
@Table(name = "action")
data class EntityAction(
    @Id
    @Column(
        name = "action_id",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(64)"
    )
    @Type(type = "uuid-char")
    override var actionId: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = false)
    override var user: EntityUser? = null,

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    override var timeCreated: Calendar = Calendar.getInstance(),

    @Enumerated(EnumType.STRING)
    override var actionType: ActionType = ActionType.COMMENT,

    override var stringExtra: String? = null,

    @Enumerated(EnumType.STRING)
    override var fromStatus: TodoStatus? = null,

    @Enumerated(EnumType.STRING)
    override var toStatus: TodoStatus? = null,

    override var oldValue: String? = null,

    override var newValue: String? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = true)
    override var task: EntityTask? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false)
    @ExcludeToString
    var parent: EntityTodoEntry? = null
) : IAction, IEntity {

    override fun toString(): String {
        return ExcludeToStringProcessor.getToString(this)
    }
}