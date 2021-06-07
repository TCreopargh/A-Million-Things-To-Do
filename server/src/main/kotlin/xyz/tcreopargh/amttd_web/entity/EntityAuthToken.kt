package xyz.tcreopargh.amttd_web.entity

import xyz.tcreopargh.amttd_web.util.generateToken
import java.util.*
import javax.persistence.*

/**
 * @author TCreopargh
 *
 * Auth token is used to log in
 *
 * Unlike password, this is stored on the client and is used to log in automatically
 *
 * tokens have a set amount of lifespan, after which the token expires and becomes invalid.
 */
@Entity
@Table(name = "auth_token", indexes = [Index(name = "user_index", columnList = "user_uuid")])
data class EntityAuthToken(

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid", columnDefinition = "varchar(64)", updatable = false)
    var user: EntityUser? = null,

    @Id
    @Column(name = "token", columnDefinition = "VARCHAR(64)", nullable = false, updatable = false)
    var token: String = generateToken(),

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    var timeCreated: Calendar = Calendar.getInstance()

) : EntityBase<String>() {

    override fun getId(): String = token

    companion object {
        const val LIFESPAN: Long = 86400000L * 7
    }

    fun isExpired(): Boolean {
        val period = Calendar.getInstance().timeInMillis - timeCreated.timeInMillis
        return period > LIFESPAN
    }

    fun isValid() = !isExpired()
}
