package ru.ifmo.setgame

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val preferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

        val totalGames = preferences.getInt("TOTAL_GAMES", 0)
        val avgTime = preferences.getLong("TOTAL_TIME", 0).toFloat() / if (totalGames > 0) totalGames else 1
        val avgScore = preferences.getInt("TOTAL_SCORE", 0).toFloat() / if (totalGames > 0) totalGames else 1

        tv_total_games.text = getString(R.string.stat_total_games, totalGames)
        tv_average_time.text = getString(R.string.stat_avg_time, avgTime)
        tv_average_score.text = getString(R.string.stat_avg_score, avgScore)
    }
}
