package com.envanter.mobile.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.envanter.mobile.model.ItemStock;

import java.util.ArrayList;
import java.util.List;

public class StockLevelBarChartView extends View {

    private List<ItemStock> items = new ArrayList<>();

    private Paint barPaint;
    private Paint textPaint;
    private Paint axisPaint;
    private Paint minStockLinePaint;
    private Paint criticalBorderPaint;

    // Renkler
    private final int COLOR_GREEN = Color.parseColor("#4CAF50");
    private final int COLOR_ORANGE = Color.parseColor("#FF9800");
    private final int COLOR_RED = Color.parseColor("#F44336");

    private float animationProgress = 0f;

    public StockLevelBarChartView(Context context) {
        super(context);
        init();
    }

    public StockLevelBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint nesneleri onDraw icinde kesinlikle new ile uretilmemelidir, burada initialize edilir.
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(36f);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.DKGRAY);
        axisPaint.setStrokeWidth(4f);

        minStockLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minStockLinePaint.setColor(Color.RED);
        minStockLinePaint.setStyle(Paint.Style.STROKE);
        minStockLinePaint.setStrokeWidth(4f);
        // Kesikli cizgi efekti
        minStockLinePaint.setPathEffect(new DashPathEffect(new float[]{15f, 10f}, 0f));

        criticalBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        criticalBorderPaint.setColor(Color.RED);
        criticalBorderPaint.setStyle(Paint.Style.STROKE);
        criticalBorderPaint.setStrokeWidth(8f);
    }

    public void setItems(List<ItemStock> items) {
        this.items = items;
        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(800); // 800ms
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            postInvalidateOnAnimation(); // Animasyonlu cizim tetikleyicisi (Performans dostu)
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 800; 
        int desiredHeight = 600;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // onDraw icinde "new" kullanimi yapilmamistir (Optimization)

        if (items == null || items.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();

        // Grafik etrafındaki boşluklar
        float paddingLeft = 120f;
        float paddingBottom = 120f;
        float paddingTop = 80f;
        float paddingRight = 80f;

        // X ve Y ekseni cizgileri
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, axisPaint); // Y ekseni
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, axisPaint); // X ekseni

        // Y ekseni icin max degeri bul
        int maxQuantity = 0;
        for (ItemStock item : items) {
            if (item.getQuantity() > maxQuantity) maxQuantity = item.getQuantity();
            if (item.getMinStockLevel() * 2 > maxQuantity) maxQuantity = item.getMinStockLevel() * 2;
        }
        if (maxQuantity == 0) maxQuantity = 10; // Default

        float graphHeight = height - paddingBottom - paddingTop;
        float graphWidth = width - paddingLeft - paddingRight;

        int itemCount = items.size();
        float barSpacing = 40f;
        float barWidth = (graphWidth / itemCount) - barSpacing;

        for (int i = 0; i < itemCount; i++) {
            ItemStock item = items.get(i);
            
            // Renk Mantigi (>= 1.5 min -> Yesil, >= min -> Turuncu, < min -> Kirmizi)
            if (item.getQuantity() >= item.getMinStockLevel() * 1.5) {
                barPaint.setColor(COLOR_GREEN);
            } else if (item.getQuantity() >= item.getMinStockLevel()) {
                barPaint.setColor(COLOR_ORANGE);
            } else {
                barPaint.setColor(COLOR_RED);
            }

            float left = paddingLeft + (i * (barWidth + barSpacing)) + (barSpacing / 2);
            float right = left + barWidth;
            float bottom = height - paddingBottom;
            
            // Stok miktarinin boyu (Animasyonla yukselir)
            float ratio = (float) item.getQuantity() / maxQuantity;
            float top = bottom - (graphHeight * ratio * animationProgress);

            // Bar'i ciz
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Kritik seviyedeyse kirmizi dis cerceve ciz
            if (item.getQuantity() < item.getMinStockLevel()) {
                canvas.drawRect(left, top, right, bottom, criticalBorderPaint);
            }

            // Urun ismi cizimi (Barin altina)
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(item.getItemName(), left + (barWidth / 2), bottom + 50f, textPaint);

            // Miktar cizimi (Barin ustune) - Animasyon oraninda artan sayi
            int currentQty = Math.round(item.getQuantity() * animationProgress);
            canvas.drawText(String.valueOf(currentQty), left + (barWidth / 2), top - 20f, textPaint);

            // Min Stok Seviyesi Kesikli Cizgi
            float minRatio = (float) item.getMinStockLevel() / maxQuantity;
            float minTop = bottom - (graphHeight * minRatio);
            canvas.drawLine(left - 20f, minTop, right + 20f, minTop, minStockLinePaint);
        }
    }
}
