package com.example.schedulehomeworkapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.db.Task;
import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.VH> {

    private final List<Task> items = new ArrayList<>();

    public void setItems(List<Task> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Task t = items.get(position);
        holder.title.setText(t.title == null ? "" : t.title);
        holder.disc.setText(t.disciplineName == null ? "" : t.disciplineName);
        holder.deadline.setText(t.getDeadlineLabel());

        Context context = holder.itemView.getContext();
        if (t.isOverdue()) {
            holder.container.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.holo_red_light)
            );
            holder.overdue.setVisibility(View.VISIBLE);
        } else {
            holder.container.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.background_light)
            );
            holder.overdue.setVisibility(View.GONE);
        }

        holder.itemView.setAlpha(t.done ? 0.6f : 1.0f);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        androidx.cardview.widget.CardView container;
        TextView title;
        TextView disc;
        TextView deadline;
        TextView overdue;

        VH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.task_card);
            title = itemView.findViewById(R.id.task_title);
            disc = itemView.findViewById(R.id.task_discipline);
            deadline = itemView.findViewById(R.id.task_deadline);
            overdue = itemView.findViewById(R.id.task_overdue_label);
        }
    }
}