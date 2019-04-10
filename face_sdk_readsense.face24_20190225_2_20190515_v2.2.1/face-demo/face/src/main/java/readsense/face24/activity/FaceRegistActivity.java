package readsense.face24.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.readsense.cameraview.dialog.BindViewHolder;
import com.readsense.cameraview.dialog.OnBindViewListener;
import com.readsense.cameraview.dialog.OnViewClickListener;
import com.readsense.cameraview.dialog.TDialog;

import java.util.ArrayList;

import readsense.face24.R;
import readsense.face24.activity.base.BaseActivity;
import readsense.face24.activity.register.RegistFromBatchPicActivity;
import readsense.face24.activity.register.RegistFromCamAcitvity;
import readsense.face24.activity.register.RegistFromPicActivity;
import readsense.face24.adapter.OnItemClickListener;
import readsense.face24.adapter.UserListAdapter;
import readsense.face24.dataSource.UserDataUtil;
import readsense.face24.faceSdk.FaceSet;
import readsense.face24.modle.User;

public class FaceRegistActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {
    private RecyclerView rcv_head;
    private UserListAdapter adapter = null;
    private ArrayList<User> userList = null;
    private ImageButton registCam;
    private ImageButton faceRec;


    private FaceSet faceSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_face_regist);
        super.onCreate(savedInstanceState);
        userList = UserDataUtil.updateDataSource();
        adapter = new UserListAdapter(userList);
        rcv_head = findViewById(R.id.rcv_head);
        rcv_head.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcv_head.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        registCam = findViewById(R.id.regist_cam);
        registCam.setOnClickListener(this);
        faceRec = findViewById(R.id.face_rec);
        faceRec.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        faceSet = new FaceSet(getApplication());
        faceSet.startTrack(270);
        userList = UserDataUtil.updateDataSource();
        adapter.updateData(userList);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (faceSet != null) {
            faceSet.stopTrack();
        }
        super.onPause();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initEvent() {

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist_cam:
                Intent camIntent = new Intent(FaceRegistActivity.this, RegistFromCamAcitvity.class);
                startActivityForResult(camIntent, 1000);
                break;
            case R.id.face_rec:
                Intent faceDemoIntent = new Intent(FaceRegistActivity.this, FaceDemoActivity.class);
                startActivity(faceDemoIntent);
//                Intent picIntent = new Intent(FaceRegistActivity.this, RegistFromPicActivity.class);
//                startActivityForResult(picIntent, 1000);
                break;
//            case R.id.regist_batch:
//                Intent batchIntent = new Intent(FaceRegistActivity.this, RegistFromBatchPicActivity.class);
//                startActivityForResult(batchIntent, 1000);
//                break;
//            case R.id.all_delete:
//                if (faceSet.getPersonCount() == 0) {
//                    showShortToast(FaceRegistActivity.this, "当前无录入人脸");
//                    return;
//                }
//                new TDialog.Builder(getSupportFragmentManager())
//                        .setLayoutRes(R.layout.dialog_regist)
//                        .setHeight(600)
//                        .setGravity(Gravity.CENTER)
//                        .setScreenWidthAspect(this, 0.9f)
//                        .addOnClickListener(R.id.regist_cancle, R.id.regist_confirm)
//                        .setCancelableOutside(true)
//                        .setOnBindViewListener(new OnBindViewListener() {
//                            @Override
//                            public void bindView(BindViewHolder viewHolder) {
//                                TextView txShow = viewHolder.getView(R.id.regist_tx_show);
//                                txShow.setText(String.format("是否确定删除所有已注册用户？"));
//                            }
//                        })
//                        .setOnViewClickListener(new OnViewClickListener() {
//                            @Override
//                            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
//                                if (view.getId() == R.id.regist_confirm) {
//                                    if (faceSet.removeAllUser()) {
//                                        Log.e("Rs", "全部删除成功");
//                                        userList = UserDataUtil.updateDataSource();
//                                        adapter.updateData(userList);
//                                    }
//                                    tDialog.dismiss();
//                                }
//                            }
//                        })
//                        .create()
//                        .show();
//                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(FaceRegistActivity.this, FaceDemoActivity.class); //注：抽取注册模块，注销代码
//            startActivityForResult(intent, 1000);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10000) {
            //刷新一次
            userList = UserDataUtil.updateDataSource();
            adapter.updateData(userList);
        }
    }

    /**
     * 删除
     *
     * @param position 点击的位置
     */
    @Override
    public void onClick(int position) {
        User user = userList.get(position);
        if (user != null && user.getPersonId() != null) {
            if (faceSet.removeUserByPersonId(Integer.valueOf(user.getPersonId()))) {
                Log.e("Rs", "删除成功");
            }
            userList = UserDataUtil.updateDataSource();
            adapter.updateData(userList);
        }
    }
}
