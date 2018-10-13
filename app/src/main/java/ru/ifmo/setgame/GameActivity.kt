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

    private val board = mutableListOf<PlayingCard>()
    private val images = mutableListOf<ImageView>()

    private val deck = loadDefaultDeck()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game_grid.rowCount = DEFAULT_ROWS
        game_grid.columnCount = DEFAULT_COLUMNS

        deck.shuffle()
        for (i in 0 until DEFAULT_ROWS) {
            for (j in 0 until DEFAULT_COLUMNS) {
                val params = GridLayout.LayoutParams(GridLayout.spec(i, GridLayout.FILL, 1f), GridLayout.spec(j,GridLayout.FILL,1f))
                params.width = 0
                params.height = 0

                val image = ImageView(this)
                image.setImageDrawable(ResourcesCompat.getDrawable(resources, deck[0].drawable_id, null))
                image.scaleType = ImageView.ScaleType.CENTER_INSIDE

                image.setOnClickListener {
                    val index = i * DEFAULT_COLUMNS + j
                    board[index].selected = !board[index].selected
                    (it as ImageView).setColorFilter(if(board[index].selected) SELECTION_COLOR else Color.TRANSPARENT, PorterDuff.Mode.SCREEN)
                    Log.d("TG", index.toString())
                }

                images.add(image)
                board.add(deck[0])
                deck.removeAt(0)

                game_grid.addView(image, params)
            }
        }
    }
}
