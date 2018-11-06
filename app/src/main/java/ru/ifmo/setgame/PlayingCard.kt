package ru.ifmo.setgame

import ru.ifmo.setgame.R.drawable.*

class PlayingCard(val drawable_id: Int, val properties: IntArray) {
    var selected = false
}

fun loadDefaultDeck(): MutableList<PlayingCard> {
    val drawableIds = intArrayOf(

            diamond_1_empty_green,
            diamond_1_empty_red,
            diamond_1_empty_violet,
            diamond_1_full_green,
            diamond_1_full_red,
            diamond_1_full_violet,
            diamond_1_stripes_green,
            diamond_1_stripes_red,
            diamond_1_stripes_violet,

            diamond_2_empty_green,
            diamond_2_empty_red,
            diamond_2_empty_violet,
            diamond_2_full_green,
            diamond_2_full_red,
            diamond_2_full_violet,
            diamond_2_stripes_green,
            diamond_2_stripes_red,
            diamond_2_stripes_violet,

            diamond_3_empty_green,
            diamond_3_empty_red,
            diamond_3_empty_violet,
            diamond_3_full_green,
            diamond_3_full_red,
            diamond_3_full_violet,
            diamond_3_stripes_green,
            diamond_3_stripes_red,
            diamond_3_stripes_violet,

            oval_1_empty_green,
            oval_1_empty_red,
            oval_1_empty_violet,
            oval_1_full_green,
            oval_1_full_red,
            oval_1_full_violet,
            oval_1_stripes_green,
            oval_1_stripes_red,
            oval_1_stripes_violet,

            oval_2_empty_green,
            oval_2_empty_red,
            oval_2_empty_violet,
            oval_2_full_green,
            oval_2_full_red,
            oval_2_full_violet,
            oval_2_stripes_green,
            oval_2_stripes_red,
            oval_2_stripes_violet,

            oval_3_empty_green,
            oval_3_empty_red,
            oval_3_empty_violet,
            oval_3_full_green,
            oval_3_full_red,
            oval_3_full_violet,
            oval_3_stripes_green,
            oval_3_stripes_red,
            oval_3_stripes_violet,

            wave_1_empty_green,
            wave_1_empty_red,
            wave_1_empty_violet,
            wave_1_full_green,
            wave_1_full_red,
            wave_1_full_violet,
            wave_1_stripes_green,
            wave_1_stripes_red,
            wave_1_stripes_violet,

            wave_2_empty_green,
            wave_2_empty_red,
            wave_2_empty_violet,
            wave_2_full_green,
            wave_2_full_red,
            wave_2_full_violet,
            wave_2_stripes_green,
            wave_2_stripes_red,
            wave_2_stripes_violet,

            wave_3_empty_green,
            wave_3_empty_red,
            wave_3_empty_violet,
            wave_3_full_green,
            wave_3_full_red,
            wave_3_full_violet,
            wave_3_stripes_green,
            wave_3_stripes_red,
            wave_3_stripes_violet
    )

    var result = mutableListOf<PlayingCard>()
    for (shape in 0..2)
        for (number in 0..2)
            for (fill in 0..2)
                for (color in 0..2)
                    result.add(PlayingCard(drawableIds[shape * 27 + number * 9 + fill * 3 + color], intArrayOf(shape, number, fill, color)))

    return result
}