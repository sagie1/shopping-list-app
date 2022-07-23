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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegitrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private TextView signin;
    private Button btnReg;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitration);
        email = findViewById(R.id.reg_email);
        pass = findViewById(R.id.reg_pass);
        btnReg = findViewById(R.id.reg_btn);
        signin = findViewById(R.id.login_txt);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        //when the registertion button pressed
        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                //email can't be empty
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field...");
                    return;
                }

                //password can't be empty
                if(TextUtils.isEmpty(mPass)){
                    pass.setError("Required Field...");
                    return;
                }

                //password needs to at least 6 chars, digits
                if(mPass.length() < 6){
                    pass.setError("Password is at least 6 chars, digits ");
                    return;
                }

                mDialog.setMessage("Proccessing...");
                mDialog.show();

                //register the user in the database
                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    //if the email is not registerd in the database and the password meeting the requirements then
                    //go to home page(HomeActivity)
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            //prevent the user from returning to this page(RegitrationActivity) by pressing the
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

        //when signin text line is pressed the user is being
        //transferred to the main page which is the sign in page
        signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));

                //prevent the user from returning to this page(RegitrationActivity) by pressing the
                //back button(triangular button).
                finish();
            }
        });




    }
}