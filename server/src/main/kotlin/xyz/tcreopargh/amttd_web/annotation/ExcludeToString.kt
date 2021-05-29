package xyz.tcreopargh.amttd_web.annotation

/**
 * Exclude a field from `toString()` method to prevent infinite recursive calls.
 *
 * You must call [ExcludeToStringProcessor.getToString] in the class's `toString()` method.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcludeToString
