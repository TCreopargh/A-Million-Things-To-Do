package xyz.tcreopargh.amttd_web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import xyz.tcreopargh.amttd_web.util.nextString
import xyz.tcreopargh.amttd_web.util.toJsonString
import kotlin.random.Random

@SpringBootTest
class UtilTests {

    @Test
    fun testGenerateRandomString() {
        assertTrue(Random.nextString(100).matches("[a-zA-Z0-9]{100}".toRegex()))
        assertTrue(Random.nextString(50, "abc").matches("[abc]{50}".toRegex()))
    }

    @Test
    fun testJsonSerialization() {
        assertEquals(
            """{"a":2,"b":"abc"}""",
            TestObject().toJsonString()
        )
    }

    data class TestObject(
        var a: Int = 2,
        var b: String = "abc"
    )
}