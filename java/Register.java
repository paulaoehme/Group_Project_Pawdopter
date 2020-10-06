package com.myapp.pawdopter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText cName, cPhone, cEmail, cPass, cRepeatPass;
    private Button submit;
    private ProgressBar regProgressBar;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private static final String TAG = "RegisterActivity";
    private User user;

    //Requirements for valid phone
    private static final Pattern PHONE_PATTERN = Pattern.compile("(0)?[8][0-9]{8}");

    //Requirements for valid password
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^\\\\*}\\[{&+\\]=.,!?'\"â‚¬Â£`)(/_Â¬Ã¡:;ÃºÃ³Ã©|~])(?=\\S+$).{8,}$");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Initialise FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //Initialise progress bar
        regProgressBar = findViewById(R.id.progressBar);

        //Check if user has already logged in
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        //Initialise buttons
        submit = (Button) findViewById(R.id.submitBtn);

        //Initialise fields
        cName = (EditText) findViewById(R.id.customerName);
        cPhone = (EditText) findViewById(R.id.customerPhone);
        cEmail = (EditText) findViewById(R.id.customerEmail);
        cPass = (EditText) findViewById(R.id.customerPass);
        cRepeatPass = (EditText) findViewById(R.id.customerRepeatPass);

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("User");

        //OnClick listener for register button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = cName.getText().toString();
                String phone = cPhone.getText().toString();
                String email = cEmail.getText().toString();
                String password = cPass.getText().toString();

                user = new User(email, name, phone, password);
                registerUser(email, password);
            }
        });
    }

    private boolean validateName(){
        String nameInput = cName.getText().toString().trim();

        if(nameInput.isEmpty()){
            cName.setError("Name is required");
            return false;
        }
        else if(nameInput.length()<3) {
            cName.setError("Name is too short");
            return false;
        }
        else if(nameInput.length()>20){
            cName.setError("Name is too long");
            return false;
        }
        else{
            cName.setError(null);
            return true;
        }
    }

    private boolean validatePhone(){
        String phoneInput = cPhone.getText().toString().trim();

        if(phoneInput.isEmpty()){
            cPhone.setError("Phone is required");
            return false;
        }
        else if(phoneInput.length()!=10){
            cPhone.setError("Not valid Irish mobile number");
            return false;
        }
        else if(!PHONE_PATTERN.matcher(phoneInput).matches()){
            cPhone.setError("Not valid Irish mobile number");
            return false;
        }
        else{
            cPhone.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String emailInput = cEmail.getText().toString().trim();

        if(emailInput.isEmpty()){
            cEmail.setError("E-mail is required");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            cEmail.setError("Please enter a valid e-mail address");
            return false;
        }
        else{
            cEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String passInput = cPass.getText().toString().trim();

        if(passInput.isEmpty()){
            cPass.setError("Password is required");
            return false;
        }
        else if(!PASSWORD_PATTERN.matcher(passInput).matches()){
            cPass.setError("Password should contain at least one uppercase, one lowercase, one special character and one number");
            return false;
        }
        else if(passInput.length()<8){
            cPass.setError("Password should be at least 8 characters long");
            return false;
        }
        else {
            cPass.setError(null);
            return true;
        }
    }

    private boolean validateRepeatPass(){
        String repeatPassInput = cRepeatPass.getText().toString().trim();
        String pass = cPass.getText().toString().trim();

        if(repeatPassInput.isEmpty()){
            cRepeatPass.setError("Confirm password is required");
            return false;
        }
        else if(!repeatPassInput.equals(pass)){
            cRepeatPass.setError("Passwords do not match");
            return false;
        }
        else{
            cRepeatPass.setError(null);
            return true;
        }
    }

    private void registerUser(String email, String password){
        //Check if info provided is valid
        if(!validateName() | !validatePhone() | !validateEmail() | !validatePassword() | !validateRepeatPass()){
            return;
        }

        //Authenticating user
        else{

            regProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Sign in successful, update UI with the signed-in user's info
                        Log.d(TAG, "Registration successful");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                    else{
                        //If fails, display message to the user
                        Log.w(TAG, "Authentication failed", task.getException());
                        Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        regProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    //Insert user info into database
    private void updateUI(FirebaseUser currentUser){
        String uid = currentUser.getUid();
        mRef.child(uid).setValue(user);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        regProgressBar.setVisibility(View.GONE);
    }

}
