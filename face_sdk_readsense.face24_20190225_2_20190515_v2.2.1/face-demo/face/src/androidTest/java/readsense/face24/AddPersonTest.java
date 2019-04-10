package readsense.face24;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import readsense.face24.util.BitmapUtil;
import readsense.face24.util.FileUtil;


@RunWith(AndroidJUnit4.class)
public class AddPersonTest {
    private static final String TAG = "AddPersonTest";
    private YMFaceTrack faceTrack;
    private static final String db_path = "/sdcard/test_add_person";
    private static final String out_add_person_path = "/sdcard/test_add_person/out.txt";
    private Context appContext;

    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();

        File file_feature_db = new File("/sdcard/test_add_person/faces_v2.db");

        //判断文件是否存在
        if (file_feature_db.exists()) {
            file_feature_db.delete();//文件如果存在删除这个文件
        } else {
            File file = new File("/sdcard/test_add_person");
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        faceTrack = TestUtil.getFaceTrack(appContext, db_path, 75);

        File file_feature = new File("/sdcard/test_add_person/out.txt");
        try {
            //判断文件是否存在
            if (file_feature.exists()) {
                file_feature.delete();//文件如果存在删除这个文件
            } else {
                file_feature = new File("/sdcard/test_add_person");
                file_feature.mkdir();//先创建文件夹
                file_feature = new File("/sdcard/test_add_person/out.txt");//创建这个文件
                file_feature.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testAddPerson() throws Exception {
        Log.e(TAG, "start");
        String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_face/3_0.jpg";
        Bitmap targetBitmap = BitmapUtil.decodeScaleImage(imgPath, 1000, 1000);
        List<YMFace> ymFaces = faceTrack.detectMultiBitmap(targetBitmap);
        if (null != ymFaces && ymFaces.size() > 0) {
            for (int i = 0; i < 100; i++) {
                int result = faceTrack.addPerson(0);
                if (result > 0) {//成功
                    List<Integer> enrolledPersonIds = faceTrack.getEnrolledPersonIds();
                    FileUtil.writeFile(out_add_person_path, enrolledPersonIds.toString() + "\n", true);
                    if (i % 1 == 0) {
                        Log.e(TAG, "current: " + i);
                    }
                } else {//失败

                }
            }
            Log.e(TAG, "finished");
        }
    }

    @After
    public void releaseSource() {

    }
}
