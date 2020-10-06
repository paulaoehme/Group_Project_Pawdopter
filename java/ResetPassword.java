package com.myapp.pawdopter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button resetPassword;
    private EditText username;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Initialising button
        resetPassword = (Button) findViewById(R.id.resetPass);

        //Initilising field
        username = (EditText) findViewById(R.id.resetPassUsername);

        //Initialising firebase instance
        mAuth = FirebaseAuth.getInstance();

        //OnClick listener for reset password button
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = username.getText().toString().trim();

                //If username field is empty, display an error
                if(TextUtils.isEmpty(userEmail)){
                    username.setError("Username is required");
                }
                //If not empty, send email with link to reset password to the registered e-mail
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //If successful, display a message letting the user know about the e-mail that was sent
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassword.this, "An e-mail was sent your e-mail account to reset your password", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPassword.this, Login.class));
                            }
                            //If not successful, display an error
                            else{
                                Toast.makeText(ResetPassword.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
