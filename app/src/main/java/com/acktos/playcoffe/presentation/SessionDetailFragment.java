package com.acktos.playcoffe.presentation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.android.DateTimeUtils;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.SessionsController;
import com.acktos.playcoffe.controllers.TracksController;
import com.acktos.playcoffe.controllers.UsersController;
import com.acktos.playcoffe.models.Card;
import com.acktos.playcoffe.models.Credit;
import com.acktos.playcoffe.models.Session;
import com.acktos.playcoffe.models.Track;
import com.acktos.playcoffe.models.User;
import com.acktos.playcoffe.util.CapturePortraitOrientationActivity;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SessionDetailFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private PlayListFragment.OnFragmentInteractionListener mListener;

    //Components
    private UsersController usersController;
    private SessionsController sessionsController;
    private TracksController tracksController;

    //Android Utils
    Gson gson;

    // UI references
    private Button btnScan;


    //Attributes
    private Card card;
    private User user;
    private String sessionMessage;
    private String cardMessage;
    private Boolean addCreditsSuccessful =false;
    private Boolean sessionJoinedSuccessful =false;

    public SessionDetailFragment() {
        // Required empty public constructor
    }


    public static SessionDetailFragment newInstance(String param1, String param2) {
        SessionDetailFragment fragment = new SessionDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(getActivity());

        //Initialize components

        usersController=new UsersController(getActivity());
        user=usersController.getUser();
        tracksController=new TracksController(getActivity());
        gson=new Gson();

        sessionsController=new SessionsController(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_session_detail, container, false);

        btnScan= (Button) view.findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(this);

        Log.i(BaseController.TAG, "entry on creteView");

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.i(BaseController.TAG,"entry on attach");

        if(mListener!=null){
            Log.i(BaseController.TAG,"mListener No is null");
        }else{
            Log.i(BaseController.TAG,"mListener is null");
        }

        if (activity instanceof PlayListFragment.OnFragmentInteractionListener) {
            mListener = (PlayListFragment.OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener");
        }

        if(mListener!=null){
            Log.i(BaseController.TAG,"mListener No is null");
        }else{
            Log.i(BaseController.TAG,"mListener is null");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_scan){

            startScanning();
        }
    }

    private void startScanning(){

        IntentIntegrator.forFragment(this)
                .setCaptureActivity(CapturePortraitOrientationActivity.class)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setPrompt(getString(R.string.scan_promt))
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Reset flags
        sessionJoinedSuccessful =false;
        addCreditsSuccessful =false;

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.i(BaseController.TAG, "Cancelled scan");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d(BaseController.TAG, "Scanned");
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                if(mListener!=null){
                    Log.i(BaseController.TAG,"mListener No is null");
                }else{
                    Log.i(BaseController.TAG,"mListener is null");
                }

                checkCard(result.getContents());
            }
        } else {
            Log.d(BaseController.TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * shows messages after finish scan process
     */
    private void showResultProcessDialog(){

        String title="";
        String message="";

        Log.i(BaseController.TAG,"dialog session success:"+ sessionJoinedSuccessful);
        Log.i(BaseController.TAG,"dialog credit success:"+ addCreditsSuccessful);

        if(sessionJoinedSuccessful && addCreditsSuccessful){

            title=getString(R.string.congratulations);
            message=sessionMessage+" "+getString(R.string.and)+" "+cardMessage.toLowerCase();

        }else if(sessionJoinedSuccessful){

            title=getString(R.string.title_you_are_in_the_playlist);
            message=sessionMessage+" "+getString(R.string.however)+", "+cardMessage.toLowerCase();

        }else if(addCreditsSuccessful){
            title=getString(R.string.title_earn_credits);
            message=cardMessage+" "+getString(R.string.however)+" "+sessionMessage;
        }else if(!addCreditsSuccessful && !sessionJoinedSuccessful){

            title=getString(R.string.sorry);
            message=(TextUtils.isEmpty(sessionMessage))?cardMessage:sessionMessage+ " "
                    +getString(R.string.however)+", "
                    +cardMessage.toLowerCase();

        }

        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(message)
                .iconRes(R.mipmap.ic_qr)
                .positiveText(R.string.accept)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(sessionJoinedSuccessful){
                            mListener.showPlayListFragment();
                        }
                    }
                })
                .show();
    }

    /* *************************************
     *              FIREBASE               *
     ***************************************/

    private void checkCard(String scanResult){

        // check code format
        if(checkScanFormat(scanResult)){

            //get code
            final String code=getCodeFromScan(scanResult);
            Log.i(BaseController.TAG,"code:"+code);

            //get card information
            Firebase cardReference=new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_CARDS+"/"+code);

            cardReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    //checks if the card exists
                    if(dataSnapshot.getChildrenCount()>=1){

                        card=dataSnapshot.getValue(Card.class);
                        card.setCode(code);

                        // Checks if the card hasn't expired
                        //TODO: get current time from firebase for more accuracy
                        String currentDate=DateTimeUtils.getCurrentTime();

                        int compareExpirationDate=DateTimeUtils.compareTwoDateStrings(currentDate,card.getExpirationForScan());

                        if(compareExpirationDate>0){

                            Log.i(BaseController.TAG,"This card was already expired");
                            cardMessage=getString(R.string.msg_card_expired);
                            showResultProcessDialog();
                            // TODO: end card flow

                        }else{
                            checkSession(card);
                            Log.i(BaseController.TAG, "card credits:" + card.getCredits());
                            Log.i(BaseController.TAG,"card bardId:"+card.getBarId());
                        }


                    }else{

                        cardMessage=getString(R.string.msg_invalid_card);
                        Log.i(BaseController.TAG,"This card doesn't exists");
                        showResultProcessDialog();
                        // TODO:end card flow
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.i(BaseController.TAG,"card request was cancelled");
                    // TODO: show dialog error connection.
                }
            });

        }else{

            // QR format is not valid
            Log.i(BaseController.TAG, "card with invalid format");
            cardMessage=getString(R.string.msg_invalid_card_format);
            showResultProcessDialog();
            // TODO:end card flow
        }
    }

    /**
     * Checks if there is an a active session with give BarId
     * @param card
     */
    private void checkSession(Card card){


        final String barId=card.getBarId();
        Firebase sessionsReference=new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS);
        final Query querySession=sessionsReference.orderByChild(Session.KEY_BAR_ID).equalTo(barId);

        querySession.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot,String previousChild) {

                if (dataSnapshot.getChildrenCount() >= 1) {

                    // only for log status
                    Log.i(BaseController.TAG, "There are sessions for this bar, checking if there is any active...");

                    String sessionKey=dataSnapshot.getKey();
                    String endSessionDate = (String) dataSnapshot.child("endDate").getValue();


                    Log.i(BaseController.TAG,"session key:"+sessionKey);

                    // Check if current date is less than the endDate session

                    if (!TextUtils.isEmpty(endSessionDate)) {

                        // TODO: get current date from firebase for more accuracy.
                        String currentDate = DateTimeUtils.getCurrentTime();

                        int compareDates = DateTimeUtils.compareTwoDateStrings(currentDate, endSessionDate);

                        if (compareDates > 0) {
                            Log.i(BaseController.TAG, "current date is after, the session is over");
                            sessionMessage=getString(R.string.msg_session_is_over);
                            addCreditsToProfile();
                        } else if (compareDates == 0) {
                            Log.i(BaseController.TAG, "compare session dates fail");
                            // TODO: show error session dialog.
                        } else if (compareDates < 0) {
                            Log.i(BaseController.TAG, "current date is before, the session is ACTIVE");

                            Session session=new Session(
                                    sessionKey,
                                    dataSnapshot.child(Session.KEY_BAR_ID).getValue().toString(),
                                    dataSnapshot.child(Session.KEY_SESSION_NAME).getValue().toString(),
                                    dataSnapshot.child(Session.KEY_START_DATE).getValue().toString(),
                                    dataSnapshot.child(Session.KEY_END_DATE).getValue().toString());


                            sessionsController.saveJoinedSession(session);

                            addUserToSession(sessionKey, session.getSessionName());

                            // Download the play list from the bar.
                            downloadPlayList(barId);

                        }
                    }else{
                        Log.i(BaseController.TAG, "endSession date is empty");
                        //TODO: show error session dialog.
                    }

                } else {

                    // TODO: check credits in card
                    Log.i(BaseController.TAG, "This bar doesn't have sessions");
                    sessionMessage=getString(R.string.msg_no_sessions);
                }

                // Remove sessions event listener
                querySession.removeEventListener(this);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    /**
     * Add credits to user profile and burn card that was used
     */
    private void addCreditsToProfile(){

        //check if card is already used
        if(TextUtils.isEmpty(card.getUsedBy())){


            // Burn Card from firebase
            Firebase cardsReference=new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_CARDS);

            Map<String,Object> cardMap=new HashMap<>();
            cardMap.put("usedBy",user.getId());

            Log.i(BaseController.TAG,"code:"+card.getCode());

            cardsReference.child(card.getCode()).updateChildren(cardMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Log.i(BaseController.TAG, "Card couldn't be fired. " + firebaseError.getMessage());
                        // TODO: show error network dialog .
                    } else {
                        Log.i(BaseController.TAG, "Card was fired successfully.");

                        // Save credit to firebase
                        Credit credit = new Credit(
                                card.getCredits(),
                                card.getExpirationForUse(),
                                "card",
                                DateTimeUtils.getCurrentTime());

                        Firebase creditsReference = new Firebase(BaseController.FIREBASE_URL + BaseController.TABLE_CREDITS);
                        creditsReference.child(user.getId() + "/" + card.getCode()).setValue(credit, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    Log.i(BaseController.TAG, "Credit couldn't be saved. " + firebaseError.getMessage());
                                } else {
                                    Log.i(BaseController.TAG, "Credit saved successfully.");
                                    addCreditsSuccessful =true;

                                    Log.i(BaseController.TAG, "Card credits:"+card.getCredits());
                                    cardMessage = String.format(getString(R.string.msg_add_credits_successfully), card.getCredits());
                                    showResultProcessDialog();
                                }
                            }
                        });
                    }
                }
            });


        }else{
            Log.i(BaseController.TAG,"This card was already used");
            cardMessage=getString(R.string.msg_card_already_used);
            showResultProcessDialog();
        }
    }

    /**
     * Add this userId to "joinedUsers" object in "SESSIONS" firebase table.
     */
    private void addUserToSession(String sessionKey,final String sessionName){

        Firebase sessionsRef=new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_SESSIONS);

        Map<String,Object> joinedUserMap=new HashMap<>();
        joinedUserMap.put(user.getId(),true);

        sessionsRef.child(sessionKey+"/"+Session.KEY_JOINED_USERS)
                .updateChildren(joinedUserMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.i(BaseController.TAG,"joinedUser object couldn't be updates. " + firebaseError.getMessage());
                        } else {
                            sessionJoinedSuccessful =true;
                            sessionMessage=getString(R.string.msg_joined_session_successfully)+" \""+sessionName+"\"";
                            Log.i(BaseController.TAG, "user joined to session successfully.");

                        }

                        addCreditsToProfile();
                    }
                });

    }

    /**
     * checks if QR format is valid
     * @param scanResult
     * @return
     */
    private boolean checkScanFormat(String scanResult){

        String cardFormat="rp://\\w{1,8}";
        return scanResult.matches(cardFormat);

    }

    /**
     * Get only code from QR scan
     * @param scanResult
     * @return
     */
    private String getCodeFromScan(String scanResult){

        return scanResult.substring(5);
    }


    private void downloadPlayList(String barId){

        if(barId!=null){

            Firebase playListReference=new Firebase(BaseController.FIREBASE_URL+BaseController.TABLE_BARS);

            playListReference.child(barId).child(BaseController.TABLE_PLAYLIST)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Log.i(BaseController.TAG, "onDataChange: barPlayList:"+dataSnapshot);

                                List<Track> barTracksList=new ArrayList<>();

                                for (DataSnapshot barTracksSnapshot : dataSnapshot.getChildren()) {

                                    Track track=new Track(
                                            barTracksSnapshot.getKey(),
                                            barTracksSnapshot.child(Track.KEY_ARTIST).getValue().toString(),
                                            barTracksSnapshot.child(Track.KEY_DURATION).getValue().toString(),
                                            barTracksSnapshot.child(Track.KEY_DURATION_TEXT).getValue().toString(),
                                            barTracksSnapshot.child(Track.KEY_SONG).getValue().toString());


                                    barTracksList.add(track);
                                    //Log.i(BaseController.TAG, "Saving playlist..." + dataSnapshot.getValue().toString());
                                    //tracksController.saveBarPlayList(dataSnapshot.getValue().toString());
                                    //Log.i(BaseController.TAG, "Playlist saved");
                                }


                                String barTracksString=gson.toJson(barTracksList);
                                Log.i(BaseController.TAG, "Saving playlist..." + barTracksString);
                                tracksController.saveBarPlayList(barTracksString);
                                Log.i(BaseController.TAG, "Playlist saved");

                            }

                            @Override
                            public void onCancelled (FirebaseError firebaseError){

                                Log.i(BaseController.TAG, "(downloadPlayList) an occurred error trying to download the playlist ");
                            }
                        }

                        );


                    }else{

            Log.i(BaseController.TAG, "(downloadPlayList) the bar ID is NULL");
        }
    }


}
