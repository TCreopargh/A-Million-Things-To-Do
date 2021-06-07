package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.api.data.IUser
import xyz.tcreopargh.amttd_web.api.data.IWorkGroup
import java.util.*
import javax.persistence.*

/**
 * @author TCreopargh
 *
 * An workgroup is where a group of users co-operate on a list of to-do entries.
 *
 * Workgroup members can invite other users to join.
 *
 * A workgroup maintains a list of to-do entries.
 * You must be a member of the workgroup to view or edit any of the to-do entries in it.
 */
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
    override var groupId: UUID = UUID.randomUUID(),

    var groupName: String = "",

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    override var timeCreated: Calendar = Calendar.getInstance(),

    @ManyToMany(mappedBy = "joinedWorkGroups")
    @ExcludeToString
    var users: MutableSet<EntityUser> = mutableSetOf(),

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @ExcludeToString
    var entries: MutableSet<EntityTodoEntry> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", columnDefinition = "varchar(64)", updatable = true)
    @ExcludeToString
    override var leader: EntityUser? = null

) : IWorkGroup, EntityBase<UUID>() {
    override val name: String
        get() = groupName
    override val usersInGroup: List<IUser>
        get() = users.toList()

    @Suppress("unused")
    @OneToMany(
        targetEntity = EntityInvitationCode::class,
        mappedBy = "workGroup",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var invitationCodes: MutableSet<EntityInvitationCode> = mutableSetOf()

    override fun getId(): UUID = groupId
}
