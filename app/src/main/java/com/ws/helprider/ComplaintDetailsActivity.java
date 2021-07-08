package com.ws.helprider;

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
import android.widget.TableRow;
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
import com.ws.helprider.Adapters.ImagesAdapter;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;
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

//    TextView tvUserName, tvUserMobNo, tvAddress, tvToken, tvComplaintType, tvTitle, tvDescription, tvAssignName, tvAssignNo, tvAssignMail;
    private TextView tvTokenNo,tvServiceTpe,tvPatientName,tvPatientAge,tvContactPersonName,tvContactPersonMobNo,tvDate,tvPatientBG,tvPatientOxyLevel,tvPatientHRCTScore,tvUnitsRequired,tvHospitalname,tvHospitalAddress,tvPatientAddress,tvRTPCRTestDate,tvRTPCRTestResult,tvHospitalType,tvO2RoomLevel,tvO2WithSupport,tvCurrentCondition,tvOtherDisease,tvHelpNeeded,tvDrName,tvDrMob,tvWard,tvRequireBed,tvAssignName, tvAssignNo, tvAssignMail;
    private TableRow trPatientBG,trPatientOxyLevel,trPatientHRCTScore,trUnitsRequired,trHospitalname,trHospitalAddress,trPatientAddress,trRTPCRTestDate,trRTPCRTestResult,trHospitalType,trO2RoomLevel,trO2WithSupport,trCurrentCondition,trOtherDisease,trHelpNeeded,trDrName,trDrMob,trWard,trRequireBed;
    ImageView btnCall1, btnCall2;
    ProgressDialog progressDialog;
    Urllink url;
    Spinner spnVolunteers;
    String SecurityToken, firstName, middleName, lastName, emailId, userMobileNo, vMoNO, address;
    String userRole;
    private SharedPreferences sharedPreferences;
    int complaintId;
    String userId, complaintType;
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
        getSupportActionBar().setTitle(getResources().getString(R.string.serviceRequestDetails));
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
        tvTokenNo = findViewById(R.id.tv_token);
        tvServiceTpe = findViewById(R.id.complaintType);
        tvPatientName = findViewById(R.id.tv_patient_name);
        tvPatientAge = findViewById(R.id.tv_patient_age);
        tvContactPersonName = findViewById(R.id.tv_contact_person_name);
        tvContactPersonMobNo = findViewById(R.id.tv_contact_person_mob_no);
        tvDate = findViewById(R.id.tv_date);
        tvPatientBG = findViewById(R.id.tv_patient_bg);
        tvPatientOxyLevel = findViewById(R.id.tv_patient_oxygen_level);
        tvPatientHRCTScore = findViewById(R.id.tv_patient_hrct_score);
        tvUnitsRequired = findViewById(R.id.tv_units_required);
        tvHospitalname = findViewById(R.id.tv_hospital_name);
        tvHospitalAddress = findViewById(R.id.tv_hospital_address);
        tvPatientAddress = findViewById(R.id.tv_patient_address);
        tvRTPCRTestDate = findViewById(R.id.tv_rtpcr_test_date);
        tvRTPCRTestResult = findViewById(R.id.tv_rtpcr_test_result);
        tvHospitalType= findViewById(R.id.tv_gov_private);
        tvO2RoomLevel= findViewById(R.id.tv_o2_room_level);
        tvO2WithSupport= findViewById(R.id.tv_o2_with_support);
        tvCurrentCondition= findViewById(R.id.tv_patient_current_condition);
        tvOtherDisease= findViewById(R.id.tv_other_disease);
        tvHelpNeeded= findViewById(R.id.tv_help_needed);
        tvDrName= findViewById(R.id.tv_dr_name);
        tvDrMob= findViewById(R.id.tv_dr_mob_no);
        tvWard= findViewById(R.id.tv_ward);
        tvRequireBed= findViewById(R.id.tv_bed_require);

        trPatientBG = findViewById(R.id.tr_bg);
        trPatientOxyLevel = findViewById(R.id.tr_oxygen_level);
        trPatientHRCTScore = findViewById(R.id.tr_hrct_score);
        trUnitsRequired = findViewById(R.id.tr_units_required);
        trHospitalname = findViewById(R.id.tr_hospital_name);
        trHospitalAddress = findViewById(R.id.tr_hospital_address);
        trPatientAddress = findViewById(R.id.tr_paient_address);
        trRTPCRTestDate = findViewById(R.id.tr_rtpcr_test_date);
        trRTPCRTestResult = findViewById(R.id.tr_rtpcr_test_result);
        trHospitalType= findViewById(R.id.tr_hospital_type);
        trO2RoomLevel= findViewById(R.id.tr_o2_room_level);
        trO2WithSupport= findViewById(R.id.tr_o2_with_support);
        trCurrentCondition= findViewById(R.id.tr_current_condition);
        trOtherDisease= findViewById(R.id.tr_other_disease);
        trHelpNeeded= findViewById(R.id.tr_help_needed);
        trDrName= findViewById(R.id.tr_dr_name);
        trDrMob= findViewById(R.id.tr_drMobNo);
        trWard= findViewById(R.id.tr_ward);
        trRequireBed= findViewById(R.id.tr_bed_required);


       // tvAssign = findViewById(R.id.tv_assign);
        tvSelfAssign = findViewById(R.id.tv_self_assign);

       // spnVolunteers = findViewById(R.id.spn_assign);
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

        String selfAssign=getIntent().getStringExtra("selfAssign");
        if (selfAssign!=null){
            if (selfAssign.equals("selfAssign")){
                tvSelfAssign.setVisibility(View.GONE);
            }else {
                tvSelfAssign.setVisibility(View.VISIBLE);
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            complaintId = Integer.parseInt(bundle.getString("id"));
            complaintType = bundle.getString("complaintType");

            tvTokenNo.setText(bundle.getString("tokennumber"));
            tvServiceTpe.setText(bundle.getString("complaintType"));
            tvPatientName.setText(bundle.getString("patient_name"));
            tvPatientAge.setText(bundle.getString("age"));
            tvContactPersonName.setText(bundle.getString("contact_person_name"));
            tvContactPersonMobNo.setText(bundle.getString("relative_mobile"));
//            tv.setText(bundle.getString("assignToId"));

//            tvDate.setText(bundle.getString("assignFname"));
//            tvDate.setText(bundle.getString("assignMname"));
//            tvDate.setText(bundle.getString("assignLname"));
//           String assignName = bundle.getString("assignFname")+bundle.getString("assignMname")+bundle.getString("assignLname");
//           tvAssignName.setText(assignName);
//            tvAssignNo.setText(bundle.getString("assignMono"));
//            tvAssignMail.setText(bundle.getString("assignEmailid"));
            if (complaintType.equals("Plasma")){
                trPatientBG.setVisibility(View.VISIBLE);
                trPatientOxyLevel.setVisibility(View.VISIBLE);
                trPatientHRCTScore.setVisibility(View.VISIBLE);
                trUnitsRequired.setVisibility(View.VISIBLE);
                trHospitalname.setVisibility(View.VISIBLE);
                trHospitalAddress.setVisibility(View.VISIBLE);
                trPatientAddress.setVisibility(View.GONE);
                trRTPCRTestDate.setVisibility(View.GONE);
                trRTPCRTestResult.setVisibility(View.GONE);
                trHospitalType.setVisibility(View.GONE);
                trO2RoomLevel.setVisibility(View.GONE);
                trO2WithSupport.setVisibility(View.GONE);
                trCurrentCondition.setVisibility(View.GONE);
                trOtherDisease.setVisibility(View.GONE);
                trHelpNeeded.setVisibility(View.GONE);
                trDrName.setVisibility(View.GONE);
                trDrMob.setVisibility(View.GONE);
                trWard.setVisibility(View.GONE);
                trRequireBed.setVisibility(View.GONE);

               tvPatientBG.setText(bundle.getString("blood_group"));
               tvPatientOxyLevel.setText(bundle.getString("oxygen"));
               tvPatientHRCTScore.setText(bundle.getString("hrct_score"));
               tvUnitsRequired.setText(bundle.getString("units_required"));
               tvHospitalname.setText(bundle.getString("hospital_name"));
               tvHospitalAddress.setText(bundle.getString("hospital_address"));
            }else if (complaintType.equals("Hospital & beds")){
                trPatientOxyLevel.setVisibility(View.GONE);
                trUnitsRequired.setVisibility(View.GONE);
                trOtherDisease.setVisibility(View.GONE);
                trWard.setVisibility(View.GONE);

                trPatientBG.setVisibility(View.VISIBLE);
                trPatientHRCTScore.setVisibility(View.VISIBLE);
                trHospitalname.setVisibility(View.VISIBLE);
                trHospitalAddress.setVisibility(View.VISIBLE);
                trPatientAddress.setVisibility(View.VISIBLE);
                trRTPCRTestDate.setVisibility(View.VISIBLE);
                trRTPCRTestResult.setVisibility(View.VISIBLE);
                trHospitalType.setVisibility(View.VISIBLE);
                trO2RoomLevel.setVisibility(View.VISIBLE);
                trO2WithSupport.setVisibility(View.VISIBLE);
                trCurrentCondition.setVisibility(View.VISIBLE);
                trHelpNeeded.setVisibility(View.VISIBLE);
                trDrName.setVisibility(View.VISIBLE);
                trDrMob.setVisibility(View.VISIBLE);
                trRequireBed.setVisibility(View.VISIBLE);

                tvPatientBG.setText(bundle.getString("blood_group"));
                tvPatientHRCTScore.setText(bundle.getString("hrct_score"));
                tvHospitalname.setText(bundle.getString("hospital_name"));
                tvHospitalAddress.setText(bundle.getString("hospital_address"));
                tvPatientAddress.setText(bundle.getString("location"));
                SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy");

                long complaintDate = Long.parseLong(bundle.getString("date_of_test"));
                String date = s1.format(new Date(complaintDate));
                tvRTPCRTestDate.setText(date);

                tvRTPCRTestResult.setText(bundle.getString("rtpcr_or_antigen"));
                tvHospitalType.setText(bundle.getString("govt_or_private"));
                tvO2RoomLevel.setText(bundle.getString("oxy_room_level"));
                tvO2WithSupport.setText(bundle.getString("oxy_with_support"));
                tvCurrentCondition.setText(bundle.getString("current_situation"));
                tvHelpNeeded.setText(bundle.getString("help_detail"));
                tvDrName.setText(bundle.getString("doc_name"));
                tvDrMob.setText(bundle.getString("doc_mobile"));
                tvRequireBed.setText(bundle.getString("bed_required"));
            }else if (complaintType.equals("ICU & Ventilateor")){
                trPatientOxyLevel.setVisibility(View.GONE);
                trUnitsRequired.setVisibility(View.GONE);
                trPatientBG.setVisibility(View.GONE);
                trPatientHRCTScore.setVisibility(View.GONE);
                trHospitalAddress.setVisibility(View.GONE);
                trHospitalType.setVisibility(View.GONE);
                trHelpNeeded.setVisibility(View.GONE);
                trRequireBed.setVisibility(View.GONE);

                trPatientAddress.setVisibility(View.VISIBLE);
                trWard.setVisibility(View.VISIBLE);
                trRTPCRTestDate.setVisibility(View.VISIBLE);
                trRTPCRTestResult.setVisibility(View.VISIBLE);
                trO2RoomLevel.setVisibility(View.VISIBLE);
                trO2WithSupport.setVisibility(View.VISIBLE);
                trOtherDisease.setVisibility(View.VISIBLE);
                trCurrentCondition.setVisibility(View.VISIBLE);
                trDrName.setVisibility(View.VISIBLE);
                trDrMob.setVisibility(View.VISIBLE);
                trHospitalname.setVisibility(View.VISIBLE);

                tvPatientAddress.setText(bundle.getString("location"));
                tvWard.setText(bundle.getString("ward"));

                SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy");

                long complaintDate = Long.parseLong(bundle.getString("date_of_test"));
                String date = s1.format(new Date(complaintDate));
                tvRTPCRTestDate.setText(date);
                tvRTPCRTestResult.setText(bundle.getString("rtpcr_or_antigen"));
                tvO2RoomLevel.setText(bundle.getString("oxy_room_level"));
                tvO2WithSupport.setText(bundle.getString("oxy_with_support"));
                tvOtherDisease.setText(bundle.getString("other_disease"));
                tvCurrentCondition.setText(bundle.getString("current_situation"));
                tvDrName.setText(bundle.getString("doc_name"));
                tvDrMob.setText(bundle.getString("doc_mobile"));
                tvHospitalname.setText(bundle.getString("hospital_name"));

            }else if (complaintType.equals("")){

            }



//            tvUserName.setText(bundle.getString("firstName") + " " + bundle.getString("middleName") + " " + bundle.getString("lastName"));
//            tvUserMobNo.setText(bundle.getString("mobileno"));
//            tvToken.setText(getResources().getString(R.string.token) + " : " + bundle.getString("tokennumber"));
//            tvComplaintType.setText(bundle.getString("complaintType"));
//            tvTitle.setText(bundle.getString("title"));
//            tvDescription.setText(bundle.getString("description"));
//            tvAddress.setText(bundle.getString("Area"));
             // final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy");
            long complaintDate = Long.parseLong(bundle.getString("created_date"));
            String date = s1.format(new Date(complaintDate));
            tvDate.setText(date);

            if (bundle.getString("assignFname") != null && !bundle.getString("assignFname").equals("null")) {
                cvAssignTo.setVisibility(View.VISIBLE);

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
                //getAllVolunteer();
            }

            complaintId = Integer.parseInt(bundle.getString("id"));
            userMobileNo = bundle.getString("relative_mobile");
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
                String msgg = "<b>" + bundle.getString("firstname") + " : " + "</b> " + " " + bundle.getString("uComment");

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

        //getAllImagesOfComplaint();

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
