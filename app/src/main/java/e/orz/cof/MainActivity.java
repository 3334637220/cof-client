package e.orz.cof;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import e.orz.cof.adapter.ContentAdapter;

public class MainActivity extends AppCompatActivity{
    private final int LOAD_SUCCESS = 0x123;
    private ListView listView;
    private ImageView background;
    List<Content> contentList;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.lv_content);
        background = findViewById(R.id.iv_background);
        contentList = new ArrayList<>();

        final ContentAdapter contentAdapter = new ContentAdapter(this, contentList);
        listView.setAdapter(contentAdapter);

    }



    private class LoadData extends Thread{
        @Override
        public void run(){
            try {
                URL url = new URL("http://192.168.136.1:8080/CircleOfFriends/getData");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                int code = connection.getResponseCode();
                if(code==HttpURLConnection.HTTP_OK){
                    InputStream is = connection.getInputStream();
                    String result = inputStream2String(is);
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Content content = new Content(jo.getString("userName"), Uri.parse(jo.getString("portrait")), jo.getString("text"), jo.getInt("upNum"));
                        JSONArray comments = jo.getJSONArray("comments");
                        for(int j=0;j<comments.length();j++){
                            content.addComment(comments.getString(j));
                        }
                        JSONArray photos = jo.getJSONArray("photos");
                        for(int j=0;j<comments.length();j++){
                            content.addPhoto(photos.getString(j));
                        }
                        contentList.add(content);
                    }

                }
                handler.sendEmptyMessage(LOAD_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String inputStream2String(InputStream in) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(in, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("发布");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, PublishActivity.class);
        startActivityForResult(intent, 1);
        return super.onOptionsItemSelected(item);
    }


}
