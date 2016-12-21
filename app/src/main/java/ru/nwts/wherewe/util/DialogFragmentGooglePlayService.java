package ru.nwts.wherewe.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.nwts.wherewe.R;

/**
 * Created by Надя on 28.11.2016.
 */

public class DialogFragmentGooglePlayService extends DialogFragment {

    private DialogFragmentGooglePlayServiceListener dialogFragmentGooglePlayServiceListener;

    public static DialogFragmentGooglePlayService newInstance() {
        String title = "Warning";
        DialogFragmentGooglePlayService dialogFragmentGooglePlayService = new DialogFragmentGooglePlayService();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialogFragmentGooglePlayService.setArguments(args);
        return dialogFragmentGooglePlayService;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dialogFragmentGooglePlayServiceListener = (DialogFragmentGooglePlayServiceListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Вы жертвуете миллион коту")
                .setIcon(R.drawable.picasso)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogFragmentGooglePlayServiceListener.onDialogPositiveClick(DialogFragmentGooglePlayService.this);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogFragmentGooglePlayServiceListener
                                .onDialogNegativeClick(DialogFragmentGooglePlayService.this);
                    }
                });
        return builder.create();
    }

    public interface DialogFragmentGooglePlayServiceListener {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
