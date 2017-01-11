package ru.nwts.wherewe;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ru.nwts.wherewe.util.DialogFragmentGooglePlayService;
import ru.nwts.wherewe.util.PreferenceActivities;
import ru.nwts.wherewe.util.PreferenceHelper;

import static android.view.View.GONE;
import static java.lang.Boolean.getBoolean;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener, DialogFragmentGooglePlayService.DialogFragmentGooglePlayServiceListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private TextView textViewSignin;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    PreferenceHelper preferenceHelper;

    private String email;
    private String password;

    //Google Play Services Constant
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "WhW";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        editTextEmail.setVisibility(GONE);
        editTextPassword.setVisibility(GONE);
        textViewSignin.setVisibility(GONE);
        buttonSignup.setVisibility(GONE);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        //check google play services available
        if (!checkPlayServices()) {
            DialogFragmentGooglePlayService dialogFragmentGooglePlayService = DialogFragmentGooglePlayService.newInstance();
            FragmentManager manager = getSupportFragmentManager();
            dialogFragmentGooglePlayService.show(manager, "dialog");
            Toast.makeText(this, "You need install Google play Services", Toast.LENGTH_LONG).show();
        } else {
            //Goto program
            initPreferences();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {

            return false;
        }

        return true;
    }


    public void showSettings() {
        Intent intent = new Intent(BaseActivity.this, PreferenceActivities.class);
        startActivityForResult(intent, 0);
    }


    private void registerUser() {

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            Toast.makeText(BaseActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        } else {
                            //display some message here
                            Toast.makeText(BaseActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        //calling register method on click
        if (view == buttonSignup) {
            registerUser();
        }

        if (view == textViewSignin) {
            //open login activity when user taps on the already registered textview
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog) {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку OK!",
                Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog) {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку отмены!",
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void initPreferences() {

        //initializing preference
        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();

        if (preferenceHelper.getString("behaviour").length() == 0) {
            Toast.makeText(BaseActivity.this, " PreferenceHelper = (wifi) ", Toast.LENGTH_LONG).show();
            showSettings();
        }

        initFireBaseLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if user not choice whw type mode - write normal
        if (preferenceHelper.getString("behaviour").length() == 0) {
            preferenceHelper.putString("behaviour", "Person");
        }
        initFireBaseLogin();
    }

    private void initFireBaseLogin() {

        //initializing firebase auth object
        //firebaseAuth = FirebaseAuth.getInstance(); -- old variant
        firebaseAuth = TODOApplication.getFireBaseAuth();

        boolean is_login = !preferenceHelper.getString("login").isEmpty();
        boolean is_password = !preferenceHelper.getString("password").isEmpty();

        if (is_login && is_password ){
            //if getCurrentUser does not returns null
            if (firebaseAuth.getCurrentUser() != null)  {
                //that means user is already logged in
                //so close this activity
                finish();
                //and open profile activity
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }else{

                email = preferenceHelper.getString("login");
                password  = preferenceHelper.getString("password");

                progressDialog.setMessage("Registering Please Wait...");
                progressDialog.show();

                //logging in the user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                //if the task is successfull
                                if (task.isSuccessful()) {
                                    //start the profile activity
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                }else{
                                    //display some message here
                                    Toast.makeText(BaseActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }
        }

        editTextEmail.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        textViewSignin.setVisibility(View.VISIBLE);
        buttonSignup.setVisibility(View.VISIBLE);

    }
}
