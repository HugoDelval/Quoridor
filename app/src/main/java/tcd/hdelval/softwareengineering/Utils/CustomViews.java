package tcd.hdelval.softwareengineering.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by hdelval on 09/10/16.
 */

public class CustomViews {

    public static class ImageDragShadowBuilder extends View.DragShadowBuilder {
        private Drawable shadow;

        private ImageDragShadowBuilder() {
            super();
        }

        public static View.DragShadowBuilder fromResource(Context context, Drawable drawable, int barrierWidth, int barrierHeight) {
            ImageDragShadowBuilder builder = new ImageDragShadowBuilder();

            if (drawable == null) {
                throw new NullPointerException("Drawable is null");
            }

            Bitmap b = ((BitmapDrawable)drawable).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, barrierWidth, barrierHeight, false);
            builder.shadow = new BitmapDrawable(context.getResources(), bitmapResized);

            builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

            return builder;
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            shadowSize.x = shadow.getMinimumWidth();
            shadowSize.y = shadow.getMinimumHeight();

            shadowTouchPoint.x = shadowSize.x / 2;
            shadowTouchPoint.y = shadowSize.y / 2;
        }
    }
}
