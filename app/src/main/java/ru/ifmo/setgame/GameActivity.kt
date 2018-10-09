package ru.ifmo.setgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    val DEFAULT_COLUMNS = 3
    val DEFAULT_ROWS = 4


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

                game_grid.addView(tv, params)
            }
        }
    }
}
