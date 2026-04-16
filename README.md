# Skynet Chat App 🚀

**TR:** Firebase altyapısı kullanılarak Java ile geliştirilmiş, gerçek zamanlı oda sistemli bir mesajlaşma uygulamasıdır. Kullanıcıların dinamik olarak sohbet odaları oluşturmasına, mesajlarını anlık olarak gönderip düzenlemesine veya silmesine olanak tanır.

**EN:** A real-time messaging application developed in Java using the Firebase infrastructure. It features a dynamic room-based system, allowing users to create rooms and send, edit, or delete messages with real-time synchronization.

---

## 🛠 Özellikler / Features

### 🇹🇷 Türkçe
* **Gerçek Zamanlı Mesajlaşma:** Firebase Firestore entegrasyonu ile anlık veri senkronizasyonu.
* **Dinamik Oda Yönetimi:** Kullanıcılar yeni odalar oluşturabilir, silebilir veya isimlerini güncelleyebilir.
* **Mesaj Kontrolü (CRUD):** Gönderilen mesajları düzenleme ve silme desteği. Düzenlenen mesajlarda otomatik "(düzenlendi)" ibaresi.
* **Akıllı Tarih Formatı:** Mesajın atıldığı zamana göre (Bugün/Geçmiş) değişen dinamik tarih ve saat gösterimi.
* **Temiz Kod Yapısı:** Merkezi sabitler ve optimize edilmiş döküman yönetimi.

### 🇺🇸 English
* **Real-time Messaging:** Instant data synchronization with Firebase Firestore.
* **Dynamic Room Management:** Ability to create, delete, or rename chat rooms.
* **Message Operations (CRUD):** Support for editing and deleting sent messages with an "(edited)" tag.
* **Smart Time Formatting:** Dynamic date and time display based on when the message was sent (Today/Past).
* **Clean Architecture:** Optimized document management and use of central constants.

---

## 📱 Ekran Görüntüleri / Screenshots

<img width="383" height="846" alt="Capture" src="https://github.com/user-attachments/assets/8d5a32e1-3572-4911-b8a6-3f88d603b9c1" />
<img width="381" height="847" alt="Screenshot 2026-04-16 160034" src="https://github.com/user-attachments/assets/3f5f551f-0b61-4363-ab39-b2ca0230fc9f" />

---


## ⚙️ Kurulum / Installation
1. Projeyi bilgisayarınıza indirin (Clone).
2. Android Studio ile açın.
3. Kendi Firebase projenizi oluşturun ve `google-services.json` dosyasını `app/` klasörüne ekleyin.
4. Firebase üzerinde Firestore ve Authentication (Email/Password) servislerini aktif edin.
5. Uygulamayı çalıştırın!
