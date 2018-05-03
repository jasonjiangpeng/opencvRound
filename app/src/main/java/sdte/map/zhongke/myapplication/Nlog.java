package sdte.map.zhongke.myapplication;

import android.util.Log;

public class Nlog
{
  static String tag = "输出结果----------------------:";
  
  public static void show(Object paramObject)
  {
    Log.e(tag, paramObject.toString());
  }
  
  public static void show(Object... paramVarArgs)
  {
    int i = 0;
    StringBuilder  stringBuilder=new StringBuilder();
    while (i < paramVarArgs.length)
    {

      stringBuilder.append(paramVarArgs[i].toString());

     i++;
    }
    Log.e(tag, stringBuilder.toString());
  }
}
