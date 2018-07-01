package e.orz.cof.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import e.orz.cof.R;
import e.orz.cof.model.User;
import e.orz.cof.util.NetUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private static final int LOGIN_CODE = 0x1;
    private static final int REGISTER_CODE = 0x2;
    private static final int PICK_IMAGE_CODE = 0x3;
    private EditText etUserName;
    private EditText etPassword;
    private Button btLogin;
    private Button btRegister;
    private ImageView ivFace;
    private Handler handler;
    private String faceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_register);
        ivFace = findViewById(R.id.iv_face);
        faceUrl = "";
        btLogin.setOnClickListener(new BtLoginListener());
        btRegister.setOnClickListener(new BtRegisterListener());
        ivFace.setOnClickListener(new IvFaceListener());
        handler = new NoLeakHandler(this);


    }

    class BtLoginListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {
                    OkHttpClient mClient = NetUtil.getClient();
                    Request.Builder builder = new Request.Builder();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName", etUserName.getText().toString())
                            .add("password", etPassword.getText().toString())
                            .build();
                    Request request = builder.url(NetUtil.BASE_URL + "/login.do")
                            .post(requestBody).build();
                    try {
                        Response response = mClient.newCall(request).execute();
                        Message msg = new Message();
                        msg.what = LOGIN_CODE;
                        msg.obj = response.body().string();
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
    }

    class BtRegisterListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            new Thread() {
                @Override
                public void run() {
                    OkHttpClient mClient = NetUtil.getClient();
                    Request.Builder builder = new Request.Builder();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userName", etUserName.getText().toString())
                            .add("password", etPassword.getText().toString())
                            .add("faceUrl", faceUrl)
                            .build();
                    Request request = builder.url(NetUtil.BASE_URL + "/register.do")
                            .post(requestBody).build();
                    try {
                        Response response = mClient.newCall(request).execute();
                        Message msg = new Message();
                        msg.what = REGISTER_CODE;
                        msg.obj = response.body().string();
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    class IvFaceListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_CODE);
        }
    }


    private static class NoLeakHandler extends Handler {
        private WeakReference<LoginActivity> mActivity;

        private NoLeakHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_CODE:
                    try {
                        JSONObject jo = new JSONObject(msg.obj.toString());
                        if(!"ok".equals(jo.getString("status"))){
                            mActivity.get().makeToast("用户名或密码错误");
                        }else{
                            User user = new User();
                            user.setUserName(jo.getString("userName"));
                            user.setPassword(jo.getString("password"));
                            user.setFaceUrl(jo.getString("faceUrl"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(msg.obj.toString());
                    break;
                case REGISTER_CODE:
                    mActivity.get().makeToast(msg.obj.toString());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        uploadImage(path);
                        System.out.println(path);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }




    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void uploadImage(String imagePath) {
        new UploadTask().execute(imagePath);
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class UploadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadPicture(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!"error".equals(result)) {
                Log.i("LoginActivity", "图片地址 " + NetUtil.BASE_URL + result);
                Glide.with(LoginActivity.this)
                        .load(NetUtil.BASE_URL + result)
                        .into(ivFace);
                faceUrl = result;
            }
        }
    }

    public String uploadPicture(String imagePath) {
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
