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

import java.io.File;
import java.io.IOException;
import java.util.List;

import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import readsense.face24.util.BitmapUtil;
import readsense.face24.util.FileUtil;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class livenessTest {
    private static final String TAG = "livenessTest";

    private YMFaceTrack faceTrack;
    private static final String db_path = "/sdcard/img/";
    private Context appContext;

    private String imgPath = "/sdcard/liveness/img";//测试图片路径
    private String out_liveness_path = "/sdcard/liveness/out.txt";//特征存储路径

    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();
        faceTrack = TestUtil.getFaceTrack(appContext, "", 75);

        File out_liveness_path = new File("/sdcard/liveness/out.txt");
        try {
            //判断文件是否存在
            if (out_liveness_path.exists()) {
                out_liveness_path.delete();//文件如果存在删除这个文件
            } else {
                out_liveness_path = new File("/sdcard/liveness");
                out_liveness_path.mkdir();//先创建文件夹
                out_liveness_path = new File("/sdcard/liveness/out.txt");//创建这个文件
                out_liveness_path.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void releaseSource() {

    }

    @Test
    public void test() {
        testLiveness(imgPath);
    }

    /**
     * @param path 图片文件夹所在路径
     */
    public void testLiveness(String path) {
        Log.e(TAG, "Test, start");
        File dir = new File(path);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                assertTrue(null != files && files.length > 0);
                Bitmap targetBitmap;
                List<YMFace> ymFaces;
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < files.length; i++) {
                    if (i % 10 == 0) {
                        Log.e(TAG, "track count : " + files.length + " now : " + i);
                    }
                    // 根据图片路径获取图片检测人脸
                    File currFile = files[i];
                    String currName = files[i].getName();
                    if (currName.contains(".jpg") || currName.contains(".png") || currName.contains(".jpeg") || currName.contains(".JPG")) {
                        targetBitmap = BitmapUtil.decodeScaleImage(currFile.getAbsolutePath(), 1000, 1000);
                        ymFaces = faceTrack.detectMultiBitmap(targetBitmap);
                        targetBitmap.recycle();
                        if (ymFaces != null && ymFaces.size() != 0) {
                            int maxIndex = 0;
                            for (int j = 0; j < ymFaces.size(); j++) {
                                if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(j).getRect()[2]) {
                                    maxIndex = j;
                                    int[] ints = faceTrack.livenessDetect(maxIndex, 0.7f);
                                    if (sb.length() > 0) {
                                        sb.delete(0, sb.length() - 1);
                                    }
                                    if (ints[0] == 1) {
                                        sb.append("活体识别结果: " + currName + "  活体通过" + "\n");
                                    } else {
                                        sb.append("活体识别结果: " + currName + "  活体失败" + "\n");
                                        int faceQuality = faceTrack.getFaceQuality(maxIndex);
                                        sb.append("人脸质量: " + faceQuality + "\n");
                                        float[] headposes = ymFaces.get(maxIndex).getHeadpose();
                                        if ((Math.abs(headposes[0]) > 30 || Math.abs(headposes[1]) > 30 || Math.abs(headposes[2]) > 30)) {
                                            sb.append("人脸角度不佳（超过30）" + "\n");
                                        } else {
                                            sb.append("人脸角度满足要求（不超过30）" + "\n");
                                        }
                                    }
                                    FileUtil.writeFile(out_liveness_path, sb.toString(), true);
                                }
                            }
                        } else {
                            Log.e(TAG, "no face in : " + currFile.getName());
                            if (sb.length() > 0) {
                                sb.delete(0, sb.length() - 1);
                            }
                            sb.append("no face in : " + currName + "\n");
                            FileUtil.writeFile(out_liveness_path, sb.toString(), true);
                        }
                    }
                }
                Log.e(TAG, "Test, finished");
            } else {
                Log.e(TAG, path + "不是目录");
            }
        } else {
            Log.e(TAG, path + "路径不存在");
        }
    }
}
