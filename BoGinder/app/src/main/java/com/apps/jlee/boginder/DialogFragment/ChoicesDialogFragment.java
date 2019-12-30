package com.apps.jlee.boginder.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apps.jlee.boginder.Adapter.ChoicesAdapter;
import com.apps.jlee.boginder.R;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChoicesDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private RecyclerView rv;
    private ChoicesAdapter choicesAdapter;

    private List<String> choices;

    public ChoicesDialogFragment(List<String> choices)
    {
        this.choices = choices;
    }

    public void onResume()
    {
        super.onResume();
        //Gets the window of the Dialog
        Window window = getDialog().getWindow();
        window.setLayout((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogfragment_choices, null);

        rv = dialogView.findViewById(R.id.choices_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        choicesAdapter = new ChoicesAdapter(getChoices(),getContext());
        rv.setAdapter(choicesAdapter);

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    private List<String> getChoices()
    {
        return choices;
    }
}
