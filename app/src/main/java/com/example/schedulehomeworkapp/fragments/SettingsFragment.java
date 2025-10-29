package com.example.schedulehomeworkapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // 🔹 Настройка количества недель (цикла)
        Spinner s = v.findViewById(R.id.spinner_cycle_mode);
        String[] modes = {"1 (еженед.)", "2", "3", "4"};
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, modes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(aa);

        int cur = requireContext().getSharedPreferences("app_prefs", 0)
                .getInt("cycle_mode", 1);
        s.setSelection(Math.max(0, cur - 1));

        s.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       View view, int position, long id) {
                requireContext().getSharedPreferences("app_prefs", 0)
                        .edit()
                        .putInt("cycle_mode", position + 1)
                        .apply();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // 🔹 Кнопка управления дисциплинами
        Button manageDisciplines = v.findViewById(R.id.btn_manage_disciplines);
        manageDisciplines.setOnClickListener(x -> showDisciplineDialog());

        return v;
    }

    // 🔹 Диалог со списком дисциплин (долгое нажатие — удалить)
    private void showDisciplineDialog() {
        DBHelper db = new DBHelper(requireContext());
        List<String> disciplines = db.getAllDisciplineNames();

        if (disciplines.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Дисциплины")
                    .setMessage("Список пуст.")
                    .setPositiveButton("ОК", null)
                    .show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, disciplines);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Управление дисциплинами")
                .setAdapter(adapter, null)
                .setNegativeButton("Закрыть", null)
                .create();

        dialog.getListView().setOnItemLongClickListener((parent, view, position, id) -> {
            String name = disciplines.get(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить дисциплину?")
                    .setMessage("Удалить \"" + name + "\" и все связанные занятия?")
                    .setPositiveButton("Да", (d, w) -> {
                        db.deleteDisciplineByName(name);
                        disciplines.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
            return true;
        });

        dialog.show();
    }
}
