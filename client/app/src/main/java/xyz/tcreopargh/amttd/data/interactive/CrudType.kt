package xyz.tcreopargh.amttd.data.interactive

import java.io.Serializable

@Suppress("unused")
enum class CrudType : Serializable {
    CREATE,
    READ,
    UPDATE,
    DELETE
}
