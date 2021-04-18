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

import team.projects.madquizapplication.Admin.ViewQuestionsActivity;
import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class AdapterAdminQuiz extends RecyclerView.Adapter<AdapterAdminQuiz.ViewHolder> {
    Context ctx;
    List<ModelQuizData> quizData;

    public AdapterAdminQuiz(Context ctx, List<ModelQuizData> quizData) {
        this.ctx = ctx;
        this.quizData = quizData;
    }

    @NonNull
    @Override
    public AdapterAdminQuiz.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterAdminQuiz.ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recycler_quiz,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAdminQuiz.ViewHolder holder, int position) {
        holder.setData(quizData, position);
    }

    @Override
    public int getItemCount() {
        if (quizData.size()==0){
            return 0;
        }
        return quizData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView quizImage, lock;
        TextView quizTitle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quizImage = itemView.findViewById(R.id.old_quiz_image);
            quizTitle = itemView.findViewById(R.id.old_quiz_title);
            lock = itemView.findViewById(R.id.private_icon);
        }

        public void setData(List<ModelQuizData> quizData, int position) {
            if (!quizData.get(position).getQuizImage().equalsIgnoreCase("") || quizData.get(position).getQuizImage().equalsIgnoreCase("default")) {
                Glide.with(ctx).load(quizData.get(position).getQuizImage()).into(quizImage);
            }
            quizTitle.setText(quizData.get(position).getQuizName());

            if (quizData.get(position).isPrivate()){
                lock.setVisibility(View.VISIBLE);
            }else{
                lock.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ctx, ViewQuestionsActivity.class);
                intent.putExtra("QUIZID", quizData.get(position).getQuizId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            });
        }
    }
}
