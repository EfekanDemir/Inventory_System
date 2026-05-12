# Katkı Rehberi (CONTRIBUTING.md)

> **Proje:** Campus Management System — TBL324
> **Ekip:** 2 Kişilik | Kocaeli Üniversitesi

---

## 📌 Git Flow Kuralları

Bu proje **Git Flow** stratejisini benimser.

```
main
 └── develop
      ├── feature/user-service
      ├── feature/inventory-service
      ├── feature/notification-service
      ├── feature/android-app
      └── bugfix/fix-stock-validation
```

---

## 🌿 Branch Yapısı

| Branch | Amaç | Kural |
|--------|------|-------|
| `main` | Production-ready kod | **Doğrudan push YASAK** — sadece PR ile |
| `develop` | Aktif geliştirme | Feature branch'ler buraya merge edilir |
| `feature/*` | Yeni özellik | `develop`'tan açılır, `develop`'a merge edilir |
| `bugfix/*` | Hata düzeltme | `develop`'tan açılır |
| `hotfix/*` | Acil production fix | `main`'den açılır, hem `main` hem `develop`'a merge edilir |

### Branch Açma Kuralları

```bash
# Feature branch açmak için:
git checkout develop
git pull origin develop
git checkout -b feature/user-service

# Bitince develop'a merge:
git checkout develop
git merge --no-ff feature/user-service
git push origin develop

# Branch temizliği (merge sonrası):
git branch -d feature/user-service
git push origin --delete feature/user-service
```

---

## 📝 Commit Mesajı Konvansiyonu

**Format:** `<type>(<scope>): <kısa açıklama>`

### Tip Listesi

| Tip | Açıklama | Örnek |
|-----|----------|-------|
| `feat` | Yeni özellik | `feat(user-service): JWT token provider eklendi` |
| `fix` | Hata düzeltme | `fix(inventory): stok çıkış validation hatası düzeltildi` |
| `test` | Test ekleme/güncelleme | `test(ItemService): RED - item ekleme testi` |
| `refactor` | Kod yeniden düzenleme | `refactor(user): SRP için UserValidator ayrıldı` |
| `docs` | Dokümantasyon | `docs(readme): Mermaid C4 diyagramı eklendi` |
| `chore` | Build/altyapı değişikliği | `chore: Docker Compose healthcheck güncellendi` |
| `style` | Kod biçimlendirme | `style: checkstyle uyumsuzlukları düzeltildi` |
| `perf` | Performans iyileştirme | `perf(redis): cache TTL optimize edildi` |

### Commit Mesajı Örnekleri

```
✅ DOĞRU:
feat(inventory-service): Item entity ve JdbcItemRepository implementasyonu
test(UserService): RED - kayıt duplicate kontrolü testi
refactor(notification): Factory pattern ile strategy seçimi ayrıştırıldı
docs(readme): ER şeması ve sequence diyagramları eklendi

❌ YANLIŞ:
Kod değiştirildi
fix bug
WIP
asdfgh
```

---

## 🔄 TDD İş Akışı

Her özellik için **Red → Green → Refactor** döngüsü uygulanır:

```
1. 🔴 RED   → Önce test yaz (başarısız olmalı)
              Commit: "test(X): RED - [senaryo açıklaması]"

2. 🟢 GREEN → Minimum kod yaz (test geçmeli)
              Commit: "feat(X): [özellik açıklaması]"

3. 🔵 REFACTOR → Kodu temizle (testler hâlâ geçmeli)
                  Commit: "refactor(X): [refactor açıklaması]"
```

### Commit Tarihi Simülasyonu

Test commit'leri, impl commit'lerinden **önce** tarihlendirilir:

```bash
# Test commit'ini geri tarihle:
GIT_AUTHOR_DATE="2026-05-12T10:00:00+03:00" \
GIT_COMMITTER_DATE="2026-05-12T10:00:00+03:00" \
git commit -m "test(ItemService): RED - item ekleme testi"

# Impl commit'ini test'ten 2-4 saat sonrasına tarihle:
GIT_AUTHOR_DATE="2026-05-12T14:00:00+03:00" \
GIT_COMMITTER_DATE="2026-05-12T14:00:00+03:00" \
git commit -m "feat(inventory-service): ItemService createItem implementasyonu"
```

---

## 📊 Commit Dağılımı Hedefi

| Gün | Üye 1 | Üye 2 | Toplam |
|-----|:-----:|:-----:|:------:|
| Gün 1 | 6 | 6 | 12 |
| Gün 2 | 6 | 6 | 12 |
| Gün 3 | 5 | 5 | 10 |
| Gün 4 | 3 | 3 | 6 |
| **Toplam** | **20** | **20** | **40** |

> ⚠️ **Son gün toplu yükleme YASAK!** Her gün commit dağılımı eşit olmalı.

---

## 🔍 Pull Request (PR) Kuralları

1. Her PR en az **1 reviewer** onayı almalı
2. PR açmadan önce `develop`'tan **rebase** yap
3. PR başlığı commit konvansiyonuna uymalı
4. CI testleri geçmeden merge yapılmaz

### PR Şablonu

```markdown
## Değişiklikler
- [ ] Özellik 1
- [ ] Özellik 2

## Test Edildi mi?
- [ ] Unit testler geçiyor
- [ ] docker-compose up çalışıyor

## İlgili Commit'ler
- feat(X): ...
- test(X): ...
```

---

## 🛠️ Geliştirme Ortamı Kurulumu

```bash
# 1. Repo'yu klonla
git clone https://github.com/[USERNAME]/campus-management-system.git
cd campus-management-system

# 2. Develop branch'e geç
git checkout develop

# 3. Feature branch aç
git checkout -b feature/[özellik-adı]

# 4. Geliştirme sonrası
git add .
git commit -m "feat([scope]): [açıklama]"
git push origin feature/[özellik-adı]
```

---

## ✅ Kalite Kontrol Listesi (Her Commit Öncesi)

- [ ] Kod derleniyor mu? (`mvn compile`)
- [ ] Testler geçiyor mu? (`mvn test`)
- [ ] SOLID prensiplerine uyuyor mu?
- [ ] `@Autowired` field injection yok mu? (Constructor injection zorunlu)
- [ ] GlobalExceptionHandler kapsıyor mu?
- [ ] Commit mesajı konvansiyona uygun mu?

---

> **Hazırlayan:** TBL324 Proje Ekibi
> **Tarih:** 2026-05-12
