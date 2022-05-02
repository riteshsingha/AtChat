package com.example.atchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String LOGIN_CHECK = "LOGIN";
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static String userId;
    private TextView navUserName, navUserEmail;
    private CircleImageView navUserImg;
    private View navClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startConfigs();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        viewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyChatList(), "MyChat");
        adapter.addFragment(new UserList(), "Users");
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);


    }

    private void startConfigs() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        navUserImg = header.findViewById(R.id.imageView);
        navUserEmail = header.findViewById(R.id.tvEmail);
        navUserName = header.findViewById(R.id.tvName);
        navClick=header.findViewById(R.id.navClick);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Log.d(TAG, "onStart: "+currentUser.getEmail());

        if (currentUser != null) {

            Log.d(TAG, "onStart: Current User" + currentUser.getEmail());
            userId = currentUser.getUid();
            getUserData(userId);
            SharedPreferences prefs = getSharedPreferences(LOGIN_CHECK, MODE_PRIVATE);
            String name = prefs.getString("login", "false");
            Log.d(TAG, "onStart: Preference " + name);
            if (name.equals("false")) {

                //write user data
                FirebaseUser user = mAuth.getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("UserData").child("Users").child(user.getUid());
                Map map = new HashMap();
                map.put("userId", user.getUid());
                map.put("userName", user.getDisplayName());
                map.put("userEmail", user.getEmail());
                map.put("userImg", user.getPhotoUrl().toString());
                databaseReference.setValue(map).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: UserData Wrote");
                    }
                });


                SharedPreferences.Editor editor = getSharedPreferences
                        (LOGIN_CHECK, MODE_PRIVATE).edit();
                editor.putString("login", "true");
                editor.apply();
            }

        } else {
            Log.d(TAG, "onStart: Current User Null");
            startActivity(new Intent(getApplicationContext(), GoogleLogin.class));
            finish();
        }

    }

    private void getUserData(final String userId) {

        navClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UserProfile.class)
                        .putExtra("userId",userId));
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserData").child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Helper helper=dataSnapshot.getValue(Helper.class);
                Glide
                        .with(getApplicationContext())
                        .load(helper.getUserImg())
                        .centerInside()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.placeholder(R.drawable.gif_spinner_move_forward)
                        .into(navUserImg);

                navUserEmail.setText(helper.getUserEmail());
                navUserName.setText(helper.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_details) {
            Intent myIntent = new Intent(MainActivity.this, NewActivity.class);
            startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(getBaseContext(), "No actions added yet." , Toast.LENGTH_SHORT ).show();

        } else if (id == R.id.nav_manage) {
            Toast.makeText(getBaseContext(), "No actions added yet." , Toast.LENGTH_SHORT ).show();

        } else if (id == R.id.nav_share) {
            Toast.makeText(getBaseContext(), "No actions added yet." , Toast.LENGTH_SHORT ).show();

        } else if (id == R.id.nav_send) {
            Toast.makeText(getBaseContext(), "No actions added yet." , Toast.LENGTH_SHORT ).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
