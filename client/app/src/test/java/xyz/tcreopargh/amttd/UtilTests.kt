package xyz.tcreopargh.amttd

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.tcreopargh.amttd.util.getGroupInvitationCode
import xyz.tcreopargh.amttd.util.getGroupUri
import xyz.tcreopargh.amttd.util.withPath
import java.net.URL

class UtilTests {
    @Test
    fun testAmttdUri() {
        assertEquals(getGroupInvitationCode("amttd://group/someInvitationCode"), "someInvitationCode")
        assertEquals(getGroupUri("someInvitationCode"), "amttd://group/someInvitationCode")
    }

    @Test
    fun testUrlWithPath() {
        val url = URL("https://google.com")
        assertEquals(url.withPath("mail"), URL("https://google.com/mail"))
        assertEquals(url.withPath("/mail"), URL("https://google.com/mail"))
    }
}