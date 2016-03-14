package com.acktos.playcoffe.presentation.adapters;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.models.Session;

import java.util.List;

/**
 * Created by Acktos on 9/1/15.
 */
public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {

    private List<Session> sessions;
    private static OnRecyclerViewClickListener onRecyclerViewClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView place;
        public TextView schedule;
        public CardView sessionCard;

        public ViewHolder(View itemView) {

            super(itemView);
            sessionCard=(CardView)itemView.findViewById(R.id.session_card);
            name=(TextView)itemView.findViewById(R.id.lbl_session_name);
            place=(TextView)itemView.findViewById(R.id.lbl_session_place);
            schedule=(TextView)itemView.findViewById(R.id.lbl_session_schedule);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRecyclerViewClickListener.onRecyclerViewClick(view, this.getLayoutPosition());
        }
    }

    public SessionsAdapter(List<Session> sessions,OnRecyclerViewClickListener onRecyclerViewClick){

        this.sessions=sessions;
        this.onRecyclerViewClickListener=onRecyclerViewClick;
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.session_card, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.name.setText(sessions.get(i).getSessionName());
        viewHolder.place.setText(sessions.get(i).getSessionName());
        viewHolder.schedule.setText(sessions.get(i).getFinalDate());
    }



    public interface OnRecyclerViewClickListener
    {

        public void onRecyclerViewClick(View v, int position);
    }
}
