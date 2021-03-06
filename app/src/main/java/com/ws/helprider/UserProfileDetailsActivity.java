package com.ws.helprider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileDetailsActivity extends AppCompatActivity {
    CommonCode commonCode = new CommonCode();

    TextView tvUserName, tvMobNo, tvEmailId, tvAddress,tvCityPincode,tvStateCountry;
    public Bundle getBundle = null;
    ImageView btnMakeCall;
    ProgressDialog progressDialog;

    String SecurityToken, firstName, middleName, lastName, emailId, mobileno, area,address,state,city,pincode,country;
    int id, userId;
    String userRole;
    private SharedPreferences sharedPreferences;
    String complaintStatus;
    int complaintId;
    TextView tvTotalCount, tvResolveCount, tvPendingCount;

    Urllink urllink = new Urllink();
    String UserRole;
    String imageName;
    CircleImageView ivProfilePic;

    Dialog myDialog;
    String imageUrl;
    SwitchCompat switchBtn;
    boolean activeDeactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_details);
        commonCode.updateLocaleIfNeeded(UserProfileDetailsActivity.this);
        myDialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userRole = sharedPreferences.getString("role", "");

        firstName = sharedPreferences.getString("firstName", "");
        middleName = sharedPreferences.getString("middleName", "");
        lastName = sharedPreferences.getString("lastName", "");

        emailId = sharedPreferences.getString("emailId", "");
        address = sharedPreferences.getString("address", "");

        tvTotalCount = findViewById(R.id.tv_total_complaints);
        tvResolveCount = findViewById(R.id.tv_resolved_complaints);
        tvPendingCount = findViewById(R.id.tv_pending_complaints);

        tvUserName = findViewById(R.id.tv_userName);
        tvMobNo = findViewById(R.id.tv_userMobNo);
        tvEmailId = findViewById(R.id.tv_userEmailAddress);
        tvAddress = findViewById(R.id.tv_userAddress);
        tvCityPincode = findViewById(R.id.tv_city_pincode);
        tvStateCountry = findViewById(R.id.tv_state_country);

        ivProfilePic = findViewById(R.id.iv_profile);
        switchBtn = (SwitchCompat) findViewById(R.id.switchButton);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id");
            userId = bundle.getInt("userId");
            UserRole = bundle.getString("role");
            imageName = bundle.getString("image");
            activeDeactive = bundle.getBoolean("activeDeactive");
            tvUserName.setText(bundle.getString("firstName") + " " + bundle.getString("middleName") + " " + bundle.getString("lastName"));
            tvMobNo.setText(bundle.getString("mobileno"));
            mobileno = bundle.getString("mobileno");
            tvEmailId.setText(bundle.getString("emailid"));
            tvAddress.setText(bundle.getString("address")+","+bundle.getString("area"));
            tvCityPincode.setText(bundle.getString("city")+","+bundle.getString("pincode"));
            tvStateCountry.setText(bundle.getString("state")+","+bundle.getString("country"));

            imageUrl = urllink.downloadProfilePic + imageName;
            Picasso.with(getApplicationContext()).load(imageUrl).into(ivProfilePic);

        }
        btnMakeCall = findViewById(R.id.btn_makeCall);

        btnMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mobileno.equals("")) {
                    //    boolean result = getApplicationContext().Utility.checkPermission(getApplicationContext());
                    //      if (result) {
                    //Intent intent = new Intent(Intent.ACTION_CALL);
                    //  intent.setData(Uri.parse("tel:" + mobileno));
                    //   startActivity(intent);
                    //      } else {
                    //           Toast.makeText(getApplicationContext(), getResources().getString(R.string.allowPermissionFromAppSetting), Toast.LENGTH_LONG).show();
                    //       }


                    if (isPermissionGranted()) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + mobileno));
                            startActivity(intent);
                        } catch (Exception e) {
                            // no activity to handle intent. show error dialog/toast whatever
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseAllowPermission), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        if (ivProfilePic.getDrawable() == null) {
            ivProfilePic.setImageResource(R.mipmap.avatar);
        }

        getComplaintCount();

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfilePicPopup(v);
            }
        });


        //Switch Active / Deactive
        if (userRole.equals("Administrator")) {
            switchBtn.setVisibility(View.VISIBLE);
        } else {
            switchBtn.setVisibility(View.GONE);
        }

        if (activeDeactive) {
            switchBtn.setChecked(true);
        } else {
            switchBtn.setChecked(false);
        }

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activeDeactive = true;
                } else {
                    activeDeactive = false;
                }
                updateActiveDeactive();
            }
        });
    }

    private void updateActiveDeactive() {
        if (commonCode.checkConnection(getApplicationContext())) {
            String jsonurl = urllink.url + "user/ActiveandDeactiveUser/" + userId;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(UserProfileDetailsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(false);
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("inactive_deactivate", activeDeactive);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl,
                    jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                String message = response.getString("message");
                                if (message.equals("Updated Sucessfully")) {
                                    if (activeDeactive) {
                                        Toast.makeText(getApplicationContext(), R.string.profileActivated, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.profileDeactivated, Toast.LENGTH_LONG).show();
                                    }
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String err = error.toString();
                            if (err.equals("com.android.volley.AuthFailureError")) {
                                Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                sp.edit().clear().commit();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(UserProfileDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getComplaintCount() {
        if (commonCode.checkConnection(UserProfileDetailsActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "complaint/getAllStatusCount";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(UserProfileDetailsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    progressDialog.dismiss();
                    if (response.length() > 0) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                tvTotalCount.setText(response.getString("Total"));
                                tvResolveCount.setText(response.getString("Closed"));
                                tvPendingCount.setText(response.getString("Open"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(UserProfileDetailsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
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
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        //  finish();
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
            commonCode.AlertDialog_Pbtn(UserProfileDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
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

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }

    }


    public void viewProfilePicPopup(View v) {
        ImageView imgviewShowImage;
        myDialog.setContentView(R.layout.profile_pic_preview);
        myDialog.setCancelable(true);
        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


        imgviewShowImage = (ImageView) myDialog.findViewById(R.id.imageView);

        Picasso.with(getApplicationContext()).load(imageUrl).into(imgviewShowImage);

        if (imgviewShowImage.getDrawable() == null) {
            imgviewShowImage.setImageResource(R.mipmap.avatar);
        }

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}
