package ru.ifmo.setgame

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stats.*

const val STAT_TOTAL_GAMES = "TOTAL_GAMES"
const val STAT_TOTAL_TIME = "TOTAL_TIME"
const val STAT_TOTAL_SCORE = "TOTAL_SCORE"

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val totalGames = preferences.getInt(STAT_TOTAL_GAMES, 0)
        val avgTime = preferences.getLong(STAT_TOTAL_TIME, 0).toFloat() / if (totalGames > 0) totalGames else 1
        val avgScore = preferences.getInt(STAT_TOTAL_SCORE, 0).toFloat() / if (totalGames > 0) totalGames else 1

        tv_total_games.text = getString(R.string.stat_total_games, totalGames)
        tv_average_time.text = getString(R.string.stat_avg_time, avgTime)
        tv_average_score.text = getString(R.string.stat_avg_score, avgScore)
    }
}
