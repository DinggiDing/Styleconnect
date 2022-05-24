package com.example.styleconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    fragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton btnSet = findViewById(R.id.btnSet);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    case R.id.dashboard: {
                        startActivity(new Intent(getApplicationContext(), LeftTab.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                    case R.id.home: {
                        return true;
                    }

                    case R.id.about: {
                        startActivity(new Intent(getApplicationContext(), RightTab.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }

                }
                return  false;
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.addTab((tabLayout.newTab().setText("상의")));
        tabLayout.addTab((tabLayout.newTab().setText("하의")));
        tabLayout.addTab((tabLayout.newTab().setText("외투")));
        tabLayout.addTab((tabLayout.newTab().setText("기타")));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentAdapter = new fragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                fragmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SetActivity.class);
                startActivityForResult(intent, 888);
            }
        });

    }
}