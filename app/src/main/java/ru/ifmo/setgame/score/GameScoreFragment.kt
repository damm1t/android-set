package ru.ifmo.setgame.score

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_score.view.*
import ru.ifmo.setgame.*
import ru.ifmo.setgame.di.ComponentHelper
import ru.ifmo.setgame.di.GameComponent

class GameScoreFragment : androidx.fragment.app.Fragment() {
    private val TITLE_TAG = "TITLE_TAG"
    private val TIME_TAG = "TIME_TAG"
    private val PLAYERS_TAG = "PLAYERS_TAG"
    private val SCORES_TAG = "SCORES_TAG"


    private var title = ""
    private var time = 0L
    private var playersArray = arrayOf<String>()
    private var scoresArray = intArrayOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_score, container, false)

        val gameComponent = ComponentHelper.getGameComponent(activity!!)
        val scoreViewModel = gameComponent.scoreViewModel()

        scoreViewModel.getScoreLiveData().observe(viewLifecycleOwner) { scoreInfo ->
            displayScore(scoreInfo)
        }

        return view
    }

    private fun displayScore(scoreInfo: ScoreInfo) {
        title = scoreInfo.title
        time = scoreInfo.time
        playersArray = scoreInfo.players
        scoresArray = scoreInfo.scores

        view!!.tv_score_title.text = title
        view!!.tv_score_time.text = getString(R.string.game_time, time)

        var thisScore = 0

        for (i in 0 until playersArray.size) {
            val tv = TextView(context)
            tv.text = getString(R.string.player_score, playersArray[i], scoresArray[i])
            tv.textSize = 16f
            view!!.ll_score_list.addView(tv)

            if (playersArray[i] == getString(R.string.player_you)) {
                thisScore = scoresArray[i]
            }
        }

        val preferences = activity!!.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit()
                .putInt(STAT_TOTAL_GAMES, preferences.getInt(STAT_TOTAL_GAMES, 0) + 1)
                .putLong(STAT_TOTAL_TIME, preferences.getLong(STAT_TOTAL_TIME, 0) + time)
                .putInt(STAT_TOTAL_SCORE, preferences.getInt(STAT_TOTAL_SCORE, 0) + thisScore)
                .apply()
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameScoreFragment()
    }
}
