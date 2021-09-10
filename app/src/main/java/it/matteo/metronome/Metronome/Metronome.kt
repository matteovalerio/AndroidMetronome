package it.matteo.metronome.Metronome

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import it.matteo.metronome.R
import org.joda.time.DateTime
import java.util.*

class Metronome(val context: Context) {
    companion object {
        private const val DEFAULT_START_DELAY = 0L
    }

    private val timer = Timer()


    fun launch(beatPerMinutes: Long) {
        if (beatPerMinutes <= 0L)
            error("Please provide a positive number")
        val millisecondsDelay = ((beatPerMinutes/60)*1000)
        timer.scheduleAtFixedRate(
            ClickTask(context),
            DEFAULT_START_DELAY,
            millisecondsDelay
        )
    }

    fun stop() {
        timer.cancel()
    }

}

private class ClickTask(context: Context): TimerTask() {

    private val mediaPlayer = MediaPlayer.create(
        context,
        R.raw.click_sound
    )

    override fun run() {
        mediaPlayer.start()
        Log.i("Metronome", "Ehi ${DateTime.now()}")
    }

}