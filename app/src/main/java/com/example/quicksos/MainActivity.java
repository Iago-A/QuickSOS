package com.example.quicksos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private boolean isGuestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences and check user mode
        sharedPreferences = getSharedPreferences("QuickSOSPrefs", MODE_PRIVATE);
        isGuestMode = sharedPreferences.getBoolean("isGuestMode", false);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configure bottom navigation based on user mode
        setupBottomNavigation();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.default_contacts) {
                    openFragment(new ContactsFragment());
                } else if (id == R.id.personal_contacts) {
                    if (!isGuestMode) {
                        openFragment(new PersonalContactsFragment());
                    } else {
                        openFragment(new GuestPersonalContactsFragment());
                    }
                } else if (id == R.id.more) {
                    MoreFragment moreFragment = new MoreFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isGuestMode", isGuestMode);
                    moreFragment.setArguments(bundle);
                    openFragment(moreFragment);
                }
                return true;
            }
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.default_contacts);
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_frame_layout, fragment)
                .commit();
    }

    private void setupBottomNavigation() {
        if (isGuestMode) {
            // For guest mode, we could hide the personal contacts tab or keep it with limited functionality
            // Let's keep it visible but it will show a different content
            // No changes needed to the menu in this approach
        }
        // If we wanted to hide the personal contacts tab for guests:
        // bottomNavigationView.getMenu().findItem(R.id.personal_contacts).setVisible(!isGuestMode);
    }

    public boolean isGuestMode() {
        return isGuestMode;
    }

    public SharedPreferences getAppPreferences() {
        return sharedPreferences;
    }
}
