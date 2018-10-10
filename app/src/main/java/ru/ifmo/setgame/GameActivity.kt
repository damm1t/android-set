package ru.ifmo.setgame

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayout
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val DEFAULT_COLUMNS = 3
    private val DEFAULT_ROWS = 4

    private val SELECTION_COLOR = Color.argb(255,255,128,0)

    private val board_state = MutableList(DEFAULT_COLUMNS * DEFAULT_ROWS){false}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game_grid.rowCount = DEFAULT_ROWS
        game_grid.columnCount = DEFAULT_COLUMNS

        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j,GridLayout.FILL,1f))
                params.width = 0
                params.height = 0

                val image = ImageView(this)
                image.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.diamond_1_stripes_violet, null))
                image.scaleType = ImageView.ScaleType.CENTER_INSIDE

                image.setOnClickListener {
                    val index = i * DEFAULT_COLUMNS + j
                    board_state[index] = !board_state[index]
                    (it as ImageView).setColorFilter(if(board_state[index]) SELECTION_COLOR else Color.TRANSPARENT, PorterDuff.Mode.SCREEN)
                    Log.d("TG", index.toString())
                }

                game_grid.addView(image, params)
            }
        }
    }
}
