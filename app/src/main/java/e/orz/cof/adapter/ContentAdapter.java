package e.orz.cof.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import e.orz.cof.Content;
import e.orz.cof.R;

/**
 * Created by ORZ on 2018/6/14.
 */

public class ContentAdapter extends BaseAdapter {

    private Context context;
    private List<Content> contentList;

    public ContentAdapter(Context context, List<Content> contentList) {
        this.context = context;
        this.contentList = contentList;
    }

    @Override
    public int getCount() {
        return contentList.size();
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
        view = View.inflate(context, R.layout.content_item, null);
        ImageView portrait = view.findViewById(R.id.iv_portrait);
        TextView name = view.findViewById(R.id.tv_name);
        TextView textContent = view.findViewById(R.id.tv_content);
        GridView gridView = view.findViewById(R.id.gv_image);
        portrait.setImageURI(contentList.get(i).getPortrait());
        name.setText(contentList.get(i).getUserName());
        textContent.setText(contentList.get(i).getText());
        List<String> bitmapList = contentList.get(i).getPhotoList();
        ImageAdapter imageAdapter = new ImageAdapter(context, bitmapList);
        gridView.setAdapter(imageAdapter);
        return view;
    }
}
