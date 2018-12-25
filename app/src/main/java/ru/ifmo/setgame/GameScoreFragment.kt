package ru.ifmo.setgame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
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


    var title = ""
    var time = 0L
    var playersArray = arrayOf<String>()
    var scoresArray = intArrayOf()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_score, container, false)

        arguments?.let {
            title = it.getString(TITLE_TAG)!!
            time = it.getLong(TIME_TAG)
            playersArray = it.getStringArray(PLAYERS_TAG)!!
            scoresArray = it.getIntArray(SCORES_TAG)!!
        }

        view.tv_score_title.text = title
        view.tv_score_time.text = "Game time: ${time.toString()} seconds"

        for (i in 0 until playersArray.size) {
            val tv = TextView(context)
            tv.text = "${playersArray[i]}: ${scoresArray[i]}"
            tv.textSize = 16f
            view.ll_score_list.addView(tv)
        }

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
