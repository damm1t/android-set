package ru.ifmo.setgame.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ConnectorTest {
    private val testScope = TestCoroutineScope()

    private val inputBuffer = ByteArray(1024)
    private val inputStream = ByteArrayInputStream(inputBuffer)

    private val outputStream = ByteArrayOutputStream(1024)

    private val socket = mock(Socket::class.java).also {
        `when`(it.getInputStream()).thenReturn(inputStream)
        `when`(it.getOutputStream()).thenReturn(outputStream)
    }

    private val connector = Connector(socket, testScope)

    @Test
    fun foo() {
        testScope.runBlockingTest {
            connector.connect()
            connector.leaveLobby()
        }

        val request = String(outputStream.toByteArray())

        assertTrue(request.contains("leave_lobby"))
    }
}