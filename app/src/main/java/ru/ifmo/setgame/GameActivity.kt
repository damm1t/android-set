package ru.ifmo.setgame

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4

    private val board_state = MutableList(DEFAULT_COLUMNS * DEFAULT_ROWS){false}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game_grid.rowCount = DEFAULT_ROWS
        game_grid.columnCount = DEFAULT_COLUMNS

        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j,GridLayout.FILL,1f))

                val tv = TextView(this)
                tv.text = "$i x $j"
                tv.gravity = Gravity.CENTER

                tv.setOnClickListener {
                    val index = i * DEFAULT_COLUMNS + j
                    board_state[index] = !board_state[index]
                    it.setBackgroundColor(if(board_state[index]) Color.RED else Color.WHITE)
                }

                game_grid.addView(tv, params)
            }
        }
    }
}
