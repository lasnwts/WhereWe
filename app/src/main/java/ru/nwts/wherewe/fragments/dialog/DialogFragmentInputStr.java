package ru.nwts.wherewe.fragments.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ru.nwts.wherewe.R;

/**
 * Created by пользователь on 15.02.2017.
 */

public class DialogFragmentInputStr extends DialogFragment {

    private EditText inputStr;
    private Button btnOk, btnNegative;

    public DialogFragmentInputStr() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment, container);
        inputStr = (EditText) view.findViewById(R.id.editTextName);
        btnOk = (Button) view.findViewById(R.id.buttonDialogOk);
        btnNegative = (Button) view.findViewById(R.id.buttonDialogNegative);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroyView();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputStr.setText("");
                onDestroyView();
            }
        });

        getDialog().setTitle(getResources().getString(R.string.dialog_insert_new_abonent_title));
        return view;
    }

    public interface DialogFragmentInputStrListener{
        void onFinishEditDialog(String inputText);
    }

    @Override
    public void onDestroyView() {
        DialogFragmentInputStrListener activity = (DialogFragmentInputStrListener) getActivity();
        activity.onFinishEditDialog(inputStr.getText().toString());
        super.onDestroyView();
    }
}
