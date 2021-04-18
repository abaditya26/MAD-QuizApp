package team.projects.madquizapplication.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import team.projects.madquizapplication.LoginActivity;
import team.projects.madquizapplication.Models.ModelQuestions;
import team.projects.madquizapplication.R;

public class ActivityAddQuestions extends AppCompatActivity {

    int answerIndex = -1;
    EditText question, option1, option2, option3, option4;
    String answer = "";

    DatabaseReference reference;
    FirebaseAuth auth;
    String quizId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        reference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        quizId = getIntent().getStringExtra("QUIZID");
        if (quizId.equalsIgnoreCase("")){
            Toast.makeText(this, "Unable to fetch quiz id", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void option1Select(View view) {
        answerIndex = 0;
    }

    public void option2Select(View view) {
        answerIndex = 1;
    }

    public void option3Select(View view) {
        answerIndex = 2;
    }

    public void option4Select(View view) {
        answerIndex = 3;
    }

    public void addQuestion(View view) {
        if (!validateData()){
            return;
        }
//        do update stuff
        switch (answerIndex){
            case 0:
                answer = option1.getText().toString();
                break;
            case 1:
                answer = option2.getText().toString();
                break;
            case 2:
                answer = option3.getText().toString();
                break;
            case 3:
                answer = option4.getText().toString();
                break;
            default:
                answer = "";
                Toast.makeText(this, "Err", Toast.LENGTH_SHORT).show();
                return;
        }
        ModelQuestions questions = new ModelQuestions(getRandomString(16),question.getText().toString(),option1.getText().toString(),option2.getText().toString(),option3.getText().toString(),option4.getText().toString(),answer);
        reference.child("Quiz").child(quizId).child("Questions").child(questions.getId()).setValue(questions).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(ActivityAddQuestions.this, "Question Added", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> Toast.makeText(ActivityAddQuestions.this, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateData() {
        if (question.getText().toString().equalsIgnoreCase("")){
            question.setError("Required");
            return false;
        }
        if (option1.getText().toString().equalsIgnoreCase("")){
            option1.setError("Required");
            return false;
        }
        if (option2.getText().toString().equalsIgnoreCase("")){
            option2.setError("Required");
            return false;
        }
        if (option3.getText().toString().equalsIgnoreCase("")){
            option3.setError("Required");
            return false;
        }
        if (option4.getText().toString().equalsIgnoreCase("")){
            option4.setError("Required");
            return false;
        }
        if (answerIndex == -1){
            ((RadioButton)findViewById(R.id.optionr1)).setError("An Option Must Be Selected As Answer");
            return false;
        }
        return true;
    }


    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}