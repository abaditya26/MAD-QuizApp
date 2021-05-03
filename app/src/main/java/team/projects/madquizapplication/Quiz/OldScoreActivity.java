package team.projects.madquizapplication.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team.projects.madquizapplication.Models.ModelScore;
import team.projects.madquizapplication.R;

public class OldScoreActivity extends AppCompatActivity {

    String total, correct, quizId;

    TextView totalView, correctView, quizIdView;

    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_score);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        totalView = findViewById(R.id.total);
        correctView = findViewById(R.id.correct);
        quizIdView = findViewById(R.id.quiz_score_title);

        quizId = getIntent().getStringExtra("QUIZID");

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("OldQuiz").child(auth.getCurrentUser().getUid()).child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelScore score = snapshot.getValue(ModelScore.class);
                progressDialog.dismiss();
                if (score != null) {
                    total = score.getTotal();
                    correct = score.getCorrect();
                    totalView.setText(" / "+total);
                    correctView.setText(correct);
                    quizIdView.setText(quizId);
                }else{
                    Toast.makeText(OldScoreActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(OldScoreActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    public void endActivity(View view) {
        finish();
    }

    public void reattempt(View view) {
        Intent intent = new Intent(this, QuizDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("QUIZID", quizId);
        startActivity(intent);
        finish();
    }
}