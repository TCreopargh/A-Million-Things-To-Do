package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.api.data.IUser
import java.util.*
import javax.persistence.*

/**
 * @author TCreopargh
 *
 * A plain, old user.
 *
 * UUID is used to uniquely identify an user, but Email is also unique.
 *
 * User names can duplicate across users.
 */
@Entity
@Table(
    name = "user", indexes = [
        Index(name = "id_index", columnList = "user_uuid", unique = true),
        Index(name = "email_index", columnList = "email_address", unique = true)
    ]
)
data class EntityUser(

    var name: String? = null,

    @Column(unique = true, columnDefinition = "varchar(80)", name = "email_address")
    var emailAddress: String? = null,

    var password: String? = null,

    @Id
    @Column(
        name = "user_uuid",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(64)"
    )
    @Type(type = "uuid-char")
    override var uuid: UUID = UUID.randomUUID(),

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    val timeCreated: Calendar = Calendar.getInstance(),

    @ManyToMany
    @JoinTable(
        name = "user_in_work_group",
        joinColumns = [JoinColumn(name = "user_uuid")],
        inverseJoinColumns = [JoinColumn(name = "groupId")]
    )
    var joinedWorkGroups: MutableSet<EntityWorkGroup> = mutableSetOf()

) : EntityBase<UUID>(), IUser {

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "avatar_id")
    var avatar: EntityUserAvatar? = null

    @Suppress("unused")
    @OneToMany(
        targetEntity = EntityAuthToken::class,
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var authTokens: MutableSet<EntityAuthToken> = mutableSetOf()

    override val username: String
        get() = name.toString()
    override val email: String
        get() = emailAddress.toString()

    override fun getId(): UUID = uuid
}
