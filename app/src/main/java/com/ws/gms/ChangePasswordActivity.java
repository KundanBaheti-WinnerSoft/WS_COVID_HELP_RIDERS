package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    CommonCode commonCode = new CommonCode();
    String SecurityToken;
    private EditText edtNewPassword, edtConfirmPassword, edtCurrentPassword;
    private Button cancel_password, save_password;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private Urllink urllink = new Urllink();
    private String userid = "";
    private String url = urllink.url;
    private String newpass = "", confirmpass = "", currentpass = "";
    String mobno,userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        commonCode.updateLocaleIfNeeded(ChangePasswordActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.changePassword));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userid = sharedPreferences.getString("userid", "");
        userName= sharedPreferences.getString("username", "");
        mobno = sharedPreferences.getString("mobileno", "");
        //userid = sharedPreferences.getString("mobileno", "");

        edtCurrentPassword = findViewById(R.id.current_password);
        edtNewPassword = findViewById(R.id.new_password);
        edtConfirmPassword = findViewById(R.id.confirm_password);

        save_password = findViewById(R.id.save_password);
        cancel_password = findViewById(R.id.cancel_password);

        save_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentpass = edtCurrentPassword.getText().toString().trim();
                newpass = edtNewPassword.getText().toString().trim();
                confirmpass = edtConfirmPassword.getText().toString().trim();

                if (!commonCode.isValidString(ChangePasswordActivity.this, currentpass)) {
                    edtCurrentPassword.setError(getResources().getString(R.string.enterCurrentPswd));
                    edtCurrentPassword.requestFocus();
                } else if (!commonCode.isValidString(ChangePasswordActivity.this, newpass)) {
                    edtNewPassword.setError(getResources().getString(R.string.enterNewPswd));
                    edtNewPassword.requestFocus();
                } else if (!commonCode.isValidString(ChangePasswordActivity.this, confirmpass)) {
                    edtConfirmPassword.setError(getResources().getString(R.string.enterConfirmNewPswd));
                    edtConfirmPassword.requestFocus();
                } else {
                    if (newpass.equals(confirmpass)) {
                        changePassword();
                    } else {
                        commonCode.AlertDialog_Pbtn(ChangePasswordActivity.this, getResources().getString(R.string.incorrectPassword), getResources().getString(R.string.passwordDoesnMatched), getResources().getString(R.string.ok));
                    }
                }
            }
        });

        cancel_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void changePassword() {
        if (commonCode.checkConnection(ChangePasswordActivity.this)) {
            String jsonurl = url + "user/ChangePassword";
            RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordActivity.this);
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("loginName", userName);
                jsonParams.put("oldpasswdString", currentpass);
                jsonParams.put("passwdString", newpass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl, jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            try {
                                String message = response.getString("message");
                                if (message.equals("Password changed successfully")) {
                                    logout();
                                } else if (message.equals("Password doesn't match")) {
                                    commonCode.AlertDialog_Pbtn(ChangePasswordActivity.this, getResources().getString(R.string.passDoesnMatch), getResources().getString(R.string.currentPassNewPassNotMatch), getResources().getString(R.string.ok));
                                } else if (message.equals("Failed to change password")) {
                                    commonCode.AlertDialog_Pbtn(ChangePasswordActivity.this, getResources().getString(R.string.failedTryAgain), getResources().getString(R.string.currentPassNewPassNotMatch), getResources().getString(R.string.ok));
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            String err = error.toString();
                            if (err.equals("com.android.volley.AuthFailureError")) {
                                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                sp.edit().clear().commit();
                                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                //  finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ChangePasswordActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    public void logout() {
        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.passwordChangedSuccessPlsLogin), Toast.LENGTH_LONG).show();
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        sp.edit().clear().commit();
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        //   finish();
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
