package com.fanny.traxivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
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
import com.fanny.traxivity.model.Alarm;
import com.fanny.traxivity.model.SetAlarm;
import com.fanny.traxivity.view.AddNewActivity;
import com.fanny.traxivity.admin.view.activities.MainMenu;
import com.fanny.traxivity.model.SlidingTabLayout;
import com.fanny.traxivity.model.ViewPagerAdapter;
import com.fanny.traxivity.view.GoalInputActivity;
import com.fanny.traxivity.view.LoginActivity;
import com.fanny.traxivity.view.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Calendar;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDataBase;
    private CharSequence Titles[]={"Day","Week","Month"};
    private String usernameString, emailString;
    private TextView username, email;

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
}
