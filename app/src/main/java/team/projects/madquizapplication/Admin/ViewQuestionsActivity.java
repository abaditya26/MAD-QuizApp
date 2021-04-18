package team.projects.madquizapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import team.projects.madquizapplication.Adapter.AdapterQuestion;
import team.projects.madquizapplication.LoginActivity;
import team.projects.madquizapplication.Models.ModelQuestions;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class ViewQuestionsActivity extends AppCompatActivity {


    List<ModelQuestions> questions;
    RecyclerView questionsRecycler;

    DatabaseReference reference;
    FirebaseAuth auth;
    String quizId;

    ModelQuizData quizData;

    TextView quizIdView, quizNameView, quizPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        quizIdView = findViewById(R.id.quiz_id);
        quizNameView = findViewById(R.id.quiz_name);
        quizPassView = findViewById(R.id.quiz_password);

        questionsRecycler = findViewById(R.id.questions_recycler);
        if (auth.getCurrentUser()==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        quizId = getIntent().getStringExtra("QUIZID");
        if (quizId.equalsIgnoreCase("") || quizId==null){
            Toast.makeText(this, "Error To Get Quiz ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reference.child("Quiz").child(quizId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questions = new ArrayList<>();
                try{
                    quizData = snapshot.getValue(ModelQuizData.class);
                    setLayout();
                }catch (Exception ignored){}
                for (DataSnapshot snapshot1:snapshot.child("Questions").getChildren()){
                    try {
                        ModelQuestions questionModel = snapshot1.getValue(ModelQuestions.class);
                        if (questionModel.getQuestion() != null) {
                            questions.add(questionModel);
                        }
                    }catch (Exception ignored){}
                }
                if (questions.size()==0){
                    questions.add(new ModelQuestions("none","NO QUESTIONS ADDED","","","","",""));
                }
                questionsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                questionsRecycler.setAdapter(new AdapterQuestion(questions, getApplicationContext(), quizId));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewQuestionsActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setLayout() {
        quizIdView.setText(quizData.getQuizId());
        quizNameView.setText(quizData.getQuizName());
        quizPassView.setText(quizData.getPassword());
        if (!quizData.isPrivate()){
            findViewById(R.id.pass_layout).setVisibility(View.GONE);
        }
    }

    public void addQuestionNavigate(View view) {
        Intent intent = new Intent(this, ActivityAddQuestions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("QUIZID", quizId);
        startActivity(intent);
    }
}