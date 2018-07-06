package e.orz.cof.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import e.orz.cof.R;
import e.orz.cof.util.NetUtil;

public class ImageActivity extends Activity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        image = findViewById(R.id.iv_image_big);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        Glide.with(this)
                .load(NetUtil.BASE_URL+url)
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
