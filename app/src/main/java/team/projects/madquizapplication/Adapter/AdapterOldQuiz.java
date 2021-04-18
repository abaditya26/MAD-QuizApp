package team.projects.madquizapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import team.projects.madquizapplication.Models.ModelQuizData;
import team.projects.madquizapplication.R;

public class AdapterOldQuiz extends RecyclerView.Adapter<AdapterOldQuiz.ViewHolder> {
    List<ModelQuizData> quizData;
    Context ctx;
    boolean attempted = false;

    public AdapterOldQuiz(List<ModelQuizData> quizData, Context ctx) {
        this.quizData = quizData;
        this.ctx = ctx;
    }

    public AdapterOldQuiz(List<ModelQuizData> quizData, Context ctx, boolean attempted) {
        this.quizData = quizData;
        this.ctx = ctx;
        this.attempted = attempted;
    }

    @NonNull
    @Override
    public AdapterOldQuiz.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recycler_quiz,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOldQuiz.ViewHolder holder, int position) {
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
        TextView quizTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quizImage = itemView.findViewById(R.id.old_quiz_image);
            quizTitle = itemView.findViewById(R.id.old_quiz_title);

            lock = itemView.findViewById(R.id.private_icon);
        }

        public void setData(List<ModelQuizData> quizData, int position) {
            if (!quizData.get(position).getQuizImage().equalsIgnoreCase("")){
                Glide.with(ctx).load(quizData.get(position).getQuizImage()).into(quizImage);
            }
            quizTitle.setText(quizData.get(position).getQuizName());

            if (quizData.get(position).isPrivate()){
                lock.setVisibility(View.VISIBLE);
            }else{
                lock.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (quizData.get(position).getQuizId().equalsIgnoreCase("null")){
                    return;
                }
                if (attempted) {
                    Toast.makeText(ctx, "Attempted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "Not Attempted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}