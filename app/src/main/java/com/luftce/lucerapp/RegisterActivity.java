package com.luftce.lucerapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    public void registerIntoFirebase(View view){
        EditText mailInput = findViewById(R.id.mailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText repPasswordInput = findViewById(R.id.repPasswordInput);
        String email = mailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String repPassword = repPasswordInput.getText().toString().trim();


        if(!email.isEmpty() && !password.isEmpty() && !repPassword.isEmpty()) {
            if (repPassword.equals(password)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(RegisterActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                             }
                        }
                        );
            }else {
                Toast.makeText(RegisterActivity.this, "Las dos contraseñas no son iguales", Toast.LENGTH_LONG).show();
                passwordInput.setText("");
            }
        }
    }
}
