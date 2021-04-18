package team.projects.madquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import team.projects.madquizapplication.Adapter.AdapterOldQuiz;
import team.projects.madquizapplication.Admin.ManageQuizActivity;
import team.projects.madquizapplication.CommonData.CommonData;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.Models.UserModel;
import team.projects.madquizapplication.Quiz.QuizActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog progressDialog;
    UserModel user;

    RecyclerView oldQuizRecycler;
    AdapterOldQuiz adapterOldQuiz;

    List<ModelQuizData> oldQuizData;

    TextView name, email, attemptedQuiz, createdQuizCount;

    LinearLayout adminViewLayout;

    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        reference = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        attemptedQuiz = findViewById(R.id.attemptedQuizCount);

        createdQuizCount = findViewById(R.id.created_quiz_count);
        adminViewLayout = findViewById(R.id.admin_view_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching User Data!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(auth.getCurrentUser().getUid())){
                    startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
                    finish();
                    progressDialog.dismiss();
                    return;
                }
                user = snapshot.child(auth.getCurrentUser().getUid()).getValue(UserModel.class);
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Welcome "+user.getName(), Toast.LENGTH_SHORT).show();
                    updateView();
                }else{
                    Toast.makeText(MainActivity.this, "User Data Not Fetched", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        oldQuizData = new ArrayList<>();

        reference.child("Users").child(auth.getCurrentUser().getUid()).child("AttemptedQuizs").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldQuizData = new ArrayList<>();

                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    ModelQuizData quizData = snapshot1.getValue(ModelQuizData.class);
                    if ((quizData != null ? quizData.getQuizName() : null) !=null){
                        oldQuizData.add(quizData);
                    }
                }

                try {
                    attemptedQuiz.setText(oldQuizData.size()+"");
                }catch (Exception e){
                    Log.e("ERROR",e.getMessage());
                }
                if (oldQuizData.size()==0){
                    oldQuizData.add(new ModelQuizData("null","NO QUIZ ATTEMPTED","null","null","null","null",false));
                }

                adapterOldQuiz = new AdapterOldQuiz(oldQuizData, getApplicationContext(),true);
                oldQuizRecycler = findViewById(R.id.old_quiz_recycler_view);

                oldQuizRecycler.setAdapter(adapterOldQuiz);
                oldQuizRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error => "+error.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void updateView() {
        name.setText(user.getName());
        email.setText(user.getEmail());
        if (user.getRole().equalsIgnoreCase("admin")){
            findViewById(R.id.admin_view_layout).setVisibility(View.VISIBLE);
            isAdmin = true;
        }else{
            findViewById(R.id.admin_view_layout).setVisibility(View.GONE);
            isAdmin = false;
        }
        CommonData.userData = user;
    }

    public void logOut(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm.")
                .setMessage("Do You Really Want To Log Out?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    dialog.dismiss();
                    auth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }).setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void attemptNewQuiz(View view) {
        startActivity(new Intent(this, ViewAllQuizActivity.class));
    }

    public void manageQuiz(View view) {
        startActivity(new Intent(this, ManageQuizActivity.class));
    }
}