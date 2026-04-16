package com.example.skynet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.skynet.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private FirebaseFirestore db;
    private String seciliOdaId = "genel_oda_sabit";
    private ArrayList<String> odaListesi = new ArrayList<>();
    private ArrayList<String> odaIdleri = new ArrayList<>();
    private OdaAdapter odaAdapterCustom;
    private ArrayList<Mesaj> mesajlar = new ArrayList<>();
    private MesajAdapter mesajAdapter;
    private final String GENEL_ODA_ID = "genel_oda_sabit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        odaAdapterCustom = new OdaAdapter();
        binding.roomList.setAdapter(odaAdapterCustom);
        odalarıGetir();

        mesajAdapter = new MesajAdapter(mesajlar, seciliOdaId);
        binding.recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerMessages.setAdapter(mesajAdapter);

        binding.toolbar.setTitle("genel");
        genelOdayiKontrolEtVeOlustur();
        mesajlarıDinle();

        binding.btnAddRoom.setOnClickListener(v -> odaEklemeDiyaloguGoster());
        binding.roomList.setOnItemClickListener((parent, view, position, id) -> {
            seciliOdaId = odaIdleri.get(position);
            binding.toolbar.setTitle(odaListesi.get(position));
            mesajAdapter.setAktifOdaId(seciliOdaId);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            mesajlarıDinle();
        });

        binding.btnSend.setOnClickListener(v -> {
            String mesajMetni = binding.editMessage.getText().toString().trim();
            if (!mesajMetni.isEmpty()) mesajGonder(mesajMetni);
        });
    }

    private void odalarıGetir() {
        db.collection(Mesaj.COL_ODALAR).addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                odaListesi.clear(); odaIdleri.clear();
                odaListesi.add("genel"); odaIdleri.add(GENEL_ODA_ID);
                for (DocumentSnapshot doc : value.getDocuments()) {
                    if (!doc.getId().equals(GENEL_ODA_ID)) {
                        odaListesi.add(doc.getString(Mesaj.FIELD_ODA_ADI));
                        odaIdleri.add(doc.getId());
                    }
                }
                odaAdapterCustom.notifyDataSetChanged();
            }
        });
    }

    private void mesajlarıDinle() {
        db.collection(Mesaj.COL_ODALAR).document(seciliOdaId).collection(Mesaj.COL_MESAJLAR)
                .orderBy(Mesaj.FIELD_TARIH, Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        mesajlar.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // mesajlarıDinle içindeki döngüde:
                            long milis = 0;
                            if (doc.getTimestamp(Mesaj.FIELD_TARIH) != null) {
                                // Sunucu saati varsa onu al
                                milis = doc.getTimestamp(Mesaj.FIELD_TARIH).toDate().getTime();
                            } else {
                                // Sunucu saati henüz yansımadıysa telefonun anlık saatini kullan (Boş kalmasın diye)
                                milis = System.currentTimeMillis();
                            }

                            boolean duzenlendi = doc.contains(Mesaj.FIELD_DUZENLENDI) && doc.getBoolean(Mesaj.FIELD_DUZENLENDI);
                            mesajlar.add(new Mesaj(doc.getString(Mesaj.FIELD_GONDEREN), doc.getString(Mesaj.FIELD_METIN), doc.getId(), duzenlendi, milis));
                        }
                        mesajAdapter.notifyDataSetChanged();
                        if (!mesajlar.isEmpty()) binding.recyclerMessages.smoothScrollToPosition(mesajlar.size() - 1);
                    }
                });
    }

    private void mesajGonder(String metin) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Map<String, Object> m = new HashMap<>();
        m.put(Mesaj.FIELD_METIN, metin);
        m.put(Mesaj.FIELD_GONDEREN, email);
        m.put(Mesaj.FIELD_TARIH, FieldValue.serverTimestamp());
        db.collection(Mesaj.COL_ODALAR).document(seciliOdaId).collection(Mesaj.COL_MESAJLAR).add(m)
                .addOnSuccessListener(dr -> binding.editMessage.setText(""));
    }

    private void genelOdayiKontrolEtVeOlustur() {
        db.collection(Mesaj.COL_ODALAR).document(GENEL_ODA_ID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                Map<String, Object> gOda = new HashMap<>();
                gOda.put(Mesaj.FIELD_ODA_ADI, "genel");
                db.collection(Mesaj.COL_ODALAR).document(GENEL_ODA_ID).set(gOda);
            }
        });
    }

    private void odaEklemeDiyaloguGoster() {
        EditText input = new EditText(this);
        input.setHint("Oda ismini girin...");
        new AlertDialog.Builder(this).setTitle("Yeni Oda").setView(input)
                .setPositiveButton("Oluştur", (d, w) -> {
                    String ad = input.getText().toString().trim();
                    if (!ad.isEmpty()) {
                        Map<String, Object> o = new HashMap<>();
                        o.put(Mesaj.FIELD_ODA_ADI, ad);
                        db.collection(Mesaj.COL_ODALAR).add(o);
                    }
                }).setNegativeButton("İptal", null).show();
    }

    private void odaSil(String odaId, int position) {
        new AlertDialog.Builder(this).setTitle("Odayı Sil").setMessage("Emin misiniz?")
                .setPositiveButton("Sil", (d, w) -> {
                    db.collection(Mesaj.COL_ODALAR).document(odaId).delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Oda silindi", Toast.LENGTH_SHORT).show();
                        if (seciliOdaId.equals(odaId)) {
                            seciliOdaId = GENEL_ODA_ID;
                            binding.toolbar.setTitle("# genel");
                            mesajlarıDinle();
                        }
                    });
                }).setNegativeButton("İptal", null).show();
    }

    private void odaYenidenAdlandir(String odaId, String eskiIsim) {
        EditText input = new EditText(this);
        input.setText(eskiIsim);
        new AlertDialog.Builder(this).setTitle("Yeniden Adlandır").setView(input)
                .setPositiveButton("Güncelle", (d, w) -> {
                    String yeni = input.getText().toString().trim();
                    if (!yeni.isEmpty()) db.collection(Mesaj.COL_ODALAR).document(odaId).update(Mesaj.FIELD_ODA_ADI, yeni);
                }).setNegativeButton("İptal", null).show();
    }

    public class OdaAdapter extends ArrayAdapter<String> {
        public OdaAdapter() { super(ChatActivity.this, R.layout.list_item_room, odaListesi); }
        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            if (convertView == null) convertView = getLayoutInflater().inflate(R.layout.list_item_room, parent, false);
            TextView tv = convertView.findViewById(R.id.textViewOdaAdi);
            android.widget.ImageView menuBtn = convertView.findViewById(R.id.btnRoomMenu);
            String oId = odaIdleri.get(position);
            tv.setText("# " + odaListesi.get(position));
            if (oId.equals(GENEL_ODA_ID)) menuBtn.setVisibility(android.view.View.GONE);
            else {
                menuBtn.setVisibility(android.view.View.VISIBLE);
                menuBtn.setOnClickListener(v -> {
                    android.widget.PopupMenu popup = new android.widget.PopupMenu(ChatActivity.this, menuBtn);
                    popup.getMenu().add("Yeniden Adlandır"); popup.getMenu().add("Sil");
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getTitle().equals("Yeniden Adlandır")) odaYenidenAdlandir(oId, odaListesi.get(position));
                        else odaSil(oId, position);
                        return true;
                    });
                    popup.show();
                });
            }
            return convertView;
        }
    }
}