package com.apps.jlee.boginder.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.apps.jlee.boginder.R;

import java.util.List;

import androidx.fragment.app.DialogFragment;

public class ChoicesDialogFragment extends DialogFragment
{
    private AlertDialog dialog;
    private ListView listView;
    private List<String> choices;
    private DialogFragmentClickListener dialogFragmentClickListener;
    private Button cancel;
    private String tag;

    public interface DialogFragmentClickListener
    {
        void choiceDialogFragmentClicked(String tag,String choice);
    }

    public ChoicesDialogFragment(List<String> choices, DialogFragmentClickListener dialogFragmentClickListener)
    {
        this.choices = choices;
        this.dialogFragmentClickListener = dialogFragmentClickListener;
    }

    public void onResume()
    {
        super.onResume();
        //Gets the window of the Dialog
        Window window = getDialog().getWindow();
        window.setLayout((int)(Resources.getSystem().getDisplayMetrics().widthPixels *.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogfragment_choices, null);

        tag = getTag();
        listView = dialogView.findViewById(R.id.choices_lv);
        cancel = dialogView.findViewById(R.id.cancel);

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, choices);
        listView.setAdapter(itemsAdapter);

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Object o = listView.getItemAtPosition(i);
                dialogFragmentClickListener.choiceDialogFragmentClicked(tag,o.toString());
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        return dialog;
    }
}
