package e.orz.cof.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import e.orz.cof.R;
import e.orz.cof.model.Comment;

/**
 * Created by ORZ on 2018/7/3.
 */

public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        return commentList.size();
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
        view = View.inflate(context, R.layout.comment_item, null);
        TextView tvComment = view.findViewById(R.id.tv_comment);
        Comment comment = commentList.get(commentList.size()-1-i);
        String userName = comment.getUserName();
        String text = comment.getText();
        tvComment.setText(userName+": "+text);

        return view;
    }
}
