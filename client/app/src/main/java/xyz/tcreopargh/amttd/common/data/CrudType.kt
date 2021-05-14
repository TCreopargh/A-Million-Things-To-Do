package xyz.tcreopargh.amttd.common.data

import java.io.Serializable

@Suppress("unused")
enum class CrudType : Serializable {
    CREATE,
    READ,
    UPDATE,
    DELETE
}
