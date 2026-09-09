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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PremiumBackdropView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private ValueAnimator animator;
    private float motion;

    private static final int BURNT_ORANGE = Color.rgb(178, 82, 46);
    private static final int ROAST = Color.rgb(106, 44, 27);
    private static final int ESPRESSO = Color.rgb(45, 24, 17);
    private static final int CREAM = Color.rgb(248, 237, 216);
    private static final int BUTTER = Color.rgb(255, 220, 143);
    private static final int FOREST = Color.rgb(13, 107, 79);

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
        glowPaint.setMaskFilter(new BlurMaskFilter(42f, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(11000L);
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

        drawGradientBase(canvas, width, height);
        drawTexture(canvas, width, height);
        drawGiantType(canvas, width, height);
        drawSteam(canvas, width, height);
        drawFloatingPieces(canvas, width, height);
        drawBottomCreamBand(canvas, width, height);
        drawVignette(canvas, width, height);
    }

    private void drawGradientBase(Canvas canvas, int width, int height) {
        paint.setShader(new LinearGradient(
                0, 0, width, height,
                new int[]{Color.rgb(196, 93, 50), BURNT_ORANGE, ROAST, ESPRESSO},
                new float[]{0f, 0.38f, 0.76f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);

        glowPaint.setColor(Color.argb(122, 255, 205, 116));
        canvas.drawCircle(width * 0.22f, height * 0.18f, width * 0.42f, glowPaint);
        glowPaint.setColor(Color.argb(92, 10, 90, 68));
        canvas.drawCircle(width * 0.82f, height * 0.58f, width * 0.38f, glowPaint);
    }

    private void drawTexture(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.2f);
        paint.setColor(Color.argb(36, 255, 238, 205));

        float drift = motion * 48f;
        for (int i = -height; i < width; i += 42) {
            canvas.drawLine(i + drift, 0, i + height + drift, height, paint);
        }

        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(25, 80, 28, 16));
        for (int i = -width; i < width * 2; i += 58) {
            canvas.drawLine(i - drift, height, i + height - drift, 0, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawGiantType(Canvas canvas, int width, int height) {
        paint.setShader(null);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.argb(188, 248, 237, 216));

        float titleSize = Math.max(82f, width * 0.28f);
        paint.setTextSize(titleSize);
        canvas.drawText("TIC", width * 0.5f, height * 0.17f, paint);
        canvas.drawText("TAC", width * 0.5f, height * 0.29f, paint);

        paint.setColor(Color.argb(92, 248, 237, 216));
        paint.setTextSize(Math.max(130f, width * 0.52f));
        canvas.drawText("XO", width * 0.5f, height * 0.66f, paint);
    }

    private void drawSteam(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(4f);
        paint.setColor(Color.argb(72, 255, 244, 221));

        float phase = (float) Math.sin(motion * Math.PI * 2f) * 18f;
        for (int i = 0; i < 4; i++) {
            float startX = width * (0.24f + i * 0.16f);
            float startY = height * 0.47f + i * 12f;
            Path path = new Path();
            path.moveTo(startX, startY);
            path.cubicTo(startX - 30f + phase, startY - 42f,
                    startX + 36f - phase, startY - 86f,
                    startX + 8f, startY - 128f);
            canvas.drawPath(path, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawFloatingPieces(Canvas canvas, int width, int height) {
        drawToken(canvas, width * 0.18f, height * 0.39f, 24f, "X", CREAM, FOREST, 0.1f);
        drawToken(canvas, width * 0.80f, height * 0.24f, 30f, "O", ESPRESSO, BUTTER, 0.43f);
        drawToken(canvas, width * 0.72f, height * 0.50f, 22f, "X", CREAM, ROAST, 0.72f);
        drawToken(canvas, width * 0.30f, height * 0.78f, 28f, "O", ESPRESSO, CREAM, 0.88f);

        drawBean(canvas, width * 0.67f, height * 0.35f, 34f, 0.21f);
        drawBean(canvas, width * 0.32f, height * 0.22f, 39f, 0.56f);
        drawBean(canvas, width * 0.86f, height * 0.72f, 31f, 0.78f);
        drawBean(canvas, width * 0.12f, height * 0.66f, 27f, 0.36f);
    }

    private void drawToken(Canvas canvas, float x, float y, float radius, String label,
                           int fill, int textColor, float offset) {
        float wave = (float) Math.sin((motion + offset) * Math.PI * 2f);
        float bob = wave * 15f;
        float spin = (motion + offset) * 360f;

        canvas.save();
        canvas.translate(x, y + bob);
        canvas.rotate(spin);

        paint.setShader(new RadialGradient(
                -radius * 0.35f, -radius * 0.35f, radius * 1.6f,
                Color.argb(255, 255, 249, 235), fill, Shader.TileMode.CLAMP));
        canvas.drawCircle(0f, 0f, radius, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.argb(145, 75, 34, 20));
        canvas.drawCircle(0f, 0f, radius - 2f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(radius * 1.05f);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        canvas.rotate(-spin);
        canvas.drawText(label, 0f, -(metrics.ascent + metrics.descent) / 2f, paint);
        canvas.restore();
    }

    private void drawBean(Canvas canvas, float x, float y, float size, float offset) {
        float wave = (float) Math.cos((motion + offset) * Math.PI * 2f);
        canvas.save();
        canvas.translate(x, y + wave * 18f);
        canvas.rotate(-28f + wave * 14f);

        rect.set(-size * 0.42f, -size * 0.72f, size * 0.42f, size * 0.72f);
        paint.setShader(new LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                Color.rgb(91, 42, 24), Color.rgb(28, 15, 11), Shader.TileMode.CLAMP));
        canvas.drawOval(rect, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.4f);
        paint.setColor(Color.argb(155, 217, 139, 85));
        Path groove = new Path();
        groove.moveTo(0, -size * 0.54f);
        groove.cubicTo(-size * 0.22f, -size * 0.18f, size * 0.22f, size * 0.16f, 0, size * 0.54f);
        canvas.drawPath(groove, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawBottomCreamBand(Canvas canvas, int width, int height) {
        float top = height * 0.86f;
        rect.set(-20f, top, width + 20f, height + 30f);
        paint.setColor(Color.argb(238, 247, 235, 213));
        canvas.drawRoundRect(rect, 38f, 38f, paint);

        paint.setColor(Color.argb(45, 36, 25, 18));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(12f);
        canvas.drawText("TIC TAC TOE AI", width * 0.5f, top + 34f, paint);
    }

    private void drawVignette(Canvas canvas, int width, int height) {
        paint.setShader(new RadialGradient(
                width * 0.5f, height * 0.42f, Math.max(width, height) * 0.78f,
                Color.TRANSPARENT, Color.argb(118, 25, 12, 9), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);
    }
}
