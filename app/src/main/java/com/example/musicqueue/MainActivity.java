package com.example.musicqueue;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl,
    ServiceCallbacks {
    public static final String ACTION_PLAY = "action_play";
    public static final String INDEX_MP3 = "index_mp3";

    private RecyclerView mRecyclerViewSongs;
    private SongAdapter mSongAdapter;
    private ArrayList<Song> mSongs;
    private MusicController musicController;
    private MusicService musicService;
    private boolean musicBound = false;
    private Intent playIntent;
    private boolean bound;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerViewSongs = findViewById(R.id.rv_songs);
        mSongs = new ArrayList<>();
        mSongAdapter = new SongAdapter(this, getSongList());
        connectService();

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent);
        }


        mRecyclerViewSongs.setAdapter(mSongAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerViewSongs.setLayoutManager(linearLayoutManager);

        setMusicController();
    }

    public void songSelected(View view) {
        Intent intent = new Intent(ACTION_PLAY);
        intent.putExtra(INDEX_MP3, Integer.parseInt(view.getTag().toString()));
        sendBroadcast(intent);
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

    private void connectService() {
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // cast the IBinder and get MyService instance
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                bound = true;
                musicService.setCallbacks(MainActivity.this); // register
                //musicService.setServiceCallbacks(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };
    }

    @Override
    public ArrayList<Song> getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mSongs.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        return mSongs;
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
