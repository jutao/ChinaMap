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
import android.util.Log;
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
    private RectF totalRect;
    private float scale = 1.0f;

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

        loadThread.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (itemList == null) {
            return;
        }
        canvas.save();
        Log.d("TTTTT",scale+"");
        canvas.scale(scale, scale);
        for (ProvinceItem provinceItem : itemList) {
            provinceItem.drawItem(canvas, paint, false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取当前控件宽高值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //map 的宽度和高度
        if (totalRect != null) {
            double mapWidth = totalRect.width();
            scale = (float) (width / mapWidth);
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
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
                    Path path = PathParser.createPathFromPathData(pathData);
                    ProvinceItem proviceItem = new ProvinceItem(path);
                    list.add(proviceItem);
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
