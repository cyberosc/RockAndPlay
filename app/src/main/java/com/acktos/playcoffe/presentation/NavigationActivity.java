package com.acktos.playcoffe.presentation;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.acktos.playcoffe.R;
import com.acktos.playcoffe.controllers.BaseController;
import com.acktos.playcoffe.controllers.SessionsController;
import com.acktos.playcoffe.controllers.UsersController;
import com.acktos.playcoffe.models.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity implements
        SessionsFragment.OnSessionJoinedListener,
        PlayListFragment.OnFragmentInteractionListener {


    //UI references
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView txtEmailNavigation;
    private TextView txtNameNavigation;
    CircleImageView imgProfileNavigation;

    //Components
    UsersController usersController;
    SessionsController sessionsController;

    //Attributes
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        txtEmailNavigation=(TextView) findViewById(R.id.txt_email_navigation);
        txtNameNavigation=(TextView) findViewById(R.id.txt_name_navigation);
        imgProfileNavigation=(CircleImageView) findViewById(R.id.img_profile_navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        //Initialize components
        usersController=new UsersController(this);
        sessionsController=new SessionsController(this);


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_bars:
                        Toast.makeText(getApplicationContext(), "Inbox Selected", Toast.LENGTH_SHORT).show();
                        //ContentFragment fragment = new ContentFragment();
                        //android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        //fragmentTransaction.replace(R.id.frame, fragment);
                        //fragmentTransaction.commit();
                        return true;

                    // For rest of the options we just show a toast on click

                    /*case R.id.starred:
                        Toast.makeText(getApplicationContext(), "Stared Selected", Toast.LENGTH_SHORT).show();
                        return true;*/

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }

        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.common_open_on_phone, R.string.cancel) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //Get current user from local storage
        user=usersController.getUser();

        //Set user data into navigationView
        setNavigationUserInfo();

        //add fragment to frame
        addRightSessionFragment();

    }

    private void setNavigationUserInfo(){

        if(user!=null){
            txtEmailNavigation.setText(user.getEmail());
            txtNameNavigation.setText(user.getName());
            String profilePic;
            profilePic=BaseController.getProfileImageUrl(user.getProfilePic());

            if(profilePic!=null){

                Picasso.with(this)
                        .load(profilePic)
                        .placeholder(R.drawable.ic_music_note_black_24dp)
                        .into(imgProfileNavigation);
            }else{
                imgProfileNavigation.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * This method checks if the current user is attached to some session, if yes,
     * shows playListFragment, if not, shows current available sessions.
     */
    private void addRightSessionFragment(){

        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();

        if(sessionsController.isUserJoinedToSession()){

            PlayListFragment playListFragment=PlayListFragment.newInstance();
            fragmentTransaction.replace(R.id.fragment,playListFragment);
            fragmentTransaction.commit();

        }else{

            SessionsFragment sessionFragment=SessionsFragment.newInstance();
            fragmentTransaction.replace(R.id.fragment,sessionFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_track_action) {
            Intent i=new Intent(this,SearchableActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSessionJoined() {

        Log.i(BaseController.TAG,"session joined");
        addRightSessionFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
