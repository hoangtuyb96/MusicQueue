package com.example.musicqueue;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;

import static com.example.musicqueue.MusicService.ACTION_PLAY_MUSIC;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public static final String ACTION_PLAY = "action_play";
    public static final String INDEX_MP3 = "index_mp3";

    private RecyclerView mRecyclerViewSongs;
    private SongAdapter mSongAdapter;
    private ArrayList<Song> songList;
    private MusicController musicController;
    private MusicService musicService;
    private boolean musicBound = false;
    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerViewSongs = findViewById(R.id.rv_songs);
        songList = new SongManager().getSongList(this);
        mSongAdapter = new SongAdapter(this, songList);

//        if (playIntent == null) {
//            playIntent = new Intent(this, MusicService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(playIntent);
//            }
//            else
//            {
//                startService(playIntent);
//            }
//
//        }


        mRecyclerViewSongs.setAdapter(mSongAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewSongs.setLayoutManager(linearLayoutManager);

        setMusicController();
    }

    public void songSelected(View view) {


        playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(ACTION_PLAY_MUSIC);
        playIntent.putExtra(INDEX_MP3, Integer.parseInt(view.getTag().toString()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playIntent);
        }
        else
        {
            startService(playIntent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shuffle_button:
                break;
            case R.id.end_button:
                stopService(playIntent);
                musicService = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setMusicController() {
        musicController = new MusicController(this);
        musicController.setMediaPlayer(this);
        musicController.setAnchorView(findViewById(R.id.rv_songs));
        musicController.setEnabled(true);
        musicController.setPrevNextListeners(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                playNext();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                playPrev();
            }
        });
    }

    private void playNext() {
        musicService.playNext();
        musicController.show(0);
    }

    private void playPrev() {
        musicService.playPrev();
        musicController.show(0);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicService!=null && musicBound && musicService.isPng())
            return musicService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService!=null && musicBound && musicService.isPng())
            return musicService.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int i) {
        musicService.seek(i);
    }

    @Override
    public boolean isPlaying() {
        if(musicService!=null && musicBound)
            return musicService.isPng();
        else return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
