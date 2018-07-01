package e.orz.cof.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import e.orz.cof.R;

/**
 * Created by ORZ on 2018/6/14.
 */

public class ImageAdapter extends BaseAdapter{
    private Context context;
    private List<String> bitmapList;

    public ImageAdapter(Context context, List<String> bitmapList){
        this.context = context;
        this.bitmapList = bitmapList;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
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
//        imageView.setImageURL(bitmapList.get(i));
        return view;
    }
}
