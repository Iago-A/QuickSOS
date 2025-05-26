package com.example.quicksos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("QuickSOSPrefs", MODE_PRIVATE);

        // Check if user is in guest mode
        boolean isGuestMode = sharedPreferences.getBoolean("isGuestMode", false);

        // If the user was logged before, it goes directly to MainActivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Iniciate Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Obtain the views references
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        TextView loginTextView = findViewById(R.id.loginTextView);
        EditText mailEditText = findViewById(R.id.mailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        TextView questionTextView = findViewById(R.id.questionTextView);
        Button registerButton = findViewById(R.id.registerButton);
        Button guestButton = findViewById(R.id.guestButton);

        // Establish accessibility descriptions
        welcomeTextView.setContentDescription(getString(R.string.welcome));
        loginTextView.setContentDescription(getString(R.string.login));
        mailEditText.setContentDescription(getString(R.string.email_content_description));
        passwordEditText.setContentDescription(getString(R.string.password_content_description));
        loginButton.setContentDescription(getString(R.string.login));
        questionTextView.setContentDescription(getString(R.string.account_question));
        registerButton.setContentDescription(getString(R.string.create_account));
        guestButton.setContentDescription(getString(R.string.continue_as_guest));

        // Buttons click configuration
        loginButton.setOnClickListener(v -> {
            String email = mailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            validateUserData(email, password);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        guestButton.setOnClickListener(v -> {
            enterGuestMode();
        });
    }

    private void validateUserData(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, getString(R.string.two_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        loginUser(email, password);
    }

    private void loginUser(String email, String password) {
        loginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Deactivate temporarily the button
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Clear guest mode and set authenticated mode
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isGuestMode", false);
                        editor.putBoolean("isAuthenticated", true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, getString(R.string.successful_login), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.failed_login), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enterGuestMode() {
        // Save guest mode state in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGuestMode", true);
        editor.putBoolean("isAuthenticated", false);
        editor.apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}