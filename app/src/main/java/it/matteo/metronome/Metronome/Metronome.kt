package it.matteo.metronome.Metronome

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import it.matteo.metronome.R
import org.joda.time.DateTime
import java.util.*

class Metronome(private val context: Context) {
    companion object {
        private const val DEFAULT_START_DELAY = 0L
        private const val DEFAULT_TEMPO = 60L
    }

    private val timer = Timer()

    var isRunning: Boolean = false


    fun start(beatPerMinutes: Long?) {
        var tempo = beatPerMinutes?.toDouble()
        if (beatPerMinutes == null) {
            tempo = DEFAULT_TEMPO.toDouble()
        } else if (beatPerMinutes <= 0L)
            error("Please provide a positive number")
        val millisecondsDelay = ((60/ tempo!!) * 1000).toLong()
        timer.scheduleAtFixedRate(
            ClickTask(context),
            DEFAULT_START_DELAY,
            millisecondsDelay
        )
        isRunning = true
    }

    fun stop() {
        timer.cancel()
        isRunning = false
    }

}

private class ClickTask(context: Context) : TimerTask() {

    private val mediaPlayer = MediaPlayer.create(
        context,
        R.raw.click_sound
    )

    override fun run() {
        mediaPlayer.start()
        Log.i("Metronome", "Ehi ${DateTime.now()}")
    }

}