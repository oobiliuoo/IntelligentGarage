#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "QDebug"

typedef pair<int, double> PAIR;
struct CmpByValue {
  bool operator()(const PAIR& lhs, const PAIR& rhs) {
    return lhs.second > rhs.second;
  }
};


MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    // 读取图片
    Mat img = imread("../pic/carnum3.jpg");
    if(img.empty()){
        qDebug()<<"img can not open !";
    }
    if (img.cols != 640)
            cv::resize(img, img, Size(640, 640 * img.rows / img.cols));
    this->orgImg = img.clone();

    pictureProcessing(img);
    licensePlateExtraction(img);

}



void MainWindow::pictureProcessing(Mat &img){
    // 1 车牌预处理--------------------------------------------------
    // 1 彩色图转灰度图
    cvtColor(img,img,COLOR_BGR2GRAY);
    // 2 高斯滤波，中值滤波
    // 2.1 高斯滤波
    GaussianBlur(img,img,Size(3,3),0,0,BORDER_DEFAULT);
    // 2.2 中值滤波
    medianBlur(img,img,5);

    // 2 边缘检测----------------------------------------------------
    Mat sobelImg;
    // 边缘化检测
    Sobel(img,sobelImg,CV_16S,1,0,3);
    convertScaleAbs(sobelImg,img);

    // 3 形态学处理---------------------------------------------------
    // 二值化操作
    threshold(img,img,0,255,THRESH_OTSU);
    // 闭运算:
    Mat element  = getStructuringElement(MORPH_RECT,Size(17,5));
    morphologyEx(img,img,MORPH_CLOSE,element);


  //  imshow("t",img);
}


void MainWindow::licensePlateExtraction(Mat &img){
    // 4 区域提取-----------------------------------------------------
    // 1.轮廓提取
    double area = 0;    //轮廓面积
    int height = 0;     //外接矩高
    int weight = 0;     //外接矩长
    float mLWR = 0;     //当前长宽比
    int mArea = 0;      //外接距面积
    float rectRate = 1; //矩形率

    std::vector<std::vector<Point>> contours;
    findContours(img,contours,RETR_TREE,CHAIN_APPROX_SIMPLE);
    std::map<int,double> contours_areas;
    for(int i = 0; i< contours.size(); i++){
        double area = contourArea(contours[i]);
        if(area > 800 && area < 50000)
            contours_areas.insert(make_pair(i,area));
    }
    vector<PAIR> index_area_vec(contours_areas.begin(), contours_areas.end());
    sort(index_area_vec.begin(), index_area_vec.end(), CmpByValue());

    double last_area = 0;  // 上次面积
    double current_area = 0; // 当前面积
    int final_index = 0; // 最终轮廓号
    for (int i = 0; i != index_area_vec.size(); ++i) {
        // 获取最小外接矩
        RotatedRect mRect = minAreaRect(contours[index_area_vec[i].first]);
        // 获取四个角点坐标
        // 获取长宽
        Size2f s = mRect.size;
        height = s.height;
        weight = s.width;
        // 获取长宽比
        if(height > weight)
            mLWR = (float) height / weight;
        else
            mLWR = (float) weight / height;
        // 矩形面积
        mArea = height * weight;
        area = index_area_vec[i].second;
        rectRate = area/mArea;

        if(checkLicenseFromShape(mLWR,rectRate,area) ){
            current_area = index_area_vec[i].second;
            // 优先选择面积接近6000的
            if(abs(current_area - 6000) > abs(last_area - 6000)){
                cout<<"out becease:---last_area---- "<<last_area<<" current_area"<<current_area<<endl;
                continue ;
            }
            last_area = current_area;
            final_index = index_area_vec[i].first;

            Point2f mPoints[4];// 左下，左上，右上，右下
            mRect.points(mPoints);
            Point points[4];
            for(int i=0;i<4;i++){
                points[i].x = (int) mPoints[i].x;
                points[i].y = (int) mPoints[i].y;
            }
            Mat temp2 = this->orgImg.clone();
            for (int i = 0; i < 4; i++)
                  line(temp2, mPoints[i], mPoints[(i + 1) % 4], Scalar(0, 0,255), 2);

            imshow("lpe",temp2);

        }


    }

    if(final_index != 0){
        // 区域提取
        RotatedRect fRect = minAreaRect(contours[final_index]);
        Mat licenceImg = this->orgImg.clone();
        licenceImg = regionalExtract(licenceImg,fRect);

        // 字符提取
        characterExtraction(licenceImg);
    }

}

bool MainWindow::checkLicenseFromShape(float mLWR, float rectRate, double area){

    const float LENGTH_WIDTH_RATIO = 3.142857; // 标准长宽比
    const float ERROR_RATE = 1.5;       // 浮动范围
    float errEara = 0.5;
    float minLWR = LENGTH_WIDTH_RATIO - ERROR_RATE + 0.8;  // 最小长宽比
    float maxLWR = LENGTH_WIDTH_RATIO + ERROR_RATE;  // 最大长宽比


    if(mLWR>minLWR && mLWR < maxLWR && rectRate > 1 - errEara && rectRate < 1 + errEara
            && area > 1500 && area<80000){
        return true;
    }

    return false;
}


Mat MainWindow::regionalExtract(Mat &img,  RotatedRect rRect){
    Mat licenceImg;
    // 掩码
    Mat mask = Mat::zeros(img.size(),CV_8UC3);
    // 标记轮廓区域
    Point2f mPoints[4];
    rRect.points(mPoints);
    Point points[4];
    for(int i=0;i<4;i++){
        points[i].x = (int) mPoints[i].x;
        points[i].y = (int) mPoints[i].y;
    }

    float ff = 90;
    std::cout<<"angle"<<rRect.angle<<std::endl;
    // 判断是否需要旋转
    if(abs(abs(rRect.angle) - ff) < 5 ){
        fillConvexPoly(mask,points,4,Scalar(255,255,255),LINE_AA);
        Mat element  = getStructuringElement(MORPH_RECT,Size(17,17));
        dilate(mask,mask,element);
        Mat t1 ;
        img.copyTo(t1,mask);
        // 始终保持宽 > 高
        Size rect_size = rRect.size;
        rect_size.width += 10;
        rect_size.height += 10;
        if (rect_size.width < rect_size.height) {
            swap(rect_size.width, rect_size.height);
        }
        std::cout<<"sub"<<std::endl;
        getRectSubPix(t1,rect_size,rRect.center,licenceImg);
    }else{
        fillConvexPoly(mask,points,4,Scalar(255,255,255),LINE_AA);
        Mat t1 ;
        img.copyTo(t1,mask);
        std::cout<<"rotate"<<std::endl;
        licenceImg =  rotate_demo(t1,rRect);
    }

    return licenceImg;
}

Mat MainWindow::rotate_demo(Mat &image,RotatedRect rRect){

    Mat dst,M;
    int w = image.cols;
    int h = image.rows;
    // 获取旋转2*3矩阵 arg1:中心坐标 arg2:旋转角度 arg3:
    M = getRotationMatrix2D(Point2f(w/2,h/2),rRect.angle,1.0);

    // 计算旋转后大小
    double cos = abs(M.at<double>(0,0));
    double sin = abs(M.at<double>(0,1));
    int nw = cos*w + sin*h;
    int nh = sin*w + cos*h;
    // 修改矩阵中心
    M.at<double>(0,2) += (nw/2 - w/2);
    M.at<double>(1,2) += (nh/2 - h/2);

    // 旋转函数 原图，目标图，旋转矩阵，大小，插值方式，边缘检测，背景色
    warpAffine(image,dst,M,Size(nw,nh),INTER_LINEAR,0,Scalar(0,0,0));
    // imshow("warp",dst);

    // 图像切割
    Mat licenceImg;
    // 始终保持宽 > 高
    Size rect_size = rRect.size;
    rect_size.width += 5;
    rect_size.height += 5;
    if (rect_size.width < rect_size.height) {
        swap(rect_size.width, rect_size.height);
    }

    Point2f center ;
    center.x =h * sin + (rRect.center.x - rRect.center.y * sin / cos) * cos;
    center.y = rRect.center.y / cos + (rRect.center.x - rRect.center.y * sin / cos) * sin;

    getRectSubPix(dst, rect_size ,center, licenceImg);

    return licenceImg;

}


void MainWindow::characterExtraction(Mat img){
    characterPicProcessing(img);
    characterDivision(img);
    characterChineseDiscern(this->character[0]);

    for(int i=1;i<7;i++){
         characterDiscern(this->character[i]);
    }


}
void MainWindow::characterPicProcessing(Mat &img){

    //-------------------图像处理----------------------
    Size s(144,33);
    cv::resize(img,img,s,0,0,INTER_CUBIC);
    Mat dst;
    cvtColor(img,dst,COLOR_BGR2GRAY);
    threshold(dst,dst,0,255,THRESH_OTSU);
    Mat element = getStructuringElement(MORPH_RECT,Size(2,1));
    erode(dst,dst,element);

    img = dst.clone();
}

void MainWindow::characterDivision(Mat &img){

    Mat dst = img.clone();
    //-----------------字符分割-------------
    // 获取宽度
    int width = dst.cols;
    // 获取高度
    int height = dst.rows;


    int x1[10];  // 左边界坐标
    int x2[10];  // 右边界坐标
    int pos = 0; // 字符个数

    for(int col=4;col<width-5;col++){
        int white_num = 0;
        for(int row=4;row<30;row++){
            int px = dst.at<uchar>(row,col);
            if(px == 255)
                white_num ++ ;
        }
        if(pos < 3 && white_num > 5){
            white_num = 0;
            int flog_num = 0;
            int ncol = col + 12;
            int col2 = col;
            for(col2;col2<=ncol;col2++){
                for(int row=6;row<33;row++){
                    int px = dst.at<uchar>(row,col2);
                    if(px == 255){
                        white_num++;
                        if(col2 == ncol)
                            flog_num++;
                    }

                }
            }
            if(white_num > 30 && flog_num < 6){
                x1[pos] = col - 3;
                x2[pos] = col2;
                pos++;
                col = ncol ;
            }
        }

        if(pos >=3){
            while(pos<7){
                x1[pos] = col;
                col = col + 15;
                for(int t_col = col; t_col<col + 6;t_col++){
                    white_num = 0;
                    for(int row=4;row<30;row++){
                        int px = dst.at<uchar>(row,t_col);
                        if(px == 255)
                            white_num ++ ;
                    }
                    if(white_num < 5 ){
                        col = t_col+1;
                        break;
                    }
                }
                x2[pos] = col;
                pos++;
            }
            break;
        }

    }

  Mat num[10];
  for(int x=0;x<pos;x++){
   // cout<<"x: "<<x<<" x1: "<<x1[x]<<" x2:"<<x2[x]<<endl;
    float width2 = x2[x] - x1[x];
    getRectSubPix(dst,Size(width2,25),Point2f(x1[x]+width2/2,16),num[x],-1);
    //threshold(num[x],num[x],0,255,THRESH_OTSU);
    char str[3];
    sprintf(str,"%d",x);
    std::string name(str);
    imshow(name,num[x]);
    this->character[x] = num[x].clone();
  }

}

void MainWindow::characterChineseDiscern(Mat &img){
    QString cnum[200] = {"0","1","2","3","4","5","6","7","8","9"
                  ,"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"
                  ,"藏","川","鄂","甘","赣","桂","贵","黑","沪","吉","冀","津","晋","京","辽","鲁","蒙","闽","宁","青"
                  ,"琼","陕","苏","皖","湘","新","渝","豫","粤","云","浙"};

    //读取一张手写数字图片(28,28)
    Mat image = img.clone();
    Mat img_show = image.clone();
    //更换数据类型有uchar->float32
    image.convertTo(image, CV_32F);
    cv::resize(image,image,Size(20,20),0,0,INTER_AREA);
    //归一化
    image = image / 255.0;
    image = image.reshape(1, 1);

    //加载ann模型
    cv::Ptr<cv::ml::ANN_MLP> ann= cv::ml::StatModel::load<cv::ml::ANN_MLP>("../mnist_ann3_1.xml");
    //预测图片
    Mat pre_out;
    float ret = ann->predict(image,pre_out);
   // cout<<"ret"<<ret;
    double maxVal = 0;
    imshow("test",pre_out);
    cv::Point maxPoint;
    cv::minMaxLoc(pre_out, NULL, &maxVal, NULL, &maxPoint);
    int max_index = maxPoint.x;
    cout << "图像上的数字为：" <<  max_index << " 置信度为：" << maxVal << endl;
    qDebug()<<cnum[max_index];

}

void MainWindow::characterDiscern(Mat &img){

    QString cnum[200] = {"0","1","2","3","4","5","6","7","8","9"
                  ,"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"
                  ,"藏","川","鄂","甘","赣","桂","贵","黑","沪","吉","冀","津","晋","京","辽","鲁","蒙","闽","宁","青"
                  ,"琼","陕","苏","皖","湘","新","渝","豫","粤","云","浙"};

    //读取一张手写数字图片(28,28)
    Mat image = img.clone();
    Mat img_show = image.clone();
    //更换数据类型有uchar->float32
    image.convertTo(image, CV_32F);
    cv::resize(image,image,Size(20,20),0,0,INTER_AREA);

    //归一化
    image = image / 255.0;
    image = image.reshape(1, 1);

    //加载ann模型
    cv::Ptr<cv::ml::ANN_MLP> ann= cv::ml::StatModel::load<cv::ml::ANN_MLP>("../mnist_ann3_num.xml");
    //预测图片
    Mat pre_out;
    float ret = ann->predict(image,pre_out);
   // cout<<"ret"<<ret;
    double maxVal = 0;
    cv::Point maxPoint;
    cv::minMaxLoc(pre_out, NULL, &maxVal, NULL, &maxPoint);
    int max_index = maxPoint.x;
    cout << "图像上的数字为：" <<  max_index << " 置信度为：" << maxVal << endl;
    qDebug()<<cnum[max_index];
}

MainWindow::~MainWindow()
{
    delete ui;
}
