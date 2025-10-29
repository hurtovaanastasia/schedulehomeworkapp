package com.example.schedulehomeworkapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.db.Discipline;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CreateLessonBottomSheet extends BottomSheetDialogFragment {
    public interface Callback { void onCreated(); }
    private Callback cb;
    public void setCallback(Callback c){ cb = c; }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.sheet_create_lesson, container, false);
        Spinner spDisc = v.findViewById(R.id.spinner_disc);
        Spinner spDay = v.findViewById(R.id.spinner_day);
        EditText etStart = v.findViewById(R.id.et_start);
        EditText etEnd = v.findViewById(R.id.et_end);
        EditText etRoom = v.findViewById(R.id.et_room);
        Spinner spWeekType = v.findViewById(R.id.spinner_week_type);
        Button btnCreate = v.findViewById(R.id.btn_create_lesson);

        DBHelper db = new DBHelper(requireContext());
        List<Discipline> discs = db.getAllDisciplines();
        String[] names = new String[discs.size()+1];
        names[0] = "Без дисциплины";
        for (int i=0;i<discs.size();i++) names[i+1]=discs.get(i).name;
        ArrayAdapter<String> ada = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDisc.setAdapter(ada);

        String[] days = {"Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье"};
        ArrayAdapter<String> adaDays = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days);
        adaDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adaDays);

        String[] wk = {"Каждую неделю","Неделя 1","Неделя 2","Неделя 3","Неделя 4"};
        ArrayAdapter<String> adaW = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, wk);
        adaW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWeekType.setAdapter(adaW);

        btnCreate.setOnClickListener(x->{
            int discPos = spDisc.getSelectedItemPosition();
            Long discId = null;
            if (discPos>0) discId = discs.get(discPos-1).id;
            int day = spDay.getSelectedItemPosition()+1;
            String start = etStart.getText().toString().trim();
            String end = etEnd.getText().toString().trim();
            String room = etRoom.getText().toString().trim();
            int weekType = spWeekType.getSelectedItemPosition(); // 0..4 where 0 means every week

            if (start.isEmpty() || end.isEmpty()){ etStart.setError("Введите время"); return; }
            DBHelper dbh = new DBHelper(requireContext());
            dbh.addLesson(discId, day, start, end, room, weekType);
            if (cb!=null) cb.onCreated();
            dismiss();
        });

        return v;
    }
}
