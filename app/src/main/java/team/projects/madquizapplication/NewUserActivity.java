package team.projects.madquizapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import team.projects.madquizapplication.Models.UserModel;

public class NewUserActivity extends AppCompatActivity {

    CircleImageView profileImage;
    ImageButton editBtn;
    TextView email;
    EditText name, phone;

    String imageUrl = "default";

    FirebaseAuth auth;
    DatabaseReference reference;

    StorageReference storageReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        profileImage = findViewById(R.id.new_user_image);
        editBtn = findViewById(R.id.new_user_image_edit);

        email = findViewById(R.id.new_user_email);
        name = findViewById(R.id.new_user_name);
        phone = findViewById(R.id.new_user_phone);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Checking User Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser()==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        email.setText(auth.getCurrentUser().getEmail());

        reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(auth.getCurrentUser().getUid())){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NewUserActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        editBtn.setOnClickListener(v -> editProfileImage());
        profileImage.setOnClickListener(v -> editProfileImage());

    }

    private void editProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,11);
    }

    public void createUser(View view) {
        if (!validateData()){
            return;
        }
        progressDialog.setMessage("Creating User");
        progressDialog.show();
        UserModel user = new UserModel(Objects.requireNonNull(auth.getCurrentUser()).getUid(), email.getText().toString(), name.getText().toString(), phone.getText().toString(), imageUrl);
        reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(NewUserActivity.this, "Error => " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    private boolean validateData() {
        if (name.getText().toString().equalsIgnoreCase("")){
            name.setError("Required Field");
            return false;
        }
        if (phone.getText().toString().equalsIgnoreCase("")){
            phone.setError("Required Field");
            return false;
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri imageUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image Uploading");
            progressDialog.show();

            StorageReference uploadImage = storageReference.child("images/"+auth.getCurrentUser().getUid());

            uploadImage.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(NewUserActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(uri -> {
                            imageUrl=uri.toString();
                            Glide.with(this).load(imageUri).into(profileImage);
                        });

                    })
                    .addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        Toast.makeText(NewUserActivity.this, "Failed to upload"+exception, Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading :- "+(int)progressPercent+" %");
            });
        }else{
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}