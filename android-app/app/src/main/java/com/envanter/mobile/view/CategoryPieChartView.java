package com.envanter.mobile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CategoryPieChartView extends View {

    private float[] percentages = new float[0];
    private String[] categoryNames = new String[0];
    
    // 6 renkli palet
    private int[] colors = {
        Color.parseColor("#2196F3"), // Mavi
        Color.parseColor("#4CAF50"), // Yesil
        Color.parseColor("#FF9800"), // Turuncu
        Color.parseColor("#F44336"), // Kirmizi
        Color.parseColor("#9C27B0"), // Mor
        Color.parseColor("#00BCD4")  // Turkuaz
    };

    private Paint slicePaint;
    private Paint textPaint;
    private Paint legendPaint;
    private RectF pieBounds;

    public CategoryPieChartView(Context context) {
        super(context);
        init();
    }

    public CategoryPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slicePaint.setStyle(Paint.Style.FILL);

        // Dilim uzerindeki yuzde yazilari (kontrast - beyaz renk)
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);
        textPaint.setFakeBoldText(true);

        // Alttaki aciklama yazilari (Legend)
        legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legendPaint.setColor(Color.BLACK);
        legendPaint.setTextSize(20f); // Istenen yazi boyutu

        pieBounds = new RectF();
    }

    public void setData(float[] percentages, String[] categoryNames) {
        this.percentages = percentages;
        this.categoryNames = categoryNames;
        invalidate(); // Degisiklik sonrasi yeniden ciz
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 800;
        int desiredHeight = 800; // Ekranda karemsi bir yer kaplasin

        int width = resolveSize(desiredWidth, widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (percentages == null || percentages.length == 0 || categoryNames == null) return;

        int width = getWidth();
        int height = getHeight();

        // Pasta grafik cemberinin yariçapi
        float radius = Math.min(width, height) / 3f; 
        float centerX = width / 2f;
        float centerY = (height / 2f) - 100f; // Legend icin altta bosluk birak

        // Cizim sinirlari (RectF bounds)
        pieBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float currentAngle = 0f; // 0 derece, saat 3 yonunden baslar

        for (int i = 0; i < percentages.length; i++) {
            float sweepAngle = (percentages[i] / 100f) * 360f;
            slicePaint.setColor(colors[i % colors.length]);

            // Dilimi ciz (useCenter: true)
            canvas.drawArc(pieBounds, currentAngle, sweepAngle, true, slicePaint);

            // Yuzde etiketini ciz (Dilimin yari yolunda)
            float textAngle = currentAngle + (sweepAngle / 2f);
            float x = (float) (centerX + (radius * 0.6) * Math.cos(Math.toRadians(textAngle)));
            float y = (float) (centerY + (radius * 0.6) * Math.sin(Math.toRadians(textAngle)));

            // Eger dilim cok kucukse icine text yazma (orn. <%3)
            if (percentages[i] > 3f) {
                String text = String.format("%.1f%%", percentages[i]);
                // Metni dikeyde ortalamak icin ufak bir ofset ekledim (y+12f)
                canvas.drawText(text, x, y + 12f, textPaint);
            }

            currentAngle += sweepAngle;
        }

        // Alttaki aciklamalari (Legend) ciz
        drawLegend(canvas, width, height, radius, centerY);
    }

    private void drawLegend(Canvas canvas, int width, int height, float radius, float centerY) {
        float legendStartY = centerY + radius + 60f; // Pastanin hemen alti
        float legendStartX = 50f;
        
        float currentX = legendStartX;
        float currentY = legendStartY;
        
        float squareSize = 30f;
        float textOffset = 15f;
        float columnSpacing = (width - 100f) / 2f; // Ekrani 2 kolona bol
        float rowSpacing = 60f;

        for (int i = 0; i < percentages.length; i++) {
            // Renk karesini ciz
            slicePaint.setColor(colors[i % colors.length]);
            canvas.drawRect(currentX, currentY - squareSize, currentX + squareSize, currentY, slicePaint);

            // Kategori ismini ciz
            // Yazi dikeyde kareyle hizalansin diye ufak ayar
            canvas.drawText(categoryNames[i], currentX + squareSize + textOffset, currentY - 5f, legendPaint);

            currentX += columnSpacing;

            // Sag sinira dayandiysa alt satira gec
            if (currentX + columnSpacing > width + 10f) {
                currentX = legendStartX;
                currentY += rowSpacing;
            }
        }
    }
}
