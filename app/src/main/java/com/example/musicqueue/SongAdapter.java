package com.example.musicqueue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context mContext;
    private List<Song> mSongs;
    private LayoutInflater mLayoutInflater;

    public SongAdapter(Context mContext,List<Song> mSongs) {
        this.mContext = mContext;
        this.mSongs = mSongs;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_song, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindData(mSongs.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mArtistTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.text_title);
            mArtistTextView = itemView.findViewById(R.id.text_artist);
        }

        public void bindData(Song song) {
            mTitleTextView.setText(song.getTitle());
            mArtistTextView.setText(song.getArtist());
        }
    }
}
