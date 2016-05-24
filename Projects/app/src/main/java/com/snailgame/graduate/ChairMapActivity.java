package com.snailgame.graduate;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.snailgame.graduate.R;

public class ChairMapActivity extends AppCompatActivity {

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chairmap);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        WebView chairs = (WebView)findViewById(R.id.webView);

        chairs.setWebChromeClient(new MyWebChromeClient());

        WebSettings settings = chairs.getSettings();
        settings.setJavaScriptEnabled(true);

        chairs.loadUrl("http://121.42.191.9:8088/Graduation-Project/Netbar/index.php/Main/mobile?id="+id);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
