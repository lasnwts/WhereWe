package ru.nwts.wherewe.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import ru.nwts.wherewe.R;
import ru.nwts.wherewe.model.SmallModel;

import static android.R.attr.name;

/**
 * Created by пользователь on 17.02.2017.
 */

public class DialogFragmentOneItem extends DialogFragment {

    public static DialogFragmentOneItem newInstance(SmallModel smallModel, int position){

        DialogFragmentOneItem dialogFragmentOneItem = new DialogFragmentOneItem();

        Bundle args = new Bundle();
        args.putString("email", smallModel.getEmail());
        args.putString("name", smallModel.getName());
        args.putInt("id", smallModel.getId());
        args.putInt("position",position);
        dialogFragmentOneItem.setArguments(args);
        return dialogFragmentOneItem;
    }


    private EditingTaskListener editingTaskListener;

    public interface EditingTaskListener {
        void onTaskEdited(SmallModel updatedModel, int pos, int id);
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
        String email = args.getString("email");
        if (email == null && email.isEmpty() && email.length()<1){
            email = "";
        }
        String title = getResources().getString(R.string.dialog_edit_one_titile) + ": " + email;
        String name = args.getString("name");
        final int position = args.getInt("position");
        final int id = args.getInt("id");

        //Plan in future work with object, with all fields
        //Now need only one field = name
        final SmallModel updatedModel = new SmallModel();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View container = inflater.inflate(R.layout.dialog_one_item, null);

        final TextInputLayout tilTitle = (TextInputLayout) container.findViewById(R.id.dialog_one_name);
        final EditText etTitle = tilTitle.getEditText();
        etTitle.setText(name);


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
                updatedModel.setName(etTitle.getText().toString().trim());
                editingTaskListener.onTaskEdited(updatedModel, position, id);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();

        return alertDialog;
    }
}
