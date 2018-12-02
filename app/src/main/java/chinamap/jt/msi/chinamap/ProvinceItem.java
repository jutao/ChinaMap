package chinamap.jt.msi.chinamap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class ProvinceItem {
    private Path path;

    /**
     * 绘制颜色
     */
    private int drawColor;

    public ProvinceItem(Path path) {
        this.path = path;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    void drawItem(Canvas canvas, Paint paint, boolean isSelect) {
        //描边
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(8, 0, 0, 0xffffff);
        canvas.drawPath(path, paint);

        //填充
        paint.clearShadowLayer();
        paint.setColor(drawColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        canvas.drawPath(path, paint);
    }
}
