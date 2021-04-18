package team.projects.madquizapplication.Quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import team.projects.madquizapplication.R;

public class QuizActivity extends AppCompatActivity {


    String quizId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizId = getIntent().getStringExtra("QUIZID");
    }
}