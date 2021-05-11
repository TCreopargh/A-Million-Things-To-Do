package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.annotation.ExcludeToStringProcessor
import xyz.tcreopargh.amttd_web.data.IUser
import xyz.tcreopargh.amttd_web.data.IWorkGroup
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "work_group")
data class EntityWorkGroup(
    @Id
    @Column(
        name = "group_id",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(128)"
    )
    @Type(type = "uuid-char")
    override val groupId: UUID = UUID.randomUUID(),

    val groupName: String = "",

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    override var timeCreated: Calendar = Calendar.getInstance(),

    @ManyToMany(mappedBy = "joinedWorkGroups")
    @ExcludeToString
    var users: MutableSet<EntityUser> = mutableSetOf(),

    @OneToMany(mappedBy = "parent")
    @ExcludeToString
    var entries: List<EntityTodoEntry> = listOf()

) : IWorkGroup, IEntity {
    override val name: String
        get() = groupName
    override val usersInGroup: List<IUser>
        get() = users.toList()

    override fun toString(): String {
        return ExcludeToStringProcessor.getToString(this)
    }

}
