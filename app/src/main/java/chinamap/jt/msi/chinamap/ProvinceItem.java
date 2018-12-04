package chinamap.jt.msi.chinamap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.ColorInt;

public class ProvinceItem {

    private Path path;

    /**
     * 绘制颜色
     */
    private int drawColor;

    private String provinceName;
    private @ColorInt
    final int selectStrokeColor;
    private @ColorInt
    final int selectFILLColor;
    private final RectF helpRectF;
    private final Region helpRegion;

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public ProvinceItem(Path path) {
        this.path = path;
        selectStrokeColor = 0xFFFFFFFF;
        selectFILLColor = 0xFFFF5050;
        helpRectF = new RectF();
        helpRegion = new Region();
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    void drawItem(Canvas canvas, Paint paint, boolean isSelect) {
        if (isSelect) {
            //描边
            paint.setStrokeWidth(2);
            paint.setColor(selectStrokeColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, 0xffffff);
            canvas.drawPath(path, paint);

            //填充
            paint.clearShadowLayer();
            paint.setColor(selectFILLColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path, paint);

        } else {
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

    /**
     * 是否触摸
     *
     * @param x
     * @param y
     */
    public boolean isTouch(float x, float y) {
        if (path == null) {
            return false;
        }
        path.computeBounds(helpRectF, true);
        Region region = new Region((int) helpRectF.left, (int) helpRectF.top, (int) helpRectF.right,
                (int) helpRectF.bottom);
        helpRegion.setPath(path, region);
        return helpRegion.contains((int) x, (int) y);
    }
}
