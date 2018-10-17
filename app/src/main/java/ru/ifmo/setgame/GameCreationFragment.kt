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
            TextView(activity).let { tv ->
                tv.text = "Default deck"
                tv.textSize = 32f
                tv.gravity = Gravity.CENTER
                tv.setOnClickListener { (activity as GameInterface).startGame() }
                tv }
}