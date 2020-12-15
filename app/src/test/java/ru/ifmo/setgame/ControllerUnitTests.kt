package ru.ifmo.setgame

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
class ControllerUnitTests {

    private val mockViewCallBack: GameController.ViewCallback = Mockito.mock(GameController.ViewCallback::class.java)

    @Test
    fun hasSetsTest() {
        val controller = GameController(false)
        while (controller.getDeckSize() > 2) {
            assertTrue(controller.hasSets())
            val row = Random.nextInt(4)
            controller.makeMove(intArrayOf(0 + 3 * row, 1 + 3 * row, 2 + 3 * row))
        }
    }

    @Test
    fun checkSetsTest() {
        val controller = GameController(false)
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
                        continue
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
        val controller : GameController  = GameController(false)
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

    @Test
    fun createBoardFromJsonTest(){
        val controller : GameController  = GameController(false)
        val json : String = "{\"game_id\": 337, \"in_game\": [6, 89], \"board\": {\"0\": [0, 2, 1, 1], \"1\": [1, 0, 1, 2], \"2\": [0, 2, 0, 0], \"3\": [1, 0, 2, 1], \"4\": [1, 2, 0, 2], \"5\": [0, 2, 1, 2], \"6\": [2, 0, 2, 0], \"7\": [2, 0, 1, 0], \"8\": [1, 2, 2, 1], \"9\": [2, 1, 0, 1], \"10\": [2, 1, 1, 0], \"11\": [0, 1, 1, 0]}}"
        controller.createBoardFromJSON(json)
        val board = controller.getBoardLiveData().value
        if (board != null) {
            val intArray = intArrayOf(0, 2, 1, 1)
            assertTrue(Arrays.equals(board.get(0).properties, intArrayOf(0, 2, 1, 1)))
            assertTrue(Arrays.equals((board.get(1).properties),  intArrayOf(1, 0, 1, 2)))
            assertTrue(Arrays.equals(board.get(2).properties,  intArrayOf(0, 2, 0, 0)))
            assertTrue(Arrays.equals(board.get(3).properties,  intArrayOf(1, 0, 2, 1)))
            assertTrue(Arrays.equals(board.get(4).properties,  intArrayOf(1, 2, 0, 2)))
            assertTrue(Arrays.equals(board.get(5).properties,  intArrayOf(0, 2, 1, 2)))
            assertTrue(Arrays.equals(board.get(6).properties,  intArrayOf(2, 0, 2, 0)))
            assertTrue(Arrays.equals(board.get(7).properties,  intArrayOf(2, 0, 1, 0)))
            assertTrue(Arrays.equals(board.get(8).properties,  intArrayOf(1, 2, 2, 1)))
            assertTrue(Arrays.equals(board.get(9).properties,  intArrayOf(2, 1, 0, 1)))
            assertTrue(Arrays.equals(board.get(10).properties,  intArrayOf(2, 1, 1, 0)))
            assertTrue(Arrays.equals(board.get(11).properties,  intArrayOf(0, 1, 1, 0)))
        }
    }
}
