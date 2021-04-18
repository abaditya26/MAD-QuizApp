package team.projects.madquizapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.Quiz.QuizDetailsActivity;
import team.projects.madquizapplication.R;

public class AdapterAllQuiz extends RecyclerView.Adapter<AdapterAllQuiz.ViewHolder> {

    List<ModelQuizData> quizData;
    Context ctx;

    public AdapterAllQuiz(List<ModelQuizData> quizData, Context ctx) {
        this.quizData = quizData;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public AdapterAllQuiz.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recycler_all_quiz,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllQuiz.ViewHolder holder, int position) {
        holder.setData(quizData, position);
    }

    @Override
    public int getItemCount() {
        if (quizData == null){
            return 0;
        }
        return quizData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView quizImage, lock;
        TextView quizTitle, authorName, quizId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quizImage = itemView.findViewById(R.id.quiz_image);
            quizTitle = itemView.findViewById(R.id.quiz_title);
            authorName = itemView.findViewById(R.id.author_name);
            quizId = itemView.findViewById(R.id.quiz_id_text);

            lock = itemView.findViewById(R.id.quiz_private);
        }

        public void setData(List<ModelQuizData> quizData, int position) {
            if (!quizData.get(position).getQuizImage().equalsIgnoreCase("")){
                Glide.with(ctx).load(quizData.get(position).getQuizImage()).into(quizImage);
            }
            quizTitle.setText(quizData.get(position).getQuizName());
            authorName.setText(quizData.get(position).getAuthorName());
            quizId.setText(quizData.get(position).getQuizId());

            if (quizData.get(position).isPrivate()){
                lock.setVisibility(View.VISIBLE);
            }else{
                lock.setVisibility(View.GONE);
            }

            if (quizData.get(position).getQuizId().equalsIgnoreCase("null")){
                return;
            }
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, QuizDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("QUIZID", quizData.get(position).getQuizId());
                ctx.startActivity(intent);
            });

        }
    }
}
