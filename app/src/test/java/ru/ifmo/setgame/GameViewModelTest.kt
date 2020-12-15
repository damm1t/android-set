package ru.ifmo.setgame

import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameViewModelTest {
    private val liveData = MutableLiveData<MutableList<PlayingCard>>()

    private val controller = mock(GameController::class.java).also {
        `when`(it.getBoardLiveData()).thenReturn(liveData)
    }

    private val viewModel = GameViewModel(controller)

    @Test
    fun `when controller is updated view model updates`() {
        val playingCard = PlayingCard(intArrayOf())
        val sentBoard = mutableListOf<PlayingCard>(playingCard)

        var receivedBoard: List<PlayingCard>? = null
        viewModel.getBoard().observeForever { receivedBoard = it }

        liveData.value = sentBoard

        assertNotNull(receivedBoard)
        assertTrue(receivedBoard!!.isNotEmpty())
        assertEquals(playingCard, receivedBoard!![0])
    }
}
