package xyz.tcreopargh.amttd_web.annotation

import java.lang.reflect.Field
import java.util.*

object ExcludeToStringProcessor {

    fun getToString(obj: Any): String {
        val toString = LinkedList<String>()
        getFieldsNotExludeToString(obj).forEach { prop ->
            prop.isAccessible = true
            toString += "${prop.name}=" + prop.get(obj)?.toString()?.trim()
        }
        return "${obj.javaClass.simpleName}=[${toString.joinToString(", ")}]"
    }

    private fun getFieldsNotExludeToString(obj: Any): List<Field> {
        val declaredFields = obj::class.java.declaredFields
        return declaredFields.filterNot { field ->
            isFieldWithExludeToString(field)
        }
    }

    private fun isFieldWithExludeToString(field: Field): Boolean {
        field.annotations.forEach {
            if (it.annotationClass == ExcludeToString::class) {
                return true
            }
        }
        return false
    }

}