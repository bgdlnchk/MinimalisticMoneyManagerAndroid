package com.bogdanlonchuk.minimalisticmoney.activities;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.bogdanlonchuk.minimalisticmoney.R;
import com.bogdanlonchuk.minimalisticmoney.fragments.UserDashboardFragment;
import com.bogdanlonchuk.minimalisticmoney.fragments.UserExpenseFragment;
import com.bogdanlonchuk.minimalisticmoney.fragments.UserIncomeFragment;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set toolbar for application
        Toolbar homeToolbar = findViewById(R.id.homeToolbar);
        homeToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(homeToolbar);
        //Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawerLayout,homeToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView drawerNavigationView = findViewById(R.id.drawerNavigationView);
        drawerNavigationView.setNavigationItemSelectedListener(this);
        UserDashboardFragment mDashboardFragmentUser = new UserDashboardFragment();
        setCurrentFragment(mDashboardFragmentUser);
    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    //Set onClickListeners for Navigation Drawer buttons
    public void showSelectedListener(int itemId){
        Fragment currentFragment = null;

        switch (itemId){
            case R.id.dashboard:
                currentFragment=new UserDashboardFragment();
                break;

            case R.id.income:
                currentFragment=new UserIncomeFragment();
                break;

            case R.id.expense:
                currentFragment=new UserExpenseFragment();
                break;

            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download this application from Google Play Store");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.log_out:
                mAuth.signOut();
                Intent logOutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logOutIntent);
                break;
        }

        if (currentFragment!=null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,currentFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        showSelectedListener(item.getItemId());
        return true;
    }
}
