package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ws.gms.Consatants.CommonCode;

public class AboutUsActivity extends AppCompatActivity {
    CommonCode commonCode = new CommonCode();
    TextView tvDesc1, tvDesc2;
    RelativeLayout rlRateUs;
    TextView HyperLink;
    Spanned Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        commonCode.updateLocaleIfNeeded(AboutUsActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.aboutUs));
        getSupportActionBar().setSubtitle("");

        tvDesc1 = (TextView) findViewById(R.id.tv_desc_one);
        tvDesc2 = (TextView) findViewById(R.id.tv_desc_two);

        HyperLink = (TextView)findViewById(R.id.tv_link);
        Text = Html.fromHtml("<a href='http://winnersoft.co.in//'>winnersoft.co.in</a>");

        HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
        HyperLink.setText(Text);


        tvDesc1.setText(Html.fromHtml(getResources().getString(R.string.app_name) +" for the complaints tracking and management."));

       // tvDesc2.setText(Html.fromHtml(getResources().getString(R.string.app_name)+ " and the "+" " +getResources().getString(R.string.app_name)+" Logos are trademark of Winner Software Pvt.Ltd. All rights reserved."));
        rlRateUs = (RelativeLayout) findViewById(R.id.rl_rate_us);

        rlRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String appPackageName = getApplicationContext().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
