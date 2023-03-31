package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private EditText email, pw;
    private TextView goToRegistration;
    private String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();

        email = findViewById(R.id.email);
        pw = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        goToRegistration = findViewById(R.id.notRegistered);

        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        // Configure the Google SignIn
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //checkUser();

        //Google SignIn button
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: begin Google SignIn");
                Intent intent= googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }
/**
    private void checkUser() {
        //if user is already signed in then go to TeamsView activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(this, TeamsView.class));
            finish();
        }
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result from launching the intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google Sign In Intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google SignIn successful, now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

            }catch (Exception e){
                //failed GoogleSignIn
                Log.d(TAG, "onActivityResult: "+ e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        System.out.println("aaa" + credential);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Login success
                        Log.d(TAG, "onSuccess: Logged In");

                        //get logged in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: Email: " + email);
                        Log.d(TAG, "onSuccess: UID: " + uid);

                        //Check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //user is new -> Account created
                            Log.d(TAG, "onSuccess: Account Created...\n" + email);
                            Toast.makeText(MainActivity.this, "Account Created...\n" + email, Toast.LENGTH_SHORT).show();
                        }else{
                            //existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing user... \n" + email);
                            Toast.makeText(MainActivity.this, "Existing user... \n" + email, Toast.LENGTH_SHORT).show();
                        }

                        //start TeamsView activity

                        Intent i = new Intent(MainActivity.this, TeamsView.class);
                        startActivity(i);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Login failed 
                        Log.d(TAG, "onFailure: LogIn failed" + e.getMessage());
                    }
                });
    }

    private void userLogin(){
        String userEmail = email.getText().toString();
        String userPw = pw.getText().toString();

        if (!userEmail.matches(emailPattern)){
            email.setError("Enter valid email");
        }else if (userPw.isEmpty() || userPw.length() < 6){
            pw.setError("Enter valid Password");
        }else{
            mAuth.signInWithEmailAndPassword(userEmail, userPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent i = new Intent(MainActivity.this, TeamsView.class);
                        startActivity(i);
                        Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}