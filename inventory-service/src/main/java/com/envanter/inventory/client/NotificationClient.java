package com.envanter.inventory.client;

/**
 * notification-service ile iletisim icin REST client.
 * Implementasyonu: NotificationClientImpl (HTTP / RestTemplate ile).
 *
 * Test sirasinda Mockito ile mock'lanir -- gercek HTTP cagrisi yapilmaz.
 */
public interface NotificationClient {

    /**
     * Dusu stok uyarisi gonderir.
     *
     * @param itemId        Uyari gonderilecek urun ID'si
     * @param currentStock  Mevcut stok miktari
     * @param minStockLevel Minimum stok esigi
     */
    void sendLowStockAlert(Long itemId, int currentStock, int minStockLevel);

    /**
     * Yeni envanter eklendigi bildirimini gonderir.
     *
     * @param itemId   Eklenen urun ID'si
     * @param itemName Eklenen urun adi
     */
    void sendInventoryAddedNotification(Long itemId, String itemName);
}
