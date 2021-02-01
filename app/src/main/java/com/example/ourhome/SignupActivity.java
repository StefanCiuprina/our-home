package com.example.ourhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {


    EditText editTextEmailAddressSignUp;
    EditText editTextPasswordSignUp;
    EditText editTextConfirmPassword;
    EditText editTextUsername;
    Button buttonSignup;
    TextView textViewLoginButton;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        editTextEmailAddressSignUp = findViewById(R.id.editTextEmailAddressSignUp);
        editTextPasswordSignUp = findViewById(R.id.editTextPasswordSignUp);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewLoginButton = findViewById(R.id.textViewLoginButton);

        buttonSignup.setOnClickListener(v -> {
            mFirebaseAuth.signInWithEmailAndPassword("admin@admin.admin", "1994320").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    String email = editTextEmailAddressSignUp.getText().toString();
                    String password = editTextPasswordSignUp.getText().toString();
                    String username = editTextUsername.getText().toString();
                    if(isInputValid(email, password)) {
                        Log.v("AICI", "input valid");
                        DocumentReference documentReference = fStore.collection("users").document("usernames");
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> map = documentSnapshot.getData();
                                Log.v("AICI", "AICI");
                                if(map != null) {
                                    Object entry = map.get(username);
                                    if(entry == null) {
                                        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
                                            if(task.isSuccessful()) {
                                                Log.v("AICI", "Successful");
                                                userID = mFirebaseAuth.getCurrentUser().getUid();
                                                map.put(username, userID);
                                                documentReference.set(map);
                                                DocumentReference documentReference2 = fStore.collection("users").document(userID);
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("username", username);
                                                user.put("email", email);
                                                documentReference2.set(user);
                                                Toast.makeText(SignupActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                                finish();
                                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            } else {
                                                if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                    Toast.makeText(SignupActivity.this, "Email already registered.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "An error occured.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        editTextUsername.setError("Username already exists!");
                                        editTextUsername.requestFocus();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        });

        textViewLoginButton.setOnClickListener(v -> {
            finish();
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }

    private boolean isInputValid(String email, String password) {
        if(email.isEmpty()) {
            editTextEmailAddressSignUp.setError("Email is required.");
            editTextEmailAddressSignUp.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailAddressSignUp.setError("Please enter a valid email.");
            editTextEmailAddressSignUp.requestFocus();
            return false;
        }

        if(password.isEmpty()) {
            editTextPasswordSignUp.setError("Password is required.");
            editTextPasswordSignUp.requestFocus();
            return false;
        }

        if(password.length() < 6) {
            editTextPasswordSignUp.setError("Minimum length of password should be 6");
            editTextPasswordSignUp.requestFocus();
            return false;
        }

        if(editTextConfirmPassword.getText().toString().trim().isEmpty()) {
            editTextConfirmPassword.setError("Password confirmation is required.");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if(!password.equals(editTextConfirmPassword.getText().toString().trim())) {
            editTextConfirmPassword.setError("Passwords don't match!");
            editTextConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }
}