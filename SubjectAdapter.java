package com.mm.quizmaster.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mm.quizmaster.R;
import com.mm.quizmaster.models.Subject;
import java.util.List;

/**
 * Subject Adapter for RecyclerView
 * Displays list of subjects with edit and delete functionality
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private List<Subject> subjectList;
    private OnSubjectActionListener actionListener;

    public interface OnSubjectActionListener {
        void onEdit(Subject subject);
        void onDelete(int subjectId);
    }

    public SubjectAdapter(List<Subject> subjectList, OnSubjectActionListener actionListener) {
        this.subjectList = subjectList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvSubjectName.setText(subject.getSubjectName());
        holder.tvSemester.setText("Semester " + subject.getSemester());
        
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(subject);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(subject.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void updateList(List<Subject> newList) {
        this.subjectList = newList;
        notifyDataSetChanged();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSemester;
        ImageButton btnEdit, btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvSemester = itemView.findViewById(R.id.tv_semester);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
