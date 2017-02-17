package ru.nwts.wherewe.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.nwts.wherewe.R;

/**
 * Created by пользователь on 14.02.2017.
 */

public class DialogFragmentYesNo extends DialogFragment {

    private DialogFragmentYesNoListener dialogFragmentYesNoListener;

    public static DialogFragmentYesNo newInstance(){
        String title = "Важно!";
        DialogFragmentYesNo dialogFragmentYesNo = new DialogFragmentYesNo();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialogFragmentYesNo.setArguments(args);
        return dialogFragmentYesNo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_question_yes_no)
                .setIcon(R.drawable.picasso)
                .setTitle(title)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogFragmentYesNoListener.onDialogPositiveClick(DialogFragmentYesNo.this);
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogFragmentYesNoListener.onDialogNegativeClick(DialogFragmentYesNo.this);
                    }
                });
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            dialogFragmentYesNoListener = (DialogFragmentYesNoListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface DialogFragmentYesNoListener{
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}