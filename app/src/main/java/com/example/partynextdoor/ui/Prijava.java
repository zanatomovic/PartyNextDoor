package com.example.partynextdoor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.partynextdoor.Main;
import com.example.partynextdoor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Prijava extends AppCompatActivity {
    TextInputEditText emailEditText, lozinkaEditText;
    Button btn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijava);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailEditText = findViewById(R.id.email);
        lozinkaEditText = findViewById(R.id.lozinka);
        btn = findViewById(R.id.btn_prijava);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registrujSe);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, lozinka;
                email = String.valueOf(emailEditText.getText());
                lozinka = String.valueOf(lozinkaEditText.getText());

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Prijava.this, "Neispravan format email-a.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lozinka)) {
                    Toast.makeText(Prijava.this, "Unesite lozinku", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, lozinka)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    fetchUserData(user.getUid());
                                    Toast.makeText(Prijava.this, "Uspjesna prijava.",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), Main.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Prijava.this, "Nevalidan unos.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registracija.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchUserData(String userId) {
        db.collection("korisnici").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(Prijava.this, "User data fetched successfully.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Prijava.this, "User data not found.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Prijava.this, "Error fetching user data: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
