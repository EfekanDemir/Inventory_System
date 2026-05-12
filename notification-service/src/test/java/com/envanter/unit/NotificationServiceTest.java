package com.envanter.unit;

import com.envanter.notification.dto.NotificationRequest;
import com.envanter.notification.exception.NotificationValidationException;
import com.envanter.notification.factory.NotificationFactory;
import com.envanter.notification.model.LowStockAlert;
import com.envanter.notification.model.NotificationLog;
import com.envanter.notification.repository.LowStockAlertRepository;
import com.envanter.notification.repository.NotificationLogRepository;
import com.envanter.notification.service.NotificationServiceImpl;
import com.envanter.notification.strategy.EmailSenderStrategy;
import com.envanter.notification.strategy.NotificationStrategy;
import com.envanter.notification.strategy.PushNotificationStrategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TDD -- RED Asama
 *
 * NotificationServiceImpl henuz implemente edilmedigi icin tum testler BASARISIZ.
 * Tum metodlar UnsupportedOperationException firlatir.
 *
 * Commit 6: NotificationService bildirim gonderim testleri (RED)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService - Bildirim Gonderim Testleri")
class NotificationServiceTest {

    // -- Mock'lar --------------------------------------------------------------

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private LowStockAlertRepository lowStockAlertRepository;

    // -- Test altindaki sinif -------------------------------------------------

    @InjectMocks
    private NotificationServiceImpl notificationService;

    // =========================================================================
    // EMAIL BILDIRIMI
    // =========================================================================

    /**
     * Senaryo 1 -- Gecerli EMAIL istegi gonderilir ve log kaydi olusturulur
     * RED: sendNotification() UnsupportedOperationException firlatir -> BASARISIZ.
     */
    @Test
    @DisplayName("Gecerli EMAIL istegi -- bildirim gonderilir ve NotificationLog kayit edilir")
    void sendEmail_WithValidRequest_SavesLog() {
        // -- Given --
        NotificationRequest request = new NotificationRequest(
                "EMAIL",
                "personel@envanter.com",
                "Envanter Bildirimi",
                "Yeni bir urun envantere eklendi."
        );

        NotificationLog savedLog = new NotificationLog();
        savedLog.setId("log-001");
        savedLog.setNotificationType("EMAIL");
        savedLog.setStatus("SENT");

        when(notificationLogRepository.save(any(NotificationLog.class))).thenReturn(savedLog);

        // -- When --
        notificationService.sendNotification(request);

        // -- Then --
        // Log mutlaka kaydedilmeli
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    // =========================================================================
    // PUSH BILDIRIMI
    // =========================================================================

    /**
     * Senaryo 2 -- Gecerli PUSH istegi gonderilir ve log kaydi olusturulur
     * RED: sendNotification() UnsupportedOperationException firlatir -> BASARISIZ.
     */
    @Test
    @DisplayName("Gecerli PUSH istegi -- bildirim gonderilir ve NotificationLog kayit edilir")
    void sendPush_WithValidRequest_SavesLog() {
        // -- Given --
        NotificationRequest request = new NotificationRequest(
                "PUSH",
                null,
                "Stok Uyarisi",
                "Urun stogu kritik seviyeye dusmustur."
        );

        NotificationLog savedLog = new NotificationLog();
        savedLog.setId("log-002");
        savedLog.setNotificationType("PUSH");
        savedLog.setStatus("SENT");

        when(notificationLogRepository.save(any(NotificationLog.class))).thenReturn(savedLog);

        // -- When --
        notificationService.sendNotification(request);

        // -- Then --
        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    // =========================================================================
    // DUSUK STOK UYARISI
    // =========================================================================

    /**
     * Senaryo 3 -- Stok min. esigi altinda -> LowStockAlert kaydi olusturulur
     * RED: sendLowStockAlert() UnsupportedOperationException firlatir -> BASARISIZ.
     */
    @Test
    @DisplayName("Stok min. seviyenin altinda -- LowStockAlert kayit edilir")
    void sendLowStockAlert_WhenStockBelowMin_SendsNotification() {
        // -- Given --
        Long itemId = 42L;
        int currentStock = 5;
        int minStockLevel = 10;

        LowStockAlert savedAlert = new LowStockAlert();
        savedAlert.setId("alert-001");
        savedAlert.setItemId(String.valueOf(itemId));
        savedAlert.setCurrentStock(currentStock);
        savedAlert.setMinStockLevel(minStockLevel);
        savedAlert.setAlertStatus("ACTIVE");

        when(lowStockAlertRepository.save(any(LowStockAlert.class))).thenReturn(savedAlert);

        // -- When --
        notificationService.sendLowStockAlert(itemId, currentStock, minStockLevel);

        // -- Then --
        // LowStockAlert kayit edilmeli
        verify(lowStockAlertRepository, times(1)).save(any(LowStockAlert.class));
        // Bildirim log'u da gonderilmeli (EMAIL veya PUSH)
        verify(notificationLogRepository, atLeastOnce()).save(any(NotificationLog.class));
    }

    // =========================================================================
    // HATA SENARYOLARI
    // =========================================================================

    /**
     * Senaryo 4 -- Bilinmeyen tip -> NotificationValidationException (HTTP 400)
     * RED: sendNotification() UnsupportedOperationException firlatir ->
     *      assertThrows yanlish exception alacak -> BASARISIZ.
     */
    @Test
    @DisplayName("Bilinmeyen bildirim tipi -- NotificationValidationException firlatir")
    void sendNotification_WithUnknownType_ThrowsValidationException() {
        // -- Given --
        NotificationRequest request = new NotificationRequest(
                "UNKNOWN_TYPE",
                "user@envanter.com",
                "Test",
                "Icerik"
        );

        // -- When & Then --
        NotificationValidationException exception = assertThrows(
                NotificationValidationException.class,
                () -> notificationService.sendNotification(request),
                "Bilinmeyen tip icin NotificationValidationException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();
        assertThat(exception.getErrors()).isNotEmpty();

        // Log kaydi YAPILMAMALI (gecersiz istek)
        verify(notificationLogRepository, never()).save(any(NotificationLog.class));
    }

    /**
     * Senaryo 5 -- Null tip -> NotificationValidationException (edge case)
     * RED: UnsupportedOperationException firlatir -> BASARISIZ.
     */
    @Test
    @DisplayName("Null bildirim tipi -- NotificationValidationException firlatir")
    void sendNotification_WithNullType_ThrowsValidationException() {
        // -- Given --
        NotificationRequest request = new NotificationRequest(null, "user@envanter.com", "Test", "Icerik");

        // -- When & Then --
        assertThrows(
                NotificationValidationException.class,
                () -> notificationService.sendNotification(request),
                "Null tip icin NotificationValidationException firlatilmali"
        );

        verify(notificationLogRepository, never()).save(any(NotificationLog.class));
    }

    // =========================================================================
    // LOG LISTELEME
    // =========================================================================

    /**
     * Senaryo 6 -- getLogs() 3 kayit doner
     * RED: getLogs() UnsupportedOperationException firlatir -> BASARISIZ.
     */
    @Test
    @DisplayName("getLogs() -- Mock'ta 3 kayit var, 3 eleman listesi doner")
    void getLogs_ReturnsAllLogs() {
        // -- Given --
        NotificationLog log1 = new NotificationLog();
        log1.setId("log-001");
        log1.setNotificationType("EMAIL");

        NotificationLog log2 = new NotificationLog();
        log2.setId("log-002");
        log2.setNotificationType("PUSH");

        NotificationLog log3 = new NotificationLog();
        log3.setId("log-003");
        log3.setNotificationType("LOW_STOCK");

        when(notificationLogRepository.findAll()).thenReturn(List.of(log1, log2, log3));

        // -- When --
        List<NotificationLog> result = notificationService.getLogs();

        // -- Then --
        assertNotNull(result, "Sonuc null olmamali");
        assertEquals(3, result.size(), "3 log kaydi donmeli");
        assertThat(result).extracting(NotificationLog::getId)
                .containsExactlyInAnyOrder("log-001", "log-002", "log-003");

        verify(notificationLogRepository, times(1)).findAll();
    }

    // =========================================================================
    // FACTORY PATTERN TESTLERI (unit)
    // =========================================================================

    /**
     * Senaryo 7 -- NotificationFactory "EMAIL" icin EmailSenderStrategy donmeli
     * Bu test NotificationServiceImpl'den BAGIMSIZ -- Factory'i direkt test eder.
     * Implementasyon mevcut oldugundan bu test GREEN olacak.
     */
    @Test
    @DisplayName("NotificationFactory -- EMAIL tipi -> EmailSenderStrategy doner")
    void notificationFactory_WithEmailType_ReturnsEmailStrategy() {
        // -- When --
        NotificationStrategy strategy = NotificationFactory.createStrategy("EMAIL");

        // -- Then --
        assertNotNull(strategy);
        assertThat(strategy).isInstanceOf(EmailSenderStrategy.class);
        assertEquals("EMAIL", strategy.getSupportedType());
    }

    /**
     * Senaryo 8 -- NotificationFactory "PUSH" icin PushNotificationStrategy donmeli
     * Bu test direkt Factory'i test eder -- GREEN olacak.
     */
    @Test
    @DisplayName("NotificationFactory -- PUSH tipi -> PushNotificationStrategy doner")
    void notificationFactory_WithPushType_ReturnsPushStrategy() {
        // -- When --
        NotificationStrategy strategy = NotificationFactory.createStrategy("PUSH");

        // -- Then --
        assertNotNull(strategy);
        assertThat(strategy).isInstanceOf(PushNotificationStrategy.class);
        assertEquals("PUSH", strategy.getSupportedType());
    }

    /**
     * Senaryo 9 -- NotificationFactory bilinmeyen tip -> NotificationValidationException
     * Bu test direkt Factory'i test eder -- GREEN olacak.
     */
    @Test
    @DisplayName("NotificationFactory -- bilinmeyen tip -> NotificationValidationException")
    void notificationFactory_WithUnknownType_ThrowsException() {
        // -- When & Then --
        NotificationValidationException exception = assertThrows(
                NotificationValidationException.class,
                () -> NotificationFactory.createStrategy("TELEGRAM"),
                "Bilinmeyen tip icin NotificationValidationException firlatilmali"
        );

        assertThat(exception.getMessage()).contains("TELEGRAM");
    }
}
