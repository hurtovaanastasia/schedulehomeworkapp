package com.example.schedulehomeworkapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.adapters.LessonsAdapter;
import com.example.schedulehomeworkapp.ui.CreateLessonBottomSheet;
import com.example.schedulehomeworkapp.DBHelper;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

public class ScheduleFragment extends Fragment {

    private DBHelper db;
    private LessonsAdapter adapter;
    private int currentDay = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        db = new DBHelper(requireContext());
        adapter = new LessonsAdapter();

        RecyclerView rv = v.findViewById(R.id.recycler_lessons);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        TextView header = v.findViewById(R.id.text_day_header);
        Button prev = v.findViewById(R.id.btn_prev_day);
        Button next = v.findViewById(R.id.btn_next_day);
        Spinner weekSpinner = v.findViewById(R.id.spinner_week);

        // ✅ Получаем количество недель из настроек
        int cycleCount = requireContext()
                .getSharedPreferences("app_prefs", 0)
                .getInt("cycle_mode", 1);

        // ✅ Формируем список недель
        List<String> weeks = new ArrayList<>();
        for (int i = 1; i <= cycleCount; i++) {
            weeks.add("Неделя " + i);
        }

        // ✅ Подключаем адаптер для Spinner
        ArrayAdapter<String> weekAdapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item,
                        weeks);
        weekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(weekAdapter);
        weekSpinner.setSelection(0, false);

        // ✅ Устанавливаем текущий день недели
        int dow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        currentDay = (dow == Calendar.SUNDAY) ? 7 : dow - 1;
        updateHeader(header);

        // 🔹 Кнопки переключения дней
        prev.setOnClickListener(x -> {
            currentDay = (currentDay == 1) ? 7 : currentDay - 1;
            updateHeader(header);
            loadLessons(weekSpinner);
        });

        next.setOnClickListener(x -> {
            currentDay = (currentDay == 7) ? 1 : currentDay + 1;
            updateHeader(header);
            loadLessons(weekSpinner);
        });

        // 🔹 Реакция на выбор недели
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadLessons(weekSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔹 Открытие bottom sheet по долгому нажатию
        rv.setOnLongClickListener(view -> {
            CreateLessonBottomSheet sheet = new CreateLessonBottomSheet();
            sheet.setCallback(() -> loadLessons(weekSpinner));
            sheet.show(getParentFragmentManager(), "create_lesson");
            return true;
        });

        // Первоначальная загрузка расписания
        loadLessons(weekSpinner);

        return v;
    }

    private void updateHeader(TextView header) {
        String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};
        header.setText(days[currentDay - 1]);
    }

    private void loadLessons(Spinner weekSpinner) {
        int selectedWeek = weekSpinner.getSelectedItemPosition() + 1;
        int mode = requireContext()
                .getSharedPreferences("app_prefs", 0)
                .getInt("cycle_mode", 1);
        java.util.List<com.example.schedulehomeworkapp.db.LessonItem> list =
                db.getLessonsForDay(currentDay, mode, selectedWeek);
        adapter.setItems(list);
    }
}
