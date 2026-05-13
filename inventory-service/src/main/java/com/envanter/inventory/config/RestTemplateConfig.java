package com.envanter.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate konfigürasyonu.
 *
 * <p>Timeout değerleri application.yml'den okunur; yoksa sağlıklı default değerler kullanılır.
 * @EnableAsync: NotificationClientImpl içindeki @Async metotlarını aktif eder.</p>
 */
@Configuration
@EnableAsync
public class RestTemplateConfig {

    /** Bağlantı zaman aşımı (ms). Default: 5000 ms */
    @Value("${http.client.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    /** Okuma zaman aşımı (ms). Default: 10000 ms */
    @Value("${http.client.read-timeout-ms:10000}")
    private int readTimeoutMs;

    /**
     * Merkezi RestTemplate bean — timeout + JSON destek dahil.
     *
     * <p>DIP: Bu bean'i kullanan sınıflar constructor injection ile alır.</p>
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
