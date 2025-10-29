package com.example.schedulehomeworkapp.fragments;
import android.os.Bundle; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.ArrayAdapter; import android.widget.Spinner;
import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
import com.example.schedulehomeworkapp.R;
public class SettingsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        Spinner s = v.findViewById(R.id.spinner_cycle_mode);
        String[] modes = {"1 (еженед.)","2","3","4"};
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, modes); aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(aa);
        int cur = requireContext().getSharedPreferences("app_prefs",0).getInt("cycle_mode",1);
        s.setSelection(Math.max(0, cur-1));
        s.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){ @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id){ requireContext().getSharedPreferences("app_prefs",0).edit().putInt("cycle_mode", position+1).apply(); } @Override public void onNothingSelected(android.widget.AdapterView<?> parent){} });
        return v;
    }
}
