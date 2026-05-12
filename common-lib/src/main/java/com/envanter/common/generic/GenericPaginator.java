package com.envanter.common.generic;

import java.util.Collections;
import java.util.List;

/**
 * Sayfalanmis liste sonuclarini saran genel paginator.
 *
 * Kullanim:
 *   GenericPaginator<ItemDTO> page = new GenericPaginator<>(items, 0, 10, 53L);
 *   page.hasNext();      // true (toplam 53 kayit, ilk sayfa 10'lu)
 *   page.hasPrevious();  // false (sayfa 0)
 *
 * @param <T> Sayfalanacak icerigin tipi
 */
public final class GenericPaginator<T> {

    /** Mevcut sayfadaki icerik listesi. */
    private final List<T> content;

    /** Mevcut sayfa numarasi (0-bazli). */
    private final int page;

    /** Sayfa basina eleman sayisi. */
    private final int size;

    /** Toplam eleman sayisi (tum sayfalarda). */
    private final long totalElements;

    /** Toplam sayfa sayisi. */
    private final int totalPages;

    // -- Constructor -----------------------------------------------------------

    /**
     * @param content       Mevcut sayfadaki kayitlar
     * @param page          Sayfa numarasi (0-bazli)
     * @param size          Sayfa boyutu
     * @param totalElements Veritabanindaki toplam kayit sayisi
     */
    public GenericPaginator(List<T> content, int page, int size, long totalElements) {
        if (page < 0)  throw new IllegalArgumentException("Sayfa numarasi 0'dan kucuk olamaz.");
        if (size <= 0) throw new IllegalArgumentException("Sayfa boyutu 0'dan buyuk olmalidir.");

        this.content       = Collections.unmodifiableList(content);
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = (int) Math.ceil((double) totalElements / size);
    }

    // -- Sayfalama Yardimcilari -----------------------------------------------

    /**
     * Bir sonraki sayfa var mi?
     */
    public boolean hasNext() {
        return page < totalPages - 1;
    }

    /**
     * Bir onceki sayfa var mi?
     */
    public boolean hasPrevious() {
        return page > 0;
    }

    /**
     * Bu ilk sayfa mi? (sayfa 0)
     */
    public boolean isFirst() {
        return !hasPrevious();
    }

    /**
     * Bu son sayfa mi?
     */
    public boolean isLast() {
        return !hasNext();
    }

    // -- Getters --------------------------------------------------------------

    public List<T> getContent()       { return content;       }
    public int getPage()              { return page;          }
    public int getSize()              { return size;          }
    public long getTotalElements()    { return totalElements; }
    public int getTotalPages()        { return totalPages;    }

    @Override
    public String toString() {
        return "GenericPaginator{"
                + "page=" + page
                + ", size=" + size
                + ", totalElements=" + totalElements
                + ", totalPages=" + totalPages
                + ", contentSize=" + content.size()
                + '}';
    }
}
