package ru.ifmo.setgame

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_score.view.*

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

        arguments?.let {
            title = it.getString(TITLE_TAG)!!
            time = it.getLong(TIME_TAG)
            playersArray = it.getStringArray(PLAYERS_TAG)!!
            scoresArray = it.getIntArray(SCORES_TAG)!!
        }

        view.tv_score_title.text = title
        view.tv_score_time.text = getString(R.string.game_time, time)

        var thisScore = 0

        for (i in 0 until playersArray.size) {
            val tv = TextView(context)
            tv.text = getString(R.string.player_score, playersArray[i], scoresArray[i])
            tv.textSize = 16f
            view.ll_score_list.addView(tv)

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

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, time: Long, players: Array<String>, scores: IntArray) =
                GameScoreFragment().apply {
                    arguments = Bundle().apply {
                        putString(TITLE_TAG, title)
                        putLong(TIME_TAG, time)
                        putStringArray(PLAYERS_TAG, players)
                        putIntArray(SCORES_TAG, scores)
                    }
                }
    }
}
