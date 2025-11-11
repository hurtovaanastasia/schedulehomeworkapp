package com.example.schedulehomeworkapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.db.LessonItem;
import java.util.ArrayList;
import java.util.List;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.VH> {

    private final List<LessonItem> items = new ArrayList<>();

    public void setItems(List<LessonItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        LessonItem it = items.get(position);

        holder.tvTime.setText(it.start + " - " + it.end);
        holder.tvTitle.setText(it.disciplineName == null ? "" : it.disciplineName);

        if (it.room != null && !it.room.isEmpty()) {
            holder.tvRoom.setVisibility(View.VISIBLE);
            holder.tvRoom.setText(it.room);
        } else {
            holder.tvRoom.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(v -> {
            Context context = v.getContext();
            new AlertDialog.Builder(context)
                    .setTitle("Удалить занятие?")
                    .setMessage("Удалить \"" + (it.disciplineName == null ? "занятие" : it.disciplineName) + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        DBHelper db = new DBHelper(context);
                        db.deleteLesson(it.id);
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
        TextView tvTime, tvTitle, tvRoom;
        VH(@NonNull View v) {
            super(v);
            tvTime = v.findViewById(R.id.lesson_time);
            tvTitle = v.findViewById(R.id.lesson_title);
            tvRoom = v.findViewById(R.id.lesson_room);
        }
    }
}
