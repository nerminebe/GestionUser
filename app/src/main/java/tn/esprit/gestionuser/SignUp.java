package tn.esprit.gestionuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import android.widget.Toast;


public class SignUp extends AppCompatActivity {

    private TextView textView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private Button signupButton;

    private static final String SIGNUP_URL = "http://192.168.1.112/LoginRegister/signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textView = findViewById(R.id.loginTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String username = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            signUp(phone, email, username, password);
        });

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        });

        TextView listUsersTextView = findViewById(R.id.tv_listUsers);
        listUsersTextView.setOnClickListener(v -> {
            Intent displayUsersIntent = new Intent(SignUp.this, DisplayUsersActivity.class);
            startActivity(displayUsersIntent);
        });
    }

    private void signUp(final String phone, final String email, final String username, final String password) {
        // Open the database for writing
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if email, phone, or username already exists
        if (isUserExist(db, phone, email, username)) {
            // User exists, show an error message
            Toast.makeText(this, "User with this phone, email or username already exists!", Toast.LENGTH_LONG).show();
            return; // Stop the signUp operation
        }

        // Hash the password with SHA-256 and Base64
        String safePasswordHash = hashPassword(password);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_USER_NAME, username);
        values.put(UserDatabaseHelper.COLUMN_USER_EMAIL, email);
        values.put(UserDatabaseHelper.COLUMN_USER_PASSWORD, safePasswordHash);
        values.put(UserDatabaseHelper.COLUMN_USER_PHONE, phone);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserDatabaseHelper.TABLE_USERS, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "SignUp Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUserExist(SQLiteDatabase db, String phone, String email, String username) {
        String[] columns = {UserDatabaseHelper._ID};
        String selection = UserDatabaseHelper.COLUMN_USER_PHONE + "=? OR " +
                UserDatabaseHelper.COLUMN_USER_EMAIL + "=? OR " +
                UserDatabaseHelper.COLUMN_USER_NAME + "=?";
        String[] selectionArgs = {phone, email, username};
        Cursor cursor = db.query(UserDatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }




}
