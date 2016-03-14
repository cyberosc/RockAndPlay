package com.acktos.playcoffe.presentation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.SessionsController;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.presentation.adapters.SessionsAdapter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SessionsFragment extends Fragment implements SessionsAdapter.OnRecyclerViewClickListener {

    //Attributes
    private List<Session> sessions;

    //UI References
    private RecyclerView sessionsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;

    //Components
    Firebase mFirebaseRef;
    SessionsController sessionsController;

    //Listeners
    private OnSessionJoinedListener mListener;


    public static SessionsFragment newInstance() {
        SessionsFragment fragment = new SessionsFragment();
        return fragment;
    }

    // Required empty public constructor
    public SessionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_sessions, container, false);

        //Initialize attributes
        sessions=new ArrayList<>();

        //Initialize UI
        sessionsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_sessions);
        sessionsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        sessionsRecyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new SessionsAdapter(sessions,this);
        sessionsRecyclerView.setAdapter(recyclerAdapter);

        //Initialize components
        setupFirebase();
        getSessionsFromFirebase();
        sessionsController=new SessionsController(getActivity());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSessionJoinedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSessionJoinedListener {

        void onSessionJoined();
    }

    /**
     * Override onRecyclerViewClick to communicate events on the adapter with the fragment
     * @param v
     * @param position
     */
    @Override
    public void onRecyclerViewClick(View v, int position) {

        Log.i(BaseController.TAG,sessions.get(position).toString());
        joinToSession(sessions.get(position));
    }

    private void joinToSession(Session session){

        sessionsController.saveJoinedSession(session);

        if(mListener!=null){
            mListener.onSessionJoined();
        }
    }


    /* *************************************
     *              FIREBASE               *
     ***************************************/

    private void setupFirebase(){

        Firebase.setAndroidContext(getActivity());
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

                    Session session=new Session(
                            sessionSnapshot.getKey().toString(),
                            sessionSnapshot.child(Session.KEY_BAR_ID).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_SESSION_NAME).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_START_DATE).getValue().toString(),
                            sessionSnapshot.child(Session.KEY_END_DATE).getValue().toString()
                    );

                    //String title = (String) snapshot.child("title").getValue();
                    //Session session = sessionSnapshot.get
                    firebaseSessions.add(session);
                    Log.i ( BaseController.TAG,"Session name: " + session.getSessionName());
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
