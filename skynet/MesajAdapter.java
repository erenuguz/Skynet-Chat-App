package com.example.skynet;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.databinding.MesajSatiriBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajHolder> {
    private ArrayList<Mesaj> mesajListesi;
    private String aktifOdaId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MesajAdapter(ArrayList<Mesaj> mesajListesi, String aktifOdaId) {
        this.mesajListesi = mesajListesi;
        this.aktifOdaId = aktifOdaId;
    }

    public void setAktifOdaId(String aktifOdaId) { this.aktifOdaId = aktifOdaId; }

    @NonNull
    @Override
    public MesajHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MesajSatiriBinding binding = MesajSatiriBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MesajHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajHolder holder, int position) {
        Mesaj mesaj = mesajListesi.get(position);

        // TARİH FORMATI AYARI: "dd MMM HH:mm" -> 16 Nis 15:30 gibi gösterir
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM HH:mm", java.util.Locale.getDefault());
        String formatliZaman = sdf.format(new java.util.Date(mesaj.getZaman()));

        // Bilgi satırını oluştur: e-posta + boşluk + tarih
        String bilgiSatiri = "eren" + "   " + "(" + formatliZaman + ")";

        if (mesaj.isDuzenlendi()) {
            bilgiSatiri += " (düzenlendi)";
        }

        holder.binding.txtGonderen.setText(bilgiSatiri);
        holder.binding.txtMesaj.setText(mesaj.getMetin());

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        holder.binding.btnMessageMenu.setOnClickListener(v -> {
            if (mesaj.getGonderen().equals(email)) {
                PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.binding.btnMessageMenu);
                popup.getMenu().add("Düzenle"); popup.getMenu().add("Sil");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Düzenle")) mesajDuzenleDiyalogu(holder.itemView.getContext(), mesaj);
                    else mesajSil(mesaj);
                    return true;
                });
                popup.show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Sadece kendi mesajlarını yönetebilirsin!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mesajDuzenleDiyalogu(Context context, Mesaj mesaj) {
        EditText input = new EditText(context);
        input.setText(mesaj.getMetin());
        input.setSelection(input.getText().length());
        new AlertDialog.Builder(context).setTitle("Mesajı Düzenle").setView(input)
                .setPositiveButton("Güncelle", (dialog, which) -> {
                    String yeni = input.getText().toString().trim();
                    if (!yeni.isEmpty()) {
                        Map<String, Object> up = new HashMap<>();
                        up.put(Mesaj.FIELD_METIN, yeni);
                        up.put(Mesaj.FIELD_DUZENLENDI, true);
                        db.collection(Mesaj.COL_ODALAR).document(aktifOdaId)
                                .collection(Mesaj.COL_MESAJLAR).document(mesaj.getId()).update(up);
                    }
                }).setNegativeButton("İptal", null).show();
    }

    private void mesajSil(Mesaj mesaj) {
        db.collection(Mesaj.COL_ODALAR).document(aktifOdaId).collection(Mesaj.COL_MESAJLAR).document(mesaj.getId()).delete();
    }

    @Override
    public int getItemCount() { return mesajListesi.size(); }

    public static class MesajHolder extends RecyclerView.ViewHolder {
        MesajSatiriBinding binding;
        public MesajHolder(MesajSatiriBinding b) { super(b.getRoot()); this.binding = b; }
    }
}