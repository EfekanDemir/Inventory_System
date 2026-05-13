# Campus Management System

> **Kocaeli Üniversitesi — İleri Java Uygulamaları (TBL324)**
> Dr. Öğr. Üyesi Samet Diri | 2 Kişilik Ekip

---

## 📋 İçindekiler

1. [Proje Özeti](#1-proje-özeti)
2. [Sistem Mimarisi](#2-sistem-mimarisi)
3. [Veritabanı Şeması](#3-veritabanı-şeması)
4. [API Akış Diyagramı](#4-api-akış-diyagramı)
5. [API Uç Noktaları (Endpoints)](#5-api-uç-noktaları-endpoints)
6. [Mikroservis Detayları](#6-mikroservis-detayları)
7. [Android Canvas Grafikleri](#7-android-canvas-grafikleri)
8. [Docker Compose](#8-docker-compose)
9. [Performans Test Raporu](#9-performans-test-raporu)
10. [TDD Akışı](#10-tdd-akışı)
11. [Kurulum](#11-kurulum)
12. [Puan Değerlendirmesi](#12-puan-değerlendirmesi)

---

## 1. Proje Özeti
**TBL324 Envanter Takip Sistemi**, modern mikroservis mimarisi kullanılarak geliştirilmiş, ölçeklenebilir bir envanter yönetim çözümüdür. Sistem; kullanıcı yönetimi, stok takibi ve otomatik bildirim mekanizmalarını merkezi bir API Gateway arkasında birleştirir. Android mobil uygulaması üzerinden stok seviyeleri görselleştirilebilir ve tüm hareketler gerçek zamanlı olarak izlenebilir.

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

### 🗄️ Varlık-İlişki (ER) Diyagramı (PostgreSQL)
Aşağıdaki diyagram, sistemin ilişkisel veri modelini ve tablolar arası bağlantıları temsil eder.

```mermaid
erDiagram
    USERS ||--o{ STOCK_MOVEMENTS : "yapar"
    CATEGORIES ||--|{ ITEMS : "kategorilendirir"
    ITEMS ||--o{ STOCK_MOVEMENTS : "hareketlerini içerir"

    USERS {
        long id PK
        string username "unique"
        string email "unique"
        string passwordHash
        string role "ADMIN, MANAGER, PERSONEL"
        string firstName
        string lastName
        boolean isActive
        timestamp createdAt
        timestamp updatedAt
    }

    CATEGORIES {
        long id PK
        string name "unique"
        string description
        timestamp createdAt
    }

    ITEMS {
        long id PK
        string itemCode "unique"
        string name
        string description
        long categoryId FK
        int quantity
        int minStockLevel
        string status "ACTIVE, INACTIVE"
        timestamp createdAt
        timestamp updatedAt
    }

    STOCK_MOVEMENTS {
        long id PK
        long itemId FK
        string movementType "IN, OUT"
        int quantity
        string reason
        long userId FK
        timestamp createdAt
    }
```

### 🍃 NoSQL Şeması (MongoDB)
Loglama ve asenkron bildirim verileri için kullanılan koleksiyon yapıları:

| Koleksiyon | Açıklama | Ana Alanlar |
|------------|----------|-------------|
| `NOTIFICATION_LOGS` | Kullanıcı bildirim geçmişi | `userId, message, type, sentAt, isRead` |
| `ACTIVITY_LOGS` | Sistem genelindeki tüm hareketler | `userId, action, service, details, timestamp` |
| `LOW_STOCK_ALERTS` | Kritik stok seviyesi uyarıları | `itemId, currentQty, minLevel, status, createdAt` |

### ⚡ Redis Key-Value Yapısı
Oturum yönetimi ve performans için kullanılan anahtar yapıları:

- **Session Key:** `session:{token}`  
  **Value:** `{ "userId": 123, "role": "ADMIN", "expiresAt": "..." }` (TTL: 24h)
- **Cache Key:** `item:details:{itemCode}`  
  **Value:** ItemDTO (JSON) (TTL: 1h)

---

## 4. API Akış Diyagramı

### 🔄 Kritik Senaryolar Sequence Diyagramları

#### 1. Envanter Ekleme Akışı (8 Adım)
```mermaid
sequenceDiagram
    autonumber
    participant A as Android Client
    participant G as Kong Gateway
    participant I as Inventory Service
    participant DB as PostgreSQL
    
    A->>G: POST /api/inventory/items (JWT + Payload)
    G->>I: İstek Yönlendirme & Token Validation
    I->>I: GenericValidator: Data Integrity Check
    I->>DB: INSERT INTO items (Check Unique itemCode)
    DB-->>I: 201 Created (Success)
    I->>I: Cache Update (Redis)
    I-->>G: ItemDTO + Success Response
    G-->>A: JSON Response (201 Created)
```

#### 2. Stok Hareketi (Giriş/Çıkış) Akışı (12 Adım)
```mermaid
sequenceDiagram
    autonumber
    participant A as Android Client
    participant G as Kong Gateway
    participant I as Inventory Service
    participant DB as PostgreSQL
    participant N as Notification Service
    participant M as MongoDB

    A->>G: POST /api/inventory/movements (OUT, qty)
    G->>I: JWT Valide & Request Forward
    I->>DB: Check Current Stock Level
    DB-->>I: Return currentQty
    I->>I: Verify quantity >= outgoingQty
    I->>DB: UPDATE items SET quantity = quantity - qty
    I->>DB: INSERT INTO stock_movements
    DB-->>I: Transaction Commit
    opt Stok < minStockLevel
        I->>N: Trigger LowStockEvent (Async)
        N->>M: Save LowStockAlert
        M-->>N: Log Created
    end
    I-->>G: MovementResponse (200 OK)
    G-->>A: Update UI (Stock Counter)
```

#### 3. Hata Yönetimi Akışı
```mermaid
sequenceDiagram
    autonumber
    participant A as Android Client
    participant G as Kong Gateway
    participant S as Microservice
    participant E as GlobalExceptionHandler

    A->>G: POST /api/service/action (Invalid Data)
    G->>S: Forward Request
    S->>S: Business Logic Violation
    S->>E: Throw CustomException (e.g. ConflictException)
    E->>E: Format ErrorResponse (GenericResponse)
    E-->>G: 409 Conflict + Error JSON
    G-->>A: Display Error Toast/Dialog
```

---

## 5. API Uç Noktaları (Endpoints)

Sistemdeki tüm API çağrıları **Kong Gateway (Port: 8000)** üzerinden yönlendirilmektedir.

### 🔑 Header Bilgileri
| Header | Değer | Açıklama |
|--------|-------|----------|
| `Content-Type` | `application/json` | Veri formatı |
| `api-key` | `envanter-api-key-2026` | Gateway güvenlik anahtarı |
| `Authorization` | `Bearer {token}` | JWT yetkilendirme (Login hariç zorunlu) |

### 🛠️ Endpoint Listesi

| Method | Endpoint | Açıklama | Request Body | Response (Success) |
|:-------|:---------|:---------|:-------------|:-------------------|
| `POST` | `/api/users/register` | Yeni kullanıcı kaydı | `RegisterRequest` | `201 Created` |
| `POST` | `/api/users/login` | Kimlik doğrulama & Token | `LoginRequest` | `200 OK + Token` |
| `GET` | `/api/inventory/items` | Ürün listesi | - | `200 OK + List<ItemDTO>` |
| `POST` | `/api/inventory/items` | Yeni ürün ekleme | `ItemRequest` | `201 Created` |
| `POST` | `/api/inventory/movements` | Stok hareketi (IN/OUT) | `MovementRequest` | `201 Created` |
| `GET` | `/api/notifications/logs` | Bildirim geçmişi | - | `200 OK + List<Log>` |

### 📝 Örnek JSON Verileri

#### 1. Ürün Ekleme (`POST /api/inventory/items`)
**Request:**
```json
{
  "itemCode": "URUN-001",
  "name": "Kablosuz Mouse",
  "categoryId": 1,
  "quantity": 50,
  "minStockLevel": 10
}
```

#### 2. Stok Hareketi (`POST /api/inventory/movements`)
**Request:**
```json
{
  "itemId": 1,
  "movementType": "OUT",
  "quantity": 5,
  "reason": "Müşteri satışı"
}
```

### 🚦 HTTP Durum Kodları
- `200 OK`: İşlem başarılı.
- `201 Created`: Kayıt başarıyla oluşturuldu.
- `400 Bad Request`: Geçersiz veri girişi.
- `401 Unauthorized`: Hatalı token veya giriş bilgisi.
- `404 Not Found`: Kayıt bulunamadı.
- `409 Conflict`: Çakışan veri (örn: aynı kod) veya yetersiz stok.
- `500 Internal Server Error`: Sunucu hatası.

---

## 6. Mikroservis Detayları
Sistem 4 ana modülden oluşmaktadır:
- **common-lib:** Tüm servisler tarafından paylaşılan generic yapılar, merkezi hata yönetimi ve ortak yardımcı sınıflar.
- **user-service:** JdbcUserRepository ile PostgreSQL üzerinde kullanıcı yönetimi ve Redis üzerinde JWT session takibi.
- **inventory-service:** Envanter kayıtları (PostgreSQL) ve asenkron stok hareket logları (MongoDB).
- **notification-service:** Strategy ve Factory patternları kullanılarak Email ve Push bildirim gönderimi.

---

## 7. Android Canvas Grafikleri
Mobil uygulama, performans odaklı iki adet CustomView içermektedir:
- **StockLevelBarChartView:** Stok miktarlarını dinamik olarak büyüyen çubuk grafiklerle gösterir. `ValueAnimator` ve `postInvalidateOnAnimation` ile 60 FPS akıcılık sağlar.
- **CategoryPieChartView:** Ürün kategorilerinin dağılımını pasta grafik üzerinde gösterir. Donanım hızlandırmalı Canvas çizimi kullanır.

---

## 8. Docker Compose
 Docker Compose dosyası; PostgreSQL, MongoDB, Redis ve Kong Gateway servislerini tek bir komutla ayağa kaldıracak şekilde konfigüre edilmiştir. Her servis için sağlık kontrolleri (healthcheck) tanımlıdır.
 
 ```bash
 # Tüm servisleri başlat
 docker-compose up --build
 
 # Servisleri durdur ve volume'ları temizle
 docker-compose down -v
 ```

---

## 9. Performans Test Raporu
Sistem **k6** kullanılarak test edilmiştir:
- **Load Test:** 500 VU altında sistem stabil çalışmaktadır.
- **Stress Test:** Kırılma noktası ~450 VU olarak tespit edilmiş, Redis ve DB connection pool optimizasyonları yapılmıştır.
Detaylı rapor: `k6-tests/reports/performance-report.md`

---

## 10. TDD Akışı
Proje TDD (Test Driven Development) prensiplerine göre geliştirilmiştir. Her özellik için önce başarısız (RED) unit testler yazılmış, ardından implementasyon (GREEN) yapılmış ve son olarak REFACTOR süreci işletilmiştir. Git geçmişinde `test:` ve `feat:` commitleri bu sırayı takip etmektedir.

---

## 11. Kurulum

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

## 12. Puan Değerlendirmesi

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
