# Campus Management System

> **Kocaeli Üniversitesi — İleri Java Uygulamaları (TBL324)**
> Dr. Öğr. Üyesi Samet Diri | 2 Kişilik Ekip

---

## 📋 İçindekiler

1. [Proje Özeti](#1-proje-özeti)
2. [Sistem Mimarisi](#2-sistem-mimarisi)
3. [Veritabanı Şeması](#3-veritabanı-şeması)
4. [API Akış Diyagramı](#4-api-akış-diyagramı)
5. [Mikroservis Detayları](#5-mikroservis-detayları)
6. [Android Canvas Grafikleri](#6-android-canvas-grafikleri)
7. [Docker Compose](#7-docker-compose)
8. [Performans Test Raporu](#8-performans-test-raporu)
9. [TDD Akışı](#9-tdd-akışı)
10. [Kurulum](#10-kurulum)
11. [Puan Değerlendirmesi](#11-puan-değerlendirmesi)

---

## 1. Proje Özeti

> ⚠️ *Bu bölüm doldurulacak.*

---

## 2. Sistem Mimarisi

### 🌐 C4 Container Diyagramı
Aşağıdaki diyagram, sistemin konteyner mimarisini, kullanıcıların sistemle nasıl etkileşime girdiğini ve mikroservislerin birbirleriyle ve veritabanlarıyla olan ilişkilerini göstermektedir.

```mermaid
C4Container
    title TBL324 Envanter Takip Sistemi - C4 Container Diyagramı

    Person(personel, "Personel", "Sistemi kullanan ve envanter yönetimi yapan çalışan.")
    Person(yonetici, "Yönetici", "Sistemdeki tüm hareketleri ve logları izleyebilen yönetici.")

    System_Boundary(c1, "Envanter Takip Sistemi") {
        Container(android_app, "Android App", "Java, Android SDK", "Kullanıcıların sisteme eriştiği mobil uygulama.")
        Container(kong_gateway, "Kong Gateway", "Kong 3.5", "Tüm API trafiğini yöneten ve servislere dağıtan API Ağ Geçidi.")
        
        Container(user_service, "user-service", "Java, Spring Boot", "Kullanıcı kaydı, kimlik doğrulama ve JWT token üretimi.")
        Container(inventory_service, "inventory-service", "Java, Spring Boot", "Envanter yönetimi, stok takibi ve hareketlerin işlenmesi.")
        Container(notification_service, "notification-service", "Java, Spring Boot", "Düşük stok bildirimleri, loglama ve raporlama.")
        
        ContainerDb(postgres_db, "PostgreSQL", "Relational Database", "Kullanıcı ve envanter/stok verilerinin tutulduğu ana veritabanı.")
        ContainerDb(mongodb_db, "MongoDB", "NoSQL Database", "Stok hareketleri, sistem logları ve bildirimlerin tutulduğu veritabanı.")
        ContainerDb(redis_cache, "Redis", "In-Memory Cache", "Session yönetimi ve performans artışı için önbellekleme.")
    }

    Rel(personel, android_app, "Kullanır", "HTTPS/JSON")
    Rel(yonetici, android_app, "Kullanır", "HTTPS/JSON")

    Rel(android_app, kong_gateway, "API İstekleri yapar", "HTTPS/JSON")

    Rel(kong_gateway, user_service, "İletir", "HTTP/JSON")
    Rel(kong_gateway, inventory_service, "İletir", "HTTP/JSON")
    Rel(kong_gateway, notification_service, "İletir", "HTTP/JSON")

    Rel(inventory_service, notification_service, "Bildirim gönderir", "HTTP/JSON")
    Rel(inventory_service, user_service, "Kullanıcı doğrular", "HTTP/JSON")

    Rel(user_service, postgres_db, "Okur/Yazar", "JDBC")
    Rel(user_service, redis_cache, "Oturum açar/Token saklar", "Redis Protocol")

    Rel(inventory_service, postgres_db, "Okur/Yazar", "JDBC")
    Rel(inventory_service, mongodb_db, "Log yazar", "MongoDB Protocol")

    Rel(notification_service, mongodb_db, "Bildirim/Log okur ve yazar", "MongoDB Protocol")

    UpdateElementStyle(android_app, $bgColor="#E6A822", $fontColor="#FFFFFF")
    UpdateElementStyle(kong_gateway, $bgColor="#E6399B", $fontColor="#FFFFFF")
    UpdateElementStyle(user_service, $bgColor="#2B78E4", $fontColor="#FFFFFF")
    UpdateElementStyle(inventory_service, $bgColor="#2B78E4", $fontColor="#FFFFFF")
    UpdateElementStyle(notification_service, $bgColor="#2B78E4", $fontColor="#FFFFFF")
    UpdateElementStyle(postgres_db, $bgColor="#34A853", $fontColor="#FFFFFF")
    UpdateElementStyle(mongodb_db, $bgColor="#34A853", $fontColor="#FFFFFF")
    UpdateElementStyle(redis_cache, $bgColor="#34A853", $fontColor="#FFFFFF")
```

### 🧩 Bileşen Açıklamaları
- **user-service:** Kullanıcı kimlik doğrulama, JWT token üretimi ve yetkilendirme işlemlerini gerçekleştirir. Session verilerini Redis üzerinde tutar.
- **inventory-service:** Envanter yönetimi, stok takibi ve hareketlerin işlenmesini sağlar. Kritik stok seviyelerinde bildirim servisini asenkron olarak tetikler.
- **notification-service:** Düşük stok bildirimleri gibi sistem uyarılarını işler, raporlama yapar ve aktivite loglarını MongoDB üzerinde saklar.

---

## 3. Veritabanı Şeması

### 🗄️ Varlık-İlişki (ER) Diyagramı
Aşağıdaki ER diyagramı, ilişkisel veritabanında (PostgreSQL) tutulan ana tabloları, alanlarını ve aralarındaki bağlantıları temsil eder.

```mermaid
erDiagram
    User ||--o{ StockMovement : "yapar"
    Item ||--o{ StockMovement : "hareketini içerir"
    Category ||--|{ Item : "kategorilendirir"

    User {
        Long id PK
        String username
        String email
        String password
        String role
        LocalDateTime createdAt
    }

    Item {
        Long id PK
        String itemCode
        String name
        Long categoryId FK
        Integer quantity
        Integer minStockLevel
        String status
    }

    Category {
        Long id PK
        String name
        String description
    }

    StockMovement {
        Long id PK
        Long itemId FK
        String movementType
        Integer quantity
        LocalDateTime createdAt
    }
```

---

## 4. API Akış Diyagramı

### 🔄 Kritik Senaryolar Sequence Diyagramları

#### 1. Kullanıcı Girişi (Login Akışı)
Kullanıcının kimlik doğrulaması yapıp Redis üzerinde session açması süreci.
```mermaid
sequenceDiagram
    participant A as Android
    participant K as Kong Gateway
    participant U as UserService
    participant R as Redis

    A->>K: POST /api/users/login (Credentials)
    K->>U: Yönlendir
    U->>U: Şifre Doğrulama
    U->>R: Oturum aç ve Token kaydet
    R-->>U: OK
    U-->>K: 200 OK + JWT Token
    K-->>A: JSON Response (Token)
```

#### 2. Yeni Ürün Ekleme (Create Item)
Sisteme yetkili bir kullanıcının yeni bir stok kalemi eklemesi.
```mermaid
sequenceDiagram
    participant A as Android
    participant K as Kong Gateway
    participant I as InventoryService
    participant DB as PostgreSQL

    A->>K: POST /api/inventory/items (JWT, Payload)
    K->>I: İstek Yönlendirme (Token Valide)
    I->>I: Validasyon (GenericValidator)
    I->>DB: INSERT INTO items
    DB-->>I: ID Döner
    I-->>K: 201 Created + ItemDTO
    K-->>A: JSON Response
```

#### 3. Stok Hareketi ve Uyarı (Stock Movement)
Stok düştüğünde MongoDB üzerine uyarı loglanması.
```mermaid
sequenceDiagram
    participant A as Android
    participant K as Kong Gateway
    participant I as InventoryService
    participant N as NotificationService
    participant M as MongoDB

    A->>K: POST /api/inventory/movements (OUT, qty)
    K->>I: İstek Yönlendirme
    I->>I: Stok Yeterliliği Kontrolü
    I->>I: Miktar Güncellemesi
    opt Stok < minStockLevel
        I->>N: Asenkron Bildirim İsteği (Event)
        N->>M: LowStockAlert Logu Kaydet
        M-->>N: OK
    end
    I-->>K: 200 OK
    K-->>A: JSON Response
```

---

## 5. Mikroservis Detayları

> ⚠️ *Class diyagramları eklenecek.*

---

## 6. Android Canvas Grafikleri

> ⚠️ *CustomView açıklamaları eklenecek.*

---

## 7. Docker Compose

```bash
# Tüm servisleri başlat
docker-compose up --build

# Servisleri durdur ve volume'ları temizle
docker-compose down -v
```

> ⚠️ *docker-compose.yml detayları eklenecek.*

---

## 8. Performans Test Raporu

> ⚠️ *k6 test sonuçları eklenecek.*

---

## 9. TDD Akışı

> ⚠️ *Red-Green-Refactor döngüsü ve commit zaman çizelgesi eklenecek.*

---

## 10. Kurulum

### Gereksinimler

| Araç | Versiyon |
|------|----------|
| Java | 17 |
| Maven | 3.9.x |
| Docker | 24.x |
| Docker Compose | 2.24.x |
| Android SDK | 34 |

### Hızlı Başlangıç

```bash
# 1. Repo'yu klonla
git clone https://github.com/[USERNAME]/campus-management-system.git
cd campus-management-system

# 2. Develop branch'e geç
git checkout develop

# 3. Docker ile başlat
docker-compose up --build
```

---

## 11. Puan Değerlendirmesi

| Kriter | Puan | Durum |
|--------|------|-------|
| API + Mikroservis Mimarisi | 20 pt | ⏳ |
| Generic Yapılar | 10 pt | ⏳ |
| Mobil GUI (Custom + Android) | 15 pt | ⏳ |
| JDBC + NoSQL | 10 pt | ⏳ |
| SOLID & OOP | 10 pt | ⏳ |
| Hata Yönetimi | 5 pt | ⏳ |
| Performans Testleri | 5 pt | ⏳ |
| Analiz & Doküman | 5 pt | ⏳ |
| Docker Compose | +5 pt | ⏳ |
| TDD | +10 pt | ⏳ |
| Gateway | +5 pt | ⏳ |
| **Toplam** | **100 pt** | ⏳ |

---

> **Son Güncelleme:** 2026-05-12
> **Proje:** Campus Management System — TBL324
