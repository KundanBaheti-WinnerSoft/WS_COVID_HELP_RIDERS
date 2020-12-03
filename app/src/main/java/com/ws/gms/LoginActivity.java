package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    Button btnLogin;
    private String userName = "", password = "";
    private SharedPreferences sharedPreferences;
    TextView tvNewRegi, tvforgotPassword;
    private ProgressDialog progressDialog;
    CommonCode commonCode = new CommonCode();
    Urllink urllink = new Urllink();
    String SecurityToken, role, id, userid,msg;
    Dialog myDialog;


    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDialog = new Dialog(this);

        updateLocaleIfNeeded();

        getSupportActionBar().setTitle(getResources().getString(R.string.login));
        getSupportActionBar().setSubtitle("");

        chechAppUpdate();

        edtUserName = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        edtUserName.setFocusable(false);
        edtUserName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                edtUserName.setFocusableInTouchMode(true);

                return false;
            }
        });

        edtPassword.setFocusable(false);
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                edtPassword.setFocusableInTouchMode(true);

                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chechAppUpdate();
            }
        });

        tvNewRegi = (TextView) findViewById(R.id.tvNewRegi);
        tvNewRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        tvforgotPassword = (TextView) findViewById(R.id.tvForgotPass);
        tvforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ActivityForgetPassword.class);
                startActivity(intent);
                // finish();
            }
        });

        checkAndRedirect();
    }


    public void chechAppUpdate() {
        try {
            VersionChecker versionChecker = new VersionChecker();
            String versionUpdated = versionChecker.execute().get().toString();

            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            int version_code = packageInfo.versionCode;
            String version_name = packageInfo.versionName;
            if (!version_name.equals(versionUpdated)) {
                packageName = getApplicationContext().getPackageName();
                updateAppPopup();
            } else {
                validation();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void updateAppPopup() {
        Button btnUpdate;
        myDialog.setContentView(R.layout.dialog_update);

        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        btnUpdate = (Button) myDialog.findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en"));
                startActivity(intent);
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void validation() {
        if (edtUserName.getText().toString().trim().equals("")) {
            edtUserName.setFocusableInTouchMode(true);
            edtUserName.setError(getResources().getString(R.string.thisFieldCannotBlank));
            edtUserName.requestFocus();
        } else if (edtPassword.getText().toString().trim().equals("")) {
            edtPassword.setFocusableInTouchMode(true);
            edtPassword.setError(getResources().getString(R.string.thisFieldCannotBlank));
            edtPassword.requestFocus();
        } else {
            userName = edtUserName.getText().toString();
            password = edtPassword.getText().toString();

            if ((!(userName.equals("")) && ((!password.equals(""))))) {
                save();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
//http://192.168.0.146:8081/hrms/user/authenticate
    public void save() {
        if (commonCode.checkConnection(LoginActivity.this)) {
            String jsonurl = urllink.url + "user/authenticate";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            Map<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("username", userName);
            jsonParams.put("password", password);
            //    jsonParams.put("msgtoken", newToken);
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, jsonurl, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    String re = response.toString();
                    try {

                        SecurityToken = (String) response.getString("token");
                        msg = (String) response.getString("msg");
                        if (msg.equalsIgnoreCase("Invalid UserName Or Password")) {
                            commonCode.AlertDialog_Pbtn(LoginActivity.this, getResources().getString(R.string.incorrectUsernamePassword), getResources().getString(R.string.plsEnterCorrectUsernamePassword), getResources().getString(R.string.ok));
                        }else if (msg.equalsIgnoreCase("Expire")){
                            deleteAlertDialog(getResources().getString(R.string.deactivated), getResources().getString(R.string.deactivateduser), getResources().getString(R.string.ok));

                        } else {
                            JSONArray array = response.getJSONArray("roleNames");
                            JSONObject object = array.getJSONObject(0);
                            JSONObject object1 = object.getJSONObject("role");
                            role = object1.getString("roleName");
                            id = object1.getString("id");
                            JSONObject userRolejsonobject = object.getJSONObject("userRole");
                            userid = userRolejsonobject.getString("id");

                            getLoginDetails();


                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //startActivity(intent);
                            //finish();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                }
            });
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);
        } else {
            commonCode.AlertDialog_Pbtn(LoginActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }
    public void deleteAlertDialog(String Title, String Message, String ok) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(Title);
            builder.setIcon(R.drawable.warning_sign);
            builder.setMessage(Message);


            builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.create();
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateLocaleIfNeeded() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(ChangeLanguageActivity.LANGUAGE_SETTING)) {
            String locale = sharedPreferences.getString(
                    ChangeLanguageActivity.LANGUAGE_SETTING, "");
            Locale localeSetting = new Locale(locale);

            if (!localeSetting.equals(Locale.getDefault())) {
                Resources resources = getResources();
                Configuration conf = resources.getConfiguration();
                conf.locale = localeSetting;
                resources.updateConfiguration(conf,
                        resources.getDisplayMetrics());

//                Intent refresh = new Intent(this, LoginActivity.class);
//                startActivity(refresh);
//                finish();
            }
        }
    }

    private void getLoginDetails() {
        if (commonCode.checkConnection(LoginActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "user/getByLoginName";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    progressDialog.dismiss();
                    try {

                        String userId = response.getString("id");
                        String mobileno = response.getString("mobileno");
                        String firstName = response.getString("firstname");
                        String middleName = response.getString("middlename");
                        String lastName = response.getString("lastname");
                        String emailId = response.getString("emailid");
                        String address = response.getString("address");

                        String birthdate = response.getString("birthdate");
                        String country = response.getString("country");
                        String state = response.getString("state");
                        String city = response.getString("city");
                        String gender = response.getString("gender");
                        String pincode = response.getString("pincode");
                        String image = response.getString("image");
                        String area = response.getString("area");
                        JSONObject userObj = response.getJSONObject("userId");
                        boolean inactive_deactivate = userObj.getBoolean("inactive_deactivate");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("securitytoken", SecurityToken);
                        editor.putString("username", userName);
                        editor.putString("password", password);
                        editor.putString("role", role);
                        editor.putString("userid", userid);
                        editor.putString("teacher_id", id);

                        //userInfo
                        editor.putString("userId", userId);
                        editor.putString("firstName", firstName);
                        editor.putString("middleName", middleName);
                        editor.putString("lastName", lastName);
                        editor.putString("mobileno", mobileno);
                        editor.putString("emailId", emailId);
                        editor.putString("address", address);

                        editor.putString("birthdate", birthdate);
                        editor.putString("country", country);
                        editor.putString("state", state);
                        editor.putString("city", city);
                        editor.putString("gender", gender);
                        editor.putString("pincode", pincode);
                        editor.putString("image", image);
                        editor.putString("area", area);
                        editor.putBoolean("activeDeactive", inactive_deactivate);

                        editor.commit();

                        checkAndRedirect();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                }
            })
                    //This is for Headers If You Needed
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("x-Auth-token", SecurityToken);
                    return params;
                }
            };
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(LoginActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    public void checkAndRedirect() {
        if (sharedPreferences.contains("username") && (sharedPreferences.contains("password"))) {
            String userTypeName = sharedPreferences.getString("role", "");
            //Check If SuperAdmin Change Dashboard
            if (userTypeName.equals("SuperAdmin")) {
                Intent intent = new Intent(LoginActivity.this, SADashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();

            } else if (userTypeName.equals("Administrator")) {
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();

            } else if (userTypeName.equals("volunteer")) {
                Intent intent = new Intent(LoginActivity.this, VolunteerDashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();

            } else if (userTypeName.equals("user")) {
                Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();

            }
        }
    }


}
