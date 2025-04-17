package com.example.quicksos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    }
}