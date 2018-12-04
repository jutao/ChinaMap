package chinamap.jt.msi.chinamap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 1::06:56
 */
public class MapView extends View {

    private Context context;
    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30ABE7, 0xFF47FF26, 0xFF778899, 0xFF556AFE};
    private List<ProvinceItem> itemList;
    private Paint paint;
    private Paint textPaint;
    private RectF totalRect;
    private float scale = 1.0f;
    float width = -1;
    private ProvinceItem selectItem = null;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70);
        textPaint.setColor(Color.BLACK);
        loadThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (itemList == null) {
            return;
        }
        canvas.save();
        if (totalRect != null && width != -1) {
            double mapWidth = totalRect.width();
            scale = (float) (width / mapWidth);
            if(selectItem!=null){
                canvas.drawText(selectItem.getProvinceName(),width/2,totalRect.bottom+500,textPaint);
            }

        }
        canvas.scale(scale, scale);
        for (ProvinceItem provinceItem : itemList) {
            provinceItem.drawItem(canvas, paint, provinceItem == selectItem);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        for (ProvinceItem item : itemList) {
            if (item.isTouch(x/scale, y/scale)) {
                selectItem = item;
                postInvalidate();
                break;
            }
        }
    }

    private Thread loadThread = new Thread() {
        @Override
        public void run() {
            InputStream inputStream = context.getResources().openRawResource(R.raw.china);
            List<ProvinceItem> list = new ArrayList<>();

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                Document doc = documentBuilder.parse(inputStream);
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");

                //中国地图的矩形
                float left = -1;
                float right = -1;
                float top = -1;
                float bottom = -1;

                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    String provinceName = element.getAttribute("android:name");
                    Path path = PathParser.createPathFromPathData(pathData);
                    ProvinceItem provinceItem = new ProvinceItem(path);
                    provinceItem.setProvinceName(provinceName);
                    list.add(provinceItem);
                    //获取宽高
                    RectF rect = new RectF();
                    path.computeBounds(rect, true);
                    left = left == -1 ? rect.left : Math.min(left, rect.left);
                    right = right == -1 ? rect.right : Math.max(right, rect.right);
                    top = top == -1 ? rect.top : Math.min(top, rect.top);
                    bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);

                    totalRect = new RectF(left, top, right, bottom);
                }
                itemList = list;
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (itemList == null) {
                return;
            }
            int totalNumber = itemList.size();
            for (int i = 0; i < totalNumber; i++) {
                int color = Color.WHITE;
                int flag = i % colorArray.length;
                color = colorArray[flag];
                itemList.get(i).setDrawColor(color);
            }
            postInvalidate();
        }
    };
}
