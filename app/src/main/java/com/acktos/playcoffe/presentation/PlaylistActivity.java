package com.acktos.playcoffe.presentation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.DateTimeUtils;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.TracksController;
import com.acktos.playcoffe.models.Player;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaylistActivity extends AppCompatActivity {

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


    //Components
    TracksController tracksController;
    Firebase mFirebaseRef;

    //Attributes
    String sessionId;
    String playerState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Initialize UI
        txtTrackName=(TextView) findViewById(R.id.txt_track_name_player);
        txtArtist=(TextView) findViewById(R.id.txt_artist_player);
        playerProgress = (ProgressBar) findViewById(R.id.player_progress);
        playerTrackTimeView = (TextView) findViewById(R.id.player_track_time);
        playerProgressTimeView = (TextView) findViewById(R.id.player_progress_time);
        messageContent=findViewById(R.id.message_content);
        playerContent=findViewById(R.id.player_content);
        txtMessageTitle=(TextView) findViewById(R.id.txt_player_message_title);
        txtMessageBody=(TextView) findViewById(R.id.txt_player_message_body);
        imgThumbAlbum=(CircleImageView) findViewById(R.id.thumb_album_player);

        setupFirebase();
        setupTimestampListener();
        setupPlayerListener();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    private void setupFirebase() {

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(BaseController.FIREBASE_URL);//Create the Firebase ref

    }


    private void setupPlayerListener() {

        // TODO: Get Session Id

        sessionId = "0";
        Firebase tracksRef = new Firebase(
                BaseController.FIREBASE_URL + BaseController.TABLE_SESSIONS + "/" + sessionId + "/" + BaseController.TABLE_PLAYER_STATE);

        tracksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(BaseController.TAG, dataSnapshot.getValue().toString());
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

            if (elapsedTime >= 0 && elapsedTime < duration) {

                togglePlayerMessage(false,null,null);// hidden message and then displays the player
                txtTrackName.setText(player.getTrackName());
                txtArtist.setText(player.getArtistName());

                Picasso.with(this)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
