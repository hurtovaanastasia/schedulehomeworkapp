package com.example.schedulehomeworkapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateDisciplineBottomSheet extends BottomSheetDialogFragment {

    public interface Callback { void onCreated(); }
    private Callback cb;

    public void setCallback(Callback c){ cb = c; }

    @Nullable
    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.sheet_create_discipline, container, false);
        EditText etName = v.findViewById(R.id.et_disc_name);
        EditText etTeacher = v.findViewById(R.id.et_disc_teacher);
        Button btnCreate = v.findViewById(R.id.btn_create_disc);

        btnCreate.setOnClickListener(x->{
            String name = etName.getText().toString().trim();
            String teacher = etTeacher.getText().toString().trim();
            if (name.isEmpty()) { etName.setError("Введите название"); return; }
            DBHelper db = new DBHelper(requireContext());
            db.addDiscipline(name, teacher, 0xFFDDEFFF);
            if (cb!=null) cb.onCreated();
            dismiss();
        });
        return v;
    }
}
