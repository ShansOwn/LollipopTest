package com.shansown.android.lollipoptest.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class UIUtils {

    public static int getDeviceWidthInPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            result = size.x;
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            result = metrics.widthPixels;
        }
        return result;
    }

    public static int getDeviceHeightInPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            result = size.y;
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            result = metrics.heightPixels;
        }
        return result;
    }

    public static float dpToPx(Context context, int valueInPx) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueInPx, context.getResources().getDisplayMetrics());
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        int width = getDeviceWidthInPx(context);
        int height = getDeviceHeightInPx(context);

        view.measure(width, height);
        view.layout(0, 0, width, height);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }

    public static View findParentRecursively(View view, int targetId) {
        if (view.getId() == targetId) {
            return view;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findParentRecursively(parent, targetId);
    }
}
