package tn.esprit.gestionuser;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.database.Cursor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignIn extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView signupTextView;
    private Button loginButton;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SignInActivity", "SignIn Activity started");

        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupTextView = findViewById(R.id.signupTextView);
        loginButton = findViewById(R.id.loginButton);
        userDatabaseHelper = new UserDatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            login(email, password);
        });

        signupTextView.setOnClickListener(view -> {
           // userDatabaseHelper.deleteAllUsers();
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        });
    }

    private void login(final String email, final String password) {
        // Hash the password with SHA-256 and Base64 to compare
        String hashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            hashedPassword = Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Validate the login credentials using local database instead of server
        Cursor cursor = userDatabaseHelper.getReadableDatabase().query(
                UserDatabaseHelper.TABLE_USERS,
                new String[]{UserDatabaseHelper._ID}, // Just need to check if a row exists
                UserDatabaseHelper.COLUMN_USER_EMAIL + "=? AND " + UserDatabaseHelper.COLUMN_USER_PASSWORD + "=?",
                new String[]{email, hashedPassword},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Credentials are correct
            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SignIn.this, testLogin.class);
            startActivity(intent);
            finish();
        } else {
            // Credentials are wrong
            Toast.makeText(getApplicationContext(), "Login Failed. Incorrect email or password.", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }





}
