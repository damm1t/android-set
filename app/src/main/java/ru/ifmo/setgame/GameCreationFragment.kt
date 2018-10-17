package ru.ifmo.setgame

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GameCreationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            TextView(activity).apply {
                text = "Default deck"
                textSize = 32f
                gravity = Gravity.CENTER
                setOnClickListener { (activity as GameInterface).startGame() }
            }
}