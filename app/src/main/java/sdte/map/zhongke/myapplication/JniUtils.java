package sdte.map.zhongke.myapplication;

/**
 * Created by LostboyJason on 2018/5/2.
 */

public class JniUtils  {

    /*检测圆*/
    public native static int[] getPoint2(long  mat,int  w);
    /*检测正方形*/
    public native static int[] getNrectPoint2(long  mat,int  w,int x);

}
