package ru.ifmo.setgame

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.card_frame.view.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import ru.ifmo.setgame.R.drawable.card_frame_drawable
import ru.ifmo.setgame.R.layout.card_frame
import ru.ifmo.setgame.R.layout.fragment_game
import ru.ifmo.setgame.di.ComponentHelper

class GameFragment : androidx.fragment.app.Fragment(), GameController.ViewCallback {

    private val images = mutableListOf<FrameLayout>()
    private lateinit var controller: GameController
    private lateinit var gameView: View
    private lateinit var viewModel : GameViewModel
    private lateinit var gameNavigation: GameNavigation
    private var allowCustomCards = false

    override fun onStart() {
        super.onStart()
        controller.setViewCallback(this)
        if (controller.isMultiplayer) {
            controller.setConnector((activity as GameActivity).connector)
        }
        if (controller.isComputer) {
            controller.scheduleRate()
        }
    }

    override fun onStop() {
        if (controller.isMultiplayer) {
            controller.removeConnector()
        }
        if (controller.isComputer) {
            controller.stopRate()
        }
        controller.removeViewCallback()
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val gameComponent = ComponentHelper.getGameComponent(activity!!)

        controller = gameComponent.gameController()
        viewModel = gameComponent.gameViewModel()
        gameNavigation = gameComponent.gameNavigation()

        gameView = inflater.inflate(fragment_game, container, false)

        gameView.game_grid.rowCount = controller.rowCount
        gameView.game_grid.columnCount = controller.columnCount

        for (i in 0 until controller.rowCount) {
            for (j in 0 until controller.columnCount) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j, GridLayout.FILL, 1f))
                params.width = 0
                params.height = 0

                val image = inflater.inflate(card_frame, gameView.game_grid, false) as FrameLayout
                image.card_frame.setImageDrawable(ResourcesCompat.getDrawable(resources, card_frame_drawable, null))

                image.setOnClickListener {
                    val index = i * controller.columnCount + j
                    controller.onSelectCard(index)

                    it.card_frame.visibility = if (controller.getCard(index)?.selected!!) ImageView.VISIBLE else ImageView.GONE

                    Log.d("TG", index.toString())
                    controller.checkSets()
                }

                images.add(image)

                gameView.game_grid.addView(image, params)
            }
        }


        arguments?.apply {
            controller.timerGlobalStart = System.currentTimeMillis()

            if (controller.isComputer) {
                controller.setTimer()
            }

            if (controller.isMultiplayer) {
                //todo investigate where we need to set connector
                controller.setConnector((activity as GameActivity).connector)
            }
        }

        allowCustomCards = activity!!.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(PREFERENCE_CUSTOM_CARDS, false)

        viewModel.getBoard().observe(viewLifecycleOwner, Observer { board ->
            onBoardUpdated(board)
        })

        return gameView
    }

    override fun vibrate(secs : Int) {
        val vibrator : Vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()
        val milliseconds = 1000L * secs

        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                milliseconds,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameFragment()
    }

    private fun onBoardUpdated(board: List<PlayingCard>) {
        for (i in 0 until 12) {
            images[i].card_image.setImageDrawable(board[i].getDrawable(resources, allowCustomCards))
            images[i].card_frame.visibility = ImageView.GONE
        }
    }

    override fun showScore(againstComputer: Boolean, time: Long, scores: IntArray) {
        val title =
                if (againstComputer) getString(R.string.singleplayer_over)
                else getString(R.string.training_over)

        val playersArray = if (againstComputer) {
            arrayOf(getString(R.string.player_you),
                    getString(R.string.player_computer))
        } else {
            arrayOf(getString(R.string.player_you))
        }

        gameNavigation.showScore(title, time, playersArray, scores)
    }
}
