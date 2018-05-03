#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <iostream>
#include <string>
#define   TAG  "输出结果"
#define   LOGE(x)  __android_log_print(ANDROID_LOG_ERROR, TAG, x)
using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jstring
JNICALL
Java_sdte_map_zhongke_myapplication_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
double centerPoints(vector<Point>contour)
{
    double factor = (contourArea(contour) * 4 * CV_PI) /(pow(arcLength(contour, true), 2));
    return factor;
}


extern "C"
JNIEXPORT jintArray JNICALL
Java_sdte_map_zhongke_myapplication_JniUtils_getPoint2(JNIEnv *env, jclass type, jlong mat,
                                                       jint w) {
    vector<double>Ritos;
    Mat* imgSrc ;
    Mat Temp;
   imgSrc=(Mat*)mat;
    Temp = (*imgSrc).clone();
    //imgSrc =imread("3.jpg");
int  minsize=w/20;
    Mat gray ;
    //转换为灰度图
    cvtColor(*imgSrc, gray, CV_RGB2GRAY);
    //降噪
    //blur(gray, gray, Size(3,3));
    //运行Canny算子，3为threshold1，9为threshold2
    Mat edge;
    blur(gray,gray,Size(3,3));
   // Canny(gray, edge, 30, 120,3);
    Canny(gray, edge, 20, 60,3);
    Mat element = getStructuringElement(MORPH_RECT, Size(5, 5));
   morphologyEx(edge, edge, MORPH_CLOSE, element);
    std::vector< std::vector< cv::Point> > contours;
    cv::findContours(
            edge,
            contours,
            cv::noArray(),
            CV_RETR_TREE,
            CV_CHAIN_APPROX_NONE
    );


    Ritos.clear();
    vector<vector<Point> >contoursFinal;
    for (unsigned int i = 0; i < contours.size(); ++i)
    {
        double circleRito = centerPoints(contours[i]);
        if( circleRito >= 0.7)
        {
            contoursFinal.push_back( contours[i] );
        }
    }

    vector<vector<Point> >::iterator itc= contoursFinal.begin();
    while (itc!=contoursFinal.end())
    {
        if( itc->size()<=25)
        {
            itc= contoursFinal.erase(itc);
        }
        else
        {
            ++itc;
        }
    }

    //计算矩
    vector<Moments>mu(contoursFinal.size());
    for (unsigned int i = 0; i < contoursFinal.size(); i++)
    {
        mu[i] = moments(contoursFinal[i], false);
    }
    //计算矩中心
    vector<Point2f>mc(contoursFinal.size());
    for (unsigned int i = 0; i < contoursFinal.size(); i++)
    {
        mc[i] = Point2f(static_cast<float>(mu[i].m10 / mu[i].m00), static_cast<float>(mu[i].m01 / mu[i].m00));
    }
int  size=    contoursFinal.size();
    if(size==0){
        return NULL;
    }

    int  avs=0;
    int  MaxId=-1;
    for (unsigned int i = 0; i< contoursFinal.size(); ++i)
    {
      int  raduis=  boundingRect(contoursFinal.at(i)).width/2;
        if (raduis<minsize){
            continue;
        }
         if (raduis>avs){
             avs=raduis;
             MaxId=i;
         }
    }
    if (MaxId<0){
        return NULL;
    }
    jintArray array=   env->NewIntArray(6);
    jint* va= env->GetIntArrayElements(array, false);
    va[0]= avs;
    va[1]=mc[MaxId].x;
    va[2] =mc[MaxId].y;
  //  Scalar vec3b= getScale(Temp,mc[MaxId],avs);
    va[3]=Temp.at<Vec3b>(mc[MaxId])[0];
    va[4]=Temp.at<Vec3b>(mc[MaxId])[1];
    va[5]=Temp.at<Vec3b>(mc[MaxId])[2];
  /*  va[4]=vec3b[1];
    va[5]=vec3b[2];*/

    env->SetIntArrayRegion(array,0,6,va);
    return array ;
}
struct   Nrect
{
    Point  XSmallYSmall;
    Point  XBigYSmall;
    Point  XSmallYbig;
    Point  XbigYbig;
}nrect;
Nrect  getNrect(vector<Point> points) {
    Nrect  nrect;
    int  min = 0;
    int  max = 999;
    Point  xsys(max, max);
    Point  xbys(min, max);
    Point  xsyb(max, min);
    Point  xbyb(min, min);
    for (size_t i = 0; i < points.size(); i++)
    {
        Point  po = points[i];
        if (po.x <=xsys.x&&po.y<=xsys.y) {
            xsys.x = po.x;
            xsys.y = po.y;

        }
        if (po.x <= xsyb.x&&po.y >=xsyb.y) {
            xsyb.x = po.x;
            xsyb.y = po.y;

        }
        if (po.x >= xbys.x&&po.y <= xbys.y) {
            xbys.x = po.x;
            xbys.y = po.y;

        }
        if (po.x >= xbyb.x&&po.y >xbyb.y) {
            xbyb.x = po.x;
            xbyb.y = po.y;

        }
    }

    nrect.XSmallYSmall = xsys;
    nrect.XBigYSmall = xbys;
    nrect.XSmallYbig = xsyb;
    nrect.XbigYbig = xbyb;
    return  nrect;
}
// TODO
extern "C"
JNIEXPORT jintArray JNICALL
Java_sdte_map_zhongke_myapplication_JniUtils_getNrectPoint2(JNIEnv *env, jclass type, jlong mat,
                                                            jint w, jint x) {
    vector<double>Ritos;
    Mat* imgSrc ;
    Mat Temp;
    imgSrc=(Mat*)mat;
    Temp = (*imgSrc).clone();
    //imgSrc =imread("3.jpg");
    int  minsize=w/20;
    Mat gray ;
    //转换为灰度图
    cvtColor(*imgSrc, gray, CV_RGB2GRAY);
    //降噪
    //blur(gray, gray, Size(3,3));
    //运行Canny算子，3为threshold1，9为threshold2
    Mat edge;
    blur(gray,gray,Size(3,3));
    // Canny(gray, edge, 30, 120,3);
    Canny(gray, edge, 20, 60,3);
    Mat element = getStructuringElement(MORPH_RECT, Size(5, 5));
    morphologyEx(edge, edge, MORPH_CLOSE, element);
    std::vector< std::vector< cv::Point> > contours;
    cv::findContours(
            edge,
            contours,
            cv::noArray(),
            CV_RETR_TREE,
            CV_CHAIN_APPROX_NONE
    );
    Ritos.clear();
    vector<vector<Point> >contoursFinal;
    for (unsigned int i = 0; i < contours.size(); ++i)
    {
      if (contours[i].size()<20){
          continue;
      }
        Nrect  nrect = getNrect(contours[i]);
        int  w1 = nrect.XBigYSmall.x - nrect.XSmallYSmall.x;
        int  w2 = nrect.XbigYbig.x - nrect.XBigYSmall.x;
        int h1 = nrect.XSmallYbig.y - nrect.XSmallYSmall.y;
        int h2 = nrect.XbigYbig.y - nrect.XBigYSmall.y;
        float  a1 =1.0f* w1 / w2;
        float  a2 = 1.0f* h1 / h2;
        float  a3 = 1.0f*w1/h1;

        if (a1 > 0.8&&a2 > 0.8&&a3 > 0.8) {
            contoursFinal.push_back(contours[i]);
        }
     /*   double circleRito = centerPoints(contours[i]);
        if( circleRito >= 0.7)
        {

            contoursFinal.push_back( contours[i] );
        }*/
    }

    vector<vector<Point> >::iterator itc= contoursFinal.begin();
    while (itc!=contoursFinal.end())
    {
        if( itc->size()<=25)
        {
            itc= contoursFinal.erase(itc);
        }
        else
        {
            ++itc;
        }
    }

    //计算矩
    vector<Moments>mu(contoursFinal.size());
    for (unsigned int i = 0; i < contoursFinal.size(); i++)
    {
        mu[i] = moments(contoursFinal[i], false);
    }
    //计算矩中心
    vector<Point2f>mc(contoursFinal.size());
    for (unsigned int i = 0; i < contoursFinal.size(); i++)
    {
        mc[i] = Point2f(static_cast<float>(mu[i].m10 / mu[i].m00), static_cast<float>(mu[i].m01 / mu[i].m00));
    }
    int  size=    contoursFinal.size();
    if(size==0){
        return NULL;
    }
    int  avs=0;
    int  MaxId=-1;
    for (unsigned int i = 0; i< contoursFinal.size(); ++i)
    {
        int  raduis=  boundingRect(contoursFinal.at(i)).width/2;
        if (raduis<minsize){
            continue;
        }
        if (raduis>avs){
            avs=raduis;
            MaxId=i;
        }
    }
    if (MaxId<0){
        return NULL;
    }
    jintArray array=   env->NewIntArray(6);
    jint* va= env->GetIntArrayElements(array, false);
    va[0]= avs;
    va[1]=mc[MaxId].x;
    va[2] =mc[MaxId].y;
    va[3]=Temp.at<Vec3b>(mc[MaxId])[0];
    va[4]=Temp.at<Vec3b>(mc[MaxId])[1];
    va[5]=Temp.at<Vec3b>(mc[MaxId])[2];


    env->SetIntArrayRegion(array,0,6,va);
    return  array;


}