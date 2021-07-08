package com.ws.helprider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityForgetPassword extends AppCompatActivity {
    String SecurityToken;
    private EditText edtMailId;
    private CommonCode commonCode = new CommonCode();
    private Urllink urllink = new Urllink();
    private ProgressDialog progressDialog;
    private String url = urllink.url;
    private String mobNo = "", userName;
    private Button send, backtologin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        commonCode.updateLocaleIfNeeded(ActivityForgetPassword.this);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.forgotPassword));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userName = sharedPreferences.getString("username", "");

        edtMailId = (EditText) findViewById(R.id.edt_mail_id);
        send = (Button) findViewById(R.id.send);
        backtologin = (Button) findViewById(R.id.backtologin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            send.setBackgroundResource(R.drawable.ripple_effect);
            backtologin.setBackgroundResource(R.drawable.ripple_effect);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mobNo = edtMobNo.getText().toString().trim();
                userName = edtMailId.getText().toString().trim();
                if (!commonCode.isValidMEmail(ActivityForgetPassword.this, userName)) {
                    edtMailId.setError(getResources().getString(R.string.enterValidMailId));
                    edtMailId.requestFocus();
                } else {
                    sendmail(userName);
                    //checkExistOrNot(v);
                }
            }
        });

        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityForgetPassword.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //http://192.168.0.108:8083/hrms/user/MobileNumberValidate/kundanbaheti327@gmail.com
    private void checkExistOrNot(final View v) {
        if (commonCode.checkConnection(ActivityForgetPassword.this)) {
            String jsonurl = urllink.url + "user/MobileNumberValidate/" + userName;
            progressDialog = new ProgressDialog(ActivityForgetPassword.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, jsonurl,

                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("MobNo Already exists")) {
                            sendmail(userName);
                        } else if (message.equals("MobNo not exists")) {
                            mobNoAlreadyPopup();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failedTryAgain), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(ActivityForgetPassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                }
            });    //This is for Headers If You Needed
//            {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("x-Auth-token", SecurityToken);
//                    return params;
//                }
//            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ActivityForgetPassword.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void sendmails(String userName) {
        if (commonCode.checkConnection(ActivityForgetPassword.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = url + "user/forgotPassword";
            RequestQueue requestQueue = Volley.newRequestQueue(ActivityForgetPassword.this);
            progressDialog = new ProgressDialog(ActivityForgetPassword.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject object = new JSONObject();
            try {
                object.put("message", userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.length() == 0) {
                        commonCode.AlertDialog_Pbtn(ActivityForgetPassword.this, getResources().getString(R.string.notRegister), getResources().getString(R.string.plsRegiFirst), getResources().getString(R.string.ok));
                    } else {
                        try {
                            String message = response.getString("message");
                            if (message.equals("email is not avalabel")) {
                                Toast.makeText(getApplicationContext(), R.string.emailnotAvailable, Toast.LENGTH_LONG).show();
                            } else if (message.equals("username not Exist!!!!!!!!")) {
                                Toast.makeText(getApplicationContext(), R.string.mobNoNotFound, Toast.LENGTH_LONG).show();
                            } else if (message.equals("Failed to Update password")) {
                                Toast.makeText(getApplicationContext(), R.string.failedToUpdatePassword, Toast.LENGTH_LONG).show();
                            } else if (message.equals("Password Updated successfully")) {
                                sendSuccessPopup();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.failedTryAgain, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    String err = error.toString();

                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ActivityForgetPassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
                    }
                }
            }); //This is for Headers If You Needed
           /* {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("x-Auth-token", SecurityToken);
                    return params;
                }
            };*/
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ActivityForgetPassword.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void sendmail(String emailid) {
        if (commonCode.checkConnection(ActivityForgetPassword.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = url + "user/forgotPassword";
            RequestQueue requestQueue = Volley.newRequestQueue(ActivityForgetPassword.this);
            progressDialog = new ProgressDialog(ActivityForgetPassword.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(false);
            JSONObject object = new JSONObject();
            try {
                object.put("message", emailid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, jsonurl, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("email is not avalabel")) {
                            Toast.makeText(getApplicationContext(), R.string.emailnotAvailable, Toast.LENGTH_LONG).show();

                        } else if (message.equals("username not Exist!!")) {
                            Toast.makeText(getApplicationContext(), R.string.usernameNotExist, Toast.LENGTH_LONG).show();

                        } else if (message.equals("Failed to Update password")) {
                            Toast.makeText(getApplicationContext(), R.string.failedToUpdatePassword, Toast.LENGTH_LONG).show();

                        } else if (message.equals("Password Updated successfully")) {
                            //Toast.makeText(getApplicationContext(), R.string.emailIdNotFound, Toast.LENGTH_LONG).show();
                            sendSuccessPopup();

                        } else {
                            Toast.makeText(getApplicationContext(), R.string.failedTryAgain, Toast.LENGTH_LONG).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    String err = error.toString();

                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ActivityForgetPassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
                    }
                }
            }) ;//This is for Headers If You Needed
//            {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("x-Auth-token", SecurityToken);
//                    return params;
//                }
//            };
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ActivityForgetPassword.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Intent intent = new Intent(ActivityForgetPassword.this, LoginActivity.class);
        //   startActivity(intent);
        //   finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //This method would confirm the otp
    private void mobNoAlreadyPopup() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.mobno_not_exist_dialog, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button buttonConfirm = (Button) confirmDialog.findViewById(R.id.btn_ok);

        //Creating an alertdialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }


    //This method would confirm the otp
    private void sendSuccessPopup() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_pass_send_success, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button buttonConfirm = (Button) confirmDialog.findViewById(R.id.btn_ok);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
