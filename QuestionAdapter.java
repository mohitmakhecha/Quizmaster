package com.mm.quizmaster.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mm.quizmaster.R;
import com.mm.quizmaster.models.Question;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private OnQuestionDeleteListener deleteListener;

    public interface OnQuestionDeleteListener {
        void onDelete(int questionId);
    }

    public QuestionAdapter(List<Question> questionList, OnQuestionDeleteListener deleteListener) {
        this.questionList = questionList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questionList.get(position);
        holder.tvText.setText(q.getQuestion());
        holder.tvDetails.setText("Ans: " + q.getAnswer() + " | " + q.getLevel());
        
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(q.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void updateList(List<Question> newList) {
        this.questionList = newList;
        notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvDetails;
        ImageButton btnDelete;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_question_text);
            tvDetails = itemView.findViewById(R.id.tv_question_details);
            btnDelete = itemView.findViewById(R.id.btn_delete_question);
        }
    }
}
