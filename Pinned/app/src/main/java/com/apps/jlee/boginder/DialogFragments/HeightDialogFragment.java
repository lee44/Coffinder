package com.apps.jlee.boginder.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.apps.jlee.boginder.R;

import androidx.fragment.app.DialogFragment;

public class HeightDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private NumberPicker feet, inches;
    private Button done;
    private HeightDialogFragmentListener heightDialogFragmentListener;
    private String[] feetArray = new String[]{"4'","5'","6'","7'"};
    private String[] inchesArray = new String[]{"0\"","1\"","2\"","3\"","4\"","5\"","6\"","7\"","8\"","9\"","10\"","11\""};

    public interface HeightDialogFragmentListener
    {
        void heightDialogFragmentClicked(String feet,String inches);
    }

    public HeightDialogFragment(HeightDialogFragmentListener heightDialogFragmentListener)
    {
        this.heightDialogFragmentListener = heightDialogFragmentListener;
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
        final View dialogView = inflater.inflate(R.layout.dialogfragment_height, null);

        feet = dialogView.findViewById(R.id.feet);
        inches = dialogView.findViewById(R.id.inches);
        done = dialogView.findViewById(R.id.done);

        feet.setMinValue(0);
        feet.setMaxValue(3);

        feet.setDisplayedValues(feetArray);

        inches.setMinValue(0);
        inches.setMaxValue(11);

        inches.setDisplayedValues(inchesArray);

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                heightDialogFragmentListener.heightDialogFragmentClicked(feetArray[feet.getValue()],inchesArray[inches.getValue()]);
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
