package e.orz.cof.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import e.orz.cof.R;
import e.orz.cof.model.Blog;
import e.orz.cof.util.NetUtil;

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
        Glide.with(view.getContext())
                .load(NetUtil.BASE_URL + blogList.get(i).getFaceUrl())
                .into(ivFace);
        name.setText(blogList.get(i).getUserName());
        textContent.setText(blogList.get(i).getText());
        List<String> pictureList = blogList.get(i).getPictures();
        ImageAdapter imageAdapter = new ImageAdapter(context, pictureList);
        gridView.setAdapter(imageAdapter);
        return view;
    }
}
