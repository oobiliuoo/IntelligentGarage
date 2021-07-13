#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "opencv2/opencv.hpp"

using namespace std;
using namespace cv;

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    void pictureProcessing(Mat &img);
    void licensePlateExtraction(Mat &img);
    bool checkLicenseFromShape(float mLWR, float rectRate, double area);
    Mat regionalExtract(Mat &img, RotatedRect rRect);
    Mat rotate_demo(Mat &image,RotatedRect rRect);

    void characterExtraction(Mat img);
    void characterPicProcessing( Mat &img);
    void characterDivision( Mat& img);
    void characterDiscern(Mat &img);
    void characterChineseDiscern(Mat &img);

private:
    Ui::MainWindow *ui;
    Mat orgImg;
    Mat character[10];
};

#endif // MAINWINDOW_H
