package com.rabita.shabab.aqsa2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by macbookair on 8/14/15.
 */
public class Article_activity extends AppCompatActivity {

    public static String TAG="AqsaProject";
    private ImageView imageView;
    private String image;
    private TextView articleText;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        imageView = (ImageView) findViewById(R.id.imageView);
        articleText =(TextView) findViewById(R.id.Article);
        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        content = intent.getStringExtra("content");
        byte[] imageAsBytes = Base64.decode(image, Base64.DEFAULT);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        articleText.setText(content);

    }

}
