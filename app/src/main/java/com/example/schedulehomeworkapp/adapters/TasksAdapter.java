package com.example.schedulehomeworkapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.db.Task;
import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.VH> {

    private final List<Task> items = new ArrayList<>();
    private OnTaskStatusChangeListener statusChangeListener;

    public interface OnTaskStatusChangeListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
    }

    public TasksAdapter() {}

    public TasksAdapter(OnTaskStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    public void setItems(List<Task> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setOnTaskStatusChangeListener(OnTaskStatusChangeListener listener) {
        this.statusChangeListener = listener;
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
        Context context = holder.itemView.getContext();

        holder.title.setText(t.title == null ? "" : t.title);
        holder.disc.setText(t.disciplineName == null ? "" : t.disciplineName);
        holder.deadline.setText(t.getDeadlineLabel());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(t.done);

        if (t.done) {
            holder.container.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.task_completed_background)
            );
            holder.title.setAlpha(0.6f);
            holder.disc.setAlpha(0.6f);
            holder.deadline.setAlpha(0.6f);
            holder.overdue.setVisibility(View.GONE);
        } else if (t.isOverdue()) {
            holder.container.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.holo_red_light)
            );
            holder.title.setAlpha(1.0f);
            holder.disc.setAlpha(1.0f);
            holder.deadline.setAlpha(1.0f);
            holder.overdue.setVisibility(View.VISIBLE);
        } else {
            holder.container.setCardBackgroundColor(
                    ContextCompat.getColor(context, android.R.color.background_light)
            );
            holder.title.setAlpha(1.0f);
            holder.disc.setAlpha(1.0f);
            holder.deadline.setAlpha(1.0f);
            holder.overdue.setVisibility(View.GONE);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (statusChangeListener != null) {
                    t.done = isChecked;
                    statusChangeListener.onTaskStatusChanged(t, isChecked);
                    notifyItemChanged(position);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            boolean newState = !t.done;
            holder.checkBox.setChecked(newState);
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Удалить задание?")
                    .setMessage("Удалить \"" + (t.title == null ? "задание" : t.title) + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        DBHelper db = new DBHelper(context);
                        db.deleteTask(t.id);
                        items.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        androidx.cardview.widget.CardView container;
        CheckBox checkBox;
        TextView title;
        TextView disc;
        TextView deadline;
        TextView overdue;

        VH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.task_card);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            title = itemView.findViewById(R.id.task_title);
            disc = itemView.findViewById(R.id.task_discipline);
            deadline = itemView.findViewById(R.id.task_deadline);
            overdue = itemView.findViewById(R.id.task_overdue_label);
        }
    }
}