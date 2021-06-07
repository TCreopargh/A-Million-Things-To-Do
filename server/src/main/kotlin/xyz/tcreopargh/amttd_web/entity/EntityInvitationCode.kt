package xyz.tcreopargh.amttd_web.entity

import xyz.tcreopargh.amttd_web.annotation.ExcludeToString
import xyz.tcreopargh.amttd_web.util.generateInvitationCode
import java.util.*
import javax.persistence.*

/**
 * @author TCreopargh
 *
 * An invitation code is used to join a workgroup.
 *
 * Workgroup members can generate invitation codes to invite other people into the group.
 *
 * Like tokens, this also has a lifespan, but can be specified by the client.
 */
@Entity
@Table(name = "invitation_code")
data class EntityInvitationCode(

    @Id
    @Column(name = "token", columnDefinition = "VARCHAR(8)", nullable = false, updatable = false)
    var invitationCode: String = generateInvitationCode(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", columnDefinition = "varchar(64)", updatable = false)
    @ExcludeToString
    var user: EntityUser? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    @ExcludeToString
    var workGroup: EntityWorkGroup? = null,

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    var timeCreated: Calendar = Calendar.getInstance(),

    var expirationTimeInDays: Int = 7
) : EntityBase<String>() {

    val expirationTimeInMillis
        get() = expirationTimeInDays * 86400000

    fun isValid() = !isExpired()

    fun isExpired(): Boolean {
        val period = Calendar.getInstance().timeInMillis - timeCreated.timeInMillis
        return period > expirationTimeInMillis
    }

    override fun getId(): String = invitationCode
}