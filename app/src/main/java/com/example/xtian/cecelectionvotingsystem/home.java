package com.example.xtian.cecelectionvotingsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.xtian.cecelectionvotingsystem.ResultFragment.ResultFragment;
import com.example.xtian.cecelectionvotingsystem.adapter.NavDrawerListAdapter;
import com.example.xtian.cecelectionvotingsystem.fragment.CandidatesFragment;
import com.example.xtian.cecelectionvotingsystem.fragment.HomeFragment;
import com.example.xtian.cecelectionvotingsystem.fragment.PartyFragment;
import com.example.xtian.cecelectionvotingsystem.fragment.SetFragment;
import com.example.xtian.cecelectionvotingsystem.fragment.VoterFragment;
import com.example.xtian.cecelectionvotingsystem.model.NavDrawerItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Xtian on 11/12/2015.
 */
public class home extends ActionBarActivity {

    //statement and async for database
    private ProgressDialog pDialog;
    boolean authenticated = false;
    String statement;
    DBConnection dburl = new DBConnection();
    Connection conn;
    boolean connectionlost = true;


    private CountDownTimer countDownTimer; // built in android class
    // CountDownTimer
    private long totalTimeCountInMilliseconds; // total count down time in
    // milliseconds
    int time_progress; // time progress election
    int time_elapsed;  // time elapsed  election
    int hour_value; // admin set time
    String set_election_sidebar;
    String voters_sidebar;
    String party_sidebar;
    String candidates_sidebar;





    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;


    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;




    //intent displayView
    private static Integer IntentdisplyViews = 0;


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                home.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm choose...");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to logout?");



        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Intent intent = new Intent(home.this, Main_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        flagElectionAsync flagElectionAsync = new flagElectionAsync();
        flagElectionAsync.execute();



        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

//        navDrawerItems = new ArrayList<NavDrawerItem>();
//
//        // adding nav drawer items to array
//        // Home
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
//        // votes
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,"Vote: 22" +"Not vote :21"));
//        // party
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//        // candidates
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "Vote: 22" +"Not vote :21"));
//        // set election
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1),true,""+set_election_sidebar));
//        // election result
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
//
//        // Recycle the typed array
//        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
//        adapter = new NavDrawerListAdapter(getApplicationContext(),
//                navDrawerItems);
//        mDrawerList.setAdapter(adapter);


        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item

            Intent intent = getIntent();
            IntentdisplyViews = intent.getIntExtra("IntentdisplyView",0);

            displayView(IntentdisplyViews);
        }



    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new VoterFragment();
                break;
            case 2:
                fragment = new PartyFragment();
                break;
            case 3:
                fragment = new CandidatesFragment();
                break;
            case 4:
                fragment = new SetFragment();
                break;
            case 5:
                fragment = new ResultFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            //fragmentManager.popBackStack ();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
            mDrawerToggle.syncState();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    private void startTimer() {

        int input_value = time_elapsed;// second
        totalTimeCountInMilliseconds =  input_value * 1000; // second to milisecond

        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds,100) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {



                int days = (int) ((leftTimeInMilliseconds / 1000) / 86400);
                int hours = (int) (((leftTimeInMilliseconds / 1000)
                        - (days * 86400)) / 3600);
                int minutes = (int) (((leftTimeInMilliseconds / 1000)
                        - (days * 86400) - (hours * 3600)) / 60);
                int secondss = (int) ((leftTimeInMilliseconds / 1000) % 60);

                String format_time = String.format("%02dh %02dm %02ds",
                        hours, minutes, secondss);

//                textViewShowTime.setText(String.format("%02d", seconds / 60)
//                        + ":" + String.format("%02d", seconds % 60));
                //textViewShowTime.setText(format_time);
                set_election_sidebar = format_time;


                // format the textview to show the easily readable format
            }

            @Override
            public void onFinish() {
//                Notification n =
//                        new Notification(R.mipmap.ic_launcher, getString(R.string.noticeMe),
//                                System.currentTimeMillis());
//
//                Intent notIntent = new Intent(home.this, com.example.xtian.cecelectionvotingsystem.splash.class);
//
//                PendingIntent wrappedIntent = PendingIntent.getActivity(home.this, 0, notIntent,0);
//
//                n.setLatestEventInfo(getApplicationContext(), getString(R.string.title), getString(R.string.message), wrappedIntent);
//                n.flags |= Notification.FLAG_AUTO_CANCEL;
//                //n.flags |= Notification.DEFAULT_SOUND;
//                n.sound=(Settings.System.DEFAULT_NOTIFICATION_URI);
//                //n.flags |= Notification.DEFAULT_VIBRATE;
//                n.ledARGB = 0xff0000ff;
//                n.flags |= Notification.FLAG_SHOW_LIGHTS;
//
//                // Now invoke the Notification Service
//                String notifService = Context.NOTIFICATION_SERVICE;
//                NotificationManager mgr =
//                        (NotificationManager) getSystemService(notifService);
//                mgr.notify(0, n);
                //new StartTimerElectionAsync().execute();
            }
        }.start();


    }

    class flagElectionAsync extends AsyncTask<Void,Integer,Void>{

        boolean flag_NotStart = false;
        boolean flag_Start = false;
        boolean flag_finish = false;
        int count_students;
        int count_party;
        int count_candidates;

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT COUNT(*)AS COUNT FROM students";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        count_students = rs.getInt("COUNT");
                        ResultSet party_rs = st.executeQuery("SELECT COUNT(*)AS COUNT FROM party");
                        while (party_rs.next()){
                            count_party = party_rs.getInt("COUNT");
                        }
                        ResultSet candidates_rs = st.executeQuery("SELECT COUNT(*)AS COUNT FROM candidates");
                        while (candidates_rs.next()){
                            count_candidates = candidates_rs.getInt("COUNT");
                        }

                        rs = st.executeQuery("SELECT * FROM flag");
                        //set time for the election
                        while (rs.next()) {
                            int flag_election = rs.getInt("set_election");
                            publishProgress(flag_election);
                            if (flag_election == 0) {
                                flag_NotStart = true;
                            } else if (flag_election == 1) {
                                flag_Start = true;
                            } else if (flag_election == 2) {
                                flag_finish = true;
                            }
                        }
                    }




                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(home.this);
            pDialog.setMessage("Loading Election. Please wait..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //connection status
            if (connectionlost){

                //count students
                if (count_students == 0){
                    voters_sidebar = "No csv file upload";
                }else {
                    voters_sidebar = ""+count_students;
                }
                //count party
                if (count_party == 0){
                    party_sidebar = "New+";
                }else {
                    party_sidebar = ""+count_party;
                }
                //count candidates
                if (count_candidates == 0){
                    candidates_sidebar = "New+";
                }else {
                    candidates_sidebar = ""+count_candidates;
                }
                if (flag_NotStart){
                    //side bar
                    navDrawerItems = new ArrayList<NavDrawerItem>();
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,voters_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),true,party_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1),true,candidates_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
                    navMenuIcons.recycle();
                    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
                    mDrawerList.setAdapter(adapter);

                    pDialog.dismiss();
                }else if (flag_Start){
                    //side bar
                    navDrawerItems = new ArrayList<NavDrawerItem>();
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,voters_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),true,party_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1),true,candidates_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1),true,"Election is start!"));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
                    navMenuIcons.recycle();
                    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
                    mDrawerList.setAdapter(adapter);
                    new StartTimerElectionAsync().execute();
                }else if (flag_finish){
                    set_election_sidebar = "Election is end!";
                    //side bar
                    navDrawerItems = new ArrayList<NavDrawerItem>();
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,voters_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),true,party_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1),true,candidates_sidebar));
                    navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1),true,set_election_sidebar));
                    navDrawerItems.add(new NavDrawerItem("Result", navMenuIcons.getResourceId(5, -1),true,"New CEC officers!"));
                    navMenuIcons.recycle();
                    adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
                    mDrawerList.setAdapter(adapter);
                    pDialog.dismiss();

                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(home.this);
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            String flag_election = values[0].toString();
//            //Toast.makeText(getApplicationContext(), "" + flag_election, Toast.LENGTH_SHORT).show();
//            if (flag_election.equals("0")) {
//                //flag_NotStart = true;
//            } else if (flag_election.equals("1")) {
//                //flag_Start = true;
//            } else if (flag_election.equals("2")) {
//                Notification n =
//                        new Notification(R.mipmap.ic_launcher, getString(R.string.noticeMe),
//                                System.currentTimeMillis());
//
//                Intent notIntent = new Intent(home.this, com.example.xtian.cecelectionvotingsystem.splash.class);
//
//                PendingIntent wrappedIntent = PendingIntent.getActivity(home.this, 0, notIntent,0);
//
//                n.setLatestEventInfo(getApplicationContext(), getString(R.string.title), getString(R.string.message), wrappedIntent);
//                n.flags |= Notification.FLAG_AUTO_CANCEL;
//                //n.flags |= Notification.DEFAULT_SOUND;
//                n.sound=(Settings.System.DEFAULT_NOTIFICATION_URI);
//                //n.flags |= Notification.DEFAULT_VIBRATE;
//                n.ledARGB = 0xff0000ff;
//                n.flags |= Notification.FLAG_SHOW_LIGHTS;
//
//                // Now invoke the Notification Service
//                String notifService = Context.NOTIFICATION_SERVICE;
//                NotificationManager mgr =
//                        (NotificationManager) getSystemService(notifService);
//                mgr.notify(0, n);
//            }
        }
    }

    class StartTimerElectionAsync  extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            statement = "SELECT UNIX_TIMESTAMP() as time_span";

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                try {
                    conn = DriverManager.getConnection(dburl.getUrl());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(statement);

                    while (rs.next()) {
                        //set time for the election
                        int current_time = rs.getInt("time_span");
                        rs = st.executeQuery("SELECT * FROM election");
                        while (rs.next()){
                            int end_time = rs.getInt("end_election");
                            time_progress = end_time-rs.getInt("start_election");
                            if (end_time <= current_time){ // times up
                                // flag the student to vote
                                PreparedStatement stInsert = conn.prepareStatement("UPDATE flag SET set_election=?");
                                stInsert.setInt(1, 2);
                                stInsert.executeUpdate();
                                authenticated = false;
                            }else{ // start election
                                authenticated = true;
                                time_elapsed = end_time - current_time;
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    connectionlost = false;
                }
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

            //connection status
            if (connectionlost){
                //result
                if (authenticated){
                    //election start
                   startTimer();

                }else{
                    //election times up
                    startTimer();
                    countDownTimer.cancel();
                    set_election_sidebar = "Times Up!";


                }
            }else{
                //lost Connection
                AlertDialog.Builder alertConnectionLost = new AlertDialog.Builder(home.this);
                alertConnectionLost.setTitle("Connection..");
                alertConnectionLost.setMessage("No connection in database!");
                alertConnectionLost.setCancelable(false);
                alertConnectionLost.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        System.exit(0);
                    }
                });
                alertConnectionLost.show();
            }

        }
    }
}

