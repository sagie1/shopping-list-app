package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button btnLogin;
    private TextView signup;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.btn_login);
        signup = findViewById(R.id.signup_txt);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        //when the login button pressed
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                //email can't be empty
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field...");
                    return;
                }

                //email can't be password
                if(TextUtils.isEmpty(mPass)){
                    pass.setError("Required Field...");
                    return;
                }

                //email can't be password
                if(mPass.length() < 6){
                    pass.setError("Password is at least 6 chars, digits ");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                //sign in the user in the database
                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //if the email is registered in the database and the password is correct then
                    //go to home page(HomeActivity)
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            //prevent the user from returning to this page(MainActivity) by pressing the
                            //back button(triangular button).
                            finish();

                            mDialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "Something went wrong please try again..", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });

        //when signup text line is pressed the user is being
        //transferred to the registration page
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegitrationActivity.class));

                //prevent the user from returning to this page(MainActivity) by pressing the
                //back button(triangular button).
                finish();
            }
        });



    }
}