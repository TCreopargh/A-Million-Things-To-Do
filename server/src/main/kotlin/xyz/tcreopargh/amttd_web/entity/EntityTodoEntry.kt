package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.annotation.ExcludeToStringProcessor
import xyz.tcreopargh.amttd_web.data.ITodoEntry
import xyz.tcreopargh.amttd_web.data.TodoStatus
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "todo_entry")
data class EntityTodoEntry(
    @Id
    @Column(
        name = "entry_id",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(64)"
    )
    @Type(type = "uuid-char")
    override var entryId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", columnDefinition = "varchar(64)", updatable = false)
    override var creator: EntityUser? = null,

    override var title: String = "",

    override var description: String = "",

    @Enumerated(EnumType.STRING)
    override var status: TodoStatus = TodoStatus.NOT_STARTED,

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    override var timeCreated: Calendar = Calendar.getInstance(),

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    override var deadline: Calendar? = null,

    @OneToMany(targetEntity = EntityTask::class, mappedBy = "parent")
    override var tasks: List<EntityTask> = listOf(),

    @OneToMany(targetEntity = EntityAction::class, mappedBy = "parent")
    override var actionHistory: List<EntityAction> = listOf(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    @ExcludeToString
    var parent: EntityWorkGroup? = null

) : ITodoEntry, IEntity {
    override fun toString(): String {
        return ExcludeToStringProcessor.getToString(this)
    }
}