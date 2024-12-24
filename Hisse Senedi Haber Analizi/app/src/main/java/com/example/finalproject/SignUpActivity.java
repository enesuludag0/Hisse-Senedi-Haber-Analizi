package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String ad, soyad, eposta, sifre;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressBar = binding.progressBar;

        binding.bttnKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
    }

    private void signUpUser() {
        ad = binding.editAd.getText().toString().trim();
        soyad = binding.editSoyad.getText().toString().trim();
        eposta = binding.editEposta.getText().toString().trim();
        sifre = binding.editSifre.getText().toString().trim();

        if (ad.isEmpty() || soyad.isEmpty() || eposta.isEmpty() || sifre.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Firestore'da e-posta adresini kontrol etme
        db.collection("users")
                .whereEqualTo("eposta", eposta)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Girdiğiniz e-posta adresi kullanılmaktadır.", Toast.LENGTH_SHORT).show();
                            } else {
                                createUser();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "E-posta adresi kontrol edilemedi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(eposta, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("ad", ad);
                                userInfo.put("soyad", soyad);
                                userInfo.put("eposta", eposta);
                                userInfo.put("sifre", sifre);

                                db.collection("users").document(user.getUid())
                                        .set(userInfo)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                                                    editor.putBoolean("isLoggedIn", true);
                                                    editor.apply();

                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    intent.putExtra("ad", ad);
                                                    intent.putExtra("soyad", soyad);
                                                    intent.putExtra("eposta", eposta);
                                                    intent.putExtra("sifre", sifre);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Kullanıcı bilgileri kaydedilemedi", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
