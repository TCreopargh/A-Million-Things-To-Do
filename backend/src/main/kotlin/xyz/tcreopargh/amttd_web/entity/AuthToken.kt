package xyz.tcreopargh.amttd_web.entity

import xyz.tcreopargh.amttd_web.util.generateToken
import java.time.Duration
import java.time.ZoneId
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "auth_token", indexes = [Index(name = "user_index", columnList = "user_uuid")])
data class AuthToken(

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    var user: EntityUser? = null,

    @Id
    @Column(name = "token", columnDefinition = "VARCHAR(128)", nullable = false, updatable = false)
    var token: String = generateToken(),

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    var timeCreated: Calendar = Calendar.getInstance()

) : EntityBase() {
    companion object {
        val LIFESPAN: Duration = Duration.ofDays(14)
    }

    fun isExpired(): Boolean {
        val start = timeCreated.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val end = timeCreated.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val period = Duration.between(start, end)
        return period > LIFESPAN
    }

    fun isValid() = !isExpired()

    override fun equals(other: Any?): Boolean = this.token == (other as? AuthToken)?.token

    override fun hashCode(): Int = token.hashCode()
}
