package com.mm.quizmaster.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mm.quizmaster.R;
import com.mm.quizmaster.models.Admin;
import java.util.List;

/**
 * Admin Adapter for RecyclerView
 * Displays list of admins with delete functionality
 */
public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private List<Admin> adminList;
    private OnAdminDeleteListener deleteListener;

    public interface OnAdminDeleteListener {
        void onDelete(int adminId);
    }

    public AdminAdapter(List<Admin> adminList, OnAdminDeleteListener deleteListener) {
        this.adminList = adminList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Admin admin = adminList.get(position);
        holder.tvName.setText(admin.getName());
        holder.tvEmail.setText(admin.getEmail());
        
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(admin.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    public void updateList(List<Admin> newList) {
        this.adminList = newList;
        notifyDataSetChanged();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;
        ImageButton btnDelete;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_admin_name);
            tvEmail = itemView.findViewById(R.id.tv_admin_email);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
