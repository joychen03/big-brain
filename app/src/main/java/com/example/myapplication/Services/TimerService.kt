package com.example.myapplication.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Timer
import java.util.TimerTask

class TimerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val timer = Timer()
    private var started = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(TIMER_EXTRA, 0.0);
        if(!started){
            timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        }
        started = true
        return START_NOT_STICKY
    }

    private inner class TimeTask(private var time: Double) : TimerTask(){
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIMER_EXTRA, time)
            sendBroadcast(intent)
        }

    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }


    companion object{
        const val TIMER_UPDATED = "timerUpdated"
        const val TIMER_EXTRA = "timerExtra"
    }

}