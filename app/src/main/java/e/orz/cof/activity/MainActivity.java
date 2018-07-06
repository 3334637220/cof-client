package e.orz.cof.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import e.orz.cof.R;
import e.orz.cof.adapter.BlogAdapter;
import e.orz.cof.model.Blog;
import e.orz.cof.model.Comment;
import e.orz.cof.model.User;
import e.orz.cof.util.NetUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {
    private static final int LOAD_SUCCESS = 0x1;
    private ListView listView;
    private ImageView ivExit;
    private ImageView ivPublish;
    private List<Blog> blogList;
    private User user;
    private NoLeakHandler handler;
    private BlogAdapter blogAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new NoLeakHandler(this);
        listView = findViewById(R.id.lv_content);
        ivExit = findViewById(R.id.iv_exit);
        ivPublish = findViewById(R.id.iv_publish);
        ivExit.setOnClickListener(new IvExitListener());
        ivPublish.setOnClickListener(new IvPublishListener());
        blogList = new ArrayList<>();
        user = (User) getIntent().getSerializableExtra("user");
        blogAdapter = new BlogAdapter(this, blogList, user, this);
        listView.setAdapter(blogAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient mClient = NetUtil.getClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormBody.Builder()
                        .add("userName", user.getUserName())
                        .build();
                Request request = builder.url(NetUtil.BASE_URL + "/getBlogs.do")
                        .post(requestBody)
                        .build();
                try {
                    Response response = mClient.newCall(request).execute();
                    Message msg = new Message();
                    msg.what = LOAD_SUCCESS;
                    msg.obj = response.body().string();
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private static class NoLeakHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        private NoLeakHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    try {
                        mActivity.get().blogList.clear();
                        JSONArray ja = new JSONArray(msg.obj.toString());
                        for (int i = ja.length() - 1; i >= 0; i--) {
                            JSONObject jo = ja.getJSONObject(i);
                            Blog blog = new Blog();
                            blog.setBlogId(jo.getInt("blogId"));
                            blog.setUserName(jo.getString("userName"));
                            blog.setText(jo.getString("text"));
                            blog.setUpNum(jo.getInt("upNum"));
                            if (jo.getString("time").isEmpty() || jo.getString("time").equals("1秒前")) {
                                blog.setTime("刚刚");
                            } else {
                                blog.setTime(jo.getString("time"));
                            }
                            blog.setFaceUrl(jo.getString("faceUrl"));
                            blog.setLike(jo.getBoolean("isLike"));
                            blog.setMine(jo.getBoolean("isMine"));
                            JSONArray pictureJa = jo.getJSONArray("pictures");
                            ArrayList<String> pictures = new ArrayList<>();
                            for (int j = 0; j < pictureJa.length(); j++) {
                                pictures.add(pictureJa.getString(j));
                            }
                            JSONArray commentJa = jo.getJSONArray("comments");
                            ArrayList<Comment> comments = new ArrayList<>();
                            for (int j = 0; j < commentJa.length(); j++) {
                                Comment comment = new Comment();
                                comment.setBlogId(blog.getBlogId());
                                comment.setUserName(commentJa.getJSONObject(j).getString("userName"));
                                comment.setText(commentJa.getJSONObject(j).getString("text"));
                                comments.add(comment);
                            }
                            blog.setPictures(pictures);
                            blog.setComments(comments);
                            mActivity.get().blogList.add(blog);
                        }
                        Log.i("MainActivity", "更新blog" + mActivity.get().blogList.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mActivity.get().blogAdapter.notifyDataSetChanged();

                    break;
            }
        }
    }

    class IvExitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    class IvPublishListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, PublishActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }



}
