package team.projects.madquizapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import team.projects.madquizapplication.CommonData.CommonData;
import team.projects.madquizapplication.Models.UserModel;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    ImageButton imageButton;
    ConstraintLayout imageLayout;

    EditText name, phone;
    TextView email;

    String imageUrl = "default";

    FirebaseAuth auth;
    StorageReference storageReference;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    TextView editProfileBtn;

    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        imageButton = findViewById(R.id.editImageBtn);
        imageLayout = findViewById(R.id.editProfileDiv);

        profileImage.setOnClickListener(v -> getImage());
        imageButton.setOnClickListener(v -> getImage());
        imageLayout.setOnClickListener(v -> getImage());

        name = findViewById(R.id.edit_name);
        phone = findViewById(R.id.edit_phone);
        email = findViewById(R.id.profile_email);

        editProfileBtn=findViewById(R.id.editProfileBtn);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching Data From Server");
        progressDialog.setCancelable(false);

        name.setEnabled(false);
        phone.setEnabled(false);

        if (auth.getCurrentUser()==null){
            finish();
            return;
        }

        progressDialog.show();
        reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if ((user != null ? user.getEmail() : null) !=null){
                    setView(user);
                }else{
                    Toast.makeText(ProfileActivity.this, "Error to fetch from database", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error to fetch from database", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void getImage() {
        if(edit) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 11);
        }else{
            Toast.makeText(this, "Click Edit Button First", Toast.LENGTH_SHORT).show();
        }
    }

    private void setView(UserModel user) {
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        if(!user.getImage().equalsIgnoreCase("default")){
            Glide.with(this).load(user.getImage()).into(profileImage);
        }
    }

    public void editProfile(View view) {
        if (editProfileBtn.getText().toString().equalsIgnoreCase("Edit Profile")){
            editProfileBtn.setText("Save Data");
            edit = true;
            name.setEnabled(true);
            phone.setEnabled(true);
        }else{
            editProfileBtn.setText("Edit Profile");
            edit = false;
            name.setEnabled(false);
            phone.setEnabled(false);
            progressDialog.show();
//            save to database
            HashMap<String, Object> profileData = new HashMap<>();
            profileData.put("name",name.getText().toString());
            profileData.put("phone",phone.getText().toString());
            profileData.put("image",imageUrl);
            reference.child("Users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).updateChildren(profileData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri imageUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image Uploading");
            progressDialog.show();

            StorageReference uploadImage = FirebaseStorage.getInstance().getReference().child("images/"+auth.getCurrentUser().getUid());

            uploadImage.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded. Click Save data.", Toast.LENGTH_SHORT).show();
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(uri -> {
                            imageUrl=uri.toString();
                            Glide.with(this).load(imageUri).into(profileImage);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload"+exception, Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading :- "+(int)progressPercent+" %");
            });
        }else{
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}