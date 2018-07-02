package e.orz.cof.adapter;

import android.content.Context;
import android.net.Uri;
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

public class PubImageAdapter extends BaseAdapter {

    private Context context;
    private List<Uri> imageUriList;

    public PubImageAdapter(Context context, List<Uri> imageUriList) {
        this.context = context;
        this.imageUriList = imageUriList;
    }

    @Override
    public int getCount() {
        return imageUriList.size();
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
        view = View.inflate(context, R.layout.image_item_layout, null);
        ImageView image = view.findViewById(R.id.iv_image);
        Glide.with(view.getContext())
                .load(imageUriList.get(i))
                .into(image);
        return view;
    }
}
