/**
   Copyright: 2011 Android Aalto

   This file is part of BookingRoom.

   BookingRoom is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   BookingRoom is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with BookingRoom; if not, write to the Free Software
   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.androidaalto.bookingroom.view;

import org.androidaalto.bookingroom.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class WeekView extends View {

    private static final int HOUR_GAP = 1;
    // For drawing to an off-screen Canvas
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean mRedrawScreen = true;
    private boolean mRemeasure = true;
    private int mBitmapHeight;
    private int mGridAreaHeight;
    private int mCellHeight;
    private int mNumHours = 10;

    // Pre-allocate these objects and re-use them
    private Rect mRect = new Rect();
    private Rect mSrcRect = new Rect();
    private Rect mDestRect = new Rect();
    private Paint mPaint = new Paint();
    private Time mCurrentTime;
    private int mHoursWidth;
    private String mAmString;
    private String mPmString;
    private Resources mResources;
    private int mViewWidth;
    private int mViewHeight;
    private int mCellWidth;
    private int mNumDays = 7;
    private int mViewStartY;
    private int mFirstCell;

    private static int AMPM_FONT_SIZE = 9;

    private static final int DAY_GAP = 1;

    private static final int HOURS_LEFT_MARGIN = 2;
    private static final int HOURS_RIGHT_MARGIN = 4;
    private static final int HOURS_MARGIN = HOURS_LEFT_MARGIN + HOURS_RIGHT_MARGIN;

    private static int mCalendarGridAreaBackground;
    private static int mCalendarGridLineHorizontalColor;
    private static int mCalendarGridLineVerticalColor;

    /**
     * @param context
     */
    public WeekView(Context context) {
        super(context);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WeekView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mResources = this.getContext().getResources();

        mCurrentTime = new Time();
        long currentTime = System.currentTimeMillis();
        mCurrentTime.set(currentTime);

        mCalendarGridAreaBackground = mResources.getColor(R.color.calendar_grid_area_background);
        mCalendarGridLineHorizontalColor = mResources
                .getColor(R.color.calendar_grid_line_horizontal_color);
        mCalendarGridLineVerticalColor = mResources
                .getColor(R.color.calendar_grid_line_vertical_color);

        Paint p = mPaint;
        p.setAntiAlias(true);

        mAmString = DateUtils.getAMPMString(Calendar.AM);
        mPmString = DateUtils.getAMPMString(Calendar.PM);
        String[] ampm = {
                mAmString, mPmString
        };
        p.setTextSize(AMPM_FONT_SIZE);
        mHoursWidth = computeMaxStringWidth(mHoursWidth, ampm, p);
        mHoursWidth += HOURS_MARGIN;
    }

    private int computeMaxStringWidth(int currentMax, String[] strings, Paint p) {
        float maxWidthF = 0.0f;

        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        if (maxWidth < currentMax) {
            maxWidth = currentMax;
        }
        return maxWidth;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mRemeasure) {
            remeasure(getWidth(), getHeight());
            mRemeasure = false;
        }

        if (mRedrawScreen && mCanvas != null) {
            doDraw(mCanvas);
            mRedrawScreen = false;
        }

        // TODO: handle scrolling

        if (mBitmap != null) {
            drawCalendarView(canvas);
        }
    }

    /**
     * @param canvas
     */
    private void drawCalendarView(Canvas canvas) {
        // Copy the scrollable region from the big bitmap to the canvas.
        Rect src = mSrcRect;
        Rect dest = mDestRect;

        src.top = mViewStartY;
        src.bottom = mViewStartY + mGridAreaHeight;
        src.left = 0;
        src.right = mViewWidth;

        dest.top = mFirstCell;
        dest.bottom = mViewHeight;
        dest.left = 0;
        dest.right = mViewWidth;

        canvas.save();
        canvas.clipRect(dest);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(mBitmap, src, dest, null);
        canvas.restore();
    }

    /**
     * @param mCanvas2
     */
    private void doDraw(Canvas canvas) {
        Paint p = mPaint;
        Rect r = mRect;
        int lineY = mCurrentTime.hour * (mCellHeight + HOUR_GAP)
                + ((mCurrentTime.minute * mCellHeight) / 60)
                + 1;

        drawGridBackground(r, canvas, p);
        drawHours(r, canvas, p);

        // Draw each day
        drawEachDay(r, canvas, p, lineY);
    }

    /**
     * @param r
     * @param canvas
     * @param p
     * @param lineY
     */
    private void drawEachDay(Rect r, Canvas canvas, Paint p, int lineY) {
        // TODO Auto-generated method stub

    }

    /**
     * @param r
     * @param canvas
     * @param p
     */
    private void drawHours(Rect r, Canvas canvas, Paint p) {
        // TODO Auto-generated method stub

    }

    /**
     * @param r
     * @param canvas
     * @param p
     */
    private void drawGridBackground(Rect r, Canvas canvas, Paint p) {
        Paint.Style savedStyle = p.getStyle();

        // Clear the background
        p.setColor(mCalendarGridAreaBackground);
        r.top = 0;
        r.bottom = mBitmapHeight;
        r.left = 0;
        r.right = mViewWidth;
        canvas.drawRect(r, p);

        // Draw the horizontal grid lines
        p.setColor(mCalendarGridLineHorizontalColor);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(0);
        p.setAntiAlias(false);
        float startX = mHoursWidth;
        float stopX = mHoursWidth + (mCellWidth + DAY_GAP) * mNumDays;
        float y = 0;
        float deltaY = mCellHeight + HOUR_GAP;
        for (int hour = 0; hour <= 24; hour++) {
            canvas.drawLine(startX, y, stopX, y, p);
            y += deltaY;
        }

        // Draw the vertical grid lines
        p.setColor(mCalendarGridLineVerticalColor);
        float startY = 0;
        float stopY = HOUR_GAP + 24 * (mCellHeight + HOUR_GAP);
        float deltaX = mCellWidth + DAY_GAP;
        float x = mHoursWidth + mCellWidth;
        for (int day = 0; day < mNumDays; day++) {
            canvas.drawLine(x, startY, x, stopY, p);
            x += deltaX;
        }

        // Restore the saved style.
        p.setStyle(savedStyle);
        p.setAntiAlias(true);
    }

    /**
     * @param width
     * @param height
     */
    private void remeasure(int width, int height) {
        mGridAreaHeight = height - mFirstCell;
        mCellHeight = (mGridAreaHeight - ((mNumHours + 1) * HOUR_GAP)) / mNumHours;
        int usedGridAreaHeight = (mCellHeight + HOUR_GAP) * mNumHours + HOUR_GAP;
        int bottomSpace = mGridAreaHeight - usedGridAreaHeight;
        // mEventGeometry.setHourHeight(mCellHeight);

        createOffscreenBitmapAndCanvas(width, bottomSpace);
    }

    private void createOffscreenBitmapAndCanvas(int width, int bottomSpace) {
        // Create an off-screen bitmap that we can draw into.
        mBitmapHeight = HOUR_GAP + 24 * (mCellHeight + HOUR_GAP) + bottomSpace;
        if ((mBitmap == null || mBitmap.getHeight() < mBitmapHeight) && width > 0 &&
                mBitmapHeight > 0) {
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            mBitmap = Bitmap.createBitmap(width, mBitmapHeight, Bitmap.Config.RGB_565);
            mCanvas = new Canvas(mBitmap);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        mViewWidth = width;
        mViewHeight = height;
        int gridAreaWidth = width - mHoursWidth;
        mCellWidth = (gridAreaWidth - (mNumDays * DAY_GAP)) / mNumDays;

        remeasure(width, height);
    }
}
