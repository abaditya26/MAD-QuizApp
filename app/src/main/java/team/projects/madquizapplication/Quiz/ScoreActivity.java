package team.projects.madquizapplication.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.Models.ModelScore;
import team.projects.madquizapplication.R;

public class ScoreActivity extends AppCompatActivity {

    String total, correct, quizId;

    TextView totalView, correctView, quizIdView;

    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        total = intent.getStringExtra("TOTAL");
        correct = intent.getStringExtra("CORRECT");
        quizId = intent.getStringExtra("QUIZID");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        totalView = findViewById(R.id.total);
        correctView = findViewById(R.id.correct);
        quizIdView = findViewById(R.id.quiz_score_title);

        totalView.setText(" / "+total);
        correctView.setText(correct);
        quizIdView.setText(quizId);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        ModelScore score = new ModelScore(quizId, total, correct, uid);

        reference.child("Quiz").child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelQuizData quizData = snapshot.getValue(ModelQuizData.class);
                if(quizData!=null){
                    if(quizData.getQuizId()!=null){
                        reference.child("Users").child(uid).child("AttemptedQuiz").child(quizId).setValue(quizData).addOnSuccessListener(aVoid -> {
                            Toast.makeText(ScoreActivity.this, "Data Added to cloud", Toast.LENGTH_SHORT).show();
                            reference.child("OldQuiz").child(uid).child(quizId).setValue(score).addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    Toast.makeText(ScoreActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(ScoreActivity.this, "Unable to add to history", Toast.LENGTH_SHORT).show();
                                try{
                                    progressDialog.dismiss();
                                }catch (Exception ignored){}
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(ScoreActivity.this, "Failed to save data to cloud", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Toast.makeText(this, "TOTAL = "+total+", CORRECT = "+correct, Toast.LENGTH_SHORT).show();
    }

    public void endActivity(View view) {
        finish();
    }
}