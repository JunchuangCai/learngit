package readsense.face24;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;
import readsense.face24.util.BitmapUtil;
import readsense.face24.util.FileUtil;


@RunWith (AndroidJUnit4.class)
public class FeatureTest {
    private static final String TAG = "FeatureTest";

    private YMFaceTrack faceTrack;
    private static final String db_path = "/sdcard/img/";
    private Context appContext;

    private static final String out_people_card_compare_path = "/sdcard/test_card/out.txt";

    @Before
    public void initFaceTrack() {
        appContext = InstrumentationRegistry.getTargetContext();
        faceTrack = TestUtil.getFaceTrack(appContext, "", 75);

        File file = new File("/sdcard/test_card/out.txt");

        //判断文件是否存在
        if (file.exists()) {
            //file_feature_db.delete();//文件如果存在删除这个文件
        } else {
            File fileDir = new File("/sdcard/test_card");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        }

        pairToWindows("/sdcard/img/WechatIMG187.jpeg");


    }

    private void pairToWindows(String path1) {
        float[] feature1 = getFeatureFromPath(path1);

        String windowsFeature = "-0.009033203, 1.5942383, -0.5551758, 0.2487793, 0.2980957, 0.6789551, 0.8496094, -0.2548828, -0.26245117, 0.64086914, 1.4882812, 1.4482422, -0.42895508, -0.751709, -0.6958008, 0.46020508, -0.03955078, -0.22460938, -0.42138672, -0.7558594, -0.40625, 1.6784668, 0.13745117, 1.8195801, -1.3464355, -0.122558594, -0.5612793, 0.41430664, -0.27441406, -1.2827148, 1.1774902, -0.1484375, 2.230713, -0.3383789, 0.7727051, 0.40771484, 1.1540527, 0.18359375, 0.029296875, 0.23828125, -0.8479004, 0.018554688, 0.045166016, -0.29052734, -0.4753418, -0.3149414, -0.50805664, 0.37719727, -1.8811035, -0.15405273, 0.1496582, -1.5270996, 0.06713867, 1.5654297, 0.46948242, 0.80566406, -1.6706543, -1.2294922, 0.28637695, 1.1201172, 0.70703125, 0.13452148, 0.038330078, -1.4213867, 1.6057129, 0.5007324, 0.14624023, -0.5373535, -0.45532227, -0.9772949, -1.2067871, 0.5588379, 0.71069336, 0.24780273, -0.9580078, 0.85131836, -0.5944824, -0.4182129, -0.69921875, -1.6550293, -0.1899414, -0.25610352, -2.052246, 0.22875977, -2.3015137, -0.8261719, -0.5288086, 0.6706543, 0.5214844, 0.80078125, 0.9355469, -0.9729004, 0.03149414, -0.9226074, 0.9423828, -0.6303711, -0.6176758, 0.23779297, -0.04711914, -0.8886719, 0.09399414, -2.2438965, -0.78222656, -0.026611328, 0.16186523, 1.4851074, 1.3876953, 2.2543945, 0.47998047, 0.8469238, -0.782959, -1.375, 0.9699707, -0.88183594, -0.44970703, -0.7907715, -0.041503906, -1.8322754, 0.18652344, -1.230957, 2.779541, 0.56762695, 0.32348633, 0.9326172, -0.73461914, 0.31860352, 1.3415527, 0.9260254, -2.128662, 1.0749512, -0.9489746, 1.8515625, 0.38085938, 1.6030273, -0.05102539, -2.2155762, 0.30664062, -0.56347656, 2.479004, 0.9812012, -0.82128906, 2.3562012, 0.6286621, 0.053710938, 1.7617188, 1.2702637, -1.7504883, -0.41259766, -0.033691406, 1.4445801, 1.1916504, -0.1640625, 0.18823242, 0.7241211, 0.43188477, 0.029052734, 0.041992188, 0.9577637, 0.7585449, -1.6584473, -1.7429199, -1.0993652, 0.2434082, 0.13720703, -0.19824219, 2.5178223, 0.44702148, -0.021728516, 0.84716797, 0.31884766, -0.1850586, 0.44458008, 0.2607422, -1.1848145, -1.2956543, 0.75341797, 0.93969727, 1.1794434, 1.0693359, 0.75439453, 0.49951172, 2.1108398, -1.2512207, -0.84106445, -0.32910156, 0.25976562, 0.08105469, -0.36035156, -0.08569336, -1.1022949, 1.6706543, 0.075683594, -0.92993164, -0.2709961, 1.0297852, -0.60180664, 0.91796875, 0.35961914, 0.3173828, 1.5783691, 0.46166992, 2.2697754, 1.1577148, 0.031982422, 0.6843262, -1.7851562, 0.080078125, -0.08935547, 0.65234375, -1.126709, -0.6135254, 0.65112305, 0.79467773, 0.48535156, -0.0048828125, 0.35961914, 0.031982422, -0.7890625, 0.8544922, 1.2680664, -0.34106445, 0.50024414, -0.49560547, -0.7375488, -0.41723633, 2.4863281, 1.1206055, 0.33203125, -1.5861816, -1.0979004, 1.8203125, -0.56103516, 0.21313477, 0.6279297, 0.5319824, -1.2971191, 0.23925781, -0.6640625, 0.21313477, 1.4741211, -1.5952148, 1.5288086, 0.4074707, 0.76049805, 0.30444336, 0.57006836, -1.494873, -1.8884277, -0.76293945, 0.27929688, -1.2182617, 0.24975586, 0.26782227, -0.7961426, 0.6616211, -0.7229004, -0.76171875, 0.70043945, -1.2609863, -0.6594238, 0.63793945, -0.80371094, -1.6098633, 1.7910156, -0.3605957, -0.6828613, 0.051513672, 0.24902344, 0.3474121, 1.736084, -0.8964844, 0.26049805, 2.102539, -0.8371582, -1.4589844, 0.6040039, -1.2868652, 0.5998535, 1.2145996, -0.86743164, 0.34155273, -0.46289062, -0.10668945, -0.61499023, 0.11254883, -1.4990234, -0.70654297, -1.0708008, -0.043945312, 0.48901367, 0.15209961, -0.07324219, 0.27978516, 0.014892578, -0.6496582, -0.53344727, 1.2600098, -0.39990234, -0.7155762, -0.66259766, -0.08569336, -0.5031738, -1.623291, 0.8129883, 0.21728516, -0.8552246, -1.2583008, -0.62890625, 1.111084, -2.3032227, -2.2519531, -0.43066406, -0.8569336, -0.030273438, 0.60668945, -0.09863281, 0.3996582, -0.99438477, -0.20727539, 0.68847656, -0.31225586, -0.8129883, -2.3344727, -0.2854004, 0.7780762, -0.3947754, 0.63623047, 1.0944824, 0.22241211, -1.4245605, 1.046875, 0.7441406, -0.78222656, 0.24951172, -0.7783203, -0.4182129, -0.42700195, -0.60839844, -0.84155273, -0.9592285, -1.6604004, -0.19995117, 1.0625, 0.5427246, 0.9711914, -0.4333496, -0.3540039, -0.7167969, -1.3303223, 0.90478516, -2.4414062E-4, -0.6850586, -0.63964844, 0.06738281, 0.23925781, -0.8601074, -1.5437012, 1.0021973, 0.24902344, 1.9003906, 0.087402344, -0.0703125, 1.8327637, 0.5488281, -1.6616211, -1.7963867, -0.7282715, -1.6054688, -0.6887207, 0.48632812, 0.96875, 0.0036621094, -0.607666, 0.41967773, -0.29907227, 0.2668457, 1.767334, 0.61572266, -1.1040039, -0.70043945, -2.3120117, 0.7504883, 0.6491699, 0.89501953, 0.11450195, 0.4243164, -0.85913086, 0.1418457, 1.2324219, 0.2697754, -1.3044434, -0.57543945, -1.2233887, -0.74853516, -0.3479004, -0.3515625, 2.5117188, -0.63183594, -1.729248, 0.32006836, -2.090332, -0.8027344, -0.3935547, -0.8762207, 0.80615234, 0.6142578, 0.28076172, -0.5656738, 0.74316406, -1.3063965, 0.5932617, -0.35253906, -0.1262207, 1.5993652, -0.46362305, 1.2058105, 0.35742188, -0.6323242, 0.0034179688, 0.13793945, 3.0966797, -1.3847656, -0.44580078, 0.28393555, 0.717041, 0.18945312, -1.7805176, 1.6140137, 1.0566406, -0.97387695, -1.4069824, 0.23046875, -1.7243652, 1.223877, -0.9658203, 0.7788086, 1.638916, -2.2333984, 0.048339844, 0.73535156, -0.21972656, 0.21508789, 2.1228027, -0.9794922, -0.7858887, 0.14038086, -0.12060547, -0.4885254, -0.080322266, -0.46972656, -0.35839844, -1.6147461, -1.1137695, 0.20507812, 0.87890625, 0.14672852, 0.5065918, 1.6967773, -0.32373047, -0.37597656, -0.12084961, 0.97875977, -0.9909668, -1.2548828, 0.28564453, 1.1787109, -0.22021484, 1.4355469, 0.6203613, -0.36694336, -0.33666992, 0.7944336, 0.6533203, 0.07397461, -0.114990234, -1.5576172, -1.1762695, 1.1132812, -0.66748047, -0.010498047, 0.3552246, 0.9243164, 1.6630859, -1.1025391, 0.7902832, 1.779541, -0.07446289, 0.5605469, -0.3100586, 0.5397949, -1.2866211, 0.69628906, 1.1411133, 0.8696289, 0.26879883, -0.7470703, -0.036376953, -1.0129395, -0.5830078, -0.4399414, -0.3232422, -0.27368164, 0.51586914, 0.48779297, 0.115478516, -0.7434082, -1.0205078, -0.44726562, -1.4580078, 1.8308105, -1.0529785, -0.58032227";
        final String[] split = windowsFeature.split(",");
        float[] feature_widdows = new float[512];
        for (int i = 0; i < 512; i++) {
            feature_widdows[i] = Float.parseFloat(split[i].trim());
        }
        final float featureMix = faceTrack.compareFaceFeatureMix(feature1, feature_widdows);
        System.out.println("pair: " + featureMix);
    }

    private void compareIdcard(String path1, String path2) {

        float[] feature1 = getFeatureFromPath(path1);
        float[] feature2 = getFeatureFromPath(path2);

        final float featureMix = faceTrack.compareFaceFeatureCard(feature1, feature2);

        System.out.println("pair: " + featureMix);

    }

    private float[] getFeatureFromPath(String path) {
        final Bitmap bitmap1 = BitmapUtil.decodeScaleImage(path, 1000, 1000);
        final List<YMFace> ymFaces = faceTrack.detectMultiBitmap(bitmap1);
        if (ymFaces != null && ymFaces.size() > 0)
            return faceTrack.getFaceFeature(0);
        return null;
    }


    @org.junit.Test
    public void testFeature() {
        //peopleCardCompare();
//        peoplePeopleCompare();
    }

    @After
    public void releaseSource() {

    }

    /**
     * 获取一张图片上的人脸特征值测试
     */
    @Test
    public void getOneFeature() {
        String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/img/1_1.jpg";
        Bitmap targetBitmap = BitmapUtil.decodeScaleImage(imgPath, 1000, 1000);
        List<YMFace> ymFaces = faceTrack.detectMultiBitmap(targetBitmap);
        if (null != ymFaces && ymFaces.size() > 0) {
            float[] faceFeature = faceTrack.getFaceFeature(0);
        }
        int x = 0;
    }

    /**
     * 人证对比测试
     */
    private void peopleCardCompare() {
        Log.e(TAG, "start");
        float[] faceFeatureCard = null;
        float[] faceFeaturePeople = null;

        String imgPathCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_card/card.jpg";
        Bitmap card = BitmapUtil.decodeScaleImage(imgPathCard, 1000, 1000);
        List<YMFace> ymFacesCard = faceTrack.detectMultiBitmap(card);
        if (null != ymFacesCard && ymFacesCard.size() > 0) {
            faceFeatureCard = faceTrack.getFaceFeatureCard(0);
        }

        String imgPathPeople = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_card/people_4.jpg";
        Bitmap people = BitmapUtil.decodeScaleImage(imgPathPeople, 1000, 1000);
        List<YMFace> ymFacesPeople = faceTrack.detectMultiBitmap(people);
        if (null != ymFacesPeople && ymFacesPeople.size() > 0) {
            faceFeaturePeople = faceTrack.getFaceFeatureCard(0);
        }
        if (null != faceFeatureCard && faceFeatureCard.length > 0 && null != faceFeaturePeople && faceFeaturePeople.length > 0) {
            float result = faceTrack.compareFaceFeatureMix(faceFeatureCard, faceFeaturePeople);
            FileUtil.writeFile(out_people_card_compare_path, System.currentTimeMillis() + "\n", true);
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < faceFeatureCard.length; i++) {
                stringBuffer.append(faceFeatureCard[i]).append(" ");
            }
            FileUtil.writeFile(out_people_card_compare_path, imgPathCard + "特征值：" + stringBuffer.toString() + "\n", true);

            StringBuffer stringBuffer2 = new StringBuffer();
            for (int i = 0; i < faceFeaturePeople.length; i++) {
                stringBuffer2.append(faceFeatureCard[i]).append(" ");
            }
            FileUtil.writeFile(out_people_card_compare_path, imgPathPeople + "特征值：" + stringBuffer2.toString() + "\n", true);
            FileUtil.writeFile(out_people_card_compare_path, imgPathCard + ", " + imgPathPeople + ", " + "相似度：" + result + "\n", true);
        }
        Log.e(TAG, "finished");
    }


    /**
     * 多张人脸图片两两比较
     */
    private void peoplePeopleCompare() {
        Log.e(TAG, "start");
        int pictureCount = 13;//文件夹中照片总数
        float[] faceFeatureCard = null;
        float[] faceFeaturePeople = null;
        for (int i = 1; i < 13; i++) {
            String pictureName = "Anna_Kournikova_" + i;
            String imgPathCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShiMeiTai/img/" + pictureName + ".jpg";
            Bitmap card = BitmapUtil.decodeScaleImage(imgPathCard, 1000, 1000);
            List<YMFace> ymFacesCard = faceTrack.detectMultiBitmap(card);
            if (null != ymFacesCard && ymFacesCard.size() > 0) {
                faceFeatureCard = faceTrack.getFaceFeature(0);
            }
            for (int j = i + 1; j <= 13; j++) {
                String pictureName2 = "Anna_Kournikova_" + j;
                String imgPathPeople = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShiMeiTai/img/" + pictureName2 + ".jpg";
                Bitmap people = BitmapUtil.decodeScaleImage(imgPathPeople, 1000, 1000);
                List<YMFace> ymFacesPeople = faceTrack.detectMultiBitmap(people);
                if (null != ymFacesPeople && ymFacesPeople.size() > 0) {
                    faceFeaturePeople = faceTrack.getFaceFeature(0);
                }
                if (null != faceFeatureCard && faceFeatureCard.length > 0 && null != faceFeaturePeople && faceFeaturePeople.length > 0) {
                    float result = faceTrack.compareFaceFeatureMix(faceFeatureCard, faceFeaturePeople);
                    FileUtil.writeFile(out_people_card_compare_path, System.currentTimeMillis() + "\n", true);
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int m = 0; m < faceFeatureCard.length; m++) {
                        stringBuffer.append(faceFeatureCard[m]).append(" ");
                    }
                    FileUtil.writeFile(out_people_card_compare_path, imgPathCard + "特征值：" + stringBuffer.toString() + "\n", true);

                    StringBuffer stringBuffer2 = new StringBuffer();
                    for (int n = 0; n < faceFeaturePeople.length; n++) {
                        stringBuffer2.append(faceFeaturePeople[n]).append(" ");
                    }
                    FileUtil.writeFile(out_people_card_compare_path, imgPathPeople + "特征值：" + stringBuffer2.toString() + "\n", true);
                    FileUtil.writeFile(out_people_card_compare_path, imgPathCard + ", " + imgPathPeople + ", " + "相似度：" + result + "\n", true);
                }
            }
        }
        Log.e(TAG, "finished");
    }

    public boolean wordPattern(String pattern, String str) {
        char[] pts = pattern.toCharArray();
        final String[] strings = str.split(" ");
        boolean par = true;
        if (pts.length == strings.length) {
            for (int i = 0; i < pts.length; i++) {
                for (int i1 = i + 1; i1 < pts.length; i1++) {
                    if (!(pts[i] == pts[i1]) == (strings[i].equals(strings[i1]))) {
                        par = false;
                        break;
                    }
                }
            }
            return par;
        }
        return false;
    }
}
