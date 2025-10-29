package com.example.schedulehomeworkapp.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schedulehomeworkapp.R;
import com.example.schedulehomeworkapp.adapters.TasksAdapter;
import com.example.schedulehomeworkapp.DBHelper;
import com.example.schedulehomeworkapp.db.Discipline;
import com.example.schedulehomeworkapp.ui.FabChooserBottomSheet;
import com.example.schedulehomeworkapp.ui.CreateDisciplineBottomSheet;
import com.example.schedulehomeworkapp.ui.CreateLessonBottomSheet;
import com.example.schedulehomeworkapp.ui.CreateTaskBottomSheet;
import java.util.ArrayList; import java.util.List;
public class TasksFragment extends Fragment {
    private DBHelper db; private TasksAdapter adapter;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        db = new DBHelper(requireContext()); adapter = new TasksAdapter();
        RecyclerView rv = v.findViewById(R.id.recycler_tasks); rv.setLayoutManager(new LinearLayoutManager(requireContext())); rv.setAdapter(adapter);
        Spinner filterDisc = v.findViewById(R.id.spinner_filter_disc);
        List<Discipline> list = db.getAllDisciplines(); List<String> names = new ArrayList<>(); names.add("Все дисциплины"); for(Discipline d: list) names.add(d.name);
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names); aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); filterDisc.setAdapter(aa);
        filterDisc.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){ @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id){ long filterId = -1; if (position>0) filterId = list.get(position-1).id; java.util.List<com.example.schedulehomeworkapp.db.Task> tasks = db.getTasksFiltered(filterId); adapter.setItems(tasks); } @Override public void onNothingSelected(android.widget.AdapterView<?> parent){} });
        filterDisc.setSelection(0);
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = v.findViewById(R.id.fab_add);
        fab.setOnClickListener(x->{ FabChooserBottomSheet sheet = new FabChooserBottomSheet(); sheet.setCallback(choice -> { if (choice==0){ CreateDisciplineBottomSheet d = new CreateDisciplineBottomSheet(); d.setCallback(() -> { filterDisc.setSelection(0); }); d.show(getParentFragmentManager(), "create_disc"); } else if (choice==1){ CreateLessonBottomSheet l = new CreateLessonBottomSheet(); l.setCallback(() -> { int sel = filterDisc.getSelectedItemPosition(); filterDisc.setSelection(0); filterDisc.setSelection(sel); }); l.show(getParentFragmentManager(), "create_lesson"); } else { CreateTaskBottomSheet t = new CreateTaskBottomSheet(); t.setCallback(() -> { filterDisc.setSelection(0); }); t.show(getParentFragmentManager(), "create_task"); } }); sheet.show(getParentFragmentManager(), "fab_chooser"); });
        return v;
    }
}
