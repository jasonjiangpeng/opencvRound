package sdte.map.zhongke.myapplication;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class NUtils
{
  public static int[] getScreenWh(Activity paramActivity)
  {
   WindowManager manager = paramActivity.getWindowManager();
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    manager.getDefaultDisplay().getMetrics(localDisplayMetrics);
    return new int[] { localDisplayMetrics.widthPixels, localDisplayMetrics.heightPixels };
  }
  
  public static void requestCamera(Activity paramActivity)
  {
    ActivityCompat.requestPermissions(paramActivity, new String[] { "android.permission.CAMERA" }, 20);
  }
  
  public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
  {
    byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
    // 旋转Y
    int i = 0;
    for (int x = 0; x < imageWidth; x++) {
      for (int y = imageHeight - 1; y >= 0; y--) {
        yuv[i] = data[y * imageWidth + x];
        i++;
      }


    }
    // 旋转U和V
    i = imageWidth * imageHeight * 3 / 2 - 1;
    for (int x = imageWidth - 1; x > 0; x = x - 2) {
      for (int y = 0; y < imageHeight / 2; y++) {
        yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
        i--;
        yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                + (x - 1)];
        i--;
      }
    }

    return yuv;
  }
}
