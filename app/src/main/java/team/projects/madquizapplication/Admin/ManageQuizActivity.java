package team.projects.madquizapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import team.projects.madquizapplication.Adapter.AdapterAdminQuiz;
import team.projects.madquizapplication.LoginActivity;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class ManageQuizActivity extends AppCompatActivity {

    RecyclerView quizRecycler;
    AdapterAdminQuiz adapterAdminQuiz;

    DatabaseReference reference;
    FirebaseAuth auth;

    List<ModelQuizData> quizData;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quiz);

        try{
//        common code
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e){
            Log.e("ACTION BAR ERROR",e.getMessage());
        }

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();


        quizRecycler = findViewById(R.id.admin_quiz_list_recycler);

        if (auth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        reference.child("Users").child(auth.getCurrentUser().getUid()).child("Quiz").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizData = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelQuizData modelQuizData = snapshot1.getValue(ModelQuizData.class);
                    if (modelQuizData != null) {
                        if (modelQuizData.getQuizId() != null) {
                            quizData.add(modelQuizData);
                        }
                    }
                }

                if (quizData.size()==0){
                    quizData.add(new ModelQuizData("null","No Quiz Availiable","","","","",false));
                }

                quizRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                quizRecycler.setAdapter(new AdapterAdminQuiz(getApplicationContext(), quizData));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageQuizActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void addNewQuizNavigate(View view) {
        startActivity(new Intent(this, AddNewQuizActivity.class));
    }
}