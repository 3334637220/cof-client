package e.orz.cof.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import e.orz.cof.R;
import e.orz.cof.adapter.PubImageAdapter;
import e.orz.cof.model.Blog;
import e.orz.cof.model.Comment;
import e.orz.cof.model.User;
import e.orz.cof.util.ImageUtil;
import e.orz.cof.util.LocationApplication;
import e.orz.cof.util.LocationService;
import e.orz.cof.util.NetUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishActivity extends Activity {

    private Button btSelect;
    private GridView gridView;
    private PubImageAdapter imageAdapter;
    private Button btCancel;
    private Button btPublish;
    private EditText etText;
    private static final int REQUEST_CODE_CHOOSE = 0x1;
    private static final int PUBLISH_CODE = 0X2;
    private List<Uri> mSelected;
    private User user;
    private Handler handler;
    private LocationService locationService;
    private Spinner spLocation;
    private ArrayList<String> poiList;
    ArrayAdapter<String> locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        mSelected = new ArrayList<>();
        btSelect = findViewById(R.id.bt_select);
        btSelect.setOnClickListener(new BtSelectListener());
        gridView = findViewById(R.id.gv_image);
        imageAdapter = new PubImageAdapter(this, mSelected);
        gridView.setAdapter(imageAdapter);
        btPublish = findViewById(R.id.bt_publish);
        btCancel = findViewById(R.id.bt_cancel);
        etText = findViewById(R.id.et_text);
        btCancel.setOnClickListener(new BtCancelListener());
        btPublish.setOnClickListener(new BtPublishListener());
        user = (User) getIntent().getSerializableExtra("user");
        handler = new NoLeakHandler(this);

        spLocation = findViewById(R.id.sp_location);
        poiList = new ArrayList<>();
        poiList.add("选择位置");
        locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, poiList);
        spLocation.setAdapter(locationAdapter);

    }

    class BtSelectListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Matisse.from(PublishActivity.this)
                    .choose(MimeType.allOf()) // 选择 mime 的类型
                    .countable(true)
                    .maxSelectable(9) // 图片选择的最多数量
                    .gridExpectedSize(300)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f) // 缩略图的比例
                    .imageEngine(new GlideEngine()) // 使用的图片加载引擎
                    .forResult(REQUEST_CODE_CHOOSE);
        }
    }

    class BtCancelListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            finish();
        }
    }

    class BtPublishListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {
                    OkHttpClient mClient = NetUtil.getClient();
                    Request.Builder builder = new Request.Builder();
                    RequestBody requestBody = RequestBody.create(NetUtil.FORM_CONTENT_TYPE,
                            "userName=" + user.getUserName() +
                                    "&text=" + etText.getText() +
                                    "&location="+spLocation.getSelectedItem());
                    Request request = builder.url(NetUtil.BASE_URL + "/addBlog.do")
                            .post(requestBody).build();

                    try {
                        Response response = mClient.newCall(request).execute();
                        Message msg = new Message();
                        msg.what = PUBLISH_CODE;
                        msg.obj = response.body().string();
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected.clear();
            mSelected.addAll(Matisse.obtainResult(data));
            imageAdapter.notifyDataSetChanged();
            Log.d("Matisse", "mSelected: " + mSelected);
        }
    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            locationService.stop();
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = location.getPoiList().get(i);
                        poiList.add(poi.getName());
                    }
                }
            }
            locationAdapter.notifyDataSetChanged();

        }

    };

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((LocationApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();
    }

    private static class NoLeakHandler extends Handler {
        private WeakReference<PublishActivity> mActivity;

        private NoLeakHandler(PublishActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PUBLISH_CODE:
                    try {
                        JSONObject jo = new JSONObject(msg.obj.toString());
                        if (!"ok".equals(jo.getString("status"))) {
                            mActivity.get().makeToast("发表失败");
                        } else {
                            int blogId = jo.getInt("blogId");
                            List<Uri> mSelected = mActivity.get().mSelected;
                            for (int i = 0; i < mActivity.get().mSelected.size(); i++) {
                                String path = ImageUtil.uriToPath(mActivity.get(), mSelected.get(i));
                                mActivity.get().uploadImage(path, blogId);
                            }
                            mActivity.get().makeToast("发表成功");
                            Thread.sleep(500);
                            mActivity.get().finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(msg.obj.toString());
                    break;
            }
        }
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void uploadImage(String imagePath, int blogId) {
        new PublishActivity.NetWorkTask(blogId).execute(imagePath);
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetWorkTask extends AsyncTask<String, Integer, String> {
        int blogId;

        public NetWorkTask(int blogId) {
            this.blogId = blogId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(final String result) {
            if (!"error".equals(result)) {
                Log.i("LoginActivity", "图片地址 " + NetUtil.BASE_URL + result);
                new Thread() {
                    @Override
                    public void run() {
                        OkHttpClient mClient = NetUtil.getClient();
                        Request.Builder builder = new Request.Builder();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("blogId", blogId + "")
                                .add("imageUrl", result)
                                .build();
                        Request request = builder.url(NetUtil.BASE_URL + "/addPicture.do")
                                .post(requestBody).build();
                        try {
                            Response response = mClient.newCall(request).execute();
                            System.out.println(response.body().string() + result);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        }
    }

    public String doPost(String imagePath) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("image", imagePath,
                RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(NetUtil.BASE_URL + "/upload.do")
                .post(requestBody)
                .build();

        Log.d("LoginActivity", "请求地址 " + NetUtil.BASE_URL + "/upload.do");
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Log.d("LoginActivity", "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d("LoginActivity", "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
