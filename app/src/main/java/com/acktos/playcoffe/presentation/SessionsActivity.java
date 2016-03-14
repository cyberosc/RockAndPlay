package com.acktos.playcoffe.presentation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.presentation.adapters.SessionsAdapter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SessionsActivity extends AppCompatActivity implements SessionsAdapter.OnRecyclerViewClickListener{


    //Attributes
    private List<Session> sessions;

    //UI References
    private RecyclerView sessionsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    //Components
    Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        //Initialize attributes
        sessions=new ArrayList<>();

        //Initialize UI
        sessionsRecyclerView = (RecyclerView) findViewById(R.id.recycler_sessions);
        sessionsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        sessionsRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new SessionsAdapter(sessions,this);
        sessionsRecyclerView.setAdapter(recyclerAdapter);

        //Initialize components
        setupFirebase();
        getSessionsFromFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sessions, menu);
        return true;
    }

    @Override
    public void onRecyclerViewClick(View v, int position) {

        //Intent i=new Intent(this,MapTrackerActivity.class);
        //i.putExtra(Session.KEY_JSON,sessions.get(position).toString());

        //startActivity(i);
    }

    /* *************************************
     *              FIREBASE               *
     ***************************************/

    private void setupFirebase(){

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(BaseController.FIREBASE_URL);//Create the Firebase ref

    }

    private List<Session> getSessionsFromFirebase(){

        Firebase sessionsRef = new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS);

        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(BaseController.TAG, dataSnapshot.getValue().toString());

                List<Session> firebaseSessions=new ArrayList<>();

                for (DataSnapshot sessionSnapshot: dataSnapshot.getChildren()) {

                    String barId= (String)sessionSnapshot.child(Session.KEY_BAR_ID).getValue();
                    Log.i(BaseController.TAG,"session key:"+barId);
                    //Session session = sessionSnapshot.getValue(Session.class);
                    Session session=new Session(
                            sessionSnapshot.getKey(),
                            sessionSnapshot.child(Session.KEY_BAR_ID).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_SESSION_NAME).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_START_DATE).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_END_DATE).getValue().toString());

                    firebaseSessions.add(session);
                    //System.out.println(session.getBarName() + " - " + session.getSessionName());
                }

                if(firebaseSessions!=null){
                    sessions.clear();
                    sessions.addAll(firebaseSessions);
                    recyclerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(BaseController.TAG,firebaseError.getMessage());
            }
        });

        return sessions;
    }
}
