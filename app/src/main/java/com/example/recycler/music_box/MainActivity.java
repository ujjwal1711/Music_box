package com.example.recycler.music_box;

import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
     private MediaPlayer mediaPlayer;
    private ImageView artist;
    private TextView lefttime;
    private TextView righttime;
    private SeekBar seek;
    private Button prev;
    private  Button play;
    private Button next;
    private  Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        seek.setMax(mediaPlayer.getDuration());
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mediaPlayer.seekTo(progress);
                    int currentpos =mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    lefttime.setText(getMusicDuration(currentpos));
                    righttime.setText(getMusicDuration(duration-currentpos));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void setupUI()
    {
        artist =(ImageView) findViewById(R.id.imageView);
        lefttime =(TextView) findViewById(R.id.lefttimer);
        righttime=(TextView) findViewById(R.id.textView4);
        seek=(SeekBar) findViewById(R.id.seekBar);
        prev=(Button) findViewById(R.id.prev);
        play=(Button) findViewById(R.id.play);
        next =(Button) findViewById(R.id.next);
        prev.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.sun);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.prev:
                 backmusic();
                break;
            case R.id.play:
                if(mediaPlayer.isPlaying())
                {
                    pausemusic();
                }
                else
                {
                    playmusic();
                }

                break;
            case R.id.next:
                   nextmusic();
                break;
        }
    }
    public  void backmusic()
    {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(0);
            }
    }

    public void nextmusic()
    {
         if(mediaPlayer.isPlaying()) {
             mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
         }
    }
    public void pausemusic()
    { if(mediaPlayer !=null)
    {
        mediaPlayer.pause();
        play.setBackgroundResource(android.R.drawable.ic_media_play);
    }


    }
    public  void playmusic()
    { if(mediaPlayer !=null)
    {   updatethread();
        mediaPlayer.start();
        play.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    }
    public  void updatethread()
    {
        thread = new Thread()
        {
            @Override
            public void run() {
             try{
                 while (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                     Thread.sleep(50);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                          int newpos=mediaPlayer.getCurrentPosition();
                          int newdur = mediaPlayer.getDuration();
                             seek.setMax(newdur);
                             seek.setProgress(newpos);
                             lefttime.setText(getMusicDuration(newpos));
                             righttime.setText(getMusicDuration(newdur-newpos));
                         }
                     });
                 }
             }
             catch (InterruptedException e)
             {
                 e.printStackTrace();
             }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer.isPlaying()&&mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread= null;
        super.onDestroy();
    }
    private String getMusicDuration(int duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        return String.format("%02d : %02d", minutes, seconds);
    }
}
