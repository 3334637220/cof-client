package e.orz.cof.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import e.orz.cof.R;
import e.orz.cof.util.NetUtil;

/**
 * Created by ORZ on 2018/6/14.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> pictureList;

    public ImageAdapter(Context context, List<String> pictureList) {
        this.context = context;
        this.pictureList = pictureList;
    }

    @Override
    public int getCount() {
        return pictureList.size();
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
        view = View.inflate(context, R.layout.image_content_item, null);
        ImageView imageView = view.findViewById(R.id.image);
        Glide.with(view.getContext())
                .load(NetUtil.BASE_URL + pictureList.get(i))
                .into(imageView);
        return view;
    }
}
