package team.projects.madquizapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    TextView emailError, passwordError;

    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);
        emailError = findViewById(R.id.login_email_error_text);
        passwordError = findViewById(R.id.login_password_error_text);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Connecting to server...");
        progressDialog.setCancelable(false);

    }

    public void loginUser(View view) {
        if (!validateData()){
            return;
        }

        progressDialog.show();
        auth.signInWithEmailAndPassword(emailInput.getText().toString(),passwordInput.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(LoginActivity.this, "Error for login => " + e.getMessage(), Toast.LENGTH_SHORT).show();
            passwordError.setVisibility(View.VISIBLE);
            passwordError.setText("User Details Invalid");
            try {
                progressDialog.dismiss();
            }catch (Exception ignored) {}
        });
    }

    private boolean validateData() {
        if (emailInput.getText().toString().equalsIgnoreCase("")){
            emailError.setText("Enter Valid Email Address");
            emailError.setVisibility(View.VISIBLE);
            return false;
        }
        emailError.setVisibility(View.INVISIBLE);
        if (passwordInput.getText().toString().equalsIgnoreCase("")){
            passwordError.setText("Enter Password");
            passwordError.setVisibility(View.VISIBLE);
            return false;
        }
        passwordError.setVisibility(View.INVISIBLE);
        return true;
    }

    public void signUpNavigate(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
        finish();
    }

    public void googleSignIn(View view) {
    }
}