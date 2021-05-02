package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
data class EntityUser(

    @Column(unique = true)
    var name: String? = null,

    var password: String? = null,

    @Id
    @Column(
        name = "user_uuid",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(128)"
    )
    @Type(type = "uuid-char")
    val uuid: UUID = UUID.randomUUID(),

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    val timeCreated: Calendar = Calendar.getInstance()
) : EntityBase() {

    override fun equals(other: Any?): Boolean = this.uuid == (other as? EntityUser)?.uuid

    override fun hashCode(): Int = uuid.hashCode()
}
