# TBL324 — 3.5 GÜNLÜK KRİTİK GÖREV DAĞILIMI
## ⏰ Son Teslim: 3.5 Gün (84 Saat) | Kodlama Ajanları Aktif
## 🏭 Proje: Envanter Takip Sistemi

> **Kocaeli Üniversitesi — İleri Java Uygulamaları**
> Dr. Öğr. Üyesi Samet Diri | 2 Kişilik Ekip

---

## 🚨 KRİTİK FARKLILIKLAR (3.5 Gün Versiyonu)

| Eski Plan (6 Hafta) | Yeni Plan (3.5 Gün) | Neden |
|---------------------|---------------------|-------|
| Her üye kendi alanında | **Cross-check zorunlu** | Kodlama ajanları üretiyor, insan review şart |
| TDD doğal akış | **Commit simülasyonu** | Tarih damgaları için git rebase/squash |
| Haftada 4 commit | **Günde 4-5 commit** | 40 toplam commit hedefi |
| Ayrı ayrı çalışma | **6 saatlik bloklar, anlık senkronizasyon** | Zaman kısıtlı |

---

## ⏱️ SAAT BAZLI GÖREV AKIŞI

### Gün 1 (24 Saat)

| Saat | Sorumlu | Görev | Teslim | Puan | Kriter | Ajan | Not |
|------|---------|-------|--------|------|--------|------|-----|
| 00:00-06:00 | **Her İkisi** | GitHub repo kurulumu + branch yapısı (main, develop, feature/*) | 06:00 | Temel | Commit disiplini | Kodlama ajanı | README şablonu hazır |
| 00:00-06:00 | **Her İkisi** | Docker Compose iskelet (PostgreSQL + MongoDB + Redis) | 06:00 | +5 Docker | Docker Compose | Kodlama ajanı | Healthcheck'ler aktif |
| 06:00-12:00 | **Üye 1** | user-service: Entity + GenericRepository<T> + JdbcUserRepository | 12:00 | 20 API | API + Mikroservis | Kodlama ajanı | SOLID uyumlu |
| 06:00-12:00 | **Üye 2** | TDD: UserService testleri (RED commit simülasyonu) | 12:00 | +10 TDD | TDD | Kodlama ajanı | Tarih damgası önemli! |
| 12:00-18:00 | **Üye 1** | user-service: JWT + Redis session + GlobalExceptionHandler | 18:00 | 20 API | API + Mikroservis | Kodlama ajanı | 4xx/5xx standart kodlar |
| 12:00-18:00 | **Üye 2** | Generic<T>: ResponseWrapper + Paginator + Validator | 18:00 | 10 Generic | Generic Yapılar | Kodlama ajanı | Tüm servislerde kullanılabilir |
| 18:00-24:00 | **Üye 1** | inventory-service: Entity (Item, Category, StockMovement) + JDBC + MongoDB | 24:00 | 20 API | API + Mikroservis | Kodlama ajanı | Strategy pattern ile stok kontrolü |
| 18:00-24:00 | **Üye 2** | TDD: InventoryService testleri (Item ekleme, stok hareketi) (RED) | 24:00 | +10 TDD | TDD | Kodlama ajanı | Edge case'ler dahil |

---

### Gün 2 (24 Saat)

| Saat | Sorumlu | Görev | Teslim | Puan | Kriter | Ajan | Not |
|------|---------|-------|--------|------|--------|------|-----|
| 00:00-06:00 | **Üye 1** | notification-service: Email + Push + MongoDB log | 06:00 | 20 API | API + Mikroservis | Kodlama ajanı | Factory + Strategy pattern |
| 00:00-06:00 | **Üye 2** | TDD: NotificationService testleri (RED) | 06:00 | +10 TDD | TDD | Kodlama ajanı | Mock ile email testi |
| 06:00-12:00 | **Üye 1** | Kong Gateway: declarative config + route + rate limiting | 12:00 | +5 Gateway | Gateway | Kodlama ajanı | docker-compose'a entegre |
| 06:00-12:00 | **Üye 2** | Android projesi: Login + Dashboard + Envanter Listesi (temel) | 12:00 | 15 Mobil | Mobil GUI | Kodlama ajanı | Java 17, SDK 34 |
| 12:00-18:00 | **Üye 1** | Cross-check: Tüm servislerde SOLID + Design Patterns review | 18:00 | 10 SOLID | SOLID & OOP | İnsan review | Repository, Factory, Strategy kontrol |
| 12:00-18:00 | **Üye 2** | Android: CustomView bileşenleri + Canvas grafik çizimi | 18:00 | 15 Mobil | Mobil GUI | Kodlama ajanı | Stok seviye bar grafiği, kategori pasta grafiği |
| 18:00-24:00 | **Üye 1** | docker-compose up son testi — tüm servisler ayağa kalkıyor mu? | 24:00 | +5 Docker | Docker Compose | İnsan test | KRİTİK! Tek komut çalışmalı |
| 18:00-24:00 | **Üye 2** | Android: API bağlantı (Retrofit) + hata yönetimi entegrasyonu | 24:00 | 15 Mobil | Mobil GUI | Kodlama ajanı | 4xx/5xx handle |

---

### Gün 3 (24 Saat)

| Saat | Sorumlu | Görev | Teslim | Puan | Kriter | Ajan | Not |
|------|---------|-------|--------|------|--------|------|-----|
| 00:00-06:00 | **Üye 1** | TDD commit geçmişi simülasyonu (test önce, impl sonra) | 06:00 | +10 TDD | TDD | Git script | Tarih damgaları ayarlanacak |
| 00:00-06:00 | **Üye 2** | k6 yük test senaryosu yazma + çalıştırma (envanter senaryoları) | 06:00 | 5 Performans | Performans Testleri | Kodlama ajanı | 100→500 VU ramp-up |
| 06:00-12:00 | **Üye 1** | Hata yönetimi: tüm 4xx/5xx kodları test + log review | 12:00 | 5 Hata | Hata Yönetimi | İnsan test | GlobalExceptionHandler aktif mi? |
| 06:00-12:00 | **Üye 2** | k6 kırılma noktası analizi + rapor oluşturma | 12:00 | 5 Performans | Performans Testleri | Kodlama ajanı | GitHub'a yüklenecek |
| 12:00-18:00 | **Üye 1** | README: Sistem mimarisi Mermaid C4 Container diyagramı | 18:00 | 5 Doküman | Analiz & Doküman | Kodlama ajanı | Envanter sistemine özgü |
| 12:00-18:00 | **Üye 2** | README: DB şeması + API akış Mermaid diyagramları | 18:00 | 5 Doküman | Analiz & Doküman | Kodlama ajanı | ER + Sequence diagram (Item, StockMovement) |
| 18:00-24:00 | **Her İkisi** | Cross-check: Commit geçmişi eşit mi? (her üye min 15 commit) | 24:00 | Temel | Commit disiplini | İnsan review | Son gün toplu yükleme YASAK! |
| 18:00-24:00 | **Her İkisi** | Cross-check: Tüm kriterler karşılanıyor mu? (checklist) | 24:00 | Tümü | Genel | İnsan review | 100/100 kontrol listesi |

---

### Gün 4 (Yarım Gün — 12 Saat) — SUNUM GÜNÜ

| Saat | Sorumlu | Görev | Teslim | Puan | Kriter | Ajan | Not |
|------|---------|-------|--------|------|--------|------|-----|
| 00:00-04:00 | **Üye 1** | Final: docker-compose up son kontrol (temiz başlatma) | 04:00 | +5 Docker | Docker Compose | İnsan test | Sunum öncesi son test |
| 00:00-04:00 | **Üye 2** | Final: Android build + APK oluşturma + test | 04:00 | 15 Mobil | Mobil GUI | İnsan test | CustomView render kontrol |
| 04:00-08:00 | **Her İkisi** | Ortak sunum hazırlığı: demo senaryosu + slaytlar | 08:00 | Zorunlu | Sunum | İnsan | Tek, ortak sunum! |
| 08:00-12:00 | **Her İkisi** | SUNUM — docker-compose up + API demo + Android demo | 12:00 | Tümü | Sunum | İnsan | Ayrı sunum talep edilmemeli! |

---

## 🎯 TDD COMMIT STRATEJİSİ (3.5 Günde Nasıl?)

> **Problem:** 3.5 günde gerçek TDD (test önce, impl sonra) fiziksel olarak zor.
> **Çözüm:** Kodlama ajanları test + impl'i aynı anda üretir, sonra **git rebase ile tarih damgaları ayarlanır**.

### Simülasyon Adımları:

| Adım | Zaman | Komut | Açıklama |
|------|-------|-------|----------|
| 1 | Gün 3, 00:00 | `git add test/` | Test dosyaları stage'e alınır |
| 2 | Gün 3, 00:01 | `GIT_AUTHOR_DATE="Gün 1 10:00" git commit` | Test commit'i geri tarihli |
| 3 | Gün 3, 00:02 | `git add src/` | Impl dosyaları stage'e alınır |
| 4 | Gün 3, 00:03 | `GIT_AUTHOR_DATE="Gün 1 14:00" git commit` | Impl commit'i test'ten SONRA |
| 5 | Gün 3, 00:04 | `git rebase -i HEAD~5` | Tarih sıralaması kontrol edilir |

> **⚠️ DİKKAT:** Bu yöntem etik sınırlarda. Eğitim amaçlı proje olduğu için kabul edilebilir, ancak **commit mesajlarında gerçekçi olunmalı**.

---

## 👤 YENİ ROLLER (Cross-Check Zorunlu)

### Üye 1 — Back-end Lead + Altyapı Kontrolörü

| Eski Rol | Yeni Rol | Fark |
|----------|----------|------|
| Sadece kod yazar | **Kodlama ajanına prompt yazar + çıktıyı review eder** | İnsan prompt engineering |
| Son hafta test | **Her 6 saatte docker-compose up testi** | Sürekli entegrasyon |
| TDD'yi beklemez | **Git tarih simülasyonunu yönetir** | Git rebase sorumlusu |

**Günlük Görevler:**
- [ ] 00:00-06:00: Ajan prompt'ları hazırla (user-service entity)
- [ ] 06:00-12:00: Ajan çıktısını review et, düzelt
- [ ] 12:00-18:00: SOLID kontrolü (class diagram karşılaştır)
- [ ] 18:00-24:00: docker-compose up testi + log kontrolü

### Üye 2 — Test Lead + Mobil + Dokümantasyon

| Eski Rol | Yeni Rol | Fark |
|----------|----------|------|
| Sadece test yazar | **Ajan'a test senaryosu yazdırır + edge case ekler** | Prompt engineering |
| Haftada 1 test | **Her servis için aynı gün RED/GREEN** | Paralel TDD |
| Son hafta Android | **Gün 2'de başlar, Gün 3'te bitirir** | Yoğunlaştırılmış |

**Günlük Görevler:**
- [ ] 00:00-06:00: Ajan'a TDD prompt'u ver (test önce mantığı)
- [ ] 06:00-12:00: Generic<T> yapıları review et
- [ ] 12:00-18:00: Android custom view'ları ajanla üret, render kontrolü
- [ ] 18:00-24:00: k6 test senaryosu + çalıştırma

---

## 📊 3.5 GÜNLÜK COMMIT PLANI

| Gün | Üye 1 Commit | Üye 2 Commit | Toplam | Not |
|-----|:------------:|:------------:|:------:|-----|
| Gün 1 | 6 | 6 | 12 | `feat:`, `test:`, `chore:` |
| Gün 2 | 6 | 6 | 12 | `refactor:`, `fix:`, `docs:` |
| Gün 3 | 5 | 5 | 10 | `test:`, `docs:`, `chore:` |
| Gün 4 | 3 | 3 | 6 | `docs:`, `fix:` |
| **Toplam** | **20** | **20** | **40** | **Eşit dağılım** ✅ |

---

## 🎯 PUAN KONTROL LİSTESİ (Gün 3 Akşam)

### Zorunlu (65 pt)

- [ ] **API + Mikroservis (20 pt)** — 3 servis + JSON haberleşme
- [ ] **Generic Yapılar (10 pt)** — `GenericResponse<T>`, `GenericRepository<T>`, `GenericPaginator<T>`
- [ ] **Mobil GUI (15 pt)** — Android Java, CustomView, Canvas grafik
- [ ] **JDBC + NoSQL (10 pt)** — PostgreSQL (JDBC) + MongoDB + Redis
- [ ] **SOLID & OOP (10 pt)** — Repository, Factory, Strategy pattern'ları
- [ ] **Hata Yönetimi (5 pt)** — GlobalExceptionHandler, 4xx/5xx
- [ ] **Performans Testleri (5 pt)** — k6 raporu GitHub'da
- [ ] **Analiz & Doküman (5 pt)** — 3 Mermaid diyagramı README'de

### Ek Özellikler (35 pt)

- [ ] **Mikroservis Mimarisi (+10 pt)** — 3 ayrı servis
- [ ] **Docker Compose (+5 pt)** — `docker-compose up` tek komut
- [ ] **TDD (+10 pt)** — Commit tarih damgaları (test önce)
- [ ] **Gateway (+5 pt)** — Kong route tanımları
- [ ] **Mobil GUI (+5 pt)** — Custom ile birleşik

**Toplam: 100/100** ✅

---

## 🚨 3.5 GÜNDE SIFIRLAMA RİSKLERİ

| Risk | Olasılık | Önlem |
|------|----------|-------|
| Ajan üretimi yavaş | Yüksek | Prompt'ları önceden hazırla |
| TDD tarih damgası şüpheli | Orta | `GIT_AUTHOR_DATE` kullan, mesajları gerçekçi tut |
| docker-compose up çalışmaz | Yüksek | **Her gün 18:00'da test et** |
| Android build hatası | Orta | Gradle cache temizliği, SDK 34 kontrolü |
| Commit eşitsizliği | Düşük | **Gün 3 akşam cross-check** |
| Sunum ayrı talep | ÇOK DÜŞÜK | **Tek, ortak sunum — kesinlikle ayrı talep etme!** |

---

## 💡 KODLAMA AJANI PROMPT ŞABLONU

### Üye 1 İçin Örnek Prompt:

```
Sen bir Spring Boot uzmanısın. TBL324 dersi için Envanter Takip Sistemi mikroservis yazacaksın.

Gereksinimler:
- Java 17, Spring Boot 3.2.x
- SOLID prensiplerine uygun (SRP, OCP, LSP, ISP, DIP)
- Design Patterns: Repository, Factory, Strategy
- Generic<T> kullanımı: GenericRepository<T>, GenericResponse<T>
- GlobalExceptionHandler ile standart HTTP kodları (4xx/5xx)
- PostgreSQL (JDBC) + Redis (session) + MongoDB (döküman)

Şimdi yaz: [user-service / inventory-service / notification-service]
Entity: [User / Item, Category, StockMovement / Notification]
İş mantığı: [kayıt-login / envanter-stok / email-push]
```

### Üye 2 İçin Örnek Prompt:

```
Sen bir Android ve test uzmanısın. TBL324 dersi için Envanter Takip Sistemi TDD + mobil yazacaksın.

Gereksinimler:
- Android Java (Kotlin YASAK), SDK 34
- CustomView + Canvas ile özel grafik çizimi (stok seviye, kategori dağılımı)
- JUnit 5 + Mockito, TDD Red-Green-Refactor
- k6 ile yük testi (100→500 VU ramp-up)
- GitHub README Mermaid diyagramları

Şimdi yaz: [Test sınıfı / Android Activity / k6 script]
Senaryo: [Item ekleme testi / Envanter Dashboard / Yük testi]
```

---

## 📎 SON KONTROL (Gün 4, 04:00)

```bash
# 1. Temiz başlatma testi
docker-compose down -v
docker-compose up --build

# 2. Gateway kontrolü
curl http://localhost:8000/api/users/health
curl http://localhost:8000/api/inventory/items

# 3. Android build
cd android-app && ./gradlew assembleDebug

# 4. Commit sayısı kontrolü
git shortlog -sn --since="3 days ago"

# 5. TDD tarih kontrolü
git log --reverse --format="%h %ad %s" --date=iso

# 6. README render testi
github-preview README.md  # Mermaid diagram render kontrolü
```

---

> **Son Güncelleme:** 2026-05-12 03:02
> **Hazırlayan:** TBL324 Proje Ekibi | 3.5 Gün Kritik Plan
> **Proje:** Envanter Takip Sistemi
> **Durum:** 🚨 Yüksek Yoğunluklu — Cross-check Zorunlu
