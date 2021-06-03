package xyz.tcreopargh.amttd_web.entity

import java.io.Serializable

/**
 * @author TCreopargh
 * A database entity.
 */
abstract class EntityBase<ID: Any> : Serializable {
    abstract fun getId(): ID
    override fun hashCode(): Int = getId().hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return this.getId() == (other as? EntityBase<*>)?.getId()
    }
}