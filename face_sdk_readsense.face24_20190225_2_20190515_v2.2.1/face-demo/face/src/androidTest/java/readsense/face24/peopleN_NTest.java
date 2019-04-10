package readsense.face24;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import readsense.face24.util.BitmapUtil;
import readsense.face24.util.FileUtil;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class peopleN_NTest {
    private static final String TAG = "FeatureTest";

    private YMFaceTrack faceTrack;
    private static final String db_path = "/sdcard/img/";
    private Context appContext;

    private Map<String, float[]> feature_map;//存储获取到的特征
    private int compare_count = 0;//统计比对总次数
    private int compare_wrong = 0;//统计比对错误总次数
    private int pic_count;//获取总图片数
    private int track_count;//获取特征时记录当前已经获取到特征的图片数

    String imgPath = "/sdcard/peopleN_NTest/img";//特征存储路径
    private String out_feature_path = "/sdcard/peopleN_NTest/feature.txt";//特征存储路径
    private String out_time_feature_path = "/sdcard/peopleN_NTest/time_feature.txt";//存储获取一张图片的时间、检测一张图片的时间、获取一个人脸特征的时间
    private String out_compare_rate_path = "/sdcard/peopleN_NTest/face_compare_rate.txt";//存储比对结果

    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();
        faceTrack = TestUtil.getFaceTrack(appContext, "", 75);
        feature_map = new HashMap<>();

        File file_feature = new File("/sdcard/peopleN_NTest/feature.txt");
        try {
            //判断文件是否存在
            if (file_feature.exists()) {
                //file_feature.delete();//文件如果存在删除这个文件
            } else {
            }
            file_feature = new File("/sdcard/peopleN_NTest");
            file_feature.mkdir();//先创建文件夹
            file_feature = new File("/sdcard/peopleN_NTest/feature.txt");//创建这个文件
            file_feature.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_time_feature = new File("/sdcard/peopleN_NTest/time_feature.txt");
        try {
            //判断文件是否存在
            if (file_time_feature.exists()) {
                file_time_feature.delete();//文件如果存在删除这个文件
            } else {
                file_time_feature = new File("/sdcard/peopleN_NTest");
                file_time_feature.mkdir();//先创建文件夹
                file_time_feature = new File("/sdcard/peopleN_NTest/time_feature.txt");//创建这个文件
                file_time_feature.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_face_compare_rate = new File("/sdcard/peopleN_NTest/face_compare_rate.txt");
        try {
            //判断文件是否存在
            if (file_face_compare_rate.exists()) {
                file_face_compare_rate.delete();//文件如果存在删除这个文件
            } else {
                file_face_compare_rate = new File("/sdcard/peopleN_NTest");
                file_face_compare_rate.mkdir();//先创建文件夹
                file_face_compare_rate = new File("/sdcard/peopleN_NTest/face_compare_rate.txt");//创建这个文件
                file_face_compare_rate.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testFeature() {
        getFeature(imgPath, true);
        readFeature(out_feature_path);
        peoplePeopleN_N(feature_map, 75);
    }

    @After
    public void releaseSource() {

    }

    /**
     * n:n人脸对比测试
     * 输出特征值文件、对比相似度超过阈值的（格式：img1 img2 confidence）
     */
    private void peoplePeopleN_N(Map<String, float[]> map, float confidence_face) {
        compare_count = 0;
        compare_wrong = 0;

        //根据特征值进行对比并存储比对结果
        Log.e(TAG, "start peoplePeopleCompareManyToMany!");
        float compare_result = 0.0f;
        StringBuilder stringBuilder_wrong = new StringBuilder();
        long totalComapreTime = 0;
        for (Map.Entry<String, float[]> str : map.entrySet()) {
            // 根据命名规则，例如3_0.jpg，判断是否是同一人
            String key = str.getKey();

            for (Map.Entry<String, float[]> str2 : map.entrySet()) {
                String key2 = str2.getKey();
                if (key.equals(key2)) {
                    // 是本人

                } else {
                    // 非本人
                    long compareTime = System.currentTimeMillis();
                    compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                    compareTime = System.currentTimeMillis() - compareTime;
                    totalComapreTime += compareTime;
                    if (compare_result >= confidence_face) {//对比结果认为是同一个人(识别错误)
                        stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                        compare_wrong++;
                    }
                    compare_count++;
                    if (compare_count % 10000 == 0) {
                        Log.e(TAG, "now compare  count: " + compare_count);
                        FileUtil.writeFile(out_compare_rate_path, stringBuilder_wrong.toString(), true);
                        stringBuilder_wrong.delete(0, stringBuilder_wrong.length());
                    }
                }
            }
        }
        stringBuilder_wrong.append("比较一对特征值的时间: " + totalComapreTime / (float) compare_count + "\n");
        FileUtil.writeFile(out_compare_rate_path, stringBuilder_wrong.toString(), true);
        FileUtil.writeFile(out_compare_rate_path, "confidence = " + confidence_face + ", " +
                "wrong count = " + compare_wrong + ", " +
                "total count = " + compare_count + ", " +
                "error rate = " + ((float) compare_wrong / (float) compare_count) * 100 + "%", true);
        Log.e(TAG, "confidence = " + confidence_face + "," + "finish peoplePeopleCompareManyToMany!");
    }

    /**
     * 获取特征
     * 存入feature_map 中
     *
     * @param path       图片文件夹所在路径
     * @param isTestFace 标识是用于人脸识别的特征，还是人证识别的特征。true表示用于人脸识别，false表示用于人证识别。
     */
    private void getFeature(String path, boolean isTestFace) {
        Log.e(TAG, "getFeature, start");
        if (null != feature_map && feature_map.size() > 0) {
            feature_map.clear();
        }

        File dir = new File(path);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                assertTrue(null != files && files.length > 0);
                Bitmap targetBitmap;
                List<YMFace> ymFaces;
                long totalGetBitmapTime = 0;
                long totalDetectTime = 0;
                long totalGetFeatureTime = 0;
                for (int i = 0; i < files.length; i++) {
                    if (i % 100 == 0) {
                        Log.e(TAG, "track count : " + files.length + " now : " + i);
                    }
                    // 根据图片路径获取图片检测人脸
                    StringBuffer save1 = new StringBuffer();
                    File currFile = files[i];
                    String currName = files[i].getName();
                    if (currName.contains(".jpg") || currName.contains(".png") || currName.contains(".jpeg") || currName.contains(".JPG")) {
                        pic_count++;
                        long getBitmapTime = System.currentTimeMillis();
                        targetBitmap = BitmapUtil.decodeScaleImage(currFile.getAbsolutePath(), 1000, 1000);
                        getBitmapTime = System.currentTimeMillis() - getBitmapTime;
                        totalGetBitmapTime += getBitmapTime;

                        long detectTime = System.currentTimeMillis();
                        ymFaces = faceTrack.detectMultiBitmap(targetBitmap);
                        detectTime = System.currentTimeMillis() - detectTime;
                        totalDetectTime += detectTime;
                        targetBitmap.recycle();
                        // 检测到人脸获取特征值并存储在feature_map中
                        if (ymFaces != null && ymFaces.size() != 0) {

                            int maxIndex = 0;
                            for (int j = 1; j < ymFaces.size(); j++) {
                                if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(j).getRect()[2]) {
                                    maxIndex = j;
                                }
                            }

                            long getFeatureTime = System.currentTimeMillis();
                            float[] faceFeature = isTestFace ? faceTrack.getFaceFeature(maxIndex) : faceTrack.getFaceFeatureCard(maxIndex);
                            getFeatureTime = System.currentTimeMillis() - getFeatureTime;
                            totalGetFeatureTime += getFeatureTime;

                            feature_map.put(currName, faceFeature);
                            track_count++;
                            if (track_count % 100 == 0) {
                                Log.e(TAG, "当前获取特征数为 : " + track_count);
                                if (track_count % 500 == 0) {
                                    saveFeature(feature_map);
                                    feature_map.clear();
                                }
                            }
                        } else {
                            Log.e(TAG, "no face in : " + currFile.getName());
                        }
                    }
                }
                saveFeature(feature_map);

                StringBuilder sb = new StringBuilder();
                sb.append("获取一张图片的时间: " + totalGetBitmapTime / (float) pic_count + "\n");
                sb.append("检测一张图片的时间: " + totalDetectTime / (float) pic_count + "\n");
                sb.append("获取一个人脸特征的时间: " + totalGetFeatureTime / (float) track_count + "\n");
                FileUtil.writeFile(out_time_feature_path, sb.toString());
                Log.e(TAG, "getFeature, finished");
            }
            Log.e(TAG, path + "不是目录");
        }
        Log.e(TAG, path + "路径不存在");
    }

    /**
     * 存储特征到txt文件
     * map ： key为String 图片名，value 为float[] 特征值
     *
     * @param map
     */
    private void saveFeature(Map<String, float[]> map) {
        Log.e(TAG, "start saveFeature!");
        StringBuffer stringBuffer = new StringBuffer();
        for (String key : map.keySet()) {
            stringBuffer.append(key);
            stringBuffer.append(" ");
            for (int i = 0; i < map.get(key).length; i++) {
                stringBuffer.append(map.get(key)[i]).append(" ");
            }
            stringBuffer.append("\n");

        }
        FileUtil.writeFile(out_feature_path, stringBuffer.toString(), true);
        Log.e(TAG, "finish saveFeature");

    }

    /**
     * 读取特征
     * 存入feature_map 中
     *
     * @param path 特征值所在文件路径
     */
    private void readFeature(String path) {
        Log.e(TAG, "*********************** start read feature *************************");

        File file = new File(path);
        if (!file.exists()) return;
        if (!file.isFile()) return;

        feature_map.clear();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line_feature = null;
            String[] str;
            String pic_name;
            float[] feature;
            while ((line_feature = bufferedReader.readLine()) != null) {

                str = line_feature.trim().split(" ");
                pic_name = str[0];
                feature = new float[(str.length - 1)];
                for (int i = 1; i < str.length - 1; i++) {
                    feature[i - 1] = Float.parseFloat(str[i]);
                }
                feature_map.put(pic_name, feature);
                Log.e(TAG, str.length + " " + feature.length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "*********************** finish read feature *************************");
    }
}
