package sdte.map.zhongke.myapplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;
import java.util.List;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class MainActivity
  extends AppCompatActivity
  implements Callback, PreviewCallback
{
  private byte[] bytes;
  private Camera camera = null;
  private DrawPoint drawPoint;
  private int height;
  private boolean isRun = true;
  SurfaceView javaCameraView;
  volatile float parameter1 = 0.5F;
  volatile int parameter2 = 10;
  int[] screen;
  private byte[] sdata;
  private SeekBar seek1;
  private SeekBar seek2;
  private int weight;
  
  static
  {

    System.loadLibrary("native-lib");
  }
  
  private void initUi()
  {
    this.drawPoint = ((DrawPoint)findViewById(R.id.drawpoint));
    this.javaCameraView = ((SurfaceView)findViewById(R.id.surfaceview));
    this.seek1 = ((SeekBar)findViewById(R.id.seekbar1));
    this.seek2 = ((SeekBar)findViewById(R.id.seekbar2));
    this.seek1.setProgress((int)(this.parameter1 * 99.0F));
    this.seek2.setProgress(this.parameter2);
    this.seek1.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        parameter1 = (0.01F * paramAnonymousInt);
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
    });
    this.seek2.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        parameter1 = (paramAnonymousInt / 2 + 10);
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
    });
    this.javaCameraView.getHolder().addCallback(this);
  }
  
  public void closeCamera()
  {

      if (this.camera != null)
      {
        this.camera.stopPreview();
        this.camera.setPreviewCallback(null);
        this.camera.release();
      }
      this.camera = null;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    NUtils.requestCamera(this);
    OpenCVLoader.initDebug();
    setContentView(R.layout.activity_main);
    initUi();
  }
  private final  int DETETIONTIME=250;
  public void detectionRound(){
    long l1 = System.currentTimeMillis();
    Mat localMat1 = new Mat(height + height / 2, weight, CvType.CV_8UC1);
    localMat1.put(0, 0, sdata);
    Object localObject = new Mat();
    Imgproc.cvtColor(localMat1, (Mat)localObject, Imgproc.COLOR_YUV2RGB_NV21, 3);
    Core.transpose((Mat)localObject, (Mat)localObject);
    Core.flip((Mat)localObject, (Mat)localObject, 2);
    Rect localRect = drawPoint.getRect();
    Mat localMat2 = ((Mat)localObject).submat(localRect);
    localMat1 = ((Mat)localObject).submat(localRect);
    int[] value = JniUtils.getPoint2(localMat2.getNativeObjAddr(), localMat1.width() );
    drawPoint.setData(value);
    MainActivity.this.drawPoint.postInvalidate();
    long l2 = System.currentTimeMillis()-l1;
    Nlog.show("time" + l2);
    if (l2<DETETIONTIME){
      SystemClock.sleep(DETETIONTIME-l2);
    }
    isRun = true;
  }
  public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
  {
    this.sdata = ((byte[])paramArrayOfByte.clone());
    if (this.isRun)
    {
      this.isRun = false;
      new Thread()
      {
        public void run()
        {
           detectionRound();
        }
      }.start();
    }
    paramCamera.addCallbackBuffer(this.bytes);
  }
    /*打开摄像头*/
  public void openCamera(SurfaceHolder paramSurfaceHolder)
  {
    this.screen = NUtils.getScreenWh(this);
    this.camera = Camera.open(0);
    Parameters localParameters = this.camera.getParameters();
    List localList = localParameters.getSupportedPreviewSizes();
    int j = 99999;
    int k = 0;

    int i = 0;
    while (i < localList.size())
    {
      int n = ((Size)localList.get(i)).width;
      int m = ((Size)localList.get(i)).height;
      n -= this.screen[1];
      m -= this.screen[0];
      n = n * n + m * m;
      m = j;
      if (n < j)
      {
        m = n;
        k = i;
      }
      i += 1;
      j = m;
    }
    localParameters.setPreviewSize(((Size)localList.get(k)).width, ((Size)localList.get(k)).height);
    this.camera.setParameters(localParameters);
    this.weight = this.camera.getParameters().getPreviewSize().width;
    this.height = this.camera.getParameters().getPreviewSize().height;
   float  x=1.0f*screen[0]/height;
   float  y=1.0f*screen[1]/weight;
    drawPoint.setScalex(x);
    drawPoint.setScaley(y);
    i = this.camera.getParameters().getPreviewFormat();
    Nlog.show(i + "格式");
    i = this.weight * this.height * 3 / 2;
    try
    {
      this.camera.setPreviewDisplay(paramSurfaceHolder);
      this.bytes = new byte[i];
      this.camera.setDisplayOrientation(90);
      this.camera.addCallbackBuffer(this.bytes);
      this.camera.setPreviewCallback(this);
      this.camera.startPreview();
      return;
    }
    catch (IOException ee)
    {
      ee.printStackTrace();
    }
  }
  
  public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
  {
    openCamera(paramSurfaceHolder);
    this.javaCameraView.setOnClickListener(new ClickVideoListener());
  }
  
  public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
  {
    closeCamera();
  }
  
  class ClickVideoListener
    implements OnClickListener
  {
    ClickVideoListener() {}
    
    public void onClick(View paramView)
    {
      if (MainActivity.this.camera != null) {
        MainActivity.this.camera.autoFocus(new AutoFocusCallback()
        {
          public void onAutoFocus(boolean paramAnonymousBoolean, Camera paramAnonymousCamera)
          {
            if (paramAnonymousBoolean)
            {
              return;
            }
            paramAnonymousCamera.autoFocus(this);
          }
        });
      }
    }
  }
}
