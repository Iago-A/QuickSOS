package com.example.quicksos;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configura el listener para manejar la selección de los íconos del menú
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.default_contacts) {
                    openFragment(new ContactsFragment());
                } else if (id == R.id.personal_contacts) {
                    openFragment(new PersonalContactsFragment());
                } else if (id == R.id.more) {
                    openFragment(new MoreFragment());
                }
                return true;
            }
        });

        // Carga el fragmento por defecto al inicio (Home)
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
}
