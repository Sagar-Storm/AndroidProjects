package com.example.sagar.videoapplication;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaplayer ;
    AudioManager audioManager ;

    public void play(View view) {
        mediaplayer.start();
    }

    public void pause(View view) {
        mediaplayer.pause() ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaplayer = MediaPlayer.create(this,R.raw.sec10) ;
        audioManager  =(AudioManager) getSystemService(Context.AUDIO_SERVICE) ;
        int maxvolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ;
        int curvolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;


        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar) ;
        seekbar.setMax(maxvolume) ;
        seekbar.setProgress(curvolume) ;

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.i("this is the progresse", Integer.toString(progress)) ;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar scrubber = (SeekBar) findViewById(R.id.scrubber) ;
        scrubber.setMax(mediaplayer.getDuration())  ;

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                scrubber.setProgress(mediaplayer.getCurrentPosition());
            }
        }, 0, 100) ;

        scrubber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Toast.makeText(MainActivity.this,"progress is " + i, Toast.LENGTH_LONG).show();
                mediaplayer.seekTo(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
