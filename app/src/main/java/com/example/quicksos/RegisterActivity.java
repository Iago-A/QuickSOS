package com.example.quicksos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Iniciate Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Obtain the views references
        Toolbar toolbar = findViewById(R.id.toolbar);
        EditText mailEditText = findViewById(R.id.mailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        // TODO Seguir por aquí Iago!! También arreglar error al iniciar sesión con campos vacíos y luego xml traducciones

        // Establish accessibility descriptions


        // Show return button and tittle in toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registro");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
