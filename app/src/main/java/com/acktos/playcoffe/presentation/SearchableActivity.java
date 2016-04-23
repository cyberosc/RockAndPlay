package com.acktos.playcoffe.presentation;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.DividerItemDecoration;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.TracksController;
import com.acktos.playcoffe.controllers.UsersController;
import com.acktos.playcoffe.models.QueueTrack;
import com.acktos.playcoffe.models.SessionTrack;
import com.acktos.playcoffe.models.SpotifyTrack;
import com.acktos.playcoffe.models.Track;
import com.acktos.playcoffe.models.User;
import com.acktos.playcoffe.presentation.adapters.SearchAdapter;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.Firebase;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchableActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchAdapter.OnRecyclerViewClickListener{


    //UI References
    private List<Track> tracks;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerTracks;

    ProgressWheel progressDialogView;
    CircleImageView thumbAlbumView;

    //Components
    TracksController tracksController;
    UsersController usersController;
    Firebase mFirebaseRef;

    //Attributes
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        //setContentView(R.layout.track_search);
        onSearchRequested();

        //Initialize attributes
        tracks=new ArrayList<>();

        //Initialize components
        tracksController=new TracksController(this);
        usersController=new UsersController(this);

        user=usersController.getUser();

        setupRecyclerView();
        setupFirebase();

    }

    private void setupRecyclerView(){


        recyclerTracks = (RecyclerView) findViewById(R.id.recycler_search_tracks);
        recyclerTracks.setHasFixedSize(true);
        recyclerTracks.addItemDecoration(new DividerItemDecoration(this, null));
        recyclerTracks.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(this);
        recyclerTracks.setLayoutManager(layoutManager);

        recyclerAdapter = new SearchAdapter(this,tracks,this);
        recyclerTracks.setAdapter(recyclerAdapter);
    }


    /* *************************************
     *              FIREBASE               *
     ***************************************/

    private void setupFirebase(){

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(BaseController.FIREBASE_URL);//Create the Firebase ref

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchable, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onSearchRequested();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(BaseController.TAG,"search: OnQueryTextSubmit" );

        /*tracksController.searchSpotifyTracks(query, new Response.Listener<List<SpotifyTrack>>() {
            @Override
            public void onResponse(List<SpotifyTrack> responseTracks) {

                Log.i(BaseController.TAG,"entry onResponse onQueryTextSubmit:"+responseTracks.size());

                if (responseTracks != null) {

                    Log.i(BaseController.TAG,"number of tracks:"+responseTracks.size());

                    tracks.clear();
                    tracks.addAll(responseTracks);
                    recyclerAdapter.notifyDataSetChanged();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });*/
        List<Track> responseTracks=tracksController.searchTrackFromLocalPlayList(query);

        if(responseTracks.size()>=1){
            tracks.clear();
            tracks.addAll(responseTracks);
            recyclerAdapter.notifyDataSetChanged();
        }else
        if(responseTracks.size()<1){
            tracks.clear();
            recyclerAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.i(BaseController.TAG, "search: OnQueryTextChange:"+ newText);

        if(newText.length()>1){
            /*tracksController.searchSpotifyTracks(newText, new Response.Listener<List<SpotifyTrack>>() {
                @Override
                public void onResponse(List<SpotifyTrack> responseTracks) {

                    if (responseTracks != null) {

                        Log.i(BaseController.TAG,"number of tracks on text change:"+responseTracks.size());

                        tracks.clear();
                        tracks.addAll(responseTracks);
                        recyclerAdapter.notifyDataSetChanged();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });*/

            List<Track> responseTracks=tracksController.searchTrackFromLocalPlayList(newText);

            if(responseTracks.size()>=1){
                tracks.clear();
                tracks.addAll(responseTracks);
                recyclerAdapter.notifyDataSetChanged();
            }else
            if(responseTracks.size()<1){
                tracks.clear();
                recyclerAdapter.notifyDataSetChanged();
            }
        }


        return true;
    }


    @Override
    public void onRecyclerViewClick(View v, int position) {

        //TODO: get Bar and Session name to show in dialog message

        final Track track=tracks.get(position);
        if(track!=null){



            final MaterialDialog addTrackDialog=new MaterialDialog.Builder(this)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(final MaterialDialog dialog) {

                            progressDialogView.setVisibility(View.VISIBLE);
                            thumbAlbumView.setVisibility(View.INVISIBLE);

                            SessionTrack sessionTrack=new SessionTrack(track.getId(),user.getId() );


                            tracksController.addTrackToQueue(sessionTrack, new TracksController.OnAddTrackListener() {
                                @Override
                                public void onAddTrack(String message, boolean success) {

                                    Log.i(BaseController.TAG,"addTrackDialog success:"+success);

                                    dialog.dismiss();
                                    addTrackResultDialog(success, message);
                                }
                            });
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .customView(R.layout.confirm_add_track_dialog, true)
                    .positiveText(R.string.add)
                    .negativeText(R.string.cancel)
                    .autoDismiss(false)
                    .show();

            View dialogView=addTrackDialog.getView();
            TextView trackNameView=(TextView)dialogView.findViewById(R.id.txt_track_name_dialog);
            progressDialogView=(ProgressWheel)dialogView.findViewById(R.id.progress_add_track_dialog);
            TextView messageView=(TextView)dialogView.findViewById(R.id.lbl_message_add_track_dialog);
            thumbAlbumView=(CircleImageView) dialogView.findViewById(R.id.thumb_album_add_track_dialog);

            Picasso.with(this)
                    .load("http://www.acktos.com.co/images/thumbnail.png")
                    .placeholder(R.drawable.ic_music_note_black_24dp)
                    .into(thumbAlbumView);

            trackNameView.setText(track.getSong());
            messageView.setText(getString(R.string.msg_add_track_dialog));


        }else{
            Log.e(BaseController.TAG,"track object is null");
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addTrackResultDialog(boolean success,String message){

        String title;
        String content;
        //int resourceIcon;

        if(success){
            title=getString(R.string.congratulations);
            content=getString(R.string.msg_add_track_success)+", "+getString(R.string.msg_add_track_pending_state);
            //resourceIcon=R.drawable.ic_done_black_24dp;
        }else{
            title=getString(R.string.sorry);
            content=getString(R.string.msg_add_track_failed)+". "+message;
            //resourceIcon=R.drawable.ic_clear_black_24dp;
        }

        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                //.iconRes(resourceIcon)
                .positiveText(R.string.accept)
                .show();
    }
}
