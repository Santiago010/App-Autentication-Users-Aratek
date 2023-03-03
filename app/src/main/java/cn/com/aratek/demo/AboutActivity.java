package cn.com.aratek.demo;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import cn.com.aratek.dev.Terminal;

public class AboutActivity extends AppCompatActivity {

    private TextView mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mInfo = findViewById(R.id.info);
        mInfo.setText(Html.fromHtml(getString(R.string.about_info, getString(R.string.app_name),
                BuildConfig.VERSION_NAME, Terminal.getSdkVersion())));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
