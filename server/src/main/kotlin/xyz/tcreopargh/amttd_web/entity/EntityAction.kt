package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.data.TodoStatus
import xyz.tcreopargh.amttd_web.data.action.ActionType
import xyz.tcreopargh.amttd_web.data.action.IAction
import java.util.*
import javax.persistence.*

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    override var task: EntityTask? = null
) : IAction {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false)
    var parent: EntityTodoEntry? = null
}