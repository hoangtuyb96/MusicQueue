package com.example.musicqueue;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> songList;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle = "";
    private static final int NOTIFY_ID = 1;

    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
        setList(new SongManager().getSongList(MusicApplication.getAppContext()));

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.ACTION_PLAY);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songList = theSongs;
    }

    public int getPosn(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mMediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mMediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mMediaPlayer.pause();
    }

    public void seek(int posn){
        mMediaPlayer.seekTo(posn);
    }

    public void go(){
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn= songList.size()-1;
        playSong();
    }

    public void playNext(){
        songPosn++;
        if(songPosn>= songList.size()) songPosn=0;
        playSong();
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    private void playSong() {
        Log.e("pos", songPosn+"");
        Song playSong = songList.get(songPosn);
        try {
            mMediaPlayer.reset();
            Log.e("song_path", playSong.getPath()+"");
            mMediaPlayer.setDataSource(playSong.getPath());
            mMediaPlayer.prepare();
            createNotification(playSong.getTitle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createNotification(String title) {
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.android_music_player_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing").setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    BroadcastReceiver mReceiver;

    // use this as an inner class like here or as a top-level class
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MainActivity.ACTION_PLAY)) {
                setSong(intent.getIntExtra(MainActivity.INDEX_MP3, 0));
                playSong();
            }
        }
    }
}
