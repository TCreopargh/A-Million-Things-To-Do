package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "User")
data class EntityUser(
    //@GeneratedValue(generator = "uuid2")
    //@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Id
    @Type(type = "uuid-char")
    @Column(name = "uuid", columnDefinition = "VARCHAR(255)")
    var uuid: UUID = UUID.randomUUID(),

    var name: String? = null,
    var password: String? = null,
    var dateCreated: Date? = Date()
) : EntityBase()
