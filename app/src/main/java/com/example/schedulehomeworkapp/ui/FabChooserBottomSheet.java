package com.example.schedulehomeworkapp.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.schedulehomeworkapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class FabChooserBottomSheet extends BottomSheetDialogFragment {
    public interface Callback { void onChoose(int choice); }
    private Callback cb;
    public void setCallback(Callback c){ cb = c; }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.sheet_fab_chooser, container, false);
        v.findViewById(R.id.btn_create_disc).setOnClickListener(x->{ if (cb!=null) cb.onChoose(0); dismiss(); });
        v.findViewById(R.id.btn_create_lesson).setOnClickListener(x->{ if (cb!=null) cb.onChoose(1); dismiss(); });
        v.findViewById(R.id.btn_create_task).setOnClickListener(x->{ if (cb!=null) cb.onChoose(2); dismiss(); });
        return v;
    }
}
