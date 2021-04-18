package team.projects.madquizapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team.projects.madquizapplication.Adapter.AdapterAllQuiz;
import team.projects.madquizapplication.CommonData.CommonData;
import team.projects.madquizapplication.Models.ModelQuizData;

public class ViewAllQuizActivity extends AppCompatActivity {

    RecyclerView quizRecycler;
    DatabaseReference reference;

    List<ModelQuizData> quizData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_quiz);

        try{
//        common code
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e){
            Log.e("ACTION BAR ERROR",e.getMessage());
        }

        quizRecycler = findViewById(R.id.all_quiz_recycler);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Quiz").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizData = new ArrayList<>();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    ModelQuizData q = snapshot1.getValue(ModelQuizData.class);
                    if (q!=null && q.getQuizId()!=null){
                        quizData.add(q);
                    }
                }
                CommonData.quizData = quizData;
                if (quizData.size()==0){
                    quizData.add(new ModelQuizData("null","No Quiz Available","","","","",false));
                }
                quizRecycler.setAdapter(new AdapterAllQuiz(quizData, getApplicationContext()));
                quizRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAllQuizActivity.this, "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void navigateSearch(View view) {
        startActivity(new Intent(this, SearchQuizActivity.class));
    }
}