package com.example.skynet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.skynet.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // Tasarımdaki (XML) elemanlara kolayca ulaşmak için
    private FirebaseAuth mAuth; // Firebase kimlik doğrulama işlemleri için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance(); // Firebase bağlantısını başlat

        // Oturum Kontrolü: Eğer kullanıcı daha önce giriş yapmışsa tekrar şifre sorma
        if (mAuth.getCurrentUser() != null) {
            yonlendirChatActivity();
        }

        // Giriş Yap butonu tıklandığında
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String pass = binding.editPassword.getText().toString().trim();

            // Alanlar boş değilse işlemleri başlat
            if (!email.isEmpty() && !pass.isEmpty()) {
                // Firebase ile email ve şifre kontrolü yap
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Bilgiler doğruysa yönlendir
                                yonlendirChatActivity();
                            } else {
                                // Hatalı giriş (Yanlış şifre vb.)
                                Toast.makeText(this, "Giriş başarısız. Bilgileri kontrol edin.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Başarılı girişte veya hazırda oturum varsa Chat ekranına geçiş yapar
    private void yonlendirChatActivity() {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
        finish(); // Bu activity'yi öldür (Geri tuşuna basınca login ekranına dönmesin)
    }
}