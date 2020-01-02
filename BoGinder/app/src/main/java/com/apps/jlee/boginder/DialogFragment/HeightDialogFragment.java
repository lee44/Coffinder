package com.apps.jlee.boginder.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;

import com.apps.jlee.boginder.R;

import androidx.fragment.app.DialogFragment;

public class HeightDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private NumberPicker feet, inches;

    public HeightDialogFragment()
    {

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
        View dialogView = inflater.inflate(R.layout.dialogfragment_height, null);

        feet = dialogView.findViewById(R.id.feet);
        inches = dialogView.findViewById(R.id.inches);

        feet.setMinValue(4);
        feet.setMaxValue(8);

        inches.setMinValue(0);
        inches.setMaxValue(11);

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
