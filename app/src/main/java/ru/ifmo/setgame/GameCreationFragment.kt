package ru.ifmo.setgame

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GameCreationFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            TextView(activity).apply {
                text = getString(R.string.default_deck)
                textSize = 32f
                gravity = Gravity.CENTER
                //setOnClickListener { (activity as GameInterface).startGame() }
            }
}