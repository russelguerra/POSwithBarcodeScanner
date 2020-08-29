package com.posbarcodescanner.rjg.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.posbarcodescanner.rjg.Adapter.ItemsAdapter;
import com.posbarcodescanner.rjg.Entity.Items;
import com.posbarcodescanner.rjg.Fragment.FragmentMain;
import com.posbarcodescanner.rjg.Fragment.FragmentRecord;
import com.posbarcodescanner.rjg.R;
import com.posbarcodescanner.rjg.ViewModel.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static final int NEW_ITEM_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_ITEM_ACTIVITY_REQUEST_CODE = 2;

    private MainActivityViewModel model;
    private ItemsAdapter itemsAdapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    private ImageView facebook, instagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsAdapter = new ItemsAdapter(this);
        model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        model.getAllItems().observe(this, new Observer<List<Items>>() {
            @Override
            public void onChanged(List<Items> items) {
                itemsAdapter.setItems(items);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        facebook = findViewById(R.id.facebook);
        instagram = findViewById(R.id.instagram);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                (R.string.open), (R.string.close));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.cashier);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                new FragmentMain()).commit();

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://facebook.com/russelguerra");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.facebook.katana");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://facebook.com/russelguerra")));
                }
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/russelguerra");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/russelguerra")));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == UPDATE_ITEM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Items item = new Items(data.getStringExtra(UPDATED_ITEM_NAME),
                    Double.parseDouble(data.getStringExtra(UPDATED_ITEM_PRICE)));
            item.setId(data.getIntExtra(ITEM_ID, 0));
            MainActivityViewModel.update(item);
        } else {
            Log.i(TAG, "onActivityResult: item not updated");
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cashier:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new FragmentMain()).commit();
                navigationView.setCheckedItem(R.id.cashier);
                break;

            case R.id.transactions:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new FragmentRecord()).commit();
                navigationView.setCheckedItem(R.id.transactions);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
