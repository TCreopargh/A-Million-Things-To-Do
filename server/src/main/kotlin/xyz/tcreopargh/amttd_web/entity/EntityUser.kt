package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import xyz.tcreopargh.amttd_web.data.IUser
import java.util.*
import javax.persistence.*

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
    var joinedWorkGroups: Set<EntityWorkGroup> = setOf()
) : IEntity, IUser {
    override val username: String
        get() = name.toString()
    override val email: String
        get() = emailAddress.toString()

    override fun equals(other: Any?): Boolean = this.uuid == (other as? EntityUser)?.uuid
    override fun hashCode(): Int = uuid.hashCode()
}
