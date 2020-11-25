package com.healthpack.myhealthassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    String email, password;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //DataManager dataManager = Singleton.getInstance().getDataManager();

        //REGISTER A USER
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Registration successful " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

                            Settings settings = new Settings();
                            settings.setMetric(true); //set to metric by default
                            //add the new user to Firebase
                            myRef.child(mAuth.getCurrentUser().getUid()).child("settings").push().setValue(settings);

                            Intent i = new Intent(MainActivity.this, HomeActivity.class); //create an intent to move the the next activity
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //VERIFY USER
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, mAuth.getCurrentUser().getEmail() + " logged in", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(MainActivity.this, HomeActivity.class); //create an intent to move the the next activity
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }





}