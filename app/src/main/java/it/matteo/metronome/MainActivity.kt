package it.matteo.metronome

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import it.matteo.metronome.databinding.ActivityMainBinding
import it.matteo.metronome.service.BackgroundService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var backgroundIntent: Intent
    private var selectedTempo: Long? = null
    private lateinit var startMetronome: Button


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initializePicker()
        backgroundIntent = Intent(applicationContext, BackgroundService::class.java).putExtra(
            "tempo",
            selectedTempo
        )

        startMetronome = binding.startMetronome

        binding.startMetronome.setOnClickListener {
            startMetronome()
        }

        binding.stopMetronome.setOnClickListener {
            stopMetronome()
        }

    }

    private fun stopMetronome() {
        binding.startMetronome.isEnabled = true
        binding.stopMetronome.isEnabled = false
        stopService(backgroundIntent)
        Log.i("Metronome", "Stop")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startMetronome() {
        if (checkPermission()) {
            backgroundIntent.putExtra("tempo", selectedTempo)
            startService(backgroundIntent)
            binding.startMetronome.isEnabled = false
            binding.stopMetronome.isEnabled = true
            Log.i("Metronome", "Start")
        }
    }

    private fun initializePicker() {
        val tempoPicker = binding.tempoPicker
        tempoPicker.maxValue = 200
        tempoPicker.minValue = 40
        selectedTempo = tempoPicker.value.toLong()
        tempoPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            picker.value = newVal
            stopMetronome()
            selectedTempo = newVal.toLong()
            Log.i("Metronome", "From $oldVal to $newVal")
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkPermission(): Boolean {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE), 1
        )
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }
}