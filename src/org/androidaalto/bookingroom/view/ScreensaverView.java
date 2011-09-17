/**
   Copyright: 2011 Android Aalto

   This file is part of BookingRoom.

   BookingRoom is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   BookingRoom is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BookingRoom. If not, see <http://www.gnu.org/licenses/>.
 */

package org.androidaalto.bookingroom.view;

import org.androidaalto.bookingroom.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author hannu
 */
public class ScreensaverView extends SurfaceView implements SurfaceHolder.Callback {
    private Mover mover;
    private Bitmap image = null;
    private float left = -1;
    private float top = -1;
    private int width = -1;
    private int height = -1;
    private Speed speed = null;
    private static int BIG_FONT_SIZE = 28;
    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();
    private String msg;
    private static float mScale;

    public ScreensaverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScreensaverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ScreensaverView);
        setImage(a.getResourceId(R.styleable.ScreensaverView_image, -1));
        init();
    }

    public ScreensaverView(Context context) {
        super(context);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        mover = new Mover(getHolder(), this);
        msg = getContext().getString(R.string.screensaver_msg);
        calculateScaleFonts();
    }

    private void calculateScaleFonts() {
        if (mScale == 0) {
            mScale = getContext().getResources().getDisplayMetrics().density;
            if (mScale != 1) {
                BIG_FONT_SIZE *= mScale;
            }
        }
    }

    public void setImage(int resId) {
        image = BitmapFactory.decodeResource(getResources(), resId);
        requestLayout();
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mover.setRunning(true);
        mover.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mover.setRunning(false);
        while (retry) {
            try {
                mover.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
    }

    public void render(Canvas canvas) {
        Paint p = mPaint;
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(image, left, top, p);
        drawMessage(canvas, p);
    }

    private void drawMessage(Canvas canvas, Paint p) {
        p.setTextSize(BIG_FONT_SIZE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setAntiAlias(true);
        Rect bounds = mRect;
        p.getTextBounds(msg, 0, msg.length(), bounds);
        int x = width / 2;
        int y = height - bounds.height() / 2;
        p.setColor(Color.WHITE);
        canvas.drawText(msg, x, y, p);
    }

    public void update() {
        if (image == null || width == -1 || height == -1)
            return;
        if (left == -1 || top == -1 || speed == null) {
            left = width / 2 - image.getWidth() / 2;
            top = height / 2 - image.getHeight() / 2;
            // TODO randomize the starting angle
            speed = new Speed();
            return;
        }
        // check collision with right wall if heading right
        if (speed.getxDirection() == Speed.DIRECTION_RIGHT
                && left + image.getWidth() >= width) {
            speed.toggleXDirection();
        }
        // check collision with left wall if heading left
        if (speed.getxDirection() == Speed.DIRECTION_LEFT
                && left <= 0) {
            speed.toggleXDirection();
        }
        // check collision with bottom wall if heading down
        if (speed.getyDirection() == Speed.DIRECTION_DOWN
                && top + image.getHeight() >= getHeight()) {
            speed.toggleYDirection();
        }
        // check collision with top wall if heading up
        if (speed.getyDirection() == Speed.DIRECTION_UP
                && top <= 0) {
            speed.toggleYDirection();
        }
        left += (speed.getXv() * speed.getxDirection());
        top += (speed.getYv() * speed.getyDirection());
    }

    private static class Mover extends Thread {
        private SurfaceHolder surfaceHolder;
        private ScreensaverView screensaverView;

        public Mover(SurfaceHolder surfaceHolder, ScreensaverView screensaverView) {
            this.surfaceHolder = surfaceHolder;
            this.screensaverView = screensaverView;
        }

        private boolean running;

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        screensaverView.update();
                        screensaverView.render(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    private static class Speed {
        public static final int DIRECTION_RIGHT = 1;
        public static final int DIRECTION_LEFT = -1;
        public static final int DIRECTION_UP = -1;
        public static final int DIRECTION_DOWN = 1;

        private float xv = 1; // velocity value on the X axis
        private float yv = 1; // velocity value on the Y axis

        private int xDirection = DIRECTION_RIGHT;
        private int yDirection = DIRECTION_DOWN;

        public Speed() {
            this.xv = 1;
            this.yv = 1;
        }

        public Speed(float xv, float yv) {
            this.xv = xv;
            this.yv = yv;
        }

        public float getXv() {
            return xv;
        }

        public float getYv() {
            return yv;
        }

        public int getxDirection() {
            return xDirection;
        }

        public int getyDirection() {
            return yDirection;
        }

        // changes the direction on the X axis
        public void toggleXDirection() {
            xDirection = xDirection * -1;
        }

        // changes the direction on the Y axis
        public void toggleYDirection() {
            yDirection = yDirection * -1;
        }
    }
}
