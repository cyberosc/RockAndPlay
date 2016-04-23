package com.acktos.playcoffe.presentation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.DateTimeUtils;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.acktos.playcoffe.models.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Acktos on 9/7/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<Track> tracks;
    private static OnRecyclerViewClickListener onRecyclerViewClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTrackName;
        public TextView txtArtist;
        public TextView txtDuration;
        public CircleImageView thumbAlbum;


        public ViewHolder(View itemView) {

            super(itemView);
            txtTrackName=(TextView)itemView.findViewById(R.id.txt_track_name);
            txtArtist=(TextView)itemView.findViewById(R.id.txt_artist);
            txtDuration=(TextView)itemView.findViewById(R.id.txt_duration);
            thumbAlbum=(CircleImageView)itemView.findViewById(R.id.thumb_album);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewClickListener.onRecyclerViewClick(view, this.getLayoutPosition());
        }
    }

    public SearchAdapter(Context context, List<Track> tracks, OnRecyclerViewClickListener onRecyclerViewClick){

        this.context=context;
        this.tracks=tracks;
        this.onRecyclerViewClickListener=onRecyclerViewClick;
    }

    @Override
    public int getItemCount() {

        return tracks.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_search, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.txtTrackName.setText(tracks.get(i).getSong());
        viewHolder.txtArtist.setText(tracks.get(i).getArtist());
        viewHolder.txtDuration.setText(
                DateTimeUtils.millisecondsToMinutes(tracks.get(i).getDuration()));
        Picasso.with(context)
                .load("http://www.acktos.com.co/images/thumbnail.png")
                .placeholder(R.drawable.ic_music_note_black_24dp)
                .into(viewHolder.thumbAlbum);
    }



    public interface OnRecyclerViewClickListener
    {
        public void onRecyclerViewClick(View v, int position);
    }

}