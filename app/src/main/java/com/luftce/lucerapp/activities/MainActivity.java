package com.luftce.lucerapp.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.luftce.lucerapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    public void loginToFirebase(View view){
        EditText userInput = findViewById(R.id.userInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        String email = userInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();


        if(!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        Gson gson = new Gson();
                        String username = email.substring(0, email.indexOf("@"));
                        String json = gson.toJson(username.replaceAll("[^a-zA-Z]", ""));
                        String fileName = "usuario.json";
                        FileOutputStream fileOutputStream;
                        try {
                            fileOutputStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            fileOutputStream.write(json.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(MainActivity.this, DrawerActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(MainActivity.this, "Por favor, introduce usuario y contraseña", Toast.LENGTH_LONG).show();
        }
    }

    public void changeToRegister(View button){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}