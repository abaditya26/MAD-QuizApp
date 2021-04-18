package team.projects.madquizapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import team.projects.madquizapplication.Models.ModelQuestions;
import team.projects.madquizapplication.R;

public class ViewQuestionDetailsActivity extends AppCompatActivity {

    String quizId, questionId;

    TextView question, option1, option2, option3, option4;


    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question_details);

        quizId = getIntent().getStringExtra("QUIZID");
        questionId = getIntent().getStringExtra("QUESTIONID");

        if (quizId.equalsIgnoreCase("") || quizId==null || questionId.equalsIgnoreCase("")||questionId==null){
            Toast.makeText(this, "Error to fetch the required data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        question = findViewById(R.id.question_text);
        option1 = findViewById(R.id.option_1_text);
        option2 = findViewById(R.id.option_2_text);
        option3 = findViewById(R.id.option_3_text);
        option4 = findViewById(R.id.option_4_text);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Quiz").child(quizId).child("Questions").child(questionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelQuestions questions = snapshot.getValue(ModelQuestions.class);
                if (questions.getId()!=null){
                    setLayoutData(questions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewQuestionDetailsActivity.this, "Error 21 => "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLayoutData(ModelQuestions questions) {
        question.setText(questions.getQuestion());
        option1.setText(questions.getOption1());
        option2.setText(questions.getOption2());
        option3.setText(questions.getOption3());
        option4.setText(questions.getOption4());

        if (questions.getAnswer().equalsIgnoreCase(questions.getOption1())){
            option1.setTextColor(Color.GREEN);
        }
        if (questions.getAnswer().equalsIgnoreCase(questions.getOption2())){
            option2.setTextColor(Color.GREEN);
        }
        if (questions.getAnswer().equalsIgnoreCase(questions.getOption3())){
            option3.setTextColor(Color.GREEN);
        }
        if (questions.getAnswer().equalsIgnoreCase(questions.getOption4())){
            option4.setTextColor(Color.GREEN);
        }

    }
}