# TBL324 — Commit Başına Promptlar (3.5 Gün)
## Her Commit İçin Hazır Kodlama Ajanı Promptları
## 🏭 Proje: Envanter Takip Sistemi

> **Toplam:** 40 Commit | Üye 1: 20 | Üye 2: 20
> **Hedef:** 100/100 | TDD tarih damgaları + eşit commit dağılımı

---

# 📂 ÜYE 1 — Back-end & Altyapı (20 Commit)

---

## GÜN 1 — 6 Commit

### Commit 1: `chore: Proje repo kurulumu ve Git yapılandırması`

```
Sen bir DevOps uzmanısın. TBL324 dersi için Envanter Takip Sistemi GitHub repo kurulumu yapacaksın.

GÖREV:
- GitHub repo: envanter-takip-sistemi
- Branch yapısı: main (korunaklı), develop, feature/*
- .gitignore: Java, Maven, IDE, Docker
- README.md şablonu (boş, sonra doldurulacak)
- Git flow kuralları belgesi (CONTRIBUTING.md)

KURALLAR:
- Commit mesajı: "chore: Proje repo kurulumu ve Git yapılandırması"
- Java 17, Maven 3.9.x
- Hiçbir kod dosyası şu an eklenmeyecek, sadece altyapı
```

### Commit 2: `chore: Docker Compose iskelet (PostgreSQL + MongoDB + Redis)`

```
Sen bir Docker uzmanısın. TBL324 Envanter Takip Sistemi için Docker Compose iskeleti yazacaksın.

GÖREV:
- docker-compose.yml: PostgreSQL 15, MongoDB 6, Redis 7
- Healthcheck'ler: pg_isready, mongosh ping, redis-cli ping
- Volume'lar: postgres_data, mongodb_data, redis_data
- Network: envanter-network (bridge)
- init-scripts/01-init.sql: envanter_db oluşturma
- Container isimleri: envanter-postgres, envanter-mongodb, envanter-redis

KURALLAR:
- Commit mesajı: "chore: Docker Compose iskelet (PostgreSQL + MongoDB + Redis)"
- Container'lar birbirine bağlı olmalı (depends_on + condition)
- Port mapping: PostgreSQL 5432, MongoDB 27017, Redis 6379
```

### Commit 3: `chore: Mikroservis Maven modül yapısı`

```
Sen bir Java mimarısın. TBL324 Envanter Takip Sistemi için mikroservis Maven modül yapısı kuracaksın.

GÖREV:
- user-service/pom.xml (Spring Boot 3.2.x, Java 17)
- inventory-service/pom.xml
- notification-service/pom.xml
- Parent pom.xml (dependency management)
- Her modül için src/main/java, src/test/java, src/main/resources
- application.yml şablonları (boş config)
- Package: com.envanter.[service-name]

KURALLAR:
- Commit mesajı: "chore: Mikroservis Maven modül yapısı"
- Spring Boot 3.2.x, Spring Data JDBC, Spring Data MongoDB, Spring Data Redis
- JUnit 5, Mockito dependency'leri
```

### Commit 4: `feat: user-service entity sınıfları (User, Role)`

```
Sen bir Java backend geliştiricisisin. TBL324 user-service için entity sınıfları yazacaksın.

GÖREV:
- User.java: id, username, email, passwordHash, firstName, lastName, role, createdAt, updatedAt, isActive
- Role.java enum: ADMIN, MANAGER, PERSONEL
- Lombok @Data veya manuel getter/setter/constructor
- JPA annotations (@Entity, @Id, @GeneratedValue)

KURALLAR:
- Commit mesajı: "feat: user-service entity sınıfları (User, Role)"
- Sadece entity, repository veya service YOK
- Java 17 record kullanma, class olmalı (JPA uyumluluk)
- Role enum: ADMIN (yönetici), MANAGER (sorumlu), PERSONEL (kullanıcı)
```

### Commit 5: `feat: GenericRepository<T> arayüzü ve JdbcUserRepository implementasyonu`

```
Sen bir Java uzmanısın. TBL324 Envanter Takip Sistemi için Generic Repository Pattern yazacaksın.

GÖREV:
- GenericRepository<T, ID> interface:
  - Optional<T> findById(ID id)
  - List<T> findAll()
  - T save(T entity)
  - void deleteById(ID id)
  - boolean existsById(ID id)
- JdbcUserRepository implements GenericRepository<User, Long>:
  - JdbcTemplate kullanımı
  - RowMapper<User> implementasyonu
  - CRUD operasyonları

KURALLAR:
- Commit mesajı: "feat: GenericRepository<T> arayüzü ve JdbcUserRepository implementasyonu"
- SOLID: Interface Segregation + Dependency Inversion
- Generic<T> kullanımı zorunlu
- SQL injection'a karşı PreparedStatement (JdbcTemplate otomatik)
```

### Commit 6: `feat: user-service JWT token provider ve Redis session yapılandırması`

```
Sen bir Spring Security uzmanısın. TBL324 user-service için JWT + Redis session yazacaksın.

GÖREV:
- JwtTokenProvider.java:
  - generateToken(User user): JWT oluşturma
  - validateToken(String token): doğrulama
  - getUserIdFromToken(String token): userId çıkarma
  - Secret key, expiration (24 saat)
- RedisConfig.java: StringRedisTemplate bean
- RedisSessionService.java:
  - createSession(String token, Long userId, Duration expiry)
  - Optional<SessionData> getSession(String token)
  - deleteSession(String token)

KURALLAR:
- Commit mesajı: "feat: user-service JWT token provider ve Redis session yapılandırması"
- jjwt kütüphanesi (0.12.x)
- Redis TTL: 30 dakika (session)
- Standart HTTP kodları: 401 Unauthorized, 403 Forbidden
```

---

## GÜN 2 — 6 Commit

### Commit 7: `feat: GlobalExceptionHandler ve standart HTTP hata yanıtları`

```
Sen bir Java hata yönetimi uzmanısın. TBL324 Envanter Takip Sistemi için GlobalExceptionHandler yazacaksın.

GÖREV:
- GlobalExceptionHandler.java (@ControllerAdvice):
  - handleResourceNotFoundException → 404 Not Found
  - handleValidationException → 400 Bad Request
  - handleUnauthorizedException → 401 Unauthorized
  - handleForbiddenException → 403 Forbidden
  - handleConflictException → 409 Conflict (Insufficient Stock)
  - handleRuntimeException → 500 Internal Server Error
- ApiErrorResponse.java:
  - int status, String error, String message, LocalDateTime timestamp, String path
  - Map<String, String> validationErrors (opsiyonel)

KURALLAR:
- Commit mesajı: "feat: GlobalExceptionHandler ve standart HTTP hata yanıtları"
- TÜM 4xx/5xx kodları standart olmalı
- JSON response formatı tutarlı
- Stack trace log'a yazılır, response'ta gönderilmez
```

### Commit 8: `feat: inventory-service entity (Item, Category, StockMovement) ve GenericRepository`

```
Sen bir Java backend geliştiricisisin. TBL324 inventory-service için entity + repository yazacaksın.

GÖREV:
- Category.java: id, name, description, createdAt
- Item.java: id, itemCode, name, description, categoryId, quantity, minStockLevel, location, status, unitPrice, createdAt, updatedAt
- StockMovement.java: id, itemId, movementType (IN/OUT), quantity, reason, userId, movementDate, referenceNumber
- JdbcItemRepository implements GenericRepository<Item, Long>
- JdbcStockMovementRepository implements GenericRepository<StockMovement, Long>
- MongoStockLogRepository (MongoDB, stok hareket logları)

KURALLAR:
- Commit mesajı: "feat: inventory-service entity (Item, Category, StockMovement) ve GenericRepository"
- Generic<T> pattern devam etmeli
- MongoDB için @Document annotation
- ItemStatus enum: ACTIVE, INACTIVE, DISCONTINUED
- MovementType enum: IN (giriş), OUT (çıkış)
```

### Commit 9: `feat: inventory-service ItemService ve StockValidationStrategy pattern`

```
Sen bir Java design pattern uzmanısın. TBL324 inventory-service için Strategy pattern yazacaksın.

GÖREV:
- StockValidationStrategy.java (interface):
  - boolean validateMovement(Item item, StockMovementRequest request)
- QuantityCheckStrategy implements StockValidationStrategy:
  - OUT hareketinde yeterli stok var mı?
- MinStockCheckStrategy implements StockValidationStrategy:
  - Hareket sonrası min stock altına düşüyor mu?
- CompositeStockValidationStrategy (List<StockValidationStrategy> strategies)
- ItemService.java:
  - List<ItemDTO> getAllItems()
  - ItemDTO getItemById(Long id)
  - ItemDTO createItem(ItemRequest request)
  - ItemDTO updateItem(Long id, ItemRequest request)
  - void deleteItem(Long id)
- StockMovementService.java:
  - StockMovementDTO createMovement(StockMovementRequest request)
  - List<StockMovementDTO> getMovementsByItem(Long itemId)

KURALLAR:
- Commit mesajı: "feat: inventory-service ItemService ve StockValidationStrategy pattern"
- SOLID: Open/Closed Principle (yeni strategy eklemek kolay)
- Factory pattern ile strategy oluşturma
- Stok yetersizse 409 Conflict
- Min stock altına düşerse notification trigger
```

### Commit 10: `feat: notification-service entity ve MongoDB repository`

```
Sen bir Java + MongoDB uzmanısın. TBL324 notification-service için doküman bazlı repository yazacaksın.

GÖREV:
- NotificationLog.java (@Document):
  - ObjectId id, String recipientEmail, String notificationType (LOW_STOCK, INVENTORY_ADDED, STOCK_MOVEMENT), String subject, String content, String status, Date sentAt, Map<String, Object> metadata
- ActivityLog.java (@Document):
  - ObjectId id, String serviceName, String action, String userId, Map<String, Object> requestData, Map<String, Object> responseData, Date timestamp, Long executionTimeMs
- LowStockAlert.java (@Document):
  - ObjectId id, String itemId, String itemName, int currentStock, int minStockLevel, String alertStatus (ACTIVE, RESOLVED), Date createdAt, Date resolvedAt
- MongoNotificationLogRepository:
  - NotificationLog save(NotificationLog log)
  - List<NotificationLog> findByType(String type)
  - List<NotificationLog> findByStatus(String status)

KURALLAR:
- Commit mesajı: "feat: notification-service entity ve MongoDB repository"
- MongoTemplate kullanımı
- NoSQL: doküman bazlı, şemasız yapı
- İndeks: notificationType, status, sentAt
```

### Commit 11: `feat: notification-service NotificationFactory ve Strategy pattern`

```
Sen bir Java design pattern uzmanısın. TBL324 notification-service için Factory + Strategy yazacaksın.

GÖREV:
- NotificationStrategy.java (interface):
  - void send(NotificationRequest request)
- EmailSenderStrategy implements NotificationStrategy:
  - JavaMailSender kullanımı (mock, gerçek SMTP gerekmez)
- PushNotificationStrategy implements NotificationStrategy:
  - Loglama (gerçek push gerekmez)
- NotificationFactory.java:
  - NotificationStrategy createStrategy(String type)
  - "EMAIL" → EmailSenderStrategy
  - "PUSH" → PushNotificationStrategy
- NotificationService.java:
  - void sendNotification(NotificationRequest request)
  - List<NotificationLog> getLogs()
  - void sendLowStockAlert(String itemId, int currentStock, int minStockLevel)

KURALLAR:
- Commit mesajı: "feat: notification-service NotificationFactory ve Strategy pattern"
- SOLID: Single Responsibility (her strategy bir görev)
- Factory pattern ile runtime strategy seçimi
- Log MongoDB'ye kaydedilmeli
- Low stock alert otomatik trigger
```

### Commit 12: `feat: Kong Gateway declarative config ve Docker entegrasyonu`

```
Sen bir API Gateway uzmanısın. TBL324 Envanter Takip Sistemi için Kong Gateway yapılandırması yazacaksın.

GÖREV:
- kong-config/kong.yml (declarative):
  - services: user-service (url: http://user-service:8081), inventory-service (url: http://inventory-service:8082), notification-service (url: http://notification-service:8083)
  - routes: /api/users → user-service, /api/inventory → inventory-service, /api/notifications → notification-service
  - plugins: rate-limiting (60 req/min local), key-auth (api-key header)
  - consumers: envanter-client, keyauth_credentials: envanter-api-key-2026
- docker-compose.yml güncelleme:
  - Kong service ekleme
  - Port: 8000 (proxy), 8001 (admin)
  - Volumes: ./kong-config:/kong/declarative

KURALLAR:
- Commit mesajı: "feat: Kong Gateway declarative config ve Docker entegrasyonu"
- Kong 3.5-alpine image
- KONG_DATABASE: "off" (DB-less mode)
- Tüm trafik gateway üzerinden geçmeli
```

---

## GÜN 3 — 5 Commit

### Commit 13: `refactor: SOLID prensipleri review - Single Responsibility uygulama`

```
Sen bir SOLID prensipleri uzmanısın. TBL324 Envanter Takip Sistemi kodunu Single Responsibility Principle'a göre refactor edeceksin.

GÖREV:
- UserService.java refactor:
  - Validation logic → UserValidator.java (ayrı sınıf)
  - Mapping logic → UserMapper.java (ayrı sınıf)
  - Token logic → JwtTokenProvider.java (zaten ayrı, kontrol)
- ItemService.java refactor:
  - Stock movement logic → StockMovementService.java (ayrı sınıf)
  - Notification trigger → NotificationClient.java (ayrı sınıf)
  - Item mapping → ItemMapper.java (ayrı sınıf)
- Her sınıfın SADECE BİR değişim nedeni olmalı

KURALLAR:
- Commit mesajı: "refactor: SOLID prensipleri review - Single Responsibility uygulama"
- Mevcut testler hala geçmeli (regression)
- Interface'ler değişmemeli
- Kod kalitesi artmalı, davranış değişmemeli
```

### Commit 14: `refactor: Dependency Inversion - Constructor Injection tamamlama`

```
Sen bir Dependency Injection uzmanısın. TBL324 Envanter Takip Sistemi kodunu DI prensiplerine göre refactor edeceksin.

GÖREV:
- Tüm Service sınıfları:
  - @RequiredArgsConstructor veya @AllArgsConstructor
  - Field injection (@Autowired) YASAK → Constructor injection ZORUNLU
- Tüm Controller sınıfları:
  - Constructor injection ile service bağımlılıkları
- Tüm Repository sınıfları:
  - JdbcTemplate, MongoTemplate, RedisTemplate constructor injection

KURALLAR:
- Commit mesajı: "refactor: Dependency Inversion - Constructor Injection tamamlama"
- @Autowired field injection KALDIRILMALI
- Test edilebilirlik artmalı (mock kolaylığı)
- Spring @Component, @Service, @Repository annotation'ları kontrol
```

### Commit 15: `feat: servisler arası JSON mesajlaşma (REST client)`

```
Sen bir mikroservis iletişim uzmanısın. TBL324 Envanter Takip Sistemi servisleri arası REST client yazacaksın.

GÖREV:
- NotificationClient.java (inventory-service içinde):
  - void sendLowStockAlert(String itemId, int currentStock, int minStockLevel)
  - void sendInventoryAddedNotification(Long itemId, String itemName)
  - RestTemplate veya WebClient kullanımı
  - JSON request/response
- UserClient.java (opsiyonel):
  - UserDTO getUserById(Long userId)
- RestTemplateConfig.java:
  - @Bean RestTemplate restTemplate()
  - Timeout ayarları (connect: 5s, read: 10s)

KURALLAR:
- Commit mesajı: "feat: servisler arası JSON mesajlaşma (REST client)"
- JSON format: application/json
- Hata durumunda fallback (try-catch + log)
- Async opsiyonel (@Async)
```

### Commit 16: `feat: docker-compose up son konfigürasyon ve healthcheck`

```
Sen bir Docker uzmanısın. TBL324 Envanter Takip Sistemi docker-compose.yml final versiyonunu yazacaksın.

GÖREV:
- Tüm servisler: user-service, inventory-service, notification-service
- Her servis için:
  - build: ./service-name
  - environment: DB bağlantıları, Redis, MongoDB URI
  - depends_on: condition: service_healthy
  - ports: 8081, 8082, 8083
- Healthcheck'ler:
  - user-service: curl -f http://localhost:8081/actuator/health
  - inventory-service: curl -f http://localhost:8082/actuator/health
  - notification-service: curl -f http://localhost:8083/actuator/health
- Son kontrol: docker-compose up --build tek komut çalışmalı

KURALLAR:
- Commit mesajı: "feat: docker-compose up son konfigürasyon ve healthcheck"
- docker-compose down -v && docker-compose up --build test edilmeli
- Tüm servisler aynı network'te (envanter-network)
- Loglar stdout'a yazılmalı
```

### Commit 17: `docs: README sistem mimarisi Mermaid C4 Container diyagramı`

```
Sen bir teknik yazarsın. TBL324 Envanter Takip Sistemi README için Mermaid C4 Container diyagramı yazacaksın.

GÖREV:
- README.md bölüm: ## 2. Sistem Mimarisi
- C4Container Mermaid diagram:
  - Person: Personel, Yönetici
  - Container: Android App, Kong Gateway
  - Container: user-service, inventory-service, notification-service
  - ContainerDb: PostgreSQL, MongoDB, Redis
  - Rel: Tüm bağlantılar (HTTPS/JSON, HTTP/JSON, JDBC, Redis Protocol, MongoDB Protocol)
- Açıklama metni: Her bileşenin görevi 2-3 cümle
  - inventory-service: Envanter yönetimi, stok takibi
  - notification-service: Düşük stok bildirim, raporlama

KURALLAR:
- Commit mesajı: "docs: README sistem mimarisi Mermaid C4 Container diyagramı"
- Mermaid C4Container syntax (GitHub destekler)
- Renk kodlama: Mobil (sarı), Gateway (pembe), Servis (mavi), DB (yeşil)
- Türkçe veya İngilizce (proje diline uygun)
```

---

## GÜN 4 — 3 Commit

### Commit 18: `docs: README veritabanı ER şeması ve API akış Mermaid diyagramları`

```
Sen bir teknik yazarsın. TBL324 Envanter Takip Sistemi README için Mermaid ER ve Sequence diyagramları yazacaksın.

GÖREV:
- Bölüm 3: Veritabanı Şeması
  - ER Diagram (PostgreSQL): USERS, CATEGORIES, ITEMS, STOCK_MOVEMENTS
  - MongoDB Şeması: NOTIFICATION_LOGS, ACTIVITY_LOGS, LOW_STOCK_ALERTS
  - Redis Key-Value yapısı
- Bölüm 4: API Akış Diyagramı
  - Sequence Diagram: Envanter Ekleme Akışı (8 adım)
  - Sequence Diagram: Stok Hareketi (Giriş/Çıkış) Akışı (12 adım)
  - Sequence Diagram: Hata Yönetimi Akışı

KURALLAR:
- Commit mesajı: "docs: README veritabanı ER şeması ve API akış Mermaid diyagramları"
- Mermaid erDiagram, sequenceDiagram syntax
- Entity attribute'ları detaylı
- Sequence autonumber kullanımı
- Envanter spesifik: itemCode, quantity, minStockLevel, movementType
```

### Commit 19: `fix: docker-compose up son test hataları ve düzeltmeler`

```
Sen bir hata ayıklama uzmanısın. TBL324 Envanter Takip Sistemi docker-compose up son testinde bulunan hataları düzelteceksin.

GÖREV:
- docker-compose up --build çalıştır
- Hataları tespit et ve düzelt:
  - DB bağlantı hatası → connection string kontrol (envanter_db)
  - Port çakışması → port mapping düzeltme
  - Healthcheck timeout → interval/timeout ayarlama
  - Servis başlatma sırası → depends_on condition
  - Kong route hatası → kong.yml kontrol (/api/inventory)
- Her düzeltme için ayrı fix commit (opsiyonel, tek commit'te birleştirilebilir)

KURALLAR:
- Commit mesajı: "fix: docker-compose up son test hataları ve düzeltmeler"
- Tüm hatalar loglanmalı
- Çözüm açıklaması commit mesajında veya PR açıklamasında
- Son kontrol: temiz başlatma başarılı
```

### Commit 20: `chore: Son commit düzenlemesi ve GitHub repo finalizasyonu`

```
Sen bir Git uzmanısın. TBL324 Envanter Takip Sistemi GitHub repo'sunu son kontrolden geçireceksin.

GÖREV:
- Commit geçmişi kontrolü:
  - Üye 1: 20 commit
  - Üye 2: 20 commit
  - Toplam: 40 commit
  - Her gün en az 4 commit
- Commit mesaj konvansiyonu:
  - feat:, fix:, test:, refactor:, docs:, chore: önekleri
  - Açıklayıcı mesajlar
- Branch temizliği:
  - feature/* branch'leri merge sonrası silme
  - Sadece main ve develop kalacak
- Son push: git push origin main

KURALLAR:
- Commit mesajı: "chore: Son commit düzenlemesi ve GitHub repo finalizasyonu"
- Son gün toplu yükleme YASAK (günlük commit dağılımı kontrol)
- README.md tam ve güncel
- .gitignore düzgün çalışıyor
```

---

# 📂 ÜYE 2 — Front-end, Test & Dokümantasyon (20 Commit)

---

## GÜN 1 — 6 Commit

### Commit 1: `chore: Git flow kuralları ve TDD altyapısı (JUnit 5, Mockito)`

```
Sen bir test uzmanısın. TBL324 Envanter Takip Sistemi için TDD altyapısı kuracaksın.

GÖREV:
- Git flow kuralları belgesi:
  - Branch naming: feature/, bugfix/, hotfix/
  - Commit mesaj konvansiyonu
  - PR şablonu
- TDD altyapısı:
  - JUnit 5 (5.10.x) dependency
  - Mockito (5.x) dependency
  - AssertJ (opsiyonel)
- Test paketi yapısı:
  - src/test/java/com/envanter/...
  - unit/, integration/, controller/ paketleri

KURALLAR:
- Commit mesajı: "chore: Git flow kuralları ve TDD altyapısı (JUnit 5, Mockito)"
- TDD döngüsü: RED → GREEN → REFACTOR
- Test sınıf isimlendirme: *Test.java
- Given-When-Then yapısı
```

### Commit 2: `test: UserService kayıt testi - başarısız senaryo (RED)`

```
Sen bir TDD uzmanısın. TBL324 Envanter Takip Sistemi için UserService kayıt testini yazacaksın.

GÖREV:
- UserServiceTest.java:
  - void register_WithValidRequest_ReturnsUserDTO()
    - Given: RegisterRequest (valid username, email, password)
    - When: userService.register(request)
    - Then: assertNotNull(result), assertEquals(username, result.getUsername())
  - void register_WithDuplicateUsername_ThrowsConflictException()
    - Given: Existing username
    - When: userService.register(request)
    - Then: assertThrows(ConflictException.class, ...)
- Test BAŞARISIZ olmalı (RED) çünkü implementasyon henüz yok

KURALLAR:
- Commit mesajı: "test: UserService kayıt testi - başarısız senaryo (RED)"
- @ExtendWith(MockitoExtension.class)
- @Mock: UserRepository, PasswordEncoder, JwtTokenProvider, RedisSessionService
- @InjectMocks: UserServiceImpl
- Test BAŞARISIZ olmalı (ama compile edebilmeli)
```

### Commit 3: `test: UserService login testi - başarısız senaryo (RED)`

```
Sen bir TDD uzmanısın. TBL324 Envanter Takip Sistemi için UserService login testini yazacaksın.

GÖREV:
- UserServiceTest.java (devam):
  - void login_WithValidCredentials_ReturnsToken()
    - Given: LoginRequest (valid username, password)
    - When: userService.login(request)
    - Then: assertNotNull(result), assertNotNull(result.getToken())
  - void login_WithInvalidPassword_ThrowsUnauthorizedException()
    - Given: Valid username, wrong password
    - When: userService.login(request)
    - Then: assertThrows(UnauthorizedException.class, ...)
  - void login_WithNonExistentUser_ThrowsNotFoundException()
    - Given: Non-existent username
    - When: userService.login(request)
    - Then: assertThrows(ResourceNotFoundException.class, ...)

KURALLAR:
- Commit mesajı: "test: UserService login testi - başarısız senaryo (RED)"
- Mockito.when(...).thenReturn(...) kullanımı
- Verify: Mockito.verify(repository).findByUsername(...)
- Test BAŞARISIZ olmalı (RED)
```

### Commit 4: `feat: GenericResponseWrapper<T>, GenericPaginator<T>, GenericValidator<T>`

```
Sen bir Java generic uzmanısın. TBL324 Envanter Takip Sistemi için Generic<T> yardımcı sınıflar yazacaksın.

GÖREV:
- GenericResponseWrapper<T>:
  - T data, String message, LocalDateTime timestamp, boolean success
  - static <T> GenericResponseWrapper<T> success(T data)
  - static <T> GenericResponseWrapper<T> error(String message)
- GenericPaginator<T>:
  - List<T> content, int page, int size, long totalElements, int totalPages
  - boolean hasNext(), boolean hasPrevious()
- GenericValidator<T>:
  - ValidationResult validate(T object)
  - List<String> errors
  - boolean isValid()

KURALLAR:
- Commit mesajı: "feat: GenericResponseWrapper<T>, GenericPaginator<T>, GenericValidator<T>"
- Generic<T> kullanımı zorunlu
- Immutable (final field'lar, constructor injection)
- Builder pattern opsiyonel
```

### Commit 5: `test: InventoryService item ekleme ve stok hareketi testi (RED)`

```
Sen bir TDD uzmanısın. TBL324 Envanter Takip Sistemi için InventoryService testlerini yazacaksın.

GÖREV:
- ItemServiceTest.java:
  - void createItem_WithValidRequest_ReturnsItemDTO()
    - Given: ItemRequest (valid itemCode, name, categoryId, quantity)
    - When: itemService.createItem(request)
    - Then: assertNotNull(result), assertEquals(itemCode, result.getItemCode())
  - void createItem_WithDuplicateItemCode_ThrowsConflictException()
    - Given: Existing itemCode
    - When: itemService.createItem(request)
    - Then: assertThrows(ConflictException.class, ...)
- StockMovementServiceTest.java:
  - void createMovement_WithValidInRequest_ReturnsMovementDTO()
    - Given: StockMovementRequest (type: IN, quantity: 50)
    - When: stockMovementService.createMovement(request)
    - Then: assertNotNull(result), assertEquals("IN", result.getMovementType())
  - void createMovement_WithInsufficientStock_ThrowsConflictException()
    - Given: StockMovementRequest (type: OUT, quantity: 1000)
    - When: stockMovementService.createMovement(request)
    - Then: assertThrows(ConflictException.class, ...)

KURALLAR:
- Commit mesajı: "test: InventoryService item ekleme ve stok hareketi testi (RED)"
- @Mock: ItemRepository, StockMovementRepository, StockValidationStrategy
- @Mock: NotificationClient (opsiyonel)
- Test BAŞARISIZ olmalı (RED)
```

### Commit 6: `test: NotificationService bildirim gönderim testi (RED)`

```
Sen bir TDD uzmanısın. TBL324 Envanter Takip Sistemi için NotificationService testini yazacaksın.

GÖREV:
- NotificationServiceTest.java:
  - void sendNotification_WithLowStockType_SendsEmailAndLogs()
    - Given: NotificationRequest (type: "LOW_STOCK", recipient: "manager@envanter.com")
    - When: notificationService.sendNotification(request)
    - Then: verify(emailStrategy).send(request), verify(logRepository).save(...)
  - void sendNotification_WithInvalidType_ThrowsValidationException()
    - Given: NotificationRequest (type: "INVALID")
    - When: notificationService.sendNotification(request)
    - Then: assertThrows(ValidationException.class, ...)
  - void getLogs_ReturnsNotificationLogList()
    - Given: 5 logs in MongoDB
    - When: notificationService.getLogs()
    - Then: assertEquals(5, result.size())

KURALLAR:
- Commit mesajı: "test: NotificationService bildirim gönderim testi (RED)"
- @Mock: NotificationLogRepository, NotificationStrategy, NotificationFactory
- @Mock: JavaMailSender (opsiyonel)
- Test BAŞARISIZ olmalı (RED)
```

---

## GÜN 2 — 6 Commit

### Commit 7: `test: InventoryService edge case - yetersiz stok ve null item code (RED)`

```
Sen bir edge case test uzmanısın. TBL324 InventoryService için edge case testleri yazacaksın.

GÖREV:
- ItemServiceTest.java (edge cases):
  - void createItem_WithNullItemCode_ThrowsValidationException()
  - void createItem_WithNegativeQuantity_ThrowsValidationException()
  - void createItem_WithInvalidCategoryId_ThrowsNotFoundException()
  - void updateItem_WithNonExistentId_ThrowsNotFoundException()
- StockMovementServiceTest.java (edge cases):
  - void createMovement_WithNullItemId_ThrowsValidationException()
  - void createMovement_WithZeroQuantity_ThrowsValidationException()
  - void createMovement_WithInvalidMovementType_ThrowsValidationException()
- Her test: Given-When-Then yapısı, assertThrows

KURALLAR:
- Commit mesajı: "test: InventoryService edge case - yetersiz stok ve null item code (RED)"
- @ParameterizedTest opsiyonel (JUnit 5)
- @NullSource, @EmptySource, @ValueSource
- Tüm edge case'ler BAŞARISIZ olmalı (RED)
```

### Commit 8: `feat: Android projesi kurulumu - LoginActivity ve EnvanterListActivity temel ekranlar`

```
Sen bir Android Java uzmanısın. TBL324 Envanter Takip Sistemi için Android projesi kurulumu yapacaksın.

GÖREV:
- Android projesi yapısı:
  - app/src/main/java/com/envanter/mobile/
  - activity/: LoginActivity.java, DashboardActivity.java, EnvanterListActivity.java
  - model/: User.java, Item.java, Category.java (POJO)
  - api/: ApiService.java, RetrofitClient.java
- LoginActivity.java:
  - EditText username, password
  - Button loginButton
  - TextView registerLink
  - Basit layout (activity_login.xml)
- DashboardActivity.java:
  - RecyclerView envanterList
  - TextView welcomeMessage
  - Button logoutButton
  - Stok seviye özet kartları
  - Basit layout (activity_dashboard.xml)
- EnvanterListActivity.java:
  - RecyclerView itemList
  - SearchView arama
  - FloatingActionButton ekleme

KURALLAR:
- Commit mesajı: "feat: Android projesi kurulumu - LoginActivity ve EnvanterListActivity temel ekranlar"
- Java 17, Android SDK 34
- Kotlin YASAK
- Retrofit 2.9.x, OkHttp 4.12.x
- ConstraintLayout kullanımı
```

### Commit 9: `feat: Android CustomView - StockLevelBarChartView (Canvas stok seviye bar grafik)`

```
Sen bir Android Canvas uzmanısın. TBL324 Envanter Takip Sistemi için özel stok seviye bar grafik çizeceksin.

GÖREV:
- StockLevelBarChartView.java (extends View):
  - onDraw(Canvas canvas):
    - drawRect() ile bar çizimi
    - Renk kodlama: >=minStockLevel*1.5 yeşil (#4CAF50), >=minStockLevel turuncu (#FF9800), <minStockLevel kırmızı (#F44336)
    - drawText() ile stok miktarı ve ürün adı etiketleri
    - drawLine() ile eksen çizgileri
    - Min stock seviyesi çizgisi (kırmızı, kesikli)
    - Kritik stok uyarısı (kırmızı çerçeve)
  - setItems(List<ItemStock> items) + invalidate()
- activity_dashboard.xml'e ekleme:
  - <com.envanter.mobile.view.StockLevelBarChartView ... />

KURALLAR:
- Commit mesajı: "feat: Android CustomView - StockLevelBarChartView (Canvas stok seviye bar grafik)"
- Paint.ANTI_ALIAS_FLAG
- onMeasure() override (view boyutlandırma)
- Java 17, extends View
- CustomView paketi: view/
- ItemStock: itemName, quantity, minStockLevel
```

### Commit 10: `feat: Android CustomView - CategoryPieChartView (Canvas kategori dağılım pasta grafik)`

```
Sen bir Android Canvas uzmanısın. TBL324 Envanter Takip Sistemi için özel kategori dağılım pasta grafik çizeceksin.

GÖREV:
- CategoryPieChartView.java (extends View):
  - onDraw(Canvas canvas):
    - drawArc() ile pasta dilimleri
    - Renk paleti: 6+ farklı renk (mavi, yeşil, turuncu, kırmızı, mor, cyan)
    - drawText() ile yüzde etiketleri (beyaz renk, dilim ortası)
    - RectF bounds (çizim alanı)
    - Legend (açıklama) altta: renk kareleri + kategori adları
  - setData(float[] percentages, String[] categoryNames) + invalidate()
- activity_dashboard.xml'e ekleme

KURALLAR:
- Commit mesajı: "feat: Android CustomView - CategoryPieChartView (Canvas kategori dağılım pasta grafik)"
- Paint.Style.FILL
- drawArc(useCenter: true)
- Etiketler okunaklı olmalı (kontrast renk)
- Java 17, extends View
- Legend: 20px text, siyah renk
```

### Commit 11: `feat: Android API bağlantı katmanı (Retrofit + JWT interceptor)`

```
Sen bir Android network uzmanısın. TBL324 Envanter Takip Sistemi için API bağlantı katmanı yazacaksın.

GÖREV:
- RetrofitClient.java:
  - Singleton pattern
  - Base URL: http://localhost:8000 (Kong Gateway)
  - OkHttpClient: connectTimeout(5s), readTimeout(10s)
  - JWT Interceptor: Authorization: Bearer {token} header ekleme
  - LoggingInterceptor: HttpLoggingInterceptor.Level.BODY
- ApiService.java (interface):
  - @POST("/api/users/register") Call<GenericResponse<UserDTO>> register(@Body RegisterRequest)
  - @POST("/api/users/login") Call<GenericResponse<UserDTO>> login(@Body LoginRequest)
  - @GET("/api/inventory/items") Call<GenericResponse<List<ItemDTO>>> getItems(@Header("Authorization") String token)
  - @POST("/api/inventory/items") Call<GenericResponse<ItemDTO>> createItem(@Header("Authorization") String token, @Body ItemRequest)
  - @POST("/api/inventory/movements") Call<GenericResponse<StockMovementDTO>> createMovement(@Header("Authorization") String token, @Body StockMovementRequest)
- JWT token SharedPreferences'te saklama

KURALLAR:
- Commit mesajı: "feat: Android API bağlantı katmanı (Retrofit + JWT interceptor)"
- Retrofit 2.9.x, OkHttp 4.12.x
- Gson converter
- Hata yönetimi: 4xx/5xx kodları handle
- Java 17
```

### Commit 12: `feat: Android Navigation ve hata yönetimi entegrasyonu`

```
Sen bir Android UX uzmanısın. TBL324 Envanter Takip Sistemi için navigation ve hata yönetimi yazacaksın.

GÖREV:
- Navigation:
  - LoginActivity → DashboardActivity (başarılı login)
  - DashboardActivity → EnvanterListActivity (liste görüntüleme)
  - DashboardActivity → LoginActivity (logout)
  - Intent + startActivity()
- Hata yönetimi:
  - ApiErrorHandler.java: 401 → LoginActivity, 404 → Toast, 409 → Toast ("Yetersiz stok"), 500 → Toast
  - Toast mesajları (Türkçe)
  - ProgressBar (loading durumu)
- SharedPreferences:
  - Token saklama
  - Auto-login (token varsa Dashboard'a yönlendirme)
- EnvanterListActivity:
  - RecyclerView adapter
  - SwipeRefreshLayout
  - Item click → detay sayfası (opsiyonel)

KURALLAR:
- Commit mesajı: "feat: Android Navigation ve hata yönetimi entegrasyonu"
- onFailure() → Toast + log
- runOnUiThread() (Retrofit callback UI thread'de)
- Java 17
```

---

## GÜN 3 — 5 Commit

### Commit 13: `test: InventoryService edge case - stok çıkışı yetersiz ve null item (RED)`

```
Sen bir edge case test uzmanısın. TBL324 InventoryService için edge case testleri yazacaksın.

GÖREV:
- StockMovementServiceTest.java (edge cases):
  - void createMovement_WithOutExceedingStock_ThrowsConflictException()
    - Given: Item quantity = 10, OUT request quantity = 50
    - When: createMovement(request)
    - Then: assertThrows(ConflictException.class, ...)
  - void createMovement_WithNullItemId_ThrowsValidationException()
    - Given: StockMovementRequest (itemId: null)
    - When: createMovement(request)
    - Then: assertThrows(ValidationException.class, ...)
  - void createMovement_WithNegativeQuantity_ThrowsValidationException()
    - Given: StockMovementRequest (quantity: -5)
    - When: createMovement(request)
    - Then: assertThrows(ValidationException.class, ...)
  - void createMovement_WithInvalidType_ThrowsValidationException()
    - Given: StockMovementRequest (type: "INVALID")
    - When: createMovement(request)
    - Then: assertThrows(ValidationException.class, ...)
- ItemServiceTest.java (edge cases):
  - void getItemById_WithNonExistentId_ThrowsNotFoundException()

KURALLAR:
- Commit mesajı: "test: InventoryService edge case - stok çıkışı yetersiz ve null item (RED)"
- @Mock: StockValidationStrategy (QuantityCheckStrategy, MinStockCheckStrategy)
- verify(): strategy.validateMovement() çağrıldı mı?
- Test BAŞARISIZ olmalı (RED)
```

### Commit 14: `feat: k6 yük test senaryosu - kullanıcı kayıt + login + envanter listeleme`

```
Sen bir performans test uzmanısın. TBL324 Envanter Takip Sistemi için k6 yük test senaryosu yazacaksın.

GÖREV:
- k6-tests/load-test.js:
  - options: stages (2m:100VU, 3m:250VU, 3m:500VU, 2m:0VU)
  - thresholds: http_req_duration['p(95)<500'], http_req_failed['rate<0.05']
  - Senaryo:
    1. POST /api/users/register (random username/email)
    2. POST /api/users/login (extract token)
    3. GET /api/inventory/items (Authorization: Bearer {token})
    4. POST /api/inventory/items (create item, Authorization: Bearer {token})
  - check(): status, response time, token existence
  - sleep(1-2) between requests

KURALLAR:
- Commit mesajı: "feat: k6 yük test senaryosu - kullanıcı kayıt + login + envanter listeleme"
- k6 v0.47.0 syntax
- Base URL: http://localhost:8000
- api-key header: envanter-api-key-2026
- Random data: __VU, __ITER kullanımı
- Envanter spesifik: itemCode, itemName, quantity, categoryId
```

### Commit 15: `feat: k6 kırılma noktası analizi ve performans raporu`

```
Sen bir performans analiz uzmanısın. TBL324 Envanter Takip Sistemi k6 test sonuçlarını analiz edeceksin.

GÖREV:
- k6-tests/stress-test.js (opsiyonel, daha agresif)
- Rapor oluşturma:
  - Metrikler: avg response time, p(95), p(99), error rate, req/s
  - Kırılma noktası: ~450 VU (yavaşlama başlangıcı)
  - Tablo: VU seviyeleri vs response time
  - Öneriler: connection pool, Redis cluster, Kong rate limit
  - Envanter spesifik: stok hareketi yoğunluğu, eşzamanlı envanter sorguları
- GitHub'a yükleme: k6-tests/reports/performance-report.md

KURALLAR:
- Commit mesajı: "feat: k6 kırılma noktası analizi ve performans raporu"
- Rapor markdown formatında
- Grafik: ASCII veya Mermaid xy-chart
- Gerçekçi öneriler
- Envanter context: stok hareketleri, envanter sorguları
```

### Commit 16: `docs: README TDD akışı ve commit disiplini Mermaid diyagramları`

```
Sen bir teknik yazarsın. TBL324 Envanter Takip Sistemi README için TDD ve commit disiplini diyagramları yazacaksın.

GÖREV:
- Bölüm 5: TDD Akışı
  - Red-Green-Refactor döngüsü (Mermaid graph LR)
  - Commit zaman çizelgesi (Mermaid gantt)
- Bölüm 9: Commit Disiplini
  - Haftalık commit planı (Mermaid gantt)
  - Commit mesaj konvansiyonu tablosu
  - Örnek commit geçmişi
- Bölüm 10: Puan Değerlendirmesi
  - Pasta grafik (Mermaid pie)
  - Kriter kontrol listesi

KURALLAR:
- Commit mesajı: "docs: README TDD akışı ve commit disiplini Mermaid diyagramları"
- Mermaid graph, gantt, pie syntax
- Gerçekçi zamanlar (3.5 güne uygun)
- Türkçe veya İngilizce
```

### Commit 17: `refactor: Android CustomView optimizasyonu ve Canvas performans iyileştirmesi`

```
Sen bir Android performans uzmanısın. TBL324 Envanter Takip Sistemi Android CustomView'ları optimize edeceksin.

GÖREV:
- StockLevelBarChartView:
  - onMeasure() düzeltme (exactly, at_most, unspecified)
  - Path cache (onSizeChanged'da hesaplama)
  - Paint object reuse (init'te oluştur, onDraw'da kullan)
- CategoryPieChartView:
  - RectF reuse
  - drawArc() optimizasyonu
  - Text measurement (Paint.measureText)
- Genel:
  - @Override onDetachedFromWindow() (memory leak önleme)
  - setWillNotDraw(false) (ViewGroup için)

KURALLAR:
- Commit mesajı: "refactor: Android CustomView optimizasyonu ve Canvas performans iyileştirmesi"
- Memory leak kontrolü
- Overdraw önleme
- Java 17
```

---

## GÜN 4 — 3 Commit

### Commit 18: `docs: README API dokümantasyonu (OpenAPI/Swagger şablonu)`

```
Sen bir API dokümantasyon uzmanısın. TBL324 Envanter Takip Sistemi için API dokümantasyonu yazacaksın.

GÖREV:
- README bölüm: API Endpoints
  - Tablo: Method, Endpoint, Açıklama, Request, Response
  - Örnek: POST /api/users/register, GET /api/inventory/items, POST /api/inventory/movements
  - HTTP kodları: 200, 201, 400, 401, 403, 404, 409, 500
- OpenAPI/Swagger şablonu (opsiyonel):
  - openapi.yaml (temel şablon)
  - 3 endpoint tanımı (user, inventory, notification)
- Postman collection (opsiyonel)
- Envanter spesifik:
  - ItemRequest: itemCode, name, description, categoryId, quantity, minStockLevel, location, unitPrice
  - StockMovementRequest: itemId, movementType, quantity, reason

KURALLAR:
- Commit mesajı: "docs: README API dokümantasyonu (OpenAPI/Swagger şablonu)"
- JSON request/response örnekleri
- Header bilgileri (Authorization, api-key, Content-Type)
- Türkçe veya İngilizce
```

### Commit 19: `fix: Android build hataları ve Gradle bağımlılık düzeltmeleri`

```
Sen bir Android build uzmanısın. TBL324 Envanter Takip Sistemi Android projesindeki build hatalarını düzelteceksin.

GÖREV:
- Hata tespiti:
  - ./gradlew assembleDebug çalıştır
  - Logları analiz et
- Sık karşılaşılan hatalar:
  - Dependency conflict → exclude/force
  - SDK version mismatch → compileSdk, targetSdk düzeltme
  - Java version → sourceCompatibility, targetCompatibility
  - Missing permission → AndroidManifest.xml INTERNET
  - R.java hatası → clean/build
- Gradle cache temizliği: ./gradlew clean

KURALLAR:
- Commit mesajı: "fix: Android build hataları ve Gradle bağımlılık düzeltmeleri"
- Her düzeltme açıklaması commit mesajında
- Son kontrol: ./gradlew assembleDebug BAŞARILI
- APK oluşturma: ./gradlew assembleRelease (opsiyonel)
```

### Commit 20: `chore: Son commit düzenlemesi ve GitHub repo finalizasyonu`

```
Sen bir Git uzmanısın. TBL324 Envanter Takip Sistemi GitHub repo'sunu son kontrolden geçireceksin.

GÖREV:
- Commit geçmişi kontrolü:
  - Üye 1: 20 commit
  - Üye 2: 20 commit
  - Toplam: 40 commit
  - Her gün en az 4 commit
- Commit mesaj konvansiyonu:
  - feat:, fix:, test:, refactor:, docs:, chore: önekleri
  - Açıklayıcı mesajlar
- README.md tam kontrol:
  - 3 Mermaid diyagram (sistem mimarisi, DB şeması, API akışı)
  - TDD akışı
  - Commit disiplini
  - Puan değerlendirmesi
- Son push: git push origin main

KURALLAR:
- Commit mesajı: "chore: Son commit düzenlemesi ve GitHub repo finalizasyonu"
- Son gün toplu yükleme YASAK (günlük commit dağılımı kontrol)
- .gitignore düzgün çalışıyor
- Tüm dosyalar push edilmiş
```

---

# 📋 PROMPT KULLANIM REHBERİ

## Kodlama Ajanına Nasıl Gönderilir?

### 1. Prompt Seçimi
- Hangi commit sırasında olduğunuza bakın
- İlgili prompt'u kopyalayın
- Proje durumuna göre küçük değişiklikler yapın

### 2. Context Ekleme
```
[YUKARIDAKİ PROMPT]

EK CONTEXT:
- Şu ana kadar yapılanlar: [önceki commit'lerin özeti]
- Mevcut sorun: [varsa hata mesajı]
- Özel gereksinim: [proje spesifik ihtiyaç]
```

### 3. Çıktı Kontrolü
- Kod compile edebiliyor mu?
- Testler geçiyor mu?
- Commit mesajı doğru mu?
- Tarih damgası uygun mu?

---

## ⚡ HIZLI BAŞLANGIÇ

### Gün 1, Saat 00:00 — İlk Prompt:

**Üye 1:**
```
Sen bir DevOps uzmanısın. TBL324 dersi için Envanter Takip Sistemi GitHub repo kurulumu yapacaksın.

GÖREV:
- GitHub repo: envanter-takip-sistemi
- Branch yapısı: main (korunaklı), develop, feature/*
- .gitignore: Java, Maven, IDE, Docker
- README.md şablonu (boş, sonra doldurulacak)

KURALLAR:
- Commit mesajı: "chore: Proje repo kurulumu ve Git yapılandırması"
- Java 17, Maven 3.9.x
```

**Üye 2:**
```
Sen bir test uzmanısın. TBL324 Envanter Takip Sistemi için TDD altyapısı kuracaksın.

GÖREV:
- Git flow kuralları belgesi
- JUnit 5 (5.10.x) + Mockito (5.x) dependency
- Test paketi yapısı: src/test/java/com/envanter/...

KURALLAR:
- Commit mesajı: "chore: Git flow kuralları ve TDD altyapısı (JUnit 5, Mockito)"
- TDD döngüsü: RED → GREEN → REFACTOR
```

---

> **Son Güncelleme:** 2026-05-12 03:02
> **Toplam Prompt:** 40 (20 Üye 1 + 20 Üye 2)
> **Proje:** Envanter Takip Sistemi
> **Hedef:** 100/100 | 3.5 Gün | Eşit Commit Dağılımı
