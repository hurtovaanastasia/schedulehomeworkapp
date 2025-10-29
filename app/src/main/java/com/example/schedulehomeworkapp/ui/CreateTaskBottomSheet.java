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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateTaskBottomSheet extends BottomSheetDialogFragment {
    public interface Callback { void onCreated(); }
    private Callback cb;
    public void setCallback(Callback c){ cb = c; }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.sheet_create_task, container, false);
        Spinner spDisc = v.findViewById(R.id.spinner_disc_task);
        EditText etTitle = v.findViewById(R.id.et_task_title);
        EditText etDeadline = v.findViewById(R.id.et_task_deadline); // format dd.MM.yyyy
        Spinner spPriority = v.findViewById(R.id.spinner_priority);
        Button btnCreate = v.findViewById(R.id.btn_create_task);

        DBHelper db = new DBHelper(requireContext());
        List<Discipline> discs = db.getAllDisciplines();
        String[] names = new String[discs.size()+1];
        names[0] = "Без дисциплины";
        for (int i=0;i<discs.size();i++) names[i+1]=discs.get(i).name;
        ArrayAdapter<String> ada = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDisc.setAdapter(ada);

        String[] pr = {"Низкий","Средний","Высокий"};
        ArrayAdapter<String> ap = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, pr);
        ap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(ap);

        btnCreate.setOnClickListener(x->{
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()){ etTitle.setError("Введите заголовок"); return; }
            int discPos = spDisc.getSelectedItemPosition();
            Long discId = null;
            if (discPos>0) discId = discs.get(discPos-1).id;
            String dl = etDeadline.getText().toString().trim();
            String iso = null;
            try{
                if (!dl.isEmpty()){
                    SimpleDateFormat in = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    Date d = in.parse(dl);
                    SimpleDateFormat isoF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    iso = isoF.format(d);
                }
            } catch(Exception e){ etDeadline.setError("Неверная дата"); return; }
            int priority = spPriority.getSelectedItemPosition();
            DBHelper dbh = new DBHelper(requireContext());
            dbh.addTask(title, discId, iso, priority);
            if (cb!=null) cb.onCreated();
            dismiss();
        });

        return v;
    }
}
