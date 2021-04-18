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
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText email, password, confirmPassword;
    TextView emailError, passwordError, confirmPasswordError;

    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirmPassword = findViewById(R.id.register_password_confirm);
        emailError = findViewById(R.id.register_email_error_text);
        passwordError = findViewById(R.id.register_password_error_text);
        confirmPasswordError = findViewById(R.id.register_password_confirm_error_text);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Creating User");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser()!=null){
            auth.signOut();
        }
    }


    public void signInNavigate(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean validateData() {
        if (email.getText().toString().equalsIgnoreCase("")){
            emailError.setVisibility(View.VISIBLE);
            emailError.setText("Email Required");
            return  false;
        }
        emailError.setVisibility(View.INVISIBLE);
        if (password.getText().toString().equalsIgnoreCase("")){
            passwordError.setVisibility(View.VISIBLE);
            passwordError.setText("Password Required");
            return false;
        }
        passwordError.setVisibility(View.INVISIBLE);
        if (confirmPassword.getText().toString().equalsIgnoreCase("")){
            confirmPasswordError.setVisibility(View.VISIBLE);
            confirmPasswordError.setText("Confirm Your Password");
            return false;
        }
        if (!confirmPassword.getText().toString().equalsIgnoreCase(password.getText().toString())){
            confirmPasswordError.setVisibility(View.VISIBLE);
            confirmPasswordError.setText("Both Password Not Matched!");
            return false;
        }
        confirmPasswordError.setVisibility(View.INVISIBLE);
        return true;

    }

    public void signUpUser(View view) {
        if (!validateData()){
            return;
        }
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
                finish();
            }
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(RegistrationActivity.this, "Error User Creation => "+e.getMessage(), Toast.LENGTH_SHORT).show();
            confirmPasswordError.setText("User Creation Error");
            confirmPasswordError.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        });
    }
}