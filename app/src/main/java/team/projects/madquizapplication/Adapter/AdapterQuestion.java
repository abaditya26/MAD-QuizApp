package team.projects.madquizapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.projects.madquizapplication.Admin.ViewQuestionDetailsActivity;
import team.projects.madquizapplication.Models.ModelQuestions;
import team.projects.madquizapplication.R;

public class AdapterQuestion extends RecyclerView.Adapter<AdapterQuestion.ViewHolder> {

    List<ModelQuestions> questions;
    Context ctx;
    String quizId;

    public AdapterQuestion(List<ModelQuestions> questions, Context ctx, String quizId) {
        this.questions = questions;
        this.ctx = ctx;
        this.quizId = quizId;
    }

    @NonNull
    @Override
    public AdapterQuestion.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recycler_question,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterQuestion.ViewHolder holder, int position) {
        holder.setData(questions, position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.question_title);
        }

        public void setData(List<ModelQuestions> questions, int position) {
            questionTitle.setText(questions.get(position).getQuestion());
            itemView.setOnClickListener(v -> {
                if (!questions.get(position).getId().equalsIgnoreCase("none")){
                    Intent intent =  new Intent(ctx, ViewQuestionDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("QUIZID",quizId);
                    intent.putExtra("QUESTIONID", questions.get(position).getId());
                    ctx.startActivity(intent);
                }
            });
        }
    }
}
