package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.ws.gms.Consatants.CommonCode;

public class SettingsActivity extends AppCompatActivity {

    CommonCode commonCode = new CommonCode();

    RelativeLayout rlMyProfile,rlChangeLang, rlChangePass, rlAbout, rlFeedback;
    private SharedPreferences sharedPreferences;
    private String userEmailId, userRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        commonCode.updateLocaleIfNeeded(SettingsActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setSubtitle("");


        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        userEmailId = sharedPreferences.getString("username", "");
        userRole = sharedPreferences.getString("role", "");

        rlMyProfile = (RelativeLayout) findViewById(R.id.rl_my_profile);
        rlChangeLang = (RelativeLayout) findViewById(R.id.rl_change_lang);
        rlChangePass = (RelativeLayout) findViewById(R.id.rl_change_pass);
        rlAbout = (RelativeLayout) findViewById(R.id.rl_about);
        rlFeedback = (RelativeLayout) findViewById(R.id.rl_feedback);

        rlMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        rlChangeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangeLanguageActivity.class);
                startActivity(intent);
            }
        });

        rlChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        rlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        rlFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MailId = "sales@winnersoft.co.in";
                String From = userRole + "\n" + userEmailId;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", MailId, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "From :  " + From);
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.sendMail)));
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
