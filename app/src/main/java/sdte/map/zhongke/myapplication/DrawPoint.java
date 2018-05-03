package sdte.map.zhongke.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class DrawPoint
  extends View
{
  private Bitmap bitmap;
  private int[] data;
  private int height;
  private Paint paint;
  private Paint paintRect;
  private int weight;
  
  public DrawPoint(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DrawPoint(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initPaint();
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.weight <= 0)
    {
      this.weight = getWidth();
      this.height = getHeight();

    }
   if (bitmap!=null){
      paramCanvas.drawBitmap(bitmap,0,0,null);
   }
    int i = Math.min(this.weight / 4, this.height / 4);
    paramCanvas.drawRect(new android.graphics.Rect(i, i * 2, i * 3, i * 4), this.paintRect);

    if (this.data != null)
    {

       /*检测区域画图*/
     paramCanvas.drawCircle(this.data[1]*scalex + i, i * 2 + this.data[2]*scaley, this.data[0]*scalex, this.paint);
      /*左上方画圆*/
      paramCanvas.drawCircle(this.data[0]*scalex + 20, this.data[0]*scaley + 30, this.data[0]*scalex, getPcirsd(this.data[3], this.data[4], this.data[5]));
    }
  }
  
  public Bitmap getBitmap()
  {
    return this.bitmap;
  }
  
  public int[] getData()
  {
    return this.data;
  }
  
  public Paint getPcirsd(int paramInt1, int paramInt2, int paramInt3)
  {
    Paint localPaint = new Paint();
    localPaint.setColor(Color.rgb(paramInt1, paramInt2, paramInt3));
    localPaint.setStyle(Style.FILL);
    return localPaint;
  }
  
  public org.opencv.core.Rect getRect()
  {
    int i = Math.min(this.weight / 4, this.height / 4);
    return new org.opencv.core.Rect(i, i * 2, i * 2, i * 2);
  }
  
  public void initPaint()
  {
    this.paint = new Paint();
    this.paint.setColor(-65536);
    this.paint.setStyle(Style.STROKE);
    this.paint.setStrokeWidth(10.0F);
    initPaintRect();
  }
  
  public void initPaintRect()
  {
    this.paintRect = new Paint();
    this.paintRect.setColor(-16777216);
    this.paintRect.setStyle(Style.STROKE);
    this.paintRect.setStrokeWidth(5.0F);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    this.bitmap = paramBitmap;
  }
  
  public void setData(int[] paramArrayOfInt)
  {
    this.data = paramArrayOfInt;
  }
  private float  scalex=1;
  private float  scaley=1;

  public float getScalex() {
    return scalex;
  }

  public void setScalex(float scalex) {
    this.scalex = scalex;
  }

  public float getScaley() {
    return scaley;
  }

  public void setScaley(float scaley) {
    this.scaley = scaley;
  }
}
