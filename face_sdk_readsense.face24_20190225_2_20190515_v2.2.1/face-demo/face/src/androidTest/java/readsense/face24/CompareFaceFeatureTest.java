package readsense.face24;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

/**
 * Instrumented test, which will execute on an Android device.
 * 跑测试集
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CompareFaceFeatureTest {

    private static String TAG = "CompareFaceFeatureTest";

    private YMFaceTrack faceTrack;
    private Context appContext;
    private String img_path;
    private String out_feature_path = "/sdcard/test_face_feature/feature.txt";//特征存储路径
    private String out_feature_face_path = "/sdcard/test_face_feature/feature_face.txt";
    private String out_feature_face_1_1_path = "/sdcard/test_face_feature/feature_face_1_1.txt";
    private String out_feature_idcard_path = "/sdcard/test_face_feature/feature_idcard.txt";
    private String out_1_1_path = "/sdcard/test_face_feature/face_1_1.txt";//存储1:1人脸对比结果路径
    private String out_n_n_path = "/sdcard/test_face_feature/face_n_n.txt";//存储1:n人脸对比结果路径
    private String out_1_1_IDCard_path = "/sdcard/test_face_feature/face_1_1_IDCard.txt";//存储1:1人证对比结果路径
    private String out_n_n_IDCard_path = "/sdcard/test_face_feature/face_n_n_IDCard.txt";//存储n:n人证对比结果路径
    private String out_time_feature_path = "/sdcard/test_face_feature/time_feature.txt";//存储获取一张图片的时间、检测一张图片的时间、获取一个人脸特征的时间
    private String out_time_compare_path = "/sdcard/test_face_feature/time_compare.txt";//存储比较一对特征值的时间
    private String out_compare_rate_path = "/sdcard/test_face_feature/face_compare_rate.txt";
    private Map<String, float[]> feature_map;//存储获取到的特征
    private int compare_count = 0;//统计比对总次数
    private int compare_wrong = 0;//统计比对错误总次数
    private int pic_count;//获取总图片数
    private int track_count;//获取特征时记录当前已经获取到特征的图片数

    private float confidence_face = 75;//人脸识别阈值
    private float confidence_face_idcard = 37;//人证识别阈值

    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();
        faceTrack = TestUtil.getFaceTrack(appContext, "", 75);
        img_path = "/sdcard/img/compare";
        feature_map = new HashMap<>();

        File file_feature = new File("/sdcard/test_face_feature/feature.txt");
        try {
            //判断文件是否存在
            if (file_feature.exists()) {
                file_feature.delete();//文件如果存在删除这个文件
            } else {
            }
            file_feature = new File("/sdcard/test_face_feature");
            file_feature.mkdir();//先创建文件夹
            file_feature = new File("/sdcard/test_face_feature/feature.txt");//创建这个文件
            file_feature.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_feature_face = new File("/sdcard/test_face_feature/feature_face.txt");
        try {
            //判断文件是否存在
            if (file_feature_face.exists()) {
                //file_feature.delete();//文件如果存在删除这个文件
            } else {
                file_feature_face = new File("/sdcard/test_face_feature");
                file_feature_face.mkdir();//先创建文件夹
                file_feature_face = new File("/sdcard/test_face_feature/feature_face.txt");//创建这个文件
                file_feature_face.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_feature_face_1_1 = new File("/sdcard/test_face_feature/feature_face_1_1.txt");
        try {
            //判断文件是否存在
            if (file_feature_face_1_1.exists()) {
                //file_feature.delete();//文件如果存在删除这个文件
            } else {
                file_feature_face_1_1 = new File("/sdcard/test_face_feature");
                file_feature_face_1_1.mkdir();//先创建文件夹
                file_feature_face_1_1 = new File("/sdcard/test_face_feature/feature_face_1_1.txt");//创建这个文件
                file_feature_face_1_1.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_feature_idcard = new File("/sdcard/test_face_feature/feature_idcard.txt");
        try {
            //判断文件是否存在
            if (file_feature_idcard.exists()) {
                //file_feature.delete();//文件如果存在删除这个文件
            } else {
                file_feature_idcard = new File("/sdcard/test_face_feature");
                file_feature_idcard.mkdir();//先创建文件夹
                file_feature_idcard = new File("/sdcard/test_face_feature/feature_idcard.txt");//创建这个文件
                file_feature_idcard.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        File file_face_1_1 = new File("/sdcard/test_face_feature/face_1_1.txt");
        try {
            //判断文件是否存在
            if (file_face_1_1.exists()) {
                file_face_1_1.delete();//文件如果存在删除这个文件
            } else {
                file_face_1_1 = new File("/sdcard/test_face_feature");
                file_face_1_1.mkdir();//先创建文件夹
                file_face_1_1 = new File("/sdcard/test_face_feature/face_1_1.txt");//创建这个文件
                file_face_1_1.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_face_n_n = new File("/sdcard/test_face_feature/face_n_n.txt");
        try {
            //判断文件是否存在
            if (file_face_n_n.exists()) {
                file_face_n_n.delete();//文件如果存在删除这个文件
            } else {
                file_face_n_n = new File("/sdcard/test_face_feature");
                file_face_n_n.mkdir();//先创建文件夹
                file_face_n_n = new File("/sdcard/test_face_feature/face_n_n.txt");//创建这个文件
                file_face_n_n.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_face_compare_rate = new File("/sdcard/test_face_feature/face_compare_rate.txt");
        try {
            //判断文件是否存在
            if (file_face_compare_rate.exists()) {
                file_face_compare_rate.delete();//文件如果存在删除这个文件
            } else {
                file_face_compare_rate = new File("/sdcard/test_face_feature");
                file_face_compare_rate.mkdir();//先创建文件夹
                file_face_compare_rate = new File("/sdcard/test_face_feature/face_compare_rate.txt");//创建这个文件
                file_face_compare_rate.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_face_1_1_IDCard = new File("/sdcard/test_face_feature/face_1_1_IDCard.txt");
        try {
            //判断文件是否存在
            if (file_face_1_1_IDCard.exists()) {
                file_face_1_1_IDCard.delete();//文件如果存在删除这个文件
            } else {
                file_face_1_1_IDCard = new File("/sdcard/test_face_feature");
                file_face_1_1_IDCard.mkdir();//先创建文件夹
                file_face_1_1_IDCard = new File("/sdcard/test_face_feature/face_1_1_IDCard.txt");//创建这个文件
                file_face_1_1_IDCard.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_face_n_n_IDCard = new File("/sdcard/test_face_feature/face_n_n_IDCard.txt");
        try {
            //判断文件是否存在
            if (file_face_n_n_IDCard.exists()) {
                file_face_n_n_IDCard.delete();//文件如果存在删除这个文件
            } else {
                file_face_n_n_IDCard = new File("/sdcard/test_face_feature");
                file_face_n_n_IDCard.mkdir();//先创建文件夹
                file_face_n_n_IDCard = new File("/sdcard/test_face_feature/face_n_n_IDCard.txt");//创建这个文件
                file_face_n_n_IDCard.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_time_feature = new File("/sdcard/test_face_feature/time_feature.txt");
        try {
            //判断文件是否存在
            if (file_time_feature.exists()) {
                file_time_feature.delete();//文件如果存在删除这个文件
            } else {
                file_time_feature = new File("/sdcard/test_face_feature");
                file_time_feature.mkdir();//先创建文件夹
                file_time_feature = new File("/sdcard/test_face_feature/time_feature.txt");//创建这个文件
                file_time_feature.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_time_compare = new File("/sdcard/test_face_feature/time_compare.txt");
        try {
            //判断文件是否存在
            if (file_time_compare.exists()) {
                file_time_compare.delete();//文件如果存在删除这个文件
            } else {
                file_time_compare = new File("/sdcard/test_face_feature");
                file_time_compare.mkdir();//先创建文件夹
                file_time_compare = new File("/sdcard/test_face_feature/time_compare.txt");//创建这个文件
                file_time_compare.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compareFaceFeature() {
        int[] result_1_1 = new int[181];
        for (int i = 0; i < result_1_1.length; i++) {
            result_1_1[i] = 0;
        }
        int[] result_n_n = new int[181];
        for (int i = 0; i < result_n_n.length; i++) {
            result_n_n[i] = 0;
        }
        //人脸
        //getFeature和readFeature选取其中一个
        confidence_face = 75;
        img_path = "sdcard/test_face/";
        //img_path = "sdcard/test_face_1_1/";//人脸1:1
        getFeature(img_path, true);//获取人脸特征
        readFeature(out_feature_path);
        //readFeature(out_feature_face_path);
        //readFeature(out_feature_face_1_1_path);

        compareFeatureOneToOne(feature_map, confidence_face, result_1_1);//人脸 1:1
        compareFeatureManyToMany(feature_map, confidence_face, result_n_n);//人脸 N:N


        StringBuilder result = new StringBuilder();
        int right = result_1_1[0];
        int wrong = result_n_n[0];
        for (int i = 40; i <= 180; i++) {
            result.append(i / (float) 2).append("/");
            result.append(result_1_1[i] / (float) right).append(" : ");
            result.append(result_n_n[i] / (float) wrong).append("\n");
        }

        FileUtil.writeFile(out_compare_rate_path, result.toString(), true);

        //人证
        //getFeature和readFeature选取其中一个
        /*confidence_face_idcard = 37;
        img_path = "sdcard/test_face_idcard";
        getFeature(img_path, false);//获取人证特征
        //readFeature(out_feature_idcard_path);
        compareFeatureIDCardOneToOne(feature_map, confidence_face_idcard);//人证 1:1
        compareFeatureIDCardManyToMany(feature_map, confidence_face_idcard);//人证 N:N*/
    }

    @After
    public void releaseFaceTrack() {
        faceTrack.onRelease();
        if (null != feature_map && feature_map.size() > 0) {
            feature_map.clear();
            feature_map = null;
        }
    }


    /**
     * 人脸特征对比 1:1
     * A的注册照片和A的实拍照片进行比较叫做1：1
     *
     * @param map        特征值
     * @param result_1_1
     */
    private void compareFeatureOneToOne(Map<String, float[]> map, float confidence_face, int[] result_1_1) {
        compare_count = 0;
        compare_wrong = 0;

        //根据特征值进行对比并存储比对结果
        Log.e(TAG, "start compareFeatureOneToOne!");
        float compare_result = 0.0f;
        StringBuilder stringBuilder_wrong = new StringBuilder();
        String[] str_name_a, str_name_b, str_personId_a, str_personId_b;

        long totalComapreTime = 0;
        for (Map.Entry<String, float[]> str : map.entrySet()) {
            // 根据命名规则，例如3_0.jpg，判断是否是同一人
            String key = str.getKey();
            str_name_a = key.split("\\.");
            str_personId_a = str_name_a[0].split("_");
            // 是注册照片
            if (str_personId_a[1].equals("0")) {
                for (Map.Entry<String, float[]> str2 : map.entrySet()) {
                    String key2 = str2.getKey();
                    str_name_b = String.valueOf(key2).split("\\.");
                    str_personId_b = str_name_b[0].split("_");
                    // 判断是不是注册照片
                    if (!str_personId_b[1].equals("0")) {
                        // 判断是不是本人
                        if (str_personId_a[0].equals(str_personId_b[0])) {
                            // 是本人
                            long compareTime = System.currentTimeMillis();
                            compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                            compareTime = System.currentTimeMillis() - compareTime;
                            totalComapreTime += compareTime;
                            if (compare_result < confidence_face) {//对比结果认为不是同一个人(识别错误)
                                //stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                                compare_wrong++;

                            }
                            for (int i = 40; i <= 180; i++) {
                                if (i <= compare_result * 2) {
                                    result_1_1[i]++;
                                }
                            }
                            compare_count++;
                            result_1_1[0] = compare_count;
                            if (compare_count % 100 == 0) {
                                Log.e(TAG, "now compare  count: " + compare_count);
                            }
                        } else {
                            // 非本人
                        }
                    }
                }
            }
        }

        stringBuilder_wrong.append("比较一对特征值的时间: " + totalComapreTime / (float) compare_count + "\n");
        Log.e(TAG, "confidence = " + confidence_face + "," + "finish compareFeatureOneToOne!");
    }

    /**
     * 人脸特征对比 N:N (N:N 包括 1:N)
     * A的注册照片和 A、B、C...的实拍照片进行比较叫做 1：N
     * A、B、C...的注册照片和 A、B、C...的实拍照片进行比较叫做 N：N
     *
     * @param map        特征值
     * @param result_n_n
     */
    private void compareFeatureManyToMany(Map<String, float[]> map, float confidence_face, int[] result_n_n) {
        compare_count = 0;
        compare_wrong = 0;

        //根据特征值进行对比并存储比对结果
        Log.e(TAG, "start compareFeatureOneToMany!");
        float compare_result = 0.0f;
        StringBuilder stringBuilder_wrong = new StringBuilder();
        String[] str_name_a, str_name_b, str_personId_a, str_personId_b;
        long totalComapreTime = 0;
        for (Map.Entry<String, float[]> str : map.entrySet()) {
            // 根据命名规则，例如3_0.jpg，判断是否是同一人
            String key = str.getKey();
            str_name_a = String.valueOf(key).split("\\.");
            str_personId_a = str_name_a[0].split("_");
            // 是注册照片
            //if (str_personId_a[1].equals("0")) {
            for (Map.Entry<String, float[]> str2 : map.entrySet()) {
                String key2 = str2.getKey();
                str_name_b = String.valueOf(key2).split("\\.");
                str_personId_b = str_name_b[0].split("_");
                // 判断是不是注册照片
                //if (!str_personId_b[1].equals("0")) {
                // 判断是不是本人
                if (str_personId_a[0].equals(str_personId_b[0])) {
                    // 是本人
                            /*long compareTime = System.currentTimeMillis();
                            compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                            compareTime = System.currentTimeMillis() - compareTime;
                            totalComapreTime += compareTime;
                            if (compare_result < confidence_face) {//对比结果认为不是同一个人(识别错误)
                                stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                                compare_wrong++;
                            }
                            compare_count++;
                            if (compare_count % 100 == 0) {
                                Log.e(TAG, "now compare  count: " + compare_count);
                            }*/
                } else {
                    // 非本人
                    long compareTime = System.currentTimeMillis();
                    compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                    compareTime = System.currentTimeMillis() - compareTime;
                    totalComapreTime += compareTime;
                    if (compare_result >= confidence_face) {//对比结果认为是同一个人(识别错误)
                        //stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                        compare_wrong++;
                    }
                    for (int i = 40; i <= 180; i++) {
                        if (i <= compare_result * 2) {
                            result_n_n[i]++;
                        }
                    }
                    compare_count++;
                    result_n_n[0] = compare_count;
                    if (compare_count % 100 == 0) {
                        Log.e(TAG, "now compare  count: " + compare_count);
                    }
                }
                //}
            }
            //}
        }

        stringBuilder_wrong.append("比较一对特征值的时间: " + totalComapreTime / (float) compare_count + "\n");
        Log.e(TAG, "confidence = " + confidence_face + "," + "finish compareFeatureOneToMany!");
    }

    /**
     * 人证特征对比 1:1
     * A的注册照片和A的实拍照片进行比较叫做1：1
     *
     * @param map 特征值
     */
    private void compareFeatureIDCardOneToOne(Map<String, float[]> map, int confidence_face_idcard) {
        compare_count = 0;
        compare_wrong = 0;

        //根据特征值进行对比并存储比对结果
        Log.e(TAG, "start compareFeatureIDCardOneToOne!");
        float compare_result = 0.0f;
        StringBuilder stringBuilder_wrong = new StringBuilder();
        String[] str_name_a, str_name_b, str_personId_a, str_personId_b;
        for (Map.Entry<String, float[]> str : map.entrySet()) {
            // 根据命名规则，例如3_1.jpg，判断是否是同一人
            String key = str.getKey();
            str_name_a = String.valueOf(key).split("\\.");
            str_personId_a = str_name_a[0].split("_");
            // 是注册照片
            if (str_personId_a[1].equals("1")) {
                for (Map.Entry<String, float[]> str2 : map.entrySet()) {
                    String key2 = str2.getKey();
                    str_name_b = String.valueOf(key2).split("\\.");
                    str_personId_b = str_name_b[0].split("_");
                    // 判断是不是注册照片
                    if (!str_personId_b[1].equals("1")) {
                        // 判断是不是本人
                        if (str_personId_a[0].equals(str_personId_b[0])) {
                            // 是本人
                            compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                            if (compare_result < confidence_face_idcard) {//对比结果认为不是同一个人(识别错误)
                                stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                                compare_wrong++;
                            }
                            compare_count++;
                            if (compare_count % 100 == 0) {
                                Log.e(TAG, "now compare  count: " + compare_count);
                            }
                        } else {
                            // 非本人
                        }
                    }
                }
            }
        }
        FileUtil.writeFile(out_1_1_IDCard_path, stringBuilder_wrong.toString());
        FileUtil.writeFile(out_1_1_IDCard_path, "confidence = " + confidence_face_idcard + ", " +
                "wrong count = " + compare_wrong + ", " +
                "total count = " + compare_count + ", " +
                "error rate = " + ((float) compare_wrong / (float) compare_count) * 100 + "%", true);
        Log.e(TAG, "finish compareFeatureIDCardOneToOne!");
    }

    /**
     * 人证特征对比 N:N (N:N 包括 1:N)
     * A的注册照片和 A、B、C...的实拍照片进行比较叫做 1：N
     * A、B、C...的注册照片和 A、B、C...的实拍照片进行比较叫做 N：N
     *
     * @param map 特征值
     */
    private void compareFeatureIDCardManyToMany(Map<String, float[]> map, int confidence_face_idcard) {
        compare_count = 0;
        compare_wrong = 0;

        //根据特征值进行对比并存储比对结果
        Log.e(TAG, "start compareFeatureIDCardManyToMany!");
        float compare_result = 0.0f;
        StringBuilder stringBuilder_wrong = new StringBuilder();
        String[] str_name_a, str_name_b, str_personId_a, str_personId_b;
        for (Map.Entry<String, float[]> str : map.entrySet()) {
            // 根据命名规则，例如3_1.jpg，判断是否是同一人
            String key = str.getKey();
            str_name_a = String.valueOf(key).split("\\.");
            str_personId_a = str_name_a[0].split("_");
            // 是注册照片
            if (str_personId_a[1].equals("1")) {
                for (Map.Entry<String, float[]> str2 : map.entrySet()) {
                    String key2 = str2.getKey();
                    str_name_b = String.valueOf(key2).split("\\.");
                    str_personId_b = str_name_b[0].split("_");
                    // 判断是不是注册照片
                    if (!str_personId_b[1].equals("1")) {
                        // 判断是不是本人
                        if (str_personId_a[0].equals(str_personId_b[0])) {
                            // 是本人
                            compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                            if (compare_result < confidence_face_idcard) {//对比结果认为不是同一个人(识别错误)
                                stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                                compare_wrong++;
                            }
                            compare_count++;
                            if (compare_count % 100 == 0) {
                                Log.e(TAG, "now compare  count: " + compare_count);
                            }
                        } else {
                            // 非本人
                            compare_result = faceTrack.compareFaceFeatureMix(str.getValue(), str2.getValue());
                            if (compare_result >= confidence_face_idcard) {//对比结果认为是同一个人(识别错误)
                                stringBuilder_wrong.append(str.getKey() + " " + str2.getKey() + " " + compare_result + "\n");
                                compare_wrong++;
                            }
                            compare_count++;
                            if (compare_count % 100 == 0) {
                                Log.e(TAG, "now compare  count: " + compare_count);
                            }
                        }
                    }
                }
            }
        }
        FileUtil.writeFile(out_n_n_IDCard_path, stringBuilder_wrong.toString());
        FileUtil.writeFile(out_n_n_IDCard_path, "confidence = " + confidence_face_idcard + ", " +
                "wrong count = " + compare_wrong + ", " +
                "total count = " + compare_count + ", " +
                "error rate = " + ((float) compare_wrong / (float) compare_count) * 100 + "%", true);
        Log.e(TAG, "finish compareFeatureIDCardOneToOne!");
        Log.e(TAG, "finish compareFeatureIDCardManyToMany!");
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
