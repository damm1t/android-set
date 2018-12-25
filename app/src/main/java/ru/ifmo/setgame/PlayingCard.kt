package ru.ifmo.setgame

import android.Manifest
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import ru.ifmo.setgame.R.drawable.*
import java.io.File
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import androidx.core.app.ActivityCompat


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

class PlayingCard(val properties: IntArray) {
    fun getDrawable(resources: Resources): Drawable {
        val filename = "card_" + properties[0].toString() + properties[1].toString() + properties[2].toString() + properties[3].toString() + ".png"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val cardsDir = File(path, "set-cards")
        val cardFile = File(cardsDir, filename)

        // TODO request premissions in settings
        val drawable = Drawable.createFromPath(cardFile.canonicalPath)

        if (drawable != null) {
            return drawable
        } else {
            val cardId = properties[0] * 27 + properties[1] * 9 + properties[2] * 3 + properties[3]
            return ResourcesCompat.getDrawable(resources, drawableIds[cardId], null)!!
        }
    }

    var selected = false
}

fun loadDefaultDeck(): MutableList<PlayingCard> {


    val result = mutableListOf<PlayingCard>()
    for (shape in 0..2)
        for (number in 0..2)
            for (fill in 0..2)
                for (color in 0..2)
                    result.add(PlayingCard(intArrayOf(shape, number, fill, color)))

    return result
}