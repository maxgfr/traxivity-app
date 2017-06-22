package com.fanny.traxivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.fanny.traxivity.database.dayTiming.DayTimingManager;
import com.fanny.traxivity.database.dayTiming.DbTiming;
import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;
import com.fanny.traxivity.model.Alarm;
import com.fanny.traxivity.model.SendFileService;
import com.fanny.traxivity.model.SetAlarm;
import com.fanny.traxivity.view.AddNewActivity;
import com.fanny.traxivity.admin.view.activities.MainMenu;
import com.fanny.traxivity.model.SlidingTabLayout;
import com.fanny.traxivity.model.ViewPagerAdapter;
import com.fanny.traxivity.view.GoalInputActivity;
import com.fanny.traxivity.view.LoginActivity;
import com.fanny.traxivity.view.SettingsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;

    private DatabaseReference mDataBase;
    private CharSequence Titles[]={"Day","Week","Month"};
    private String usernameString, emailString;
    private TextView username, email;

    private int gStepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menuNav = navigationView.getMenu();

        final MenuItem addNewActivity = menuNav.findItem(R.id.new_activity);
        addNewActivity.setEnabled(false);
        final MenuItem adminItem = menuNav.findItem(R.id.nav_admin);
        adminItem.setEnabled(false);

        int numboftabs = 3;
        ViewPagerAdapter adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles, numboftabs);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorPrimaryDark);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GoalInputActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        //SetAlarm.setAlarm(this);

        username = (TextView) hView.findViewById(R.id.username);
        email = (TextView) hView.findViewById(R.id.email);

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(userId)) {
                        username.setText(child.child("username").getValue().toString());
                        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        usernameString = String.valueOf(username.getText());
                        emailString = String.valueOf(email.getText());
                        adminItem.setEnabled(true);
                        addNewActivity.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //.enableAutoManage()
                .build();

        mApiClient.connect();

    }

    public void createFolder(String nameFolder) {
        File myDir = new File(getFilesDir() + nameFolder);

        if (!myDir.exists()) {
            myDir.mkdir();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        else if (id == R.id.new_goal) {
            startActivity(new Intent(getApplicationContext(), GoalInputActivity.class));
        }
        else if (id == R.id.new_activity) {
            startActivity(new Intent(getApplicationContext(), AddNewActivity.class));
        }
        else if (id == R.id.nav_admin) {
            if(emailString.equals("admin@gmail.com") && usernameString.equals("Admin")) {
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
            }
        }
        else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("Connected");
        Timer timer = new Timer();
        final Handler handler = new Handler();

        final StepsManager managerSteps = new StepsManager();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_STEP_COUNT_DELTA).await(5, TimeUnit.SECONDS);
                DataSet data = result.getTotal();
                if (data.getDataPoints().size() > 0) {
                    final DataPoint dataPoint = data.getDataPoints().get(0);

                    final Field field = dataPoint.getDataType().getFields().get(0);

                    int stepcount = dataPoint.getValue(field).asInt();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    Date d = cal.getTime();
                    DbSteps newActivity = new DbSteps(d, stepcount);
                    managerSteps.insertNew(newActivity);
                    if (stepcount != gStepCount) {
                        gStepCount = stepcount;
                        MainActivity.this.sendBroadcast(new Intent().setAction("bcNewSteps"));
                    }

                    /*
                    handler.post(new Runnable() {

                        public void run() {
                            stepsView.setText(field.getName() + ": " + dataPoint.getValue(field));
                        }

                    });
                    */
                    System.out.println("GFit Service"+field.getName() + ": " + dataPoint.getValue(field));
                }
            }
        }, 0, 30000);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Google Fit", "Connection Failed");
        if (!authInProgress){
            try{
                authInProgress = true;
                connectionResult.startResolutionForResult(this, REQUEST_OAUTH);
            }catch(IntentSender.SendIntentException e){
                System.out.println("authInProgress");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_OAUTH){
            authInProgress = false;
            if (resultCode == RESULT_OK){
                if(!mApiClient.isConnecting() && !mApiClient.isConnected()){
                    mApiClient.connect();
                }
            }else if(resultCode == RESULT_CANCELED){
                System.out.println("RESULT_CANCELED");
            }
        }else{
            System.out.println("request code not REQUEST_OAUTH");
        }
    }
}
