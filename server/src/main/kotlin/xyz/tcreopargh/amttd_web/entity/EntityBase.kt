package xyz.tcreopargh.amttd_web.entity

import java.io.Serializable

/**
 * @author TCreopargh
 * A database entity.
 */
abstract class EntityBase<ID : Any> : Serializable {
    abstract fun getId(): ID

    // Final makes subclasses marked with data class not generate their own implementations of these methods
    final override fun hashCode(): Int = getId().hashCode()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return this.getId() == (other as? EntityBase<*>)?.getId()
    }

    final override fun toString(): String = javaClass.name + " id: " + getId()
}