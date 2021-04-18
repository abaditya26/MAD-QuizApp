package team.projects.madquizapplication.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import team.projects.madquizapplication.CommonData.CommonData;
import team.projects.madquizapplication.LoginActivity;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class AddNewQuizActivity extends AppCompatActivity {

    EditText quizId, quizName,  imageUrl, quizPassword;
    ImageView imageView;
    SwitchCompat privateSwitch;
    boolean isPrivate = false;
    boolean uploadImage = false;
    String url = "default", password = "default";

    FirebaseAuth auth;
    DatabaseReference reference;
    StorageReference mStorageReference;
    private Intent intentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_quiz);

        quizId = findViewById(R.id.new_quiz_id);
        quizName = findViewById(R.id.new_quiz_name);
        imageUrl = findViewById(R.id.new_quiz_image_url);
        imageView = findViewById(R.id.new_quiz_image_view);
        privateSwitch = findViewById(R.id.new_quiz_private);
        quizPassword = findViewById(R.id.new_quiz_password);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,11);
        });

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if (auth==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        privateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPrivate = isChecked;
            if (isChecked){
                quizPassword.setVisibility(View.VISIBLE);
            }else{
                quizPassword.setVisibility(View.GONE);
            }
        });

    }

    public void createQuiz(View view) {
        if (!validateData()){
            return;
        }

        reference.child("Quiz").child(quizId.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("quizId")){
                    Toast.makeText(AddNewQuizActivity.this, "Please Select Different Quiz Id", Toast.LENGTH_SHORT).show();
                }else{
                    if (uploadImage){
                        Uri imageUri = intentData.getData();
                        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
                        progressDialog.setTitle("Image Uploading");
                        try {
                            progressDialog.show();
                        }catch (Exception ignored) {
                            Toast.makeText(AddNewQuizActivity.this, "Please Wait Uploading Image", Toast.LENGTH_SHORT).show();
                        }
                        StorageReference uploadImage = mStorageReference.child("images/quiz/"+quizId.getText().toString());

                        uploadImage.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(uri -> {
                                        url=uri.toString();
                                        addQuiz();
                                    });
                                    Toast.makeText(AddNewQuizActivity.this, "Please Wait adding Quiz", Toast.LENGTH_SHORT).show();

                                })
                                .addOnFailureListener(exception -> {
                                    try{
                                        progressDialog.dismiss();
                                    }catch (Exception ignored){}
                                    Toast.makeText(getApplicationContext(), "Failed to upload"+exception, Toast.LENGTH_SHORT).show();
                                    Log.e("ERROR",exception.getMessage());
                                }).addOnProgressListener(s -> {
                            double progressPercent = (100.00 * s.getBytesTransferred() / s.getTotalByteCount());
                            progressDialog.setMessage("Uploading :- "+(int)progressPercent+" %");
                        });
                    }else {
                        addQuiz();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddNewQuizActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateData() {
        if (quizId.getText().toString().equalsIgnoreCase("")){
            quizId.setError("Required Field");
            return false;
        }
        if (quizName.getText().toString().equalsIgnoreCase("")){
            quizName.setError("Required Field");
            return false;
        }
        if (isPrivate) {
            if (quizPassword.getText().toString().equalsIgnoreCase("")) {
                quizPassword.setError("Required For Private Quiz");
                return false;
            } else {
                password = quizPassword.getText().toString();
            }
        }
        return true;
    }

    private void addQuiz(){
        ModelQuizData quizData = new ModelQuizData(quizId.getText().toString(),quizName.getText().toString(),url, CommonData.userData.getName(),auth.getCurrentUser().getUid(),password,isPrivate);
        reference.child("Users").child(auth.getCurrentUser().getUid()).child("Quiz").child(quizData.getQuizId()).setValue(quizData).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reference.child("Quiz").child(quizData.getQuizId()).setValue(quizData).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Toast.makeText(AddNewQuizActivity.this, "Quiz Added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), ViewQuestionsActivity.class);
                        intent.putExtra("QUIZID",quizData.getQuizId());
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AddNewQuizActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(AddNewQuizActivity.this, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show());

            }else{
                Toast.makeText(AddNewQuizActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(AddNewQuizActivity.this, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uploadImage = true;
            intentData = data;
            Uri imageUri = intentData.getData();
            Glide.with(getApplicationContext()).load(imageUri).into(imageView);
        }else{
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

}