package xyz.tcreopargh.amttd_web.api.data

import java.io.Serializable

/**
 * @author TCreopargh
 *
 * Specify the operation of a CRUD action.
 *
 * This is used to generify all kinds of requests to a certain entity.
 */
@Suppress("unused")
enum class CrudType : Serializable {
    CREATE,
    READ,
    UPDATE,
    DELETE
}
