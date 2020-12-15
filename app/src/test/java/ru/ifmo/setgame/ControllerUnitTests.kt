package ru.ifmo.setgame

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ControllerUnitTests {

    val mockViewCallBack = Mockito.mock(GameController.ViewCallback::class.java)

    @Test
    fun hasSetsTest() {
        val controller : GameController  = GameController(mockViewCallBack, false)
        while (controller.getDeckSize() > 2){
            assertTrue(controller.hasSets())
            val row = Random.nextInt(4)
            controller.makeMove(intArrayOf(0 + 3 * row, 1 + 3 * row, 2 + 3 * row))
        }
    }

    @Test
    fun checkSets() {
        val controller : GameController  = GameController(mockViewCallBack, false)
        val boardLiveData = controller.getBoardLiveData()
        val row = Random.nextInt(4)
        controller.onSelectCard(0 + 3 * row)
        controller.onSelectCard(1 + 3 * row)
        controller.onSelectCard(2 + 3 * row)
        assertTrue(controller.checkSets())
        assertFalse(controller.checkSets())
        controller.onSelectCard(Random.nextInt(12))
        controller.onSelectCard(Random.nextInt(12))
        assertFalse(controller.checkSets())
    }


}
