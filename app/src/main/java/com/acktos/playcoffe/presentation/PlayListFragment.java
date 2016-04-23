package com.acktos.playcoffe.presentation;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.DateTimeUtils;
import com.acktos.playcoffe.android.DividerItemDecoration;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.SessionsController;
import com.acktos.playcoffe.controllers.TracksController;
import com.acktos.playcoffe.models.Player;
import com.acktos.playcoffe.models.QueueTrack;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.SessionTrack;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.acktos.playcoffe.models.Track;
import com.acktos.playcoffe.presentation.adapters.PlaylistAdapter;
import com.acktos.playcoffe.presentation.adapters.SearchAdapter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayListFragment extends Fragment implements PlaylistAdapter.OnRecyclerViewClickListener {

    //UI References
    ProgressBar playerProgress;
    TextView playerTrackTimeView;
    TextView playerProgressTimeView;
    TextView txtTrackName;
    TextView txtArtist;
    View messageContent;
    View playerContent;
    TextView txtMessageTitle;
    TextView txtMessageBody;
    CircleImageView imgThumbAlbum;
    RecyclerView recyclerTracks;
    View contentEmptyQueue;




    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //Components
    TracksController tracksController;
    Firebase mFirebaseRef;
    SessionsController sessionsController;

    //Attributes
    Session session;
    String playerState;
    /**
     * session tracks from firebase
     */
    private List<SessionTrack> sessionTracksList;


    /**
     * playList built from session tracks and bar tracks;
     */
    private List<Track> playList;

    private OnFragmentInteractionListener mListener;


    public static PlayListFragment newInstance() {
        PlayListFragment fragment = new PlayListFragment();
        return fragment;
    }

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_play_list, container, false);

        //Initialize UI
        txtTrackName=(TextView) view.findViewById(R.id.txt_track_name_player);
        txtArtist=(TextView) view.findViewById(R.id.txt_artist_player);
        playerProgress = (ProgressBar) view.findViewById(R.id.player_progress);
        playerTrackTimeView = (TextView) view.findViewById(R.id.player_track_time);
        playerProgressTimeView = (TextView) view.findViewById(R.id.player_progress_time);
        messageContent=view.findViewById(R.id.message_content);
        playerContent=view.findViewById(R.id.player_content);
        txtMessageTitle=(TextView) view.findViewById(R.id.txt_player_message_title);
        txtMessageBody=(TextView) view.findViewById(R.id.txt_player_message_body);
        imgThumbAlbum=(CircleImageView) view.findViewById(R.id.thumb_album_player);
        recyclerTracks = (RecyclerView) view.findViewById(R.id.recycler_playlist_tracks);
        contentEmptyQueue=view.findViewById(R.id.content_empty_play_list);

        //Initialize components
        sessionsController=new SessionsController(getActivity());

        //Get current joined session
        session=sessionsController.getJoinedSession();
        tracksController=new TracksController(getActivity());

        //Initialize attributes
        sessionTracksList=new ArrayList<>();
        playList=new ArrayList<>();

        //SetupComponents
        setupRecyclerView();
        setupFirebase();
        setupTimestampListener();
        getPlaylistQueueFromFirebase();
        setupPlayerListener();

        return view;
    }

    private void setupRecyclerView(){

        recyclerTracks.setHasFixedSize(true);
        recyclerTracks.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        recyclerTracks.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerTracks.setLayoutManager(layoutManager);

        recyclerAdapter = new PlaylistAdapter(getActivity(),playList,this);
        recyclerTracks.setAdapter(recyclerAdapter);
    }

    private void getPlaylistQueueFromFirebase(){


        final Firebase playlistRef = new Firebase(
                BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS+"/"+session.getId()+"/"+BaseController.TABLE_TRACKS);

        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(BaseController.TAG,"playlist queue:"+dataSnapshot.getValue().toString());

                List<SessionTrack> sessionTracksFirebase = new ArrayList<>();
                List<Track> barPlayList=new ArrayList<>();

                for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {


                    SessionTrack sessionTrack=playlistSnapshot.getValue(SessionTrack.class);
                    sessionTracksFirebase.add(sessionTrack);

                }


                barPlayList=tracksController.getBarTracksFromSessionTracks(sessionTracksFirebase);

                if (barPlayList != null) {
                    playList.clear();
                    playList.addAll(barPlayList);
                    recyclerAdapter.notifyDataSetChanged();

                    if(barPlayList.size()>=1){
                        recyclerTracks.setVisibility(View.VISIBLE);
                        contentEmptyQueue.setVisibility(View.INVISIBLE);
                    }else{
                        recyclerTracks.setVisibility(View.INVISIBLE);
                        contentEmptyQueue.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(BaseController.TAG, firebaseError.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {

    }

    private void setupFirebase() {
        Firebase.setAndroidContext(getActivity());
        mFirebaseRef = new Firebase(BaseController.FIREBASE_URL);//Create the Firebase ref
    }

    private void setupPlayerListener() {

        Firebase tracksRef = new Firebase(
                BaseController.FIREBASE_URL + BaseController.TABLE_SESSIONS + "/" + session.getId() + "/" + BaseController.TABLE_PLAYER_STATE);

        tracksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(BaseController.TAG, "player state changed:"+dataSnapshot.getValue().toString());
                initProgress(dataSnapshot.getValue(Player.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.e(BaseController.TAG, firebaseError.getMessage());
            }
        });
    }

    private void initProgress(Player player) {

        long startTime = Long.parseLong(player.getStartTime());
        Log.i(BaseController.TAG,"start track time firebase:"+startTime);
        //long startTime= Long.parseLong("1442524921773");//dummy timestamp
        long currentTime = System.currentTimeMillis();
        //long currentTime= Long.parseLong("1442524961773");// dummy timestamp

        // seconds have elapsed since the start of the song up to now
        long elapsedTime = currentTime - startTime;
        long duration = Long.parseLong(player.getDuration());
        playerState = player.getState();
        String message = player.getMessage();

        Log.i(BaseController.TAG, "start time:" + DateTimeUtils.timestampToDate(startTime));
        Log.i(BaseController.TAG, "current time:" + DateTimeUtils.timestampToDate(currentTime));
        Log.i(BaseController.TAG, "duration:" + duration);
        Log.i(BaseController.TAG, "state:" + playerState);


        //set label duration-track
        playerTrackTimeView.setText(DateTimeUtils.millisecondsToMinutes(Long.toString(duration)));


        if (playerState.equals(Player.STATE_PLAY) || playerState.equals(Player.STATE_PAUSE)) {



            Log.i(BaseController.TAG, "elapsedTime:" + elapsedTime);

            if(currentTime<(startTime+duration)){
            //if (elapsedTime >= 0 && elapsedTime < duration) {

                togglePlayerMessage(false,null,null);// hidden message and then displays the player
                txtTrackName.setText(player.getTrackName());
                txtArtist.setText(player.getArtistName());

                Picasso.with(getActivity())
                        .load(player.getThumbAlbum())
                        .placeholder(R.drawable.ic_music_note_black_24dp)
                        .into(imgThumbAlbum);

                Log.i(BaseController.TAG, "elapsedTime is within limits:" + elapsedTime);
                //init progressView
                playerProgress.setMax((int) (long) (duration / 1000));

                long remainingTime = duration - elapsedTime;

                Log.i(BaseController.TAG, "remaining time" + remainingTime);
                PlayerProgressTask playerProgressTask = new PlayerProgressTask();
                playerProgressTask.execute(elapsedTime, remainingTime, duration);


            } else {
                // TODO: show inactive message
                Log.i(BaseController.TAG, "elapsedTime is´t within limits:" + elapsedTime);
                togglePlayerMessage(true, getString(R.string.player_stop), getString(R.string.msg_error_player));
            }
        } else {
            // TODO: show inactive message
            Log.i(BaseController.TAG, "player state is inactive");
            togglePlayerMessage(true,getString(R.string.player_stop),message);
        }
    }

    private void setupTimestampListener() {

        Firebase offsetRef = new Firebase("https://coffe-play.firebaseio.com/.info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                double estimatedServerTimeMs = System.currentTimeMillis() + offset;
                Log.i(BaseController.TAG, "firebase time: " + estimatedServerTimeMs);
                Log.i(BaseController.TAG, "offset: " + offset);
                Log.i(BaseController.TAG, "systemCurrentMillis: " + System.currentTimeMillis());
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }

    private void togglePlayerMessage(boolean showMessage, String title, String message) {

        if(showMessage){

            if(TextUtils.isEmpty(message)){
                message=getString(R.string.msg_player_stop);
            }

            txtMessageTitle.setText(title);
            txtMessageBody.setText(message);

            playerContent.setVisibility(View.INVISIBLE);
            messageContent.setVisibility(View.VISIBLE);

        }else{

            playerContent.setVisibility(View.VISIBLE);
            messageContent.setVisibility(View.INVISIBLE);
        }

    }

    public class PlayerProgressTask extends AsyncTask<Long, Integer, Void> {

        @Override
        protected Void doInBackground(Long... times) {

            try {
                int elapsedSeconds = (times[0].intValue() / 1000);
                int remainingSeconds = (times[1].intValue() / 1000);
                int durationSeconds = (times[2].intValue() / 1000);

                // set initial progress to progressView
                publishProgress(elapsedSeconds);

                for (int i = 0; i <= remainingSeconds; i++) {
                    publishProgress(elapsedSeconds + i);
                    //Log.i(BaseController.TAG, "progress:" + i);
                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(BaseController.TAG, "error trying to retrieve seconds");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            playerProgress.setProgress(values[0]);
            playerProgressTimeView.setText(DateTimeUtils.millisecondsToMinutes(Integer.toString(values[0] * 1000)));
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void showPlayListFragment();
    }

}
