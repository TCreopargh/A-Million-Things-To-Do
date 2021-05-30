package xyz.tcreopargh.amttd_web.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

@Suppress("ArrayInDataClass")
@Entity
@Table(name = "user_avatar")
data class EntityUserAvatar(

    @Id
    @Column(
        name = "avatar_id",
        insertable = false,
        updatable = false,
        nullable = false,
        columnDefinition = "varchar(64)"
    )
    @Type(type = "uuid-char")
    private var avatarId: UUID = UUID.randomUUID(),

    @OneToOne(mappedBy = "avatar", optional = true)
    private var user: EntityUser? = null,

    @Lob
    @Basic(fetch = FetchType.LAZY)
    var image: ByteArray? = null
) : IEntity