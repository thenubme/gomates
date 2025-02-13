package com.example.gomates;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomates.database.DatabaseHelper;
import com.example.gomates.models.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already exists
        if (dbHelper.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setName(email.split("@")[0]); // Use part before @ as name

        long userId = dbHelper.insertUser(newUser);
        if (userId != -1) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Registration failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}