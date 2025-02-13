package com.example.gomates;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomates.database.MySQLDatabaseHelper;
import com.example.gomates.models.User;
import com.example.gomates.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

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

        new RegisterTask().execute(email, password);
    }

    private class RegisterTask extends AsyncTask<String, Void, Long> {
        private String email;
        private String password;

        @Override
        protected Long doInBackground(String... params) {
            email = params[0];
            password = params[1];

            // Check if user already exists
            if (MySQLDatabaseHelper.getUserByEmail(email) != null) {
                return -2L; // User exists
            }

            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setName(email.split("@")[0]); // Use part before @ as name

            return MySQLDatabaseHelper.insertUser(newUser);
        }

        @Override
        protected void onPostExecute(Long result) {
            if (result == -2) {
                Toast.makeText(RegisterActivity.this, "Email already registered",
                        Toast.LENGTH_SHORT).show();
            } else if (result != -1) {
                sessionManager.createLoginSession(String.valueOf(result), email);
                Toast.makeText(RegisterActivity.this, "Registration successful",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}