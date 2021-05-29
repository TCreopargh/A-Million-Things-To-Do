package xyz.tcreopargh.amttd_web.annotation

/**
 * Mark a controller class or a method in controller so that the user must be logged in to send request.
 *
 * You should also use [xyz.tcreopargh.amttd_web.controller.ControllerBase.verifyWorkgroup] or similar methods to verify
 * permission of operating certain entities.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginRequired