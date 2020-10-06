package com.myapp.pawdopter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Init extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //Initialise buttons
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        //Initialise FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //Check if user has already logged in
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        //OnClick listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
        //OnClick listener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });
    }

    //Go to LoginActivity screen
    public void openLoginActivity(){
        Intent intent = new Intent(Init.this,Login.class);
        startActivity(intent);
    }

    //Go to RegisterActivity screen
    public void openRegisterActivity(){
        Intent intent = new Intent(Init.this,Register.class);
        startActivity(intent);
    }
}
