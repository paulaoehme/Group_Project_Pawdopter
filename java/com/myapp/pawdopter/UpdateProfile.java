package com.myapp.pawdopter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText cName, cPhone, cEmail, cPass, cRepeatPass;
    private Button save;
    private FirebaseDatabase database;
    private DatabaseReference mRef, userRef;
    private static final String TAG = "UpdateProfile";


    //Requirements for valid phone
    private static final Pattern PHONE_PATTERN = Pattern.compile("(0)?[8][0-9]{8}");

    //Requirements for valid password
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^*}{&+=])(?=\\S+$).{8,}$");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Initialise fields
        cName = (EditText) findViewById(R.id.profileName);
        cPhone = (EditText) findViewById(R.id.profilePhone);
        cEmail = (EditText) findViewById(R.id.profileEmail);
        cPass = (EditText) findViewById(R.id.profilePass);
        cRepeatPass = (EditText) findViewById(R.id.profileRepeatPass);

        //Initialise buttons
        save = (Button) findViewById(R.id.saveBtn);


        //Initialise FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User").child(currentUserID);


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    //Retrieving info from user table
                    String profileName = dataSnapshot.child("name").getValue().toString();
                    String profilePhone = dataSnapshot.child("phone").getValue().toString();
                    String profileEmail = dataSnapshot.child("email").getValue().toString();
                    String profilePass = dataSnapshot.child("password").getValue().toString();

                    //Displaying inf retrieved
                    cName.setText(profileName);
                    cPhone.setText(profilePhone);
                    cEmail.setText(profileEmail);
                    cPass.setText(profilePass);
                    cRepeatPass.setText(profilePass);

                    //Disabling editing for name and email fields
                    cName.setEnabled(false);
                    cEmail.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //OnClick listener for register button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
    }

    private void updateUser(){
        //Check if info provided is valid
        if(!validateName() | !validatePhone() | !validateEmail() | !validatePassword() | !validateRepeatPass()){
            return;
        }

        //Updating user info
        else{
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        //Retrieving info from user table
                        String profileEmail = dataSnapshot.child("email").getValue().toString();
                        String profilePass = dataSnapshot.child("password").getValue().toString();

                        final FirebaseUser user = mAuth.getCurrentUser();
                        AuthCredential credential = EmailAuthProvider.getCredential(profileEmail, profilePass);

                        String name = cName.getText().toString();
                        String phone = cPhone.getText().toString();
                        String email = cEmail.getText().toString();
                        final String password = cPass.getText().toString();

                        final HashMap userMap = new HashMap();
                        userMap.put("name", name);
                        userMap.put("phone", phone);
                        userMap.put("email", email);
                        userMap.put("password", password);


                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(UpdateProfile.this, "User info updated", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
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

}