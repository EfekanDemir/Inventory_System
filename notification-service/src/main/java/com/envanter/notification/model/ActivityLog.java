package com.envanter.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Servis aktivite log kaydı — MongoDB "activity_logs" koleksiyonuna persist edilir.
 *
 * <p>Her API isteği için oluşturulur; performans analizi ve denetim izi sağlar.
 * requestData / responseData şemasız Map yapısı sayesinde her servise uyum sağlar.</p>
 *
 * İndeksler:
 * - serviceName + action → servis/işlem bazlı sorgular
 * - userId              → kullanıcı aktivite geçmişi
 * - timestamp           → tarih aralığı sorguları
 */
@Document(collection = "activity_logs")
public class ActivityLog {

    @Id
    private String id;

    /** Kaydı oluşturan mikroservis adı: inventory-service, user-service vb. */
    @Field("service_name")
    @Indexed
    private String serviceName;

    /** Gerçekleştirilen işlem: CREATE_ITEM, STOCK_MOVEMENT, LOGIN vb. */
    @Field("action")
    @Indexed
    private String action;

    @Field("user_id")
    @Indexed
    private String userId;

    /** İstek parametreleri/gövdesi — şemasız, esnek yapı. */
    @Field("request_data")
    private Map<String, Object> requestData;

    /** Yanıt verisi veya hata detayı — şemasız, esnek yapı. */
    @Field("response_data")
    private Map<String, Object> responseData;

    @Field("timestamp")
    @Indexed
    private LocalDateTime timestamp;

    /** Milisaniye cinsinden toplam işlem süresi. */
    @Field("execution_time_ms")
    private Long executionTimeMs;

    // -- Constructors ---------------------------------------------------------

    public ActivityLog() {}

    public ActivityLog(String serviceName, String action, String userId) {
        this.serviceName = serviceName;
        this.action      = action;
        this.userId      = userId;
        this.timestamp   = LocalDateTime.now();
    }

    // -- Getters & Setters ----------------------------------------------------

    public String getId()                            { return id; }
    public void setId(String id)                     { this.id = id; }

    public String getServiceName()                   { return serviceName; }
    public void setServiceName(String serviceName)   { this.serviceName = serviceName; }

    public String getAction()                        { return action; }
    public void setAction(String action)             { this.action = action; }

    public String getUserId()                        { return userId; }
    public void setUserId(String userId)             { this.userId = userId; }

    public Map<String, Object> getRequestData()                  { return requestData; }
    public void setRequestData(Map<String, Object> requestData)  { this.requestData = requestData; }

    public Map<String, Object> getResponseData()                   { return responseData; }
    public void setResponseData(Map<String, Object> responseData)  { this.responseData = responseData; }

    public LocalDateTime getTimestamp()              { return timestamp; }
    public void setTimestamp(LocalDateTime t)        { this.timestamp = t; }

    public Long getExecutionTimeMs()                 { return executionTimeMs; }
    public void setExecutionTimeMs(Long ms)          { this.executionTimeMs = ms; }
}
