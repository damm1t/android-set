package ru.ifmo.setgame

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GameScoreFragment:Fragment() {
    var score = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        score = savedInstanceState?.getInt("SCORE") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TextView(activity).apply {
            text = "Your score: $score"
            textSize = 32f
            gravity = Gravity.CENTER
        }

    companion object {
        @JvmStatic
        fun newInstance(param1 : Int) =
                GameCreationFragment().apply {
                    arguments = Bundle().apply { putInt("SCORE", param1) }
                }
    }
}
