package com.example.tictactoe;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PremiumBackdropView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private ValueAnimator animator;
    private float motion;

    private static final int MIDNIGHT = Color.rgb(3, 7, 18);
    private static final int DEEP_SEA = Color.rgb(4, 30, 48);
    private static final int INK_PURPLE = Color.rgb(23, 11, 45);
    private static final int CYAN = Color.rgb(60, 230, 255);
    private static final int VIOLET = Color.rgb(188, 112, 255);
    private static final int MINT = Color.rgb(90, 255, 205);
    private static final int ROSE = Color.rgb(255, 95, 157);

    public PremiumBackdropView(Context context) {
        super(context);
        init();
    }

    public PremiumBackdropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        glowPaint.setMaskFilter(new BlurMaskFilter(54f, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(9000L);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            motion = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        drawBase(canvas, width, height);
        drawLiquidLight(canvas, width, height);
        drawAuroraCurtains(canvas, width, height);
        drawCausticNet(canvas, width, height);
        drawParticles(canvas, width, height);
        drawReadableStage(canvas, width, height);
        drawVignette(canvas, width, height);
    }

    private void drawBase(Canvas canvas, int width, int height) {
        paint.setShader(new LinearGradient(
                0, 0, width, height,
                new int[]{MIDNIGHT, DEEP_SEA, INK_PURPLE, MIDNIGHT},
                new float[]{0f, 0.34f, 0.72f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);

        float pulse = 0.5f + 0.5f * wave(0f);
        glowPaint.setColor(Color.argb((int) (82 + pulse * 34), 60, 230, 255));
        canvas.drawCircle(width * (0.22f + 0.03f * wave(0.2f)), height * 0.22f,
                width * 0.42f, glowPaint);
        glowPaint.setColor(Color.argb((int) (72 + pulse * 28), 188, 112, 255));
        canvas.drawCircle(width * (0.86f + 0.02f * wave(0.55f)), height * 0.66f,
                width * 0.46f, glowPaint);
        glowPaint.setColor(Color.argb(58, 90, 255, 205));
        canvas.drawCircle(width * 0.58f, height * 0.92f, width * 0.34f, glowPaint);
    }

    private void drawLiquidLight(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 4; i++) {
            float phase = motion + i * 0.19f;
            int color = i % 2 == 0 ? CYAN : VIOLET;
            paint.setColor(Color.argb(22 + i * 8, Color.red(color), Color.green(color), Color.blue(color)));

            Path path = new Path();
            float yBase = height * (0.18f + i * 0.18f);
            path.moveTo(-width * 0.2f, yBase);
            for (int x = -40; x <= width + 80; x += 48) {
                float y = yBase
                        + wave(phase + x * 0.0019f) * 42f
                        + (float) Math.sin((x * 0.021f) + phase * Math.PI * 2f) * 18f;
                path.lineTo(x, y);
            }
            path.lineTo(width * 1.2f, yBase + height * 0.16f);
            path.lineTo(-width * 0.2f, yBase + height * 0.18f);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void drawAuroraCurtains(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        for (int layer = 0; layer < 7; layer++) {
            float x = width * (-0.18f + layer * 0.22f + 0.04f * wave(layer * 0.13f));
            int color = layer % 3 == 0 ? CYAN : layer % 3 == 1 ? MINT : VIOLET;
            paint.setStrokeWidth(18f + layer * 3f);
            paint.setColor(Color.argb(34, Color.red(color), Color.green(color), Color.blue(color)));

            Path ribbon = new Path();
            ribbon.moveTo(x, -60f);
            ribbon.cubicTo(
                    x + width * 0.20f + wave(layer * 0.08f) * 60f, height * 0.28f,
                    x - width * 0.18f + wave(layer * 0.16f) * 70f, height * 0.62f,
                    x + width * 0.20f, height + 80f);
            canvas.drawPath(ribbon, paint);
        }

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawCausticNet(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        float drift = motion * 140f;
        for (int i = -4; i < 18; i++) {
            float y = height * 0.14f + i * height * 0.055f;
            paint.setStrokeWidth(i % 3 == 0 ? 1.8f : 1.1f);
            paint.setColor(Color.argb(i % 3 == 0 ? 74 : 42, 210, 250, 255));
            Path path = new Path();
            path.moveTo(-40f, y);
            for (int x = -40; x <= width + 40; x += 36) {
                path.lineTo(x, y + wave(0.07f * i + x * 0.002f + motion) * 16f);
            }
            canvas.drawPath(path, paint);
        }

        for (int i = -6; i < 12; i++) {
            float x = i * width * 0.12f + drift % (width * 0.24f);
            paint.setStrokeWidth(1.2f);
            paint.setColor(Color.argb(38, 188, 112, 255));
            canvas.drawLine(x, 0f, x + width * 0.48f, height, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawParticles(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 38; i++) {
            float seed = i * 0.137f;
            float x = ((seed * 997f + motion * (26f + i % 5 * 9f)) % 1f) * width;
            float y = ((seed * 571f + wave(seed) * 0.03f + motion * 0.06f) % 1f) * height;
            float radius = 1.1f + (i % 4) * 0.55f;
            int color = i % 3 == 0 ? CYAN : i % 3 == 1 ? MINT : ROSE;
            paint.setColor(Color.argb(56 + (i % 5) * 18,
                    Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawCircle(x, y, radius, paint);
        }
    }

    private void drawReadableStage(Canvas canvas, int width, int height) {
        float cx = width * 0.5f;
        float cy = height * 0.54f;
        paint.setShader(new RadialGradient(
                cx, cy, width * 0.68f,
                new int[]{Color.argb(150, 1, 5, 14), Color.argb(72, 1, 5, 14), Color.TRANSPARENT},
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);

        rect.set(width * 0.04f, height * 0.28f, width * 0.96f, height * 0.83f);
        paint.setColor(Color.argb(38, 248, 251, 255));
        canvas.drawRoundRect(rect, 34f, 34f, paint);
    }

    private void drawVignette(Canvas canvas, int width, int height) {
        paint.setShader(new RadialGradient(
                width * 0.5f, height * 0.5f, Math.max(width, height) * 0.74f,
                Color.TRANSPARENT, Color.argb(168, 0, 0, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);
    }

    private float wave(float offset) {
        return (float) Math.sin((motion + offset) * Math.PI * 2f);
    }
}
