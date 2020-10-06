package com.myapp.pawdopter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText loginEmail, loginPass;
    private Button loginBtn;
    private TextView forgotPass, register;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        //Initialise FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //Initialise progress bar
        progressBar = findViewById(R.id.loginProgressBar);

        //Initialise buttons
        loginBtn = (Button) findViewById(R.id.loginButton);

        //Initialise links
        forgotPass = (TextView) findViewById(R.id.retrievePass);
        register = (TextView) findViewById(R.id.clickToReg);

        //Initialise fields
        loginEmail = (EditText) findViewById(R.id.loginUsername);
        loginPass = (EditText) findViewById(R.id.loginPassword);

        //Check if user has already logged in
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        //OnClick listener for register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        //OnClick listener for forgot password
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPassword.class));
            }
        });

        //OnClick listener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPass.getText().toString().trim();

                //If pass the conditions below, proceed to login into Firebase
                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("E-mail is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPass.setError("Password is required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Authenticate user
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(Login.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }
}
