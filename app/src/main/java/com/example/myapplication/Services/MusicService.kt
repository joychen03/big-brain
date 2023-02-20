package com.example.myapplication.Services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.annotation.Nullable
import com.example.myapplication.R

class MusicService : Service() {

    var mediaPlayer = MediaPlayer()
    var isPlaying = false;

    @Nullable
    @Override
    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    @Override
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!isPlaying){
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
            mediaPlayer.isLooping = true
            mediaPlayer.start()
            isPlaying = true
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer = MediaPlayer();
    }

}