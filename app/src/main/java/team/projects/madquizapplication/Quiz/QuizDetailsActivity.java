package team.projects.madquizapplication.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class QuizDetailsActivity extends AppCompatActivity {

    String quizId = "";
    TextView quizIdView, quizNameView, quizAuthorView;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        quizId = getIntent().getStringExtra("QUIZID");
        if (quizId.equalsIgnoreCase("")){
            Toast.makeText(this, "Error Unable to get Quiz Id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quizIdView = findViewById(R.id.quiz_details_id);
        quizNameView = findViewById(R.id.quiz_details_name);
        quizAuthorView = findViewById(R.id.quiz_details_author_name);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Quiz").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelQuizData  quizData = snapshot.getValue(ModelQuizData.class);
                if ((quizData != null ? quizData.getAuthorName() : null) !=null){
                    setView(quizData);
                    return;
                }
                Toast.makeText(QuizDetailsActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizDetailsActivity.this, "Error to get quiz Data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setView(ModelQuizData quizData) {
        quizIdView.setText(quizData.getQuizId());
        quizNameView.setText(quizData.getQuizName());
        quizAuthorView.setText(quizData.getAuthorName());
    }

    public void startQuiz(View view) {
//        TODO:start quiz
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("QUIZID",quizId);
        startActivity(intent);
    }
}