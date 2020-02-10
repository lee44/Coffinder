package com.apps.jlee.boginder.DialogFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.apps.jlee.boginder.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PhotoDialogFragment extends DialogFragment
{
    private DeletePhotoCallback deletePhotoCallback;
    private AlertDialog dialog;
    private Button gallery, delete;

    public PhotoDialogFragment(DeletePhotoCallback deletePhotoCallback)
    {
        this.deletePhotoCallback = deletePhotoCallback;
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
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogfragment_photos, null);

        gallery = dialogView.findViewById(R.id.gallery_button);
        delete = dialogView.findViewById(R.id.delete_button);

        if(getArguments().getString("Status").equals("Empty"))
        {
            delete.setVisibility(View.INVISIBLE);
        }

        gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,24);
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                deletePhotoCallback.deletePhoto(getArguments().getInt("Position"));
                dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public interface DeletePhotoCallback
    {
        void deletePhoto(int position);
    }
}