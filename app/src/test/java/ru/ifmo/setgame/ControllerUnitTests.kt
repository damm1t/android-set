package ru.ifmo.setgame

import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import kotlin.random.Random

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
    fun checkSetsTest() {
        val controller : GameController  = GameController(mockViewCallBack, false)
        val boardLiveData = controller.getBoardLiveData()
        val propertiesSize = boardLiveData.value?.get(0)?.properties?.size
        val row = Random.nextInt(4)
        controller.onSelectCard(0 + 3 * row)
        controller.onSelectCard(1 + 3 * row)
        controller.onSelectCard(2 + 3 * row)
        assertTrue(controller.checkSets())
        assertFalse(controller.checkSets())
        controller.onSelectCard(Random.nextInt(12))
        controller.onSelectCard(Random.nextInt(12))
        assertFalse(controller.checkSets())
        controller.onSelectCard(Random.nextInt(12))
        var foundWrong = false
        var wrongSet1 = 0
        var wrongSet2 = 0
        var wrongSet3 = 0
        for (i in 0..11) {
            for (j in 0..11) {
                for (k in 0..11) {
                    wrongSet1 = i
                    wrongSet2 = j
                    wrongSet3 = k
                    if ((wrongSet1 == wrongSet2) || (wrongSet2 == wrongSet3) || (wrongSet1 == wrongSet3) ||
                            controller.isSet(listOf(boardLiveData.value?.get(wrongSet1),
                                    boardLiveData.value?.get(wrongSet2),
                                    boardLiveData.value?.get(wrongSet3)), propertiesSize)) {
                        continue;
                    }
                }
            }
        }
        controller.onSelectCard(wrongSet1)
        controller.onSelectCard(wrongSet2)
        controller.onSelectCard(wrongSet3)
        assertFalse(controller.checkSets())
    }

    @Test
    fun makeMoveTest(){
        val controller : GameController  = GameController(mockViewCallBack, false)
        while (controller.getDeckSize() > 2){
            val row = Random.nextInt(4)
            val oldBoard = (controller.getBoardLiveData().value)?.toList()
            controller.makeMove(intArrayOf(0 + 3 * row, 1 + 3 * row, 2 + 3 * row))
            val newBoard =  (controller.getBoardLiveData().value)?.toList()
            val changed = mutableListOf<Int>()
            for (i in 0..11){
                if (oldBoard != null) {
                    if (!(newBoard?.contains(oldBoard.get(i)))!!)
                        changed.add(i)
                }
            }
            assertEquals(changed.size, 3)
            assertTrue(changed.equals(mutableListOf(0 + 3 * row, 1 + 3 * row, 2 + 3 * row)))
        }
    }
}
