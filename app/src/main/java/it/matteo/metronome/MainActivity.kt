package it.matteo.metronome

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import it.matteo.metronome.databinding.ActivityMainBinding
import it.matteo.metronome.service.BackgroundService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var backgroundIntent: Intent


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backgroundIntent = Intent(applicationContext, BackgroundService::class.java)

        binding.startMetronome.setOnClickListener {
            if (checkPermission()) {
                startService(backgroundIntent)
                binding.startMetronome.isEnabled = false
                binding.stopMetronome.isEnabled = true
                Log.i("Metronome", "Start")
            }
        }

        binding.stopMetronome.setOnClickListener {
            binding.startMetronome.isEnabled = true
            binding.stopMetronome.isEnabled = false
            stopService(backgroundIntent)
            Log.i("Metronome", "Stop")
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkPermission(): Boolean {
        ActivityCompat.requestPermissions(this,
        arrayOf(android.Manifest.permission.FOREGROUND_SERVICE), 1)
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }
}