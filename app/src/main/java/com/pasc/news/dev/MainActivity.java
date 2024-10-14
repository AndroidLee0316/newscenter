package com.pasc.news.dev;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.show_top_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goNewsList();
            }
        });
    }

    private void goNewsList() {

        Bundle bundle = new Bundle();
        bundle.putString("newscenter_column_type", "0");
        ARouter.getInstance()
                .build("/newscenter/main/list")
                .with(bundle)
                .navigation();
    }
}
