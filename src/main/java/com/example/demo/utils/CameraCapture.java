package com.example.demo.utils;
import org.bytedeco.javacv.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import java.awt.image.BufferedImage;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgproc;

/**
 * 摄像头抓拍工具类
 * 用于通过摄像头采集图片
 */
public class CameraCapture {
    public static void main(String[] args) throws Exception {
        // 1. 让用户输入名字，支持中文、字母、数字、下划线
        String userName = JOptionPane.showInputDialog(null, "请输入你的名字（字母数字）:", "输入名字", JOptionPane.PLAIN_MESSAGE);
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "名字不能为空，程序退出。");
            return;
        }
        String userId = userName.trim(); // 直接用原始输入

        // 2. 在photos目录下创建同名文件夹
        File photosDir = new File("photos");
        if (!photosDir.exists()) photosDir.mkdir();
        File userDir = new File(photosDir, userName);
        if (!userDir.exists()) userDir.mkdir();

        // 3. 加载人脸检测分类器
        String classifierPath = "haarcascade_frontalface_alt.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
        if (faceDetector.empty()) {
            JOptionPane.showMessageDialog(null, "人脸检测分类器文件未找到: " + classifierPath);
            return;
        }

        // 4. 打开摄像头
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        CanvasFrame canvas = new CanvasFrame("摄像头 - 对准人脸，空格拍照，ESC退出", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Java2DFrameConverter converter = new Java2DFrameConverter();
        OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
        String lastSavedMsg = "";
        long lastSavedTime = 0;

        while (canvas.isVisible()) {
            org.bytedeco.javacv.Frame frame = grabber.grab();
            Mat mat = toMat.convert(frame);
            if (mat == null) continue;

            // 人脸检测
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(mat, faces);
            for (int i = 0; i < faces.size(); i++) {
                Rect face = faces.get(i);
                opencv_imgproc.rectangle(mat, face, new Scalar(0, 255, 0, 1));
            }

            // 用Java AWT绘制中文提示
            BufferedImage bufferedImage = converter.getBufferedImage(toMat.convert(mat));
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
            g2d.setColor(faces.size() > 0 ? new Color(0, 255, 0) : new Color(255, 0, 0));
            String statusText = faces.size() > 0 ? "人脸已检测" : "未检测到人脸";
            g2d.drawString(statusText, 30, 40);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            g2d.setColor(new Color(255,255,255));
            g2d.drawString("按空格拍照，ESC退出", 30, 80);
            // 拍摄成功提示
            if (!lastSavedMsg.isEmpty() && System.currentTimeMillis() - lastSavedTime < 500) {
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 20));
                g2d.setColor(new Color(255,255,0));
                g2d.drawString(lastSavedMsg, 30, 120);
            }
            g2d.dispose();
            org.bytedeco.javacv.Frame showFrame = converter.convert(bufferedImage);
            canvas.showImage(showFrame);

            KeyEvent keyEvent = canvas.waitKey(33);
            if (keyEvent != null) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (faces.size() > 0) {
                        // 拍照并保存到userId文件夹
                        String fileName = userName + "_" + System.currentTimeMillis() + ".jpg";
                        File saveFile = new File(userDir, fileName);
                        javax.imageio.ImageIO.write(converter.getBufferedImage(frame), "jpg", saveFile);
                        System.out.println("已保存照片：" + fileName);
                        System.out.println("照片保存路径：" + saveFile.getAbsolutePath());
                        lastSavedMsg = "已保存照片: " + fileName;
                        lastSavedTime = System.currentTimeMillis();
                    } else {
                        lastSavedMsg = "未检测到人脸，请对准摄像头重试。";
                        lastSavedTime = System.currentTimeMillis();
                    }
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    break;
                }
            }
        }
        canvas.dispose();
        grabber.stop();
        System.out.println("程序结束。");
    }
} 