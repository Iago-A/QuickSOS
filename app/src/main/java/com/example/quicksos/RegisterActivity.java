package com.example.quicksos;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button registerButton;

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
        registerButton = findViewById(R.id.registerButton);

        // Establish accessibility descriptions
        toolbar.setContentDescription("Toolbar");
        mailEditText.setContentDescription(getString(R.string.email_content_description));
        passwordEditText.setContentDescription(getString(R.string.password_content_description));
        repeatPasswordEditText.setContentDescription(getString(R.string.repeat_password_content_description));
        registerButton.setContentDescription(getString(R.string.register));

        // Buttons click configuration
        registerButton.setOnClickListener(v -> {
            String email = mailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String repeatPassword = repeatPasswordEditText.getText().toString().trim();

            validateUserData(email, password, repeatPassword);
        });

        // Show return button and tittle in toolbar
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.register));
        }
    }

    private void validateUserData(String email, String password, String repeatPassword) {
        if (email.isEmpty() && password.isEmpty() && repeatPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.three_empty_fields), Toast.LENGTH_SHORT).show();
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

        if (repeatPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.repeat_password_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, getString(R.string.short_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, getString(R.string.unmatched_password), Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(email, password);
    }

    private void registerUser(String email, String password) {
        registerButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Deactivate temporarily the button
                    registerButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.successful_register), Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, getString(R.string.email_in_use), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(this, getString(R.string.failed_register) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
