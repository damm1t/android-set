package ru.ifmo.setgame

class PlayingCard(val drawable_id : Int, val properties: IntArray) {
    var selected = false
}

fun loadDefaultDeck() : MutableList<PlayingCard> {
    val drawableIds = intArrayOf(R.drawable.diamond_1_empty_green,
            R.drawable.diamond_1_empty_red,
            R.drawable.diamond_1_empty_violet,
            R.drawable.diamond_1_full_green,
            R.drawable.diamond_1_full_red,
            R.drawable.diamond_1_full_violet,
            R.drawable.diamond_1_stripes_green,
            R.drawable.diamond_1_stripes_red,
            R.drawable.diamond_1_stripes_violet,

            R.drawable.diamond_2_empty_green,
            R.drawable.diamond_2_empty_red,
            R.drawable.diamond_2_empty_violet,
            R.drawable.diamond_2_full_green,
            R.drawable.diamond_2_full_red,
            R.drawable.diamond_2_full_violet,
            R.drawable.diamond_2_stripes_green,
            R.drawable.diamond_2_stripes_red,
            R.drawable.diamond_2_stripes_violet,

            R.drawable.diamond_3_empty_green,
            R.drawable.diamond_3_empty_red,
            R.drawable.diamond_3_empty_violet,
            R.drawable.diamond_3_full_green,
            R.drawable.diamond_3_full_red,
            R.drawable.diamond_3_full_violet,
            R.drawable.diamond_3_stripes_green,
            R.drawable.diamond_3_stripes_red,
            R.drawable.diamond_3_stripes_violet)

    val result = mutableListOf<PlayingCard>()

    for (number in 0..3)
        for (fill in 0..3)
            for (color in 0..3)
                result.add(PlayingCard(drawableIds[number * 9 + fill * 3 + color], intArrayOf(number, fill, color)))

    return result
}