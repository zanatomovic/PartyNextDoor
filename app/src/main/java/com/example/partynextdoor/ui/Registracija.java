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

import com.example.partynextdoor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registracija extends AppCompatActivity {
    TextInputEditText emailEditText, lozinkaEditText;
    Button btn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Prijava.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        lozinkaEditText = findViewById(R.id.lozinka);
        btn = findViewById(R.id.btn_registracija);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.prijaviSe);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, lozinka;
                email = String.valueOf(emailEditText.getText());
                lozinka = String.valueOf(lozinkaEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Registracija.this, "Unesite email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lozinka)) {
                    Toast.makeText(Registracija.this, "Unesite lozinku", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, lozinka)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid();
                                        dodajKorisnika(uid, email);
                                        Toast.makeText(Registracija.this, "Kreiran nalog.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Registracija.this, "Kreiranje neuspesno.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Prijava.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void dodajKorisnika(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> korisnik = new HashMap<>();
        korisnik.put("email", email);
        korisnik.put("created_at", System.currentTimeMillis());

        db.collection("korisnici").document(uid)
                .set(korisnik);
    }
}