package team.projects.madquizapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import team.projects.madquizapplication.Adapter.AdapterAllQuiz;
import team.projects.madquizapplication.CommonData.CommonData;
import team.projects.madquizapplication.Models.ModelQuizData;

public class SearchQuizActivity extends AppCompatActivity {

    RecyclerView searchRecycler;
    LinearLayout noQuizLayout, searchLayout;
    EditText searchInput;

    List<ModelQuizData> quizData;

    boolean searchResult = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quiz);


        try {
//        common code
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_baseline_arrow_back_ios_24));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            Log.e("ACTION BAR ERROR", e.getMessage());
        }

        searchRecycler = findViewById(R.id.search_result_layout);
        noQuizLayout = findViewById(R.id.no_quiz_found_layout);
        searchLayout = findViewById(R.id.search_layout);
        searchInput = findViewById(R.id.search_input);

        quizData = CommonData.quizData;


    }

    @Override
    public void onBackPressed() {
        if (!searchResult) {
            finish();
        } else {
            searchResult = false;
            resetLayout();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void startQuiz(View view) {
    }

    public void searchQuiz(View view) {
        String searchText = searchInput.getText().toString();
        List<ModelQuizData> quizDataList = new ArrayList<>();
        if (searchText.equalsIgnoreCase("")) {
            searchInput.setError("Required Field");
            return;
        }

        for (ModelQuizData quiz : quizData) {
            if (quiz.getQuizId().contains(searchText) || quiz.getQuizName().contains(searchText) || quiz.getAuthorName().contains(searchText)) {
                quizDataList.add(quiz);
            }
        }

        if (quizDataList.size() == 0) {
            showNoQuizError();
            return;
        }
        viewQuizDetails();
        searchResult = true;

        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setAdapter(new AdapterAllQuiz(quizDataList, this));

    }

    public void resetLayout() {
        searchRecycler.setVisibility(View.GONE);
        noQuizLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
    }

    public void viewQuizDetails() {
        searchRecycler.setVisibility(View.VISIBLE);
        noQuizLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);
    }

    public void showNoQuizError() {
        searchRecycler.setVisibility(View.GONE);
        noQuizLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
    }
}