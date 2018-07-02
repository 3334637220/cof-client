package e.orz.cof.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import e.orz.cof.R;
import e.orz.cof.model.Blog;
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

    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
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
        final EditText etComment = view.findViewById(R.id.et_comment);
        final ImageView ivLike = view.findViewById(R.id.iv_like);
        final LinearLayout llComment = view.findViewById(R.id.ll_comment);
        ImageView ivComment = view.findViewById(R.id.iv_comment);
        Button btComment = view.findViewById(R.id.bt_comment);
        if(blogList.get(i).getFaceUrl().isEmpty()){
            ivFace.setImageResource(R.drawable.pic);
        }else{
            Glide.with(view.getContext())
                    .load(NetUtil.BASE_URL + blogList.get(i).getFaceUrl())
                    .into(ivFace);
        }
        time.setText(blogList.get(i).getTime());
        name.setText(blogList.get(i).getUserName());
        textContent.setText(blogList.get(i).getText());

        final Activity activity = (Activity)view.getContext();
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUpNum.setVisibility(View.VISIBLE);
                llComment.clearFocus();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvUpNum.setVisibility(View.GONE);
                            }
                        });
                    }
                };
                new Timer().schedule(task,1000);
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llComment.setVisibility(View.VISIBLE);
                llComment.requestFocus();
                etComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if(!etComment.isFocused()) {
                            etComment.setText("");
                            llComment.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        final Blog blog = blogList.get(i);
        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = etComment.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        OkHttpClient mClient = NetUtil.getClient();
                        Request.Builder builder = new Request.Builder();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("blogId", blog.getBlogId()+"")
                                .add("userName", blog.getUserName())
                                .add("text", text)
                                .build();
                        Request request = builder.url(NetUtil.BASE_URL + "/addComment.do")
                                .post(requestBody).build();

                        try {
                            final Response response = mClient.newCall(request).execute();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(activity, response.body().string(), Toast.LENGTH_SHORT).show();
                                        llComment.setVisibility(View.GONE);
                                    } catch (IOException e) {
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



        List<String> pictureList = blogList.get(i).getPictures();
        ImageAdapter imageAdapter = new ImageAdapter(context, pictureList);
        gridView.setAdapter(imageAdapter);
        return view;
    }
}
