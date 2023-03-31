package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, pw, pwConfirmation;
    private Button registrationBtn;
    private String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.emailRegistration);
        pw = findViewById(R.id.passwordRegistration);
        pwConfirmation = findViewById(R.id.passwordConfirmation);
        registrationBtn = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });
    }

    private void userRegistration(){
        String userEmail = email.getText().toString();
        String userPw = pw.getText().toString();
        String userPwConfirmation = pwConfirmation.getText().toString();

        if (!userEmail.matches(emailPattern)){
            email.setError("Enter valid email");
        }else if (userPw.isEmpty() || userPw.length() < 6){
            pw.setError("Enter valid Password");
        }else if (!userPw.equals(userPwConfirmation)){
            pwConfirmation.setError("Passwords do not match");
        }else{
            mAuth.createUserWithEmailAndPassword(userEmail,userPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(i);
                        Toast.makeText(RegistrationActivity.this, "User Registrated Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        System.out.println("Error during registration: " + task.getException());
                    }
                }
            });
        }
    }

}