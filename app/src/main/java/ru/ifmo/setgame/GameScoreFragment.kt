package ru.ifmo.setgame

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GameScoreFragment : Fragment() {
    private val SCORE_TAG = "SCORE_TAG"

    var score = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = arguments?.getInt(SCORE_TAG) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            score = it.getInt(SCORE_TAG)
        }
        return TextView(activity).apply {
            text = "Your score: $score"
            textSize = 32f
            gravity = Gravity.CENTER
            setOnClickListener {
                var mainIntent = Intent(context, MenuActivity::class.java)
                context.startActivity(mainIntent)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                GameScoreFragment().apply {
                    arguments = Bundle().apply { putInt(SCORE_TAG, param1) }
                }
    }
}
