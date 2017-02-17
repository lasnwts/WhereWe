package ru.nwts.wherewe.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.model.SmallModel;

/**
 * Created by пользователь on 17.02.2017.
 */

public class DialogFragmentOneItem extends DialogFragment {

    public static DialogFragmentOneItem newInstance(SmallModel smallModel){

        DialogFragmentOneItem dialogFragmentOneItem = new DialogFragmentOneItem();

        Bundle args = new Bundle();
        args.putString("title", smallModel.getEmail());
        dialogFragmentOneItem.setArguments(args);
        return dialogFragmentOneItem;
    }


    private EditingTaskListener editingTaskListener;

    public interface EditingTaskListener {
        void onTaskEdited(SmallModel updatedModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            editingTaskListener = (EditingTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditingTaskListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title");

        final SmallModel updatedModel = new SmallModel();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Избегать! Ечто!");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View container = inflater.inflate(R.layout.dialog_one_item, null);

        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        final EditText etTitle = tilTitle.getEditText();
        etTitle.setText(title);
        TextInputLayout tilDate = (TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final EditText etDate = tilDate.getEditText();
        final TextInputLayout tilTime = (TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();

        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);
        builder.setView(container);

        builder.setNegativeButton(R.string.frag_btn_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(R.string.frag_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatedModel.setEmail(etTitle.getText().toString().trim());
                editingTaskListener.onTaskEdited(updatedModel);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();



        return alertDialog;
    }
}
