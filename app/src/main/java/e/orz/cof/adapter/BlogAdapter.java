package e.orz.cof.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import e.orz.cof.R;
import e.orz.cof.activity.ImageActivity;
import e.orz.cof.activity.MainActivity;
import e.orz.cof.model.Blog;
import e.orz.cof.model.Comment;
import e.orz.cof.model.User;
import e.orz.cof.util.NetUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ORZ on 2018/6/14.
 */

public class BlogAdapter extends BaseAdapter {

    private Context context;
    private List<Blog> blogList;
    private User user;
    private MainActivity mainActivity;
    private float mPosY;
    private float mPosX;

    public BlogAdapter(Context context, List<Blog> blogList, User user, MainActivity mainActivity) {
        this.context = context;
        this.blogList = blogList;
        this.user = user;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return blogList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.blog_item, null);
        ImageView ivFace = view.findViewById(R.id.iv_face);
        TextView name = view.findViewById(R.id.tv_name);
        TextView textContent = view.findViewById(R.id.tv_content);
        GridView gridView = view.findViewById(R.id.gv_image);
        TextView time = view.findViewById(R.id.tv_time);
        final TextView tvUpNum = view.findViewById(R.id.tv_up_num);
        final TextView tvCommentNum = view.findViewById(R.id.tv_comment_num);
        final EditText etComment = view.findViewById(R.id.et_comment);
        final ImageView ivLike = view.findViewById(R.id.iv_like);
        final LinearLayout llComment = view.findViewById(R.id.ll_comment);
        final ListView lvComment = view.findViewById(R.id.lv_comment);
        ImageView ivComment = view.findViewById(R.id.iv_comment);
        Button btComment = view.findViewById(R.id.bt_comment);
        Button btDel = view.findViewById(R.id.bt_del_blog);

        final Activity activity = (Activity) view.getContext();
        final Blog blog = blogList.get(i);
        final View vSplit = view.findViewById(R.id.v_split);
        final ImageView ivBg = view.findViewById(R.id.iv_bg);
        final Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        final ImageView ivLoding = view.findViewById(R.id.iv_loading);

        TextView tvLocation = view.findViewById(R.id.tv_location);
        tvLocation.setText(blog.getLocation());

        if (i == 0) {
            ivBg.setVisibility(View.VISIBLE);
            Glide.with(view.getContext())
                    .load(NetUtil.BASE_URL + user.getFaceUrl())
                    .into(ivBg);

        }
        // 背景图滑动监听 刷新
        ivBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float curPosX = event.getX();
                        float curPosY = event.getY();
                        if (curPosY - mPosY > 0 && Math.abs(curPosX - mPosX) < 10){
                            ivLoding.setVisibility(View.VISIBLE);
                            ivLoding.startAnimation(animation);
                            Log.i("BlogAdapter", "刷新");
                            mainActivity.loadData();
                        }
                        break;
                }


                return true;
            }
        });

        if (blog.getFaceUrl().isEmpty()) {
            ivFace.setImageResource(R.drawable.pic);
        } else {
            Glide.with(view.getContext())
                    .load(NetUtil.BASE_URL + blog.getFaceUrl())
                    .into(ivFace);
        }
        if (blog.getLike()) {
            ivLike.setImageResource(R.drawable.liked);
        } else {
            ivLike.setImageResource(R.drawable.like);
        }

        time.setText(blog.getTime());
        name.setText(blog.getUserName());
        textContent.setText(blog.getText());
        tvUpNum.setText(blog.getUpNum() + "");
        tvCommentNum.setText(blog.getComments().size() + "");
        final ViewGroup.LayoutParams params = lvComment.getLayoutParams();
        params.height += blog.getComments().size() * 48;


        if (!blog.getMine()) {
            btDel.setVisibility(View.INVISIBLE);
        } else {
            btDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread() {
                        @Override
                        public void run() {
                            OkHttpClient mClient = NetUtil.getClient();
                            Request.Builder builder = new Request.Builder();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("blogId", blog.getBlogId() + "")
                                    .add("userName", user.getUserName())
                                    .build();
                            Request request = builder.url(NetUtil.BASE_URL + "/delBlog.do")
                                    .post(requestBody).build();

                            try {
                                final Response response = mClient.newCall(request).execute();
                                final String msg = response.body().string();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                        mainActivity.loadData();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            });
        }


        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        OkHttpClient mClient = NetUtil.getClient();
                        Request.Builder builder = new Request.Builder();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("blogId", blog.getBlogId() + "")
                                .add("userName", user.getUserName())
                                .build();
                        Request request = builder.url(NetUtil.BASE_URL + "/updateLike.do")
                                .post(requestBody).build();
                        try {
                            final Response response = mClient.newCall(request).execute();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jo = new JSONObject(response.body().string());
                                        String type = "";
                                        int upNum = 0;
                                        if ("ok".equals(jo.getString("status"))) {
                                            type = jo.getString("type");
                                            upNum = jo.getInt("upNum");
                                            blog.setUpNum(upNum);
                                        } else {
                                            Toast.makeText(activity, "点赞异常", Toast.LENGTH_SHORT).show();
                                        }
                                        tvUpNum.setText(upNum + "");

                                        if ("点赞".equals(type)) {
                                            ivLike.setImageResource(R.drawable.liked);
                                        } else {
                                            ivLike.setImageResource(R.drawable.like);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        // 评论图片事件
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llComment.getVisibility() != View.VISIBLE) {
                    llComment.setVisibility(View.VISIBLE);
                    vSplit.setVisibility(View.VISIBLE);
                } else {
                    llComment.setVisibility(View.GONE);
                    vSplit.setVisibility(View.GONE);
                }

            }
        });

        final List<String> pictureList = blog.getPictures();
        ImageAdapter imageAdapter = new ImageAdapter(context, pictureList);

        // 图片列表事件监听
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                intent.putExtra("url", pictureList.get(i));
                view.getContext().startActivity(intent);
            }
        });
        if(pictureList.size()>5){
            ViewGroup.LayoutParams params1 = gridView.getLayoutParams();
            params1.height += 340;
        }



        final List<Comment> commentList = blog.getComments();
        final CommentAdapter commentAdapter = new CommentAdapter(context, commentList);
        lvComment.setAdapter(commentAdapter);

        // 评论按钮事件监听
        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String text = etComment.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        OkHttpClient mClient = NetUtil.getClient();
                        Request.Builder builder = new Request.Builder();
                        RequestBody requestBody = RequestBody.create(NetUtil.FORM_CONTENT_TYPE,
                                "blogId=" + blog.getBlogId()
                                        + "&userName=" + user.getUserName()
                                        + "&text=" + text);
                        Request request = builder.url(NetUtil.BASE_URL + "/addComment.do")
                                .post(requestBody).build();

                        try {
                            final Response response = mClient.newCall(request).execute();
                            final String msg = response.body().string();
                            commentList.add(new Comment(blog.getBlogId(), user.getUserName(), text));

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                    tvCommentNum.setText(commentList.size() + "");
                                    params.height += 48;
//                                    llComment.setVisibility(View.GONE);
//                                    vSplit.setVisibility(View.GONE);
                                    etComment.setText("");
                                    commentAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });
        return view;
    }
}
