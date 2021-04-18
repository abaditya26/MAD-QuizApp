package team.projects.madquizapplication.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

import team.projects.madquizapplication.Models.ModelQuestions;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.Models.QuizQuestionModel;
import team.projects.madquizapplication.R;

public class QuizActivity extends AppCompatActivity {


    String quizId;
    FirebaseAuth auth;
    DatabaseReference reference;
    List<QuizQuestionModel> questions;

    ProgressDialog progressDialog;

    ProgressBar progressBar;

    int index = 0;
    int totalQuestions = 0;
    int correct = 0;

    TextView quizTitle, questionIndex, total;

    TextView question, option1, option2, option3, option4;

    RadioButton optionR1, optionR2, optionR3, optionR4;

    ModelQuizData quizData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizId = getIntent().getStringExtra("QUIZID");

        quizTitle = findViewById(R.id.quiz_name_title);
        questionIndex = findViewById(R.id.question_index);
        total = findViewById(R.id.total_questions);

        progressBar = findViewById(R.id.progress_questions);

        question = findViewById(R.id.quiz_question);
        option1 = findViewById(R.id.quiz_option1);
        option2 = findViewById(R.id.quiz_option2);
        option3 = findViewById(R.id.quiz_option3);
        option4 = findViewById(R.id.quiz_option4);

        optionR1 = findViewById(R.id.radio_option_1);
        optionR2 = findViewById(R.id.radio_option_2);
        optionR3 = findViewById(R.id.radio_option_3);
        optionR4 = findViewById(R.id.radio_option_4);

        if (quizId.equalsIgnoreCase("")){
            Toast.makeText(this, "Error to fetch quizId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        auth = FirebaseAuth.getInstance();
        reference  = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching Question Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        reference.child("Quiz").child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questions = new ArrayList<>();
                quizData = snapshot.getValue(ModelQuizData.class);
                for (DataSnapshot snapshot1 : snapshot.child("Questions").getChildren()){
                    ModelQuestions q = snapshot1.getValue(ModelQuestions.class);
                    if ((q != null ? q.getId() : null) !=null){
                        QuizQuestionModel qm = new QuizQuestionModel(
                                q.getId(),
                                q.getQuestion(),
                                q.getOption1(),
                                q.getOption2(),
                                q.getOption3(),
                                q.getOption4(),
                                q.getAnswer()
                        );
                        questions.add(qm);
                    }
                }
                updateUI();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI() {
        totalQuestions = questions.size();
        total.setText(" / "+totalQuestions);
        quizTitle.setText(quizData.getQuizName());
        loadQuestion();
    }

    private void loadQuestion() {
        enableInputs();
        progressBar.setMax(totalQuestions);
        progressBar.setProgress(index+1);
        question.setText(questions.get(index).getQuestion());
        option1.setText(questions.get(index).getOption1());
        option2.setText(questions.get(index).getOption2());
        option3.setText(questions.get(index).getOption3());
        option4.setText(questions.get(index).getOption4());
        findViewById(R.id.optionL1).setBackground(getDrawable(R.drawable.option_background));
        findViewById(R.id.optionL2).setBackground(getDrawable(R.drawable.option_background));
        findViewById(R.id.optionL3).setBackground(getDrawable(R.drawable.option_background));
        findViewById(R.id.optionL4).setBackground(getDrawable(R.drawable.option_background));

        questionIndex.setText((index+1)+"");
        boolean flag = true;
        if (questions.get(index).isSelected().equalsIgnoreCase(option1.getText().toString())){
            selectOption1(optionR1);
            flag = false;
        }
        if (questions.get(index).isSelected().equalsIgnoreCase(option2.getText().toString())){
            selectOption2(optionR2);
            flag = false;
        }
        if (questions.get(index).isSelected().equalsIgnoreCase(option3.getText().toString())){
            selectOption3(optionR3);
            flag = false;
        }
        if (questions.get(index).isSelected().equalsIgnoreCase(option4.getText().toString())){
            selectOption4(optionR4);
            flag = false;
        }

        if (flag) {
            setOptions(new RadioButton(this));
        }

    }

    void setOptions(RadioButton r){
        optionR1.setChecked(false);
        optionR2.setChecked(false);
        optionR3.setChecked(false);
        optionR4.setChecked(false);
        try {
            r.setChecked(true);
        }catch (Exception ignored){}
    }

    public void selectOption1(View view) {
        setOptions(optionR1);
        validateAnswer(1,  findViewById(R.id.optionL1));
    }

    private void validateAnswer(int i, View view) {
        String answer = "";
        switch (i){
            case 1:
                answer = option1.getText().toString();
                break;
            case 2:
                answer = option2.getText().toString();
                break;
            case 3:
                answer = option3.getText().toString();
                break;
            case 4:
                answer = option4.getText().toString();
                break;
            default:
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }
        questions.get(index).setSelected(answer);
        if (answer.equalsIgnoreCase(questions.get(index).getAnswer())){
            view.setBackground(getDrawable(R.drawable.background_option_correct));
            correct++;
        }else{
            view.setBackground(getDrawable(R.drawable.background_option_incorrect));
        }
        disableInputs();
    }

//        view.setBackground(getDrawable(R.drawable.background_option_correct));

    public void selectOption2(View view) {
        setOptions(optionR2);
        validateAnswer(2, findViewById(R.id.optionL2));
    }
    public void selectOption3(View view) {
        setOptions(optionR3);
        validateAnswer(3,  findViewById(R.id.optionL3));
    }
    public void selectOption4(View view) {
        setOptions(optionR4);
        validateAnswer(4,  findViewById(R.id.optionL4));
    }

    private void disableInputs(){
        optionR1.setEnabled(false);
        optionR2.setEnabled(false);
        optionR3.setEnabled(false);
        optionR4.setEnabled(false);
        findViewById(R.id.optionL1).setEnabled(false);
        findViewById(R.id.optionL2).setEnabled(false);
        findViewById(R.id.optionL3).setEnabled(false);
        findViewById(R.id.optionL4).setEnabled(false);
    }

    private void enableInputs(){
        optionR1.setEnabled(true);
        optionR2.setEnabled(true);
        optionR3.setEnabled(true);
        optionR4.setEnabled(true);
        findViewById(R.id.optionL1).setEnabled(true);
        findViewById(R.id.optionL2).setEnabled(true);
        findViewById(R.id.optionL3).setEnabled(true);
        findViewById(R.id.optionL4).setEnabled(true);
    }

    public void next(View view) {
        if (index<totalQuestions-1){
            index ++;
            loadQuestion();
            Log.e("INDEX",index+"");
            if (index==totalQuestions-1){
                ((TextView)findViewById(R.id.next_btn)).setText("SUBMIT");
            }else{
                ((TextView)findViewById(R.id.next_btn)).setText("NEXT");
            }
        }else if(index==totalQuestions-1){
//            submit
            submitExam();
        }else{
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitExam() {
        Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("CORRECT", correct+"");
        intent.putExtra("TOTAL", totalQuestions+"");
        intent.putExtra("QUIZID",quizId);
        startActivity(intent);
        finish();
    }

    public void prev(View view) {
        if (index>=1) {
            index--;
            loadQuestion();
        }
    }

    public void endExam(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm")
                .setMessage("Do You Want to quit exam?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    finish();
                    dialog.dismiss();
                }).setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

