package xyz.tcreopargh.amttd_web.common.data

import java.io.Serializable

/**
 * Specify the type of an CRUD action (Usually used in request bodies)
 */
@Suppress("unused")
enum class CrudType : Serializable {
    CREATE,
    READ,
    UPDATE,
    DELETE
}
