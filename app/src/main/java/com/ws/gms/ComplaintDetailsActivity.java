package com.ws.gms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ws.gms.Adapters.GalleryAdapter;
import com.ws.gms.Adapters.ImagesAdapter;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CALL_PHONE;

public class ComplaintDetailsActivity extends AppCompatActivity {
    CommonCode commonCode = new CommonCode();

    TextView tvUserName, tvUserMobNo, tvAddress, tvToken, tvComplaintType, tvTitle, tvDescription, tvDate, tvAssignName, tvAssignNo, tvAssignMail;
    ImageView btnCall1, btnCall2;
    ProgressDialog progressDialog;
    Urllink url;
    Spinner spnVolunteers;
    String SecurityToken, firstName, middleName, lastName, emailId, userMobileNo, vMoNO, address;
    String userRole;
    private SharedPreferences sharedPreferences;
    int complaintId;
    String userId;
    TextView tvVCmt, tvUCmt;
    int VolunteerID;
    Urllink urllink = new Urllink();
    final ArrayList<HashMap<String, String>> VolArraymap = new ArrayList<>();
    ArrayList<String> volunteerList = new ArrayList<>();
    LinearLayout llassign;
    TextView tvAssign, tvSelfAssign;
    LinearLayout cvAssignTo;
    CircleImageView ivProfilePic;
    Dialog myDialog;
    String imageUrl;
    String complaintPicUrl;

    List<String> imagesEncodedList;
    private GridView gvComplaintImages;
    private ImagesAdapter galleryAdapter;
    ArrayList<String> imageNameAarray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);
        commonCode.updateLocaleIfNeeded(ComplaintDetailsActivity.this);
        myDialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.complaintDetails));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userRole = sharedPreferences.getString("role", "");

        firstName = sharedPreferences.getString("firstName", "");
        middleName = sharedPreferences.getString("middleName", "");
        lastName = sharedPreferences.getString("lastName", "");
        emailId = sharedPreferences.getString("emailId", "");
        address = sharedPreferences.getString("address", "");
        tvVCmt = findViewById(R.id.tv_cmt1);
        tvUCmt = findViewById(R.id.tv_cmt2);

        ivProfilePic = findViewById(R.id.iv_profile);

        userId = sharedPreferences.getString("userId", "");

        tvUserName = findViewById(R.id.tv_username);
        tvUserMobNo = findViewById(R.id.tv_mob_no);
        tvToken = findViewById(R.id.token);
        tvComplaintType = findViewById(R.id.complaintType);
        tvTitle = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_desc);
        tvAddress = findViewById(R.id.tv_area);
        tvDate = findViewById(R.id.tv_date);
        tvAssign = findViewById(R.id.tv_assign);
        tvSelfAssign = findViewById(R.id.tv_self_assign);

        spnVolunteers = findViewById(R.id.spn_assign);
        btnCall1 = findViewById(R.id.btn_call_1);
        btnCall2 = findViewById(R.id.btn_call_2);

        llassign = findViewById(R.id.ll_assign);

        cvAssignTo = findViewById(R.id.ll_assignTo);

        tvAssignName = findViewById(R.id.tv_v_name);
        tvAssignNo = findViewById(R.id.tv_v_number);
        tvAssignMail = findViewById(R.id.tv_v_mail);
        gvComplaintImages = (GridView) findViewById(R.id.gv);


        if (userRole.equals("Administrator")) {
            llassign.setVisibility(View.VISIBLE);
        } else {
            llassign.setVisibility(View.GONE);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imageName = bundle.getString("userImage");
            imageUrl = urllink.downloadProfilePic + imageName;
            Picasso.with(getApplicationContext()).load(imageUrl).into(ivProfilePic);
            if (ivProfilePic.getDrawable() == null) {
                ivProfilePic.setImageResource(R.mipmap.avatar);
            }
            tvUserName.setText(bundle.getString("firstName") + " " + bundle.getString("middleName") + " " + bundle.getString("lastName"));
            tvUserMobNo.setText(bundle.getString("mobileno"));
            tvToken.setText(getResources().getString(R.string.token) + " : " + bundle.getString("tokennumber"));
            tvComplaintType.setText(bundle.getString("complaintType"));
            tvTitle.setText(bundle.getString("title"));
            tvDescription.setText(bundle.getString("description"));
            tvAddress.setText(bundle.getString("Area"));
            //  final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            long complaintDate = Long.parseLong(bundle.getString("date"));
            String date = s1.format(new Date(complaintDate));

            tvDate.setText(date);

            if (bundle.getString("assignFname") != null && !bundle.getString("assignFname").equals("null")) {
                tvAssignName.setText(bundle.getString("assignFname") + " " + bundle.getString("assignMname") + " " + bundle.getString("assignLname"));
                tvAssignNo.setText(bundle.getString("assignMono"));
                tvAssignMail.setText(bundle.getString("assignEmailid"));
                vMoNO = bundle.getString("assignMono");
                //   btnCall2.setText(" " + bundle.getString("assignFname"));
                //    btnCall2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_black_24dp, 0, 0, 0);
                if (userRole.equals("Administrator")) {
                    llassign.setVisibility(View.GONE);
                }

            } else {
                if (userRole.equals("Administrator")) {
                    llassign.setVisibility(View.VISIBLE);
                } else {
                    llassign.setVisibility(View.GONE);
                }

                cvAssignTo.setVisibility(View.GONE);
                getAllVolunteer();
            }

            complaintId = Integer.parseInt(bundle.getString("id"));
            userMobileNo = bundle.getString("mobileno");
            // btnCall1.setText(" " + bundle.getString("firstName"));
            //   btnCall1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_black_24dp, 0, 0, 0);

            if (bundle.getString("vComment") != null && !bundle.getString("vComment").equals("null")) {
//                String msg = "<b>" + getResources().getString(R.string.volunteers) + " : " + "</b> " + " " + bundle.getString("vComment");
                String msg = "<b>" + bundle.getString("assignFname") + " : " + "</b> " + " " + bundle.getString("vComment");

                tvVCmt.setBackgroundResource(R.drawable.bubble1);
                tvVCmt.setText(Html.fromHtml(msg));
            } else {
                tvVCmt.setVisibility(View.GONE);
            }

            if (bundle.getString("uComment") != null && !bundle.getString("uComment").equals("null")) {
//                String msgg = "<b>" + getResources().getString(R.string.users) + " : " + "</b> " + " " + bundle.getString("uComment");
                String msgg = "<b>" + bundle.getString("firstName") + " : " + "</b> " + " " + bundle.getString("uComment");

                tvUCmt.setText(Html.fromHtml(msgg));
                tvUCmt.setBackgroundResource(R.drawable.bubble2);
            } else {
                tvUCmt.setVisibility(View.GONE);
            }
        }


        btnCall1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (commonCode.isValidMobileNo(ComplaintDetailsActivity.this, userMobileNo)) {
                    //        boolean result = Utility.checkPermission((Activity) getContext());
                    //       if (result) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + userMobileNo));
                        if (ContextCompat.checkSelfPermission(ComplaintDetailsActivity.this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(intent);
                        } else {
                            requestPermissions(new String[]{CALL_PHONE}, 1);
                        }
                    } catch (Exception e) {
                        // no activity to handle intent. show error dialog/toast whatever
                    }

                } else {
                    Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.invalidMobNumber), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCall2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (commonCode.isValidMobileNo(ComplaintDetailsActivity.this, vMoNO)) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + vMoNO));
                        if (ContextCompat.checkSelfPermission(ComplaintDetailsActivity.this, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(intent);
                        } else {
                            requestPermissions(new String[]{CALL_PHONE}, 1);
                        }
                    } catch (Exception e) {
                        // no activity to handle intent. show error dialog/toast whatever
                    }
                } else {
                    Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.invalidMobNumber), Toast.LENGTH_LONG).show();
                }
            }
        });


        tvAssign.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                AssignVolunteer();
            }
        });


        if (userRole.equals("user")) {
            btnCall1.setVisibility(View.GONE);
        }


        // temp

//        for (int i=0;i<5;i++) {
//            String mImageUri = "https://res.cloudinary.com/demo/image/upload/w_200/lady.jpg";
//            mArrayUri.add(mImageUri);
//        }
//        galleryAdapter = new ImagesAdapter(ComplaintDetailsActivity.this, mArrayUri);
//        gvComplaintImages.setAdapter(galleryAdapter);
//        gvComplaintImages.setVerticalSpacing(gvComplaintImages.getHorizontalSpacing());
//        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvComplaintImages
//                .getLayoutParams();
//        mlp.setMargins(0, gvComplaintImages.getHorizontalSpacing(), 0, 0);

        getAllImagesOfComplaint();

        gvComplaintImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(ComplaintDetailsActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                // imageUrl=parent.get
                complaintPicUrl = imageNameAarray.get(position);

                viewComplaintPicPopup();
            }
        });


        ////

        tvSelfAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfAssign();
            }
        });

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfilePicPopup(v);
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

    private void getAllVolunteer() {
        if (commonCode.checkConnection(ComplaintDetailsActivity.this)) {
            String jsonurl = urllink.url + "regVolunteer/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(ComplaintDetailsActivity.this);
            progressDialog = new ProgressDialog(ComplaintDetailsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            volunteerList.clear();
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    if (response.length() == 0) {
                        commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.volunteerNotFoundPleaseMakeRegisteration), getResources().getString(R.string.ok));
                    } else {
                        VolArraymap.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = response.getJSONObject(i);
                                hashMap.put("id", object.getString("id"));

                                String name = object.get("firstname").toString() + " " + object.getString("middlename") + " " + object.getString("lastname");

                                VolArraymap.add(hashMap);
                                volunteerList.add(name);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> volListAdapter = new ArrayAdapter<>(ComplaintDetailsActivity.this, android.R.layout.simple_spinner_item, volunteerList);
                        spnVolunteers.setAdapter(volListAdapter);

                        spnVolunteers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                String name = parent.getItemAtPosition(position).toString();
                                if (name.equals(getResources().getString(R.string.selVolunteer))) {
                                    VolunteerID = -1;
                                    spnVolunteers.setSelection(1);
                                } else {
                                    VolunteerID = Integer.parseInt(VolArraymap.get(position).get("id"));
                                    //   Toast.makeText(getApplicationContext(), String.valueOf(VolunteerID), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ComplaintDetailsActivity.this, LoginActivity.class);
                        ComplaintDetailsActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void AssignVolunteer() {
        if (commonCode.checkConnection(getApplicationContext())) {
            String jsonurl = urllink.url + "complaint/AssingVolunteer/" + complaintId;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(ComplaintDetailsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject jsonParams = new JSONObject();
            try {
                JSONObject obj1 = new JSONObject();
                obj1.put("id", VolunteerID);
                jsonParams.put("assingVolunteer", obj1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Updated...!!!")) {
                            Toast.makeText(getApplicationContext(), R.string.volunteerAssignSuccess, Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else if (message.equals("Volunteer already assign")) {
//                                    Toast.makeText(getApplicationContext(), "Teacher already assign", Toast.LENGTH_LONG).show();
                            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.alreadyAssign), getResources().getString(R.string.volunteerAlreadyAssign), getResources().getString(R.string.ok));

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ComplaintDetailsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                    //   Handle Error
                }
            }) //This is for Headers If You Needed
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
            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllImagesOfComplaint() {
        imageNameAarray.clear();
        if (commonCode.checkConnection(ComplaintDetailsActivity.this)) {
            String jsonurl = urllink.url + "complaintImage/getByComplaint/" + complaintId;
            RequestQueue requestQueue = Volley.newRequestQueue(ComplaintDetailsActivity.this);
            //  progressDialog = new ProgressDialog(ComplaintDetailsActivity.this);
            //   progressDialog.setMessage(getResources().getString(R.string.loading));
            //   progressDialog.show();
            //   progressDialog.setCancelable(true);
            volunteerList.clear();
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
//                         progressDialog.dismiss();
                    if (response.length() == 0) {
                      //  commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.volunteerNotFoundPleaseMakeRegisteration), getResources().getString(R.string.ok));
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = response.getJSONObject(i);
                                hashMap.put("id", object.getString("id"));
                                String name = object.getString("image");
                                String imgUrl = urllink.downloadComplaintImg + name;
                                imageNameAarray.add(imgUrl);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        galleryAdapter = new ImagesAdapter(ComplaintDetailsActivity.this, imageNameAarray);
                        gvComplaintImages.setAdapter(galleryAdapter);
                        gvComplaintImages.setVerticalSpacing(gvComplaintImages.getHorizontalSpacing());
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvComplaintImages
                                .getLayoutParams();
                        mlp.setMargins(0, gvComplaintImages.getHorizontalSpacing(), 0, 0);


                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //     progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ComplaintDetailsActivity.this, LoginActivity.class);
                        ComplaintDetailsActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(ComplaintDetailsActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    private void selfAssign() {
        if (commonCode.checkConnection(getApplicationContext())) {
            String jsonurl = urllink.url + "complaint/SelfAssingComplaintAdmin/" + complaintId;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(ComplaintDetailsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject jsonParams = new JSONObject();
            try {
                JSONObject obj1 = new JSONObject();
                obj1.put("id", userId);
                jsonParams.put("adminAssign", obj1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl, jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        //{"message":"Failed to update...!!!","internalJobCount":null,"empReqCount":null,"empSuggCount":null,"internalJobApprovedCount":null,"leaveAppApprovedCount":null,"leaveAppCount":null,"birthday":null,"mobNoOTP":null,"otp":null,"total":null,"empReqApprovedCount":null}
                        String message = response.getString("message");
                        if (message.equals("Record Updated...!!!")) {
                            Toast.makeText(getApplicationContext(), R.string.assignSuccess, Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else if (message.equals("Volunteer already assign")) {
//                                    Toast.makeText(getApplicationContext(), "Teacher already assign", Toast.LENGTH_LONG).show();
                            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.alreadyAssign), getResources().getString(R.string.volunteerAlreadyAssign), getResources().getString(R.string.ok));

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(ComplaintDetailsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                    //   Handle Error
                }
            }) //This is for Headers If You Needed
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
            commonCode.AlertDialog_Pbtn(ComplaintDetailsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
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


    public void viewComplaintPicPopup() {
        ImageView imgviewShowImage;
        myDialog.setContentView(R.layout.complaint_pic_preview);

        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        imgviewShowImage = (ImageView) myDialog.findViewById(R.id.imageView);

        Picasso.with(getApplicationContext()).load(complaintPicUrl).into(imgviewShowImage);

        if (imgviewShowImage.getDrawable() == null) {
            imgviewShowImage.setImageResource(R.drawable.ic_refresh_black_24dp);
        }

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

}
