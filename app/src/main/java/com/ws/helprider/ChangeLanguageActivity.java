package com.ws.helprider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ws.helprider.Consatants.CommonCode;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    CommonCode commonCode = new CommonCode();

    Button btnChange;
    private String[] languages = {"English", "Marathi"};
    Locale locale;
    Configuration config; // variable declaration in globally

    public static final String LANGUAGE_SETTING = "lang_setting";
    public static final int LANGUAGE_CHANGED = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        commonCode.updateLocaleIfNeeded(ChangeLanguageActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.changeLanguage));
        getSupportActionBar().setSubtitle("");

        Spinner spnLang = (Spinner) findViewById(R.id.spn_sel_lang);
        btnChange = (Button) findViewById(R.id.btn_done);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLang.setAdapter(adapter);

        spnLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                config = new Configuration(getResources().getConfiguration());

                switch (arg2) {
                    case 0:
                        //config.locale = Locale.ENGLISH;
                        locale = new Locale("en");
                        break;
                    case 1:
                        //config.locale = Locale.FRENCH;
                        locale = new Locale("mr");
                        break;
                    case 2:
                        //  config.locale = Locale.ENGLISH;
                        locale = new Locale("hi");
                        break;
                    default:
                        //   config.locale = Locale.ENGLISH;
                        locale = new Locale("en");
                        break;
                }
            }

            public void onNothingSelected(AdapterView arg0) {
                // TODO Auto-generated method stub

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                Locale.setDefault(locale);
                Configuration config = getApplicationContext().getResources().getConfiguration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                settings.edit()
                        .putString(LANGUAGE_SETTING, locale.toString()).apply();

//                // Refresh the app
//                Intent refresh = new Intent(getActivity(), getActivity()
//                        .getClass());
//                startActivity(refresh);
//                setResult(LANGUAGE_CHANGED);
//                finish();

                // onBackPressed();

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.langChange), Toast.LENGTH_LONG).show();

                SharedPreferences sp = getApplicationContext().getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                sp.edit().clear().commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

                //changeLanguage();
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
