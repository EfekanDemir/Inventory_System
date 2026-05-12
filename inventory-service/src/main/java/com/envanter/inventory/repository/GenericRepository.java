package com.envanter.inventory.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD repository sozlesmesi.
 * inventory-service icerisindeki tum JDBC repository'leri bu arayuzu implement eder.
 *
 * @param <T>  Entity tipi
 * @param <ID> Birincil anahtar tipi (genellikle Long)
 */
public interface GenericRepository<T, ID> {

    /**
     * ID'ye gore entity getirir.
     * @return entity varsa Optional.of(entity), yoksa Optional.empty()
     */
    Optional<T> findById(ID id);

    /** Tablodaki tum kayitlari getirir. */
    List<T> findAll();

    /**
     * Yeni kayit ekler veya gunceller.
     * @param entity Kaydedilecek entity
     * @return Kaydedilmis entity (uretilen ID ile)
     */
    T save(T entity);

    /** ID'ye gore kaydı siler. */
    void deleteById(ID id);

    /** Verilen ID'ye sahip kayit var mi kontrol eder. */
    boolean existsById(ID id);
}
