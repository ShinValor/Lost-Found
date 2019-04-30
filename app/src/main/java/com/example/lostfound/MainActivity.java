package com.example.lostfound;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private SearchView searchView;
    private FloatingActionButton buttonCreate;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Context context = this;

    private LostFragment lostFrag = new LostFragment();
    private FoundFragment foundFrag = new FoundFragment();

    public static final String POST_ROUTE = "com.example.lostfound.postpage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        searchView = (SearchView) findViewById(R.id.searchView);
        buttonCreate = (FloatingActionButton) findViewById(R.id.buttonCreate);

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView)findViewById(R.id.navigation);

        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id) {
                    case R.id.navigation_item_1:
                        finish();
                        startActivity(new Intent(context, ProfileActivity.class));
                        return true;
                    case R.id.navigation_item_2:
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(context, LoginActivity.class));
                        return true;
                    default:
                        return true;
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() == 0){
                    lostFrag.refreshList(newText);
                }
                else if (viewPager.getCurrentItem() == 1) {
                    foundFrag.refreshList(newText);
                }
                return false;
            }
        });

        buttonCreate.setOnClickListener(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(lostFrag,"Lost");
        adapter.addFragment(foundFrag,"Found");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonCreate){
            finish();
            Intent intent = new Intent(this, PostActivity.class);
            if (viewPager.getCurrentItem() == 0){
                intent.putExtra(POST_ROUTE, "LOST");
            }
            else if (viewPager.getCurrentItem() == 1){
                intent.putExtra(POST_ROUTE, "FOUND");
            }
            startActivity(intent);
        }
    }
}