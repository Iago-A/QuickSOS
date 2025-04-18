package com.example.quicksos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Iniciate Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Obtain the views references
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        TextView loginTextView = findViewById(R.id.loginTextView);
        EditText mailEditText = findViewById(R.id.mailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView questionTextView = findViewById(R.id.questionTextView);
        Button registerButton = findViewById(R.id.registerButton);

        // Establish accessibility descriptions
        welcomeTextView.setContentDescription("¡Bienvenido!");
        loginTextView.setContentDescription("Iniciar sesión");
        mailEditText.setContentDescription("Campo para escribir el correo electrónico");
        passwordEditText.setContentDescription("Campo para escribir la contraseña");
        loginButton.setContentDescription("Iniciar sesión");
        questionTextView.setContentDescription("¿No tienes una cuenta?");
        registerButton.setContentDescription("Crear cuenta");

        // Buttons click configuration
        loginButton.setOnClickListener(v -> {
            String email = mailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            loginUser(email, password);
        });

        registerButton.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error en autenticación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}