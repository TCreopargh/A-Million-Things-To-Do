package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.annotation.ExcludeToStringProcessor
import xyz.tcreopargh.amttd_web.common.data.ITask
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "task")
data class EntityTask(
    @Id
    @Column(
        name = "task_id",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(64)"
    )
    @Type(type = "uuid-char")
    override var taskId: UUID = UUID.randomUUID(),
    override var name: String = "",
    override var completed: Boolean = false,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false)
    @ExcludeToString
    var parent: EntityTodoEntry? = null
) : ITask, IEntity {
    override fun toString(): String {
        return ExcludeToStringProcessor.getToString(this)
    }
}