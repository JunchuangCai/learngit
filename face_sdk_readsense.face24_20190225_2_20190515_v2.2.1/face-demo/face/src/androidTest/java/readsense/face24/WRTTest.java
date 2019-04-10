package readsense.face24;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sense.icount.github.util.BitmapUtil;
import cn.sense.icount.github.util.DLog;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import mobile.ReadFace.YMPerson;
import readsense.face24.activity.base.ExApplication;
import readsense.face24.util.FileUtil;

/**
 * 地标测试
 */

@RunWith (AndroidJUnit4.class)
public class WRTTest {
    private static final String TAG = "WRTTest";
    private Context appContext;

    YMFaceTrack faceTrack;
    static String base_path = "/sdcard/img/huiruitong_test2/";
    static String out_path = "/sdcard/img/huiruitong_test2/output/data/";
    static String path_1k = "/sdcard/img/huiruitong_test2/1000/";
    static String path_1w = "/sdcard/img/huiruitong_test2/10000/";
    static String path_500kunei = "/sdcard/img/huiruitong_test2/kunei/";
    static String path_500kuwai = "/sdcard/img/huiruitong_test2/kuwai/";

    static String feature_path = "/sdcard/img/output/featurev7";


    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();


        faceTrack = new YMFaceTrack();
        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_FARTHESTER);
        faceTrack.initTrack(ExApplication.getContext(), 0, 0, "/sdcard/img/huiruitong_test2");
        faceTrack.setRecognitionConfidence(75);
        faceTrack.resetAlbum();

    }

    @org.junit.Test
    public void testAddPerson() throws Exception {

        File featurePath = new File(out_path);
        FileUtil.deleteDirectory(featurePath);
        featurePath.mkdirs();

        Map<String, float[]> feature_kunei_map = getAllFeature(faceTrack, path_500kunei);
        Map<String, float[]> feature_kuwai_map = getAllFeature(faceTrack, path_500kuwai);

        Map<String, float[]> register_featureMap = getAllFeature(faceTrack, path_1w);


        Map<Integer, String> ids = new HashMap<>();//注册id 对应的img

        File diku_pair = new File(out_path);
        if (!diku_pair.exists()) {
            diku_pair.mkdirs();
        }
        //register
        int register_count = 0;
        int register_success = 0;
        for (Map.Entry<String, float[]> entry : register_featureMap.entrySet()) {
            final float[] value = entry.getValue();
            if (value == null) continue;
            final int personID = faceTrack.addPerson(value);
            register_count++;
            if (personID > 0) {
                ids.put(personID, entry.getKey());
                register_success++;
            }

            DLog.d(entry.getKey() + " register count: " + register_count + "  personID: " + personID);
            FileUtil.writeFile(diku_pair.getAbsolutePath() + "/diku.txt", personID + " " + new File(entry.getKey()).getName() + "\n", true);
        }
        FileUtil.writeFile(diku_pair.getAbsolutePath() + "/diku.txt", "register_count: " + register_count + " register_success: " + register_success + " \n", true);


        analyzeIdentifyMap(feature_kunei_map, "/kunei_outpair.txt", ids);

        analyzeIdentifyMap(feature_kuwai_map, "/kuwai_outpair.txt", ids);
        DLog.d("*******************************************\n" +
                "*\n" +
                "*      END\n" +
                "*\n" +
                "*******************************************\n");
    }

    private void analyzeIdentifyMap(Map<String, float[]> feature_kunei_map, String s, Map<Integer, String> ids) {
        //get all feature
        File featurePath = new File(out_path);
        if (!featurePath.exists()) {
            featurePath.mkdirs();
        }

        List<RecoObj> people = new ArrayList<>();
        for (Map.Entry<String, float[]> entry : feature_kunei_map.entrySet()) {
            final float[] feature = entry.getValue();
            if (feature == null) continue;

            final List<YMPerson> similarPersons = faceTrack.findSimilarPerson(feature);
            if (similarPersons != null && similarPersons.size() > 0)
                people.add(new RecoObj(entry.getKey(), similarPersons.get(0)));
        }

        Collections.sort(people, new Comparator<RecoObj>() {
            @Override
            public int compare(RecoObj o1, RecoObj o2) {
                return Float.compare(o1.person.getConfidence(), o2.person.getConfidence());
            }
        });
        for (RecoObj recoObj : people) {
            FileUtil.writeFile(featurePath.getAbsoluteFile() + s,
                    recoObj.person.getPerson_id() + " - " + recoObj.person.getConfidence() +
                            " - " + new File(recoObj.path).getName()
                            + " - " + ids.get(recoObj.person.getPerson_id())
                            + "\n", true);
        }


    }

    class RecoObj {
        public RecoObj(String path, YMPerson person) {
            this.path = path;
            this.person = person;
        }

        String path;
        YMPerson person;
    }

    @After
    public void releaseSource() {

    }

    static Map<String, float[]> getAllFeature(YMFaceTrack faceTrack, String path) {

        File testPath_file = new File(path);
        int count = 0;

        Map<String, float[]> featureMap = new HashMap<>();


        File[] testPath_files = testPath_file.listFiles();

        for (File item : testPath_files) {
            String itemName = item.getName();
            count++;
            DLog.d("get feature count: " + count + "  " + itemName);
            if (itemName.endsWith(".jpg") || itemName.endsWith(".JPG")) {
                if (!featureMap.containsKey(item.getName())) {
                    float[] feature = getFeature(item.getAbsolutePath(), faceTrack);
                    if (feature != null)
                        featureMap.put(item.getName(), feature);
                }
            }
        }

        return featureMap;
    }

    static float[] getFeature(String path, YMFaceTrack faceTrack) {
        File path_feature = new File(feature_path);
        float[] feature = null;
        if (!path_feature.exists()) path_feature.mkdirs();
        final File file = new File(path_feature.getAbsolutePath() + "/" + new File(path).getName() + ".txt");
        if (file.exists()) {

            final StringBuilder stringBuilder = FileUtil.readFile(file, "utf-8");

            final String[] s = stringBuilder.toString().split(" ");

            feature = new float[512];
            for (int i = 0; i < 512; i++) {
                feature[i] = Float.parseFloat(s[i]);
            }
        } else {
            Bitmap idcard_bitmap = BitmapUtil.decodeScaleImage(path, 1000, 1000);

            List<YMFace> ymFaces = faceTrack.detectMultiBitmap(idcard_bitmap);
            if (ymFaces != null && ymFaces.size() > 0) {

                int maxIndex = 0;
                for (int i = 1; i < ymFaces.size(); i++) {
                    if (ymFaces.get(maxIndex).getRect()[2] <= ymFaces.get(i).getRect()[2]) {
                        maxIndex = i;
                    }
                }
                long time = System.currentTimeMillis();
                feature = faceTrack.getFaceFeature(maxIndex);
                DLog.d("" + (System.currentTimeMillis() - time));

                if (feature != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < 512; i++) {
                        stringBuffer.append(feature[i] + " ");
                    }
                    FileUtil.writeFile(file.getAbsolutePath(), stringBuffer.toString().trim());
                }
                if (feature == null)
                    DLog.d("feature ==null");
            }
        }
        return feature;
    }

}
