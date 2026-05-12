# Git Flow Kuralları ve TDD Yaklaşımı

## Branch İsimlendirme Kuralları
- `feature/<ozellik-adi>`: Yeni eklenecek özellikler için kullanılır. (Örn: `feature/user-login`)
- `bugfix/<hata-adi>`: Geliştirme aşamasındaki hataları çözmek için kullanılır.
- `hotfix/<hata-adi>`: Canlıdaki acil hataları çözmek için kullanılır.

## Commit Mesaj Konvansiyonu
- `feat:` Yeni bir özellik eklendiğinde.
- `fix:` Bir hata düzeltildiğinde.
- `test:` Test eklendiğinde veya güncellendiğinde.
- `refactor:` Kod yeniden düzenlendiğinde (yeni özellik eklemeden).
- `docs:` Dokümantasyon güncellemeleri.
- `chore:` Derleme süreci veya yardımcı araç güncellemeleri.

## TDD Döngüsü (Red-Green-Refactor)
1. **RED**: Önce başarısız olan (kızaran) testi yazın.
2. **GREEN**: Testi geçecek en basit kodu yazın.
3. **REFACTOR**: Kodu temizleyin ve iyileştirin (Given-When-Then yapısına sadık kalarak).

## Test Sınıfı İsimlendirme
Tüm test sınıfları `*Test.java` formatında isimlendirilmelidir.
Örn: `UserServiceTest.java`

## Given-When-Then Yapısı
Testlerin okunaklılığını artırmak için tüm test metodları `Given` (Verilen koşullar), `When` (Gerçekleştirilen eylem), `Then` (Beklenen sonuç) bloklarına ayrılmalıdır.
