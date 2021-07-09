package com.ws.helprider;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ws.helprider.Adapters.GalleryAdapter;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddComplaintActivity extends AppCompatActivity {

    CommonCode commonCode = new CommonCode();
    Urllink urllink = new Urllink();
    ProgressDialog progressDialog;
    String SecurityToken, firstName, middleName, lastName, userName, userRole, userId, mobileno;
    //new
    private TextInputEditText edtPatientName, edtPatientAge, edtAddress, edtDateOfTest, edtRtpcrAntigen, edtGovPrivate, edtO2RoomLevel, edtO2WithSupport, edtPatientCondition, edtPatientOtherDisease, edtWhatHelpNeeded, edtWhereHelpNeeded, edtWhereAdmittedCurrently, edtDrMobNo, edtOxygenScore, edtHRCTScore, edtUnitsRequired, edtHospitalName, edtHospitalAddress, edtContactPersonName, edtContactPersonMobNo, edtWard, edtRtpcrTestDate, edtAnyComorbidity, edtDrName;
    private TextInputLayout tilpname, tilpage, tilpaddress, tildateoftest, tilertpcantigen, tilgovprivate, tilo2roomlevel, tilo2withsupport, tilpcondition, tilotherdisease, tilwhathelpneeded, tilwherehelpneeded, tilwhereadmittedcurrently, tildrmobno, tiloxygenlevel, tilhrctscore, tilunitsreqired, tilhname, tilhaddress, tilcontactpersonname, tilcontactpersonmobno, tilWard, tilRtpcrTestDate, tilAnycomorbidity, tilDrName;
    RadioGroup rgBedRequired, rgRtpcrTestResult;
    private RadioButton rbBedRequired, rbRTPCRTestResult;

    public String patient_name = "", patient_age = "", patient_address = "", date_of_test = "", rtpcr_antigen = "", gov_private = "", o2room_level = "", o2with_support, patient_condition = "", patient_other_disease = "", whathelpneeded = "", where_help_needed = "", where_admitted_currently = "", dr_mob_no = "", oxygenscore = "", hrct_score = "", units_required = "", hospital_name = "", hospital_address = "", contact_person_name = "", contact_person_mob_no = "", patient_blood_group = "", required_bed = "", ward = "", rtpcr_test_date = "", rtpcr_test_result = "", any_comorbidity = "", drName = "";
    private Spinner spnBg;
    List<String> bloodGroup = new ArrayList<>();
    TextView tv_form_title, tv_require_bed, tv_rtpcr_test_result;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    int year, month, day;

    private SharedPreferences sharedPreferences;

    EditText edtName, edtMoNo, edtTitle, edtDesc, edtArea;
    Button btnCancel, btnAdd;
    TextView tvBrowseImage;

    String complaintType = "", title, desc, area;
    int complaintTypeId = 0;
    final ArrayList<HashMap<String, String>> arraymapClass = new ArrayList<>();
    ArrayList<String> nameListClass = new ArrayList<>();

    MaterialSpinner spnCompType;
    private ArrayList<String> imagearray = new ArrayList<>();
    private String bitImageString = "";

    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

    private String encodedUploadedFile = "";
    private Bitmap bitmap;
    Dialog myDialog;
    int o2with_support_compare,o2room_level_compare,patient_age_compare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        commonCode.updateLocaleIfNeeded(AddComplaintActivity.this);
        myDialog = new Dialog(AddComplaintActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.addServiceRequest));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userName = sharedPreferences.getString("username", "");
        userRole = sharedPreferences.getString("role", "");
        userId = sharedPreferences.getString("userId", "");

        firstName = sharedPreferences.getString("firstName", "");
        middleName = sharedPreferences.getString("middleName", "");
        lastName = sharedPreferences.getString("lastName", "");
        mobileno = sharedPreferences.getString("mobileno", "");

        spnCompType = (MaterialSpinner) findViewById(R.id.spn_comp_type);

        edtName = findViewById(R.id.edt_name);
        edtMoNo = findViewById(R.id.edt_mob_no);
        edtTitle = findViewById(R.id.edt_title);

        edtDesc = findViewById(R.id.edt_description);
        edtArea = findViewById(R.id.edt_area);
        gvGallery = (GridView) findViewById(R.id.gv);

        edtName.setText(firstName + " " + middleName + " " + lastName);
        edtMoNo.setText(mobileno);

        btnCancel = findViewById(R.id.btn_cancel);
        btnAdd = findViewById(R.id.btn_add);
        edtPatientName = findViewById(R.id.edt_patient_name);
        edtPatientAge = findViewById(R.id.edt_patient_age);
        edtAddress = findViewById(R.id.edt_patient_address);
        edtDateOfTest = findViewById(R.id.edt_date_of_test);
        edtRtpcrAntigen = findViewById(R.id.edt_rtpcr_antigen);
        edtGovPrivate = findViewById(R.id.edt_gov_private);
        edtO2RoomLevel = findViewById(R.id.edt_patient_o2_room_level);
        edtO2WithSupport = findViewById(R.id.edt_patient_o2_with_support);
        edtPatientCondition = findViewById(R.id.edt_patient_current_condition);
        edtPatientOtherDisease = findViewById(R.id.edt_patient_other_disease);
        edtWhatHelpNeeded = findViewById(R.id.edt_what_help_needed);
        edtWhereHelpNeeded = findViewById(R.id.edt_where_help_needed);
        edtWhereAdmittedCurrently = findViewById(R.id.edt_where_currently_admitted);
        edtDrMobNo = findViewById(R.id.edt_dr_mob_number);
        edtOxygenScore = findViewById(R.id.edt_oxygent_level);
        edtHRCTScore = findViewById(R.id.edt_hrct_score);
        edtUnitsRequired = findViewById(R.id.edt_units_required);
        edtHospitalName = findViewById(R.id.edt_hospital_name);
        edtHospitalAddress = findViewById(R.id.edt_hospital_address);
        edtContactPersonName = findViewById(R.id.edt_contact_person_name);
        edtContactPersonMobNo = findViewById(R.id.edt_contact_person_mob_no);
        edtWard = findViewById(R.id.edt_ward);
        edtRtpcrTestDate = findViewById(R.id.edt_rtpcr_test_date);
        edtAnyComorbidity = findViewById(R.id.edt_any_comorbidity);
        edtDrName = findViewById(R.id.edt_dr_name);

        tilpname = findViewById(R.id.til_p_name);
        tilpage = findViewById(R.id.til_p_age);
        tilpaddress = findViewById(R.id.til_p_address);
        tildateoftest = findViewById(R.id.til_date_of_text);
        tilertpcantigen = findViewById(R.id.til_p_rtpcr_antigen);
        tilgovprivate = findViewById(R.id.til_gov_private);
        tilo2roomlevel = findViewById(R.id.til_p_o2_room_level);
        tilo2withsupport = findViewById(R.id.til_p_o2_with_support);
        tilotherdisease = findViewById(R.id.til_p_other_disease);
        tilpcondition = findViewById(R.id.til_p_current_condition);
        tilwhathelpneeded = findViewById(R.id.til_what_help_needed);
        tilwherehelpneeded = findViewById(R.id.til_where_help_needed);
        tilwhereadmittedcurrently = findViewById(R.id.til_where_currently_admitted);
        tildrmobno = findViewById(R.id.til_dr_mob_no);
        tiloxygenlevel = findViewById(R.id.til_p_oxy_level);
        tilhrctscore = findViewById(R.id.til_p_hrct_score);
        tilunitsreqired = findViewById(R.id.til_p_p_units_required);
        tilhname = findViewById(R.id.til_hospital_name);
        tilhaddress = findViewById(R.id.til_hospital_address);
        tilcontactpersonname = findViewById(R.id.til_contact_person_name);
        tilcontactpersonmobno = findViewById(R.id.til_contact_person_mob_no);
        tilWard = findViewById(R.id.til_ward);
        tilRtpcrTestDate = findViewById(R.id.til_rtpcr_date);
        tilAnycomorbidity = findViewById(R.id.til_any_comorbidity);
        tilDrName = findViewById(R.id.til_dr_name);


        tv_rtpcr_test_result = findViewById(R.id.tv_rtpcr_test_result);
        rgRtpcrTestResult = findViewById(R.id.rg_rtpcr_test_result);
        rgRtpcrTestResult.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    rtpcr_test_result = String.valueOf(rb.getText());
//                    Toast.makeText(RequestFormActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        tv_require_bed = findViewById(R.id.tv_bed_require);
        rgBedRequired = findViewById(R.id.rb_bed_require);

        rgBedRequired.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    required_bed = String.valueOf(rb.getText());
//                    Toast.makeText(RequestFormActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        rbBedRequired = findViewById(R.id.rb_icu);
        rbRTPCRTestResult = findViewById(R.id.rb_rtpcr_positive);

        edtDateOfTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddComplaintActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                edtDateOfTest.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        edtRtpcrTestDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddComplaintActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

//                                edtRtpcrTestDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                edtRtpcrTestDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        rgBedRequired.clearCheck();
        rgRtpcrTestResult.clearCheck();
        spnBg = findViewById(R.id.spn_bg);

        bloodGroup.add(0, "Choose Blood Group");
        bloodGroup.add("A+ve");
        bloodGroup.add("A-ve");
        bloodGroup.add("B+ve");
        bloodGroup.add("B-ve");
        bloodGroup.add("O+ve");
        bloodGroup.add("O-ve");
        bloodGroup.add("AB+ve");
        bloodGroup.add("AB-ve");
        ArrayAdapter<String> bgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroup);
        bgAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBg.setAdapter(bgAdapter);
        spnBg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Blood Group")) {

                } else {
                    patient_blood_group = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = edtTitle.getText().toString();

                desc = edtDesc.getText().toString();
                area = edtArea.getText().toString();


                patient_name = edtPatientName.getText().toString();
                patient_age = edtPatientAge.getText().toString();
                patient_address = edtAddress.getText().toString();
                date_of_test = edtDateOfTest.getText().toString();
                rtpcr_antigen = edtRtpcrAntigen.getText().toString();
                gov_private = edtGovPrivate.getText().toString();
                o2room_level = edtO2RoomLevel.getText().toString();
                o2with_support = edtO2WithSupport.getText().toString();
                patient_condition = edtPatientCondition.getText().toString();
                patient_other_disease = edtPatientOtherDisease.getText().toString();
                whathelpneeded = edtWhatHelpNeeded.getText().toString();
                where_help_needed = edtWhereHelpNeeded.getText().toString();
                dr_mob_no = edtDrMobNo.getText().toString();
                where_admitted_currently = edtWhereAdmittedCurrently.getText().toString();
                ward = edtWard.getText().toString();
                rtpcr_test_date = edtRtpcrTestDate.getText().toString();
                any_comorbidity = edtAnyComorbidity.getText().toString();
                drName = edtDrName.getText().toString();


                oxygenscore = edtOxygenScore.getText().toString();
                hrct_score = edtHRCTScore.getText().toString();
                units_required = edtUnitsRequired.getText().toString();
                hospital_name = edtHospitalName.getText().toString();
                hospital_address = edtHospitalAddress.getText().toString();
                contact_person_name = edtContactPersonName.getText().toString();
                contact_person_mob_no = edtContactPersonMobNo.getText().toString();

//                if (complaintTypeId == 0) {
//                    spnCompType.setError(getResources().getString(R.string.plsSelectComplaintType));
//                    spnCompType.requestFocus();
//                } else if (!commonCode.isValidString(AddComplaintActivity.this, title)) {
//                    edtTitle.setError(getResources().getString(R.string.plsEnterTitle));
//                    edtTitle.requestFocus();
//                } else if (!commonCode.isValidString(AddComplaintActivity.this, desc)) {
//                    edtDesc.setError(getResources().getString(R.string.plsEnterDesc));
//                    edtDesc.requestFocus();
//                } else if (!commonCode.isValidString(AddComplaintActivity.this, area)) {
//                    edtArea.setError(getResources().getString(R.string.plsEnterArea));
//                    edtArea.requestFocus();
//                } else {
//                    confirmCommentPopup(v);
//                }
                if (o2with_support.isEmpty()) {
                } else {
                     o2with_support_compare = Integer.parseInt(o2with_support);
                }
                if (o2room_level.isEmpty()) {
                } else {
                     o2room_level_compare = Integer.parseInt(o2room_level);
                }
                if (patient_age.isEmpty()) {
                } else {
                     patient_age_compare = Integer.parseInt(patient_age);
                }
                if (complaintTypeId == 0) {
                    spnCompType.setError(getResources().getString(R.string.plsSelectServiceType));
                    spnCompType.requestFocus();
                } else if (complaintTypeId == 1) {
                    //hospital bed
                    if (patient_name.isEmpty()) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterPatientName));
                        edtPatientName.requestFocus();
                    }else if (!commonCode.isValidString(AddComplaintActivity.this, patient_name)) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterValidPatientName));
                        edtPatientName.requestFocus();
                    } else if (patient_age.isEmpty()) {
                        edtPatientAge.setError(getResources().getString(R.string.plsEnterPatientAge));
                        edtPatientAge.requestFocus();
                    } else if (patient_age_compare == 0) {
                        edtPatientAge.setError(getResources().getString(R.string.plsEnterValidPatientAge));
                        edtPatientAge.requestFocus();
                    } else if (patient_address.isEmpty()) {
                        edtAddress.setError(getResources().getString(R.string.plsEnterPatientAddress));
                        edtAddress.requestFocus();
                    } else if (required_bed.isEmpty()) {
                        rbBedRequired.setError(getResources().getString(R.string.plsSelRequiredBed));
                        rbBedRequired.requestFocus();
                    } else if (rtpcr_test_date.isEmpty()) {
                        edtRtpcrTestDate.setError(getResources().getString(R.string.plsEnterRTPCRTestDate));
                        edtRtpcrTestDate.requestFocus();
                    } else if (rtpcr_test_result.isEmpty()) {
                        rbRTPCRTestResult.setError(getResources().getString(R.string.plsSelRTPCRTestResult));
                        rbRTPCRTestResult.requestFocus();
                    } else if (gov_private.isEmpty()) {
                        edtGovPrivate.setError(getResources().getString(R.string.plsEnterHospitalType));
                        edtGovPrivate.requestFocus();
                    } else if (o2room_level.isEmpty()) {
                        edtO2RoomLevel.setError(getResources().getString(R.string.plsEnterO2RoomLevel));
                        edtO2RoomLevel.requestFocus();
                    } else if (o2room_level_compare == 0) {
                        edtO2RoomLevel.setError(getResources().getString(R.string.plsEnterValidO2RoomLevel));
                        edtO2RoomLevel.requestFocus();
                    } else if (o2with_support.isEmpty()) {
                        edtO2WithSupport.setError(getResources().getString(R.string.plsEnterO2WithSupport));
                        edtO2WithSupport.requestFocus();
                    } else if (o2with_support_compare == 0) {
                        edtO2WithSupport.setError(getResources().getString(R.string.plsEnterValidO2WithSupport));
                        edtO2WithSupport.requestFocus();
                    } else if (patient_condition.isEmpty()) {
                        edtPatientCondition.setError(getResources().getString(R.string.plsEnterPatientCurrentCondition));
                        edtPatientCondition.requestFocus();
                    } else if (whathelpneeded.isEmpty()) {
                        edtWhatHelpNeeded.setError(getResources().getString(R.string.plsEnterWhatHelpNeeded));
                        edtWhatHelpNeeded.requestFocus();
                    } else if (patient_blood_group.isEmpty()) {
                        Toast.makeText(AddComplaintActivity.this, "Select Blood group", Toast.LENGTH_SHORT).show();
//                        spnBg.setError(getResources().getString(R.string.plsSelectComplaintType));
//                        spnCompType.requestFocus();
                    } else if (hrct_score.isEmpty()) {
                        edtHRCTScore.setError(getResources().getString(R.string.plsEnterHRCTScore));
                        edtHRCTScore.requestFocus();
                    } else if (!commonCode.isValidMobileNo(AddComplaintActivity.this, dr_mob_no)) {
                        edtDrMobNo.setError(getResources().getString(R.string.plsEnterDrMobNo));
                        edtDrMobNo.requestFocus();
                    } else if (dr_mob_no.matches("^[789]\\d{9}$")) {
                        edtDrMobNo.setError(getResources().getString(R.string.plsEnterValidDrMobNo));
                        edtDrMobNo.requestFocus();
                    }  else if (hospital_name.isEmpty()) {
                        edtHospitalName.setError(getResources().getString(R.string.plsEnterHospitalName));
                        edtHospitalName.requestFocus();
                    } else if (hospital_address.isEmpty()) {
                        edtHospitalAddress.setError(getResources().getString(R.string.plsEnterHospitalAddress));
                        edtHospitalAddress.requestFocus();
                    } else if (drName.isEmpty()) {
                        edtDrName.setError(getResources().getString(R.string.plsEnterDrName));
                        edtDrName.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, contact_person_name)) {
                        edtContactPersonName.setError(getResources().getString(R.string.plsEnterContactPersonName));
                        edtContactPersonName.requestFocus();
                    } else if (!commonCode.isValidMobileNo(AddComplaintActivity.this, contact_person_mob_no)) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    } else if (contact_person_mob_no.matches("^[789]\\d{9}$")) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterValidContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    } else {
                        addComplaint();
                    }
                } else if (complaintTypeId == 2) {
                    //oxygen
                    spnCompType.setError(getResources().getString(R.string.plsSelectServiceType));
                    spnCompType.requestFocus();
                } else if (complaintTypeId == 3) {
                    //plasma
                    if (patient_name.isEmpty()) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterPatientName));
                        edtPatientName.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, patient_name)) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterValidPatientName));
                        edtPatientName.requestFocus();
                    } else if (patient_age.isEmpty()) {
                        edtPatientAge.setError(getResources().getString(R.string.plsEnterPatientAge));
                        edtPatientAge.requestFocus();
                    } else if (patient_age_compare == 0) {
                        edtPatientAge.setError(getResources().getString(R.string.plsEnterValidPatientAge));
                        edtPatientAge.requestFocus();
                    } else if (patient_blood_group.isEmpty()) {
                        Toast.makeText(AddComplaintActivity.this, "Select Blood group", Toast.LENGTH_SHORT).show();
//                        spnBg.setError(getResources().getString(R.string.plsSelectComplaintType));
//                        spnCompType.requestFocus();
                    } else if (oxygenscore.isEmpty()) {
                        edtOxygenScore.setError(getResources().getString(R.string.plsEnterOxygenScore));
                        edtOxygenScore.requestFocus();
                    } else if (hrct_score.isEmpty()) {
                        edtHRCTScore.setError(getResources().getString(R.string.plsEnterHRCTScore));
                        edtHRCTScore.requestFocus();
                    } else if (units_required.isEmpty()) {
                        edtUnitsRequired.setError(getResources().getString(R.string.plsEnterPlasmaRequiredUnits));
                        edtUnitsRequired.requestFocus();
                    } else if (hospital_name.isEmpty()) {
                        edtHospitalName.setError(getResources().getString(R.string.plsEnterHospitalName));
                        edtHospitalName.requestFocus();
                    } else if (hospital_address.isEmpty()) {
                        edtHospitalAddress.setError(getResources().getString(R.string.plsEnterHospitalAddress));
                        edtHospitalAddress.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, contact_person_name)) {
                        edtContactPersonName.setError(getResources().getString(R.string.plsEnterContactPersonName));
                        edtContactPersonName.requestFocus();
                    } else if (!commonCode.isValidMobileNo(AddComplaintActivity.this, contact_person_mob_no)) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    }else if (contact_person_mob_no.matches("^[789]\\d{9}$")) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterValidContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    }  else {
                        addComplaint();
                    }
                } else if (complaintTypeId == 4) {
                    //icu
                    if (patient_name.isEmpty()) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterPatientName));
                        edtPatientName.requestFocus();
                    }else if (!commonCode.isValidString(AddComplaintActivity.this, patient_name)) {
                        edtPatientName.setError(getResources().getString(R.string.plsEnterValidPatientName));
                        edtPatientName.requestFocus();
                    }  else if (patient_age.isEmpty()) {
                        edtPatientAge.setError(getResources().getString(R.string.plsEnterPatientAge));
                        edtPatientAge.requestFocus();
                    } else if (patient_address.isEmpty()) {
                        edtAddress.setError(getResources().getString(R.string.plsEnterPatientAddress));
                        edtAddress.requestFocus();
                    } else if (ward.isEmpty()) {
                        edtWard.setError(getResources().getString(R.string.plsEnterWardType));
                        edtWard.requestFocus();
                    } else if (rtpcr_test_date.isEmpty()) {
                        rbRTPCRTestResult.setError(getResources().getString(R.string.plsEnterRTPCRTestDate));
                        rbRTPCRTestResult.requestFocus();
                    } else if (rtpcr_test_result.isEmpty()) {
                        edtRtpcrAntigen.setError(getResources().getString(R.string.plsEnterPatientName));
                        edtRtpcrAntigen.requestFocus();
                    } else if (patient_condition.isEmpty()) {
                        edtPatientCondition.setError(getResources().getString(R.string.plsEnterPatientCurrentCondition));
                        edtPatientCondition.requestFocus();
                    } else if (o2room_level.isEmpty()) {
                        edtO2RoomLevel.setError(getResources().getString(R.string.plsEnterO2RoomLevel));
                        edtO2RoomLevel.requestFocus();
                    } else if (o2room_level_compare == 0) {
                        edtO2RoomLevel.setError(getResources().getString(R.string.plsEnterValidO2RoomLevel));
                        edtO2RoomLevel.requestFocus();
                    } else if (o2with_support.isEmpty()) {
                        edtO2WithSupport.setError(getResources().getString(R.string.plsEnterO2WithSupport));
                        edtO2WithSupport.requestFocus();
                    } else if (o2with_support_compare == 0) {
                        edtO2WithSupport.setError(getResources().getString(R.string.plsEnterValidO2WithSupport));
                        edtO2WithSupport.requestFocus();
                    } else if (any_comorbidity.isEmpty()) {
                        edtAnyComorbidity.setError(getResources().getString(R.string.plsEnterOtherDisease));
                        edtAnyComorbidity.requestFocus();
                    }
//                    else if (hospital_address.isEmpty()) {
//                        edtHospitalAddress.setError(getResources().getString(R.string.plsEnterPatientName));
//                        edtHospitalAddress.requestFocus();
//                    }
                    else if (!commonCode.isValidString(AddComplaintActivity.this, drName)) {
                        edtDrName.setError(getResources().getString(R.string.plsEnterDrName));
                        edtDrName.requestFocus();
                    } else if (!commonCode.isValidMobileNo(AddComplaintActivity.this, dr_mob_no)) {
                        edtDrMobNo.setError(getResources().getString(R.string.plsEnterDrMobNo));
                        edtDrMobNo.requestFocus();
                    }else if (dr_mob_no.matches("^[789]\\d{9}$")) {
                        edtDrMobNo.setError(getResources().getString(R.string.plsEnterValidDrMobNo));
                        edtDrMobNo.requestFocus();
                    }  else if (!commonCode.isValidString(AddComplaintActivity.this, contact_person_name)) {
                        edtContactPersonName.setError(getResources().getString(R.string.plsEnterContactPersonName));
                        edtContactPersonName.requestFocus();
                    } else if (!commonCode.isValidMobileNo(AddComplaintActivity.this, contact_person_mob_no)) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    }else if (contact_person_mob_no.matches("^[789]\\d{9}$")) {
                        edtContactPersonMobNo.setError(getResources().getString(R.string.plsEnterValidContactPersonMobNo));
                        edtContactPersonMobNo.requestFocus();
                    }  else {
                        addComplaint();

                    }

                }


            }
        });

        tvBrowseImage = findViewById(R.id.tv_selectImage);
        tvBrowseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selImages)), PICK_IMAGE_MULTIPLE);
            }
        });

        getAllComplaintsType();
    }

    //http://192.168.0.146:8081/hrms/complaint/add
    private void addComplaint() {
        if (commonCode.checkConnection(AddComplaintActivity.this)) {
            String jsonurl = urllink.url + "complaint/add";
            progressDialog = new ProgressDialog(AddComplaintActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(AddComplaintActivity.this);

            JSONObject jsonParams = new JSONObject();
            try {

                jsonParams.put("patient_name", patient_name);
                jsonParams.put("age", patient_age);
                jsonParams.put("blood_group", patient_blood_group);
                jsonParams.put("hrct_score", hrct_score);
                jsonParams.put("units_required", units_required);
                jsonParams.put("hospital_name", hospital_name);
                jsonParams.put("hospital_address", hospital_address);
                jsonParams.put("contact_person_name", contact_person_name);
                jsonParams.put("relative_mobile", contact_person_mob_no);
                jsonParams.put("oxygen", oxygenscore);


                //icu
//                jsonParams.put("patient_name", patient_name);
//                jsonParams.put("age", patient_age);
                jsonParams.put("location", patient_address);
                jsonParams.put("ward", ward);
                jsonParams.put("date_of_test", rtpcr_test_date);
                jsonParams.put("rtpcr_or_antigen", rtpcr_test_result);
                jsonParams.put("oxy_room_level", o2room_level);
                jsonParams.put("oxy_with_support", o2with_support);
                jsonParams.put("other_disease", any_comorbidity);
                jsonParams.put("current_situation", patient_condition);
//                jsonParams.put("hospital_name", hospital_name);
                jsonParams.put("doc_name", drName);
                jsonParams.put("doc_mobile", dr_mob_no);
//                jsonParams.put("contact_person_name", contact_person_name);
//                jsonParams.put("relative_mobile", contact_person_mob_no);


                //bed
                jsonParams.put("bed_required", required_bed);
                jsonParams.put("govt_or_private", gov_private);
                jsonParams.put("help_detail", whathelpneeded);


                JSONObject resource = new JSONObject();
                resource.put("id", complaintTypeId);
                jsonParams.put("resource", resource);
//                jsonParams.put("hospital_name", hospital_name);
//                jsonParams.put("hospital_address", hospital_address);

                JSONObject user = new JSONObject();
                user.put("id", userId);
                jsonParams.put("user", user);

                jsonParams.put("status", "Open");


//                jsonParams.put("title", title);
//                jsonParams.put("complaintDescription", desc);
//                jsonParams.put("area", area);
//
//                JSONObject adminObj = new JSONObject();
//                adminObj.put("id", userId);
//                jsonParams.put("localUser", adminObj);
//
//                JSONObject comp = new JSONObject();
//                comp.put("id", complaintTypeId);
//                jsonParams.put("complaintType", comp);

//                JSONArray jsonArray = new JSONArray();
//                for (int i = 0; i < imagearray.size(); i++) {
//                    jsonArray.put(imagearray.get(i));
//                }
//                jsonParams.put("images", jsonArray);


                //jsonParams.put("token", newToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, jsonurl,

                    jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Added...!!!")) {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.requestSentSuccessfully), Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AddComplaintActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //   finish();
                    } else {
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AddComplaintActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllComplaintsType() {
        arraymapClass.clear();
        nameListClass = new ArrayList<>();
        if (commonCode.checkConnection(AddComplaintActivity.this)) {
            String jsonurl = urllink.url + "complaintType/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(AddComplaintActivity.this);
            progressDialog = new ProgressDialog(AddComplaintActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            HashMap<String, String> hashMap = new HashMap<>();
                            JSONObject jsonObject = response.getJSONObject(i);
                            hashMap.put("id", jsonObject.getString("id"));
                            hashMap.put("complaintType", jsonObject.getString("complaintType"));
                            String name = jsonObject.getString("complaintType");
                            nameListClass.add(name);

                            arraymapClass.add(hashMap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(AddComplaintActivity.this, android.R.layout.simple_spinner_item, nameListClass);
                    spnCompType.setAdapter(stringArrayAdapter2);
                    spnCompType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String valuename = parent.getItemAtPosition(position).toString();
                            complaintTypeId = 0;

                            if (!valuename.equals(getResources().getString(R.string.serviceType))) {
                                complaintTypeId = Integer.parseInt(arraymapClass.get(position).get("id"));
                                complaintType = (arraymapClass.get(position).get("complaintType"));
                                switch (complaintTypeId) {
                                    case 1:
                                        //hospital bed
                                        tilpaddress.setVisibility(View.VISIBLE);
                                        tv_require_bed.setVisibility(View.VISIBLE);
                                        rgBedRequired.setVisibility(View.VISIBLE);
                                        tv_rtpcr_test_result.setVisibility(View.VISIBLE);
                                        tilRtpcrTestDate.setVisibility(View.VISIBLE);
                                        tilgovprivate.setVisibility(View.VISIBLE);
                                        tilo2roomlevel.setVisibility(View.VISIBLE);
                                        tilo2withsupport.setVisibility(View.VISIBLE);
                                        tilpcondition.setVisibility(View.VISIBLE);
                                        tilDrName.setVisibility(View.VISIBLE);
                                        rgRtpcrTestResult.setVisibility(View.VISIBLE);
                                        tildrmobno.setVisibility(View.VISIBLE);
                                        tilhname.setVisibility(View.VISIBLE);
                                        tilhaddress.setVisibility(View.VISIBLE);
                                        tilhrctscore.setVisibility(View.VISIBLE);
                                        spnBg.setVisibility(View.VISIBLE);
                                        tilwhathelpneeded.setVisibility(View.VISIBLE);

                                        tilwherehelpneeded.setVisibility(View.GONE);
                                        tilwhereadmittedcurrently.setVisibility(View.GONE);
                                        tilAnycomorbidity.setVisibility(View.GONE);
                                        tiloxygenlevel.setVisibility(View.GONE);
                                        tilunitsreqired.setVisibility(View.GONE);

                                        tilertpcantigen.setVisibility(View.GONE);
                                        tildateoftest.setVisibility(View.GONE);
                                        tilotherdisease.setVisibility(View.GONE);

                                        tilWard.setVisibility(View.GONE);


                                        break;
                                    case 2:
                                        //oxygen cylinder
                                        break;
                                    case 3:
                                        //plasma
                                        tiloxygenlevel.setVisibility(View.VISIBLE);
                                        tilhrctscore.setVisibility(View.VISIBLE);
                                        tilunitsreqired.setVisibility(View.VISIBLE);
                                        tilhname.setVisibility(View.VISIBLE);
                                        tilhaddress.setVisibility(View.VISIBLE);
                                        tilhname.setVisibility(View.VISIBLE);
                                        spnBg.setVisibility(View.VISIBLE);

                                        tv_rtpcr_test_result.setVisibility(View.GONE);
                                        tilpaddress.setVisibility(View.GONE);
                                        tilWard.setVisibility(View.GONE);
                                        tilRtpcrTestDate.setVisibility(View.GONE);
                                        rgRtpcrTestResult.setVisibility(View.GONE);
                                        tilpcondition.setVisibility(View.GONE);
                                        tilo2roomlevel.setVisibility(View.GONE);
                                        tilo2withsupport.setVisibility(View.GONE);
                                        tilAnycomorbidity.setVisibility(View.GONE);

                                        tilDrName.setVisibility(View.GONE);
                                        tildrmobno.setVisibility(View.GONE);

                                        tv_require_bed.setVisibility(View.GONE);
                                        rgBedRequired.setVisibility(View.GONE);
                                        tildateoftest.setVisibility(View.GONE);
                                        tilertpcantigen.setVisibility(View.GONE);
                                        tilgovprivate.setVisibility(View.GONE);
                                        tilotherdisease.setVisibility(View.GONE);
                                        tilwhathelpneeded.setVisibility(View.GONE);
                                        tilwherehelpneeded.setVisibility(View.GONE);
                                        tilwhereadmittedcurrently.setVisibility(View.GONE);
                                        break;
                                    case 4:
                                        //icu & ventilator
                                        tv_rtpcr_test_result.setVisibility(View.VISIBLE);
                                        tilpaddress.setVisibility(View.VISIBLE);
                                        tilWard.setVisibility(View.VISIBLE);
                                        tilRtpcrTestDate.setVisibility(View.VISIBLE);
                                        rgRtpcrTestResult.setVisibility(View.VISIBLE);
                                        tilpcondition.setVisibility(View.VISIBLE);
                                        tilo2roomlevel.setVisibility(View.VISIBLE);
                                        tilo2withsupport.setVisibility(View.VISIBLE);
                                        tilAnycomorbidity.setVisibility(View.VISIBLE);
                                        tilhname.setVisibility(View.VISIBLE);
                                        tilDrName.setVisibility(View.VISIBLE);
                                        tildrmobno.setVisibility(View.VISIBLE);

                                        tiloxygenlevel.setVisibility(View.GONE);
                                        tilhrctscore.setVisibility(View.GONE);
                                        tilunitsreqired.setVisibility(View.GONE);
                                        tilhaddress.setVisibility(View.GONE);

                                        tv_require_bed.setVisibility(View.GONE);
                                        rgBedRequired.setVisibility(View.GONE);
                                        tildateoftest.setVisibility(View.GONE);
                                        tilertpcantigen.setVisibility(View.GONE);
                                        tilgovprivate.setVisibility(View.GONE);
                                        spnBg.setVisibility(View.GONE);
                                        tilotherdisease.setVisibility(View.GONE);
                                        tilwhathelpneeded.setVisibility(View.GONE);
                                        tilwherehelpneeded.setVisibility(View.GONE);
                                        tilwhereadmittedcurrently.setVisibility(View.GONE);
                                        break;
                                }
                            } else {
                                complaintTypeId = 0;
                                complaintType = "";
                            }

                            //    Toast.makeText(AddComplaintActivity.this, arraymapClass.get(position).get("id"), Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AddComplaintActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        commonCode.AlertDialog_Pbtn(AddComplaintActivity.this, getResources().getString(R.string.serverError), "", getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(AddComplaintActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {

                mArrayUri = new ArrayList<Uri>();
                mArrayUri.clear();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();

                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();

                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(AddComplaintActivity.this, mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(AddComplaintActivity.this.getContentResolver(), mImageUri);
                        bitImageString = getStringImage(bitmap);
                        imagearray.add(bitImageString);
                    } catch (Exception e) {
                        Toast.makeText(AddComplaintActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            if (i >= 10) {
                                Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.cantShareMoreImages),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();

                                galleryAdapter = new GalleryAdapter(AddComplaintActivity.this, mArrayUri);
                                gvGallery.setAdapter(galleryAdapter);
                                gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                        .getLayoutParams();
                                mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(AddComplaintActivity.this.getContentResolver(), uri);
                                    bitImageString = getStringImage(bitmap);
                                    imagearray.add(bitImageString);
                                } catch (Exception e) {
                                }
                            }
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(AddComplaintActivity.this, "You haven't picked ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(AddComplaintActivity.this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedUploadedFile = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedUploadedFile;
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


    public void confirmCommentPopup(View v) {
        TextView tvClose, tvName, tvMobNo, tvType, tvTitle, tvDesc, tvArea;

        myDialog.setContentView(R.layout.cmplnt_verify_popup);

        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


        tvClose = (TextView) myDialog.findViewById(R.id.txtclose);
        tvClose.setText("X");
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        tvName = (TextView) myDialog.findViewById(R.id.tv_name);
        tvMobNo = (TextView) myDialog.findViewById(R.id.tv_mob_no);
        tvType = (TextView) myDialog.findViewById(R.id.tv_type);
        tvTitle = (TextView) myDialog.findViewById(R.id.tv_title);
        tvDesc = (TextView) myDialog.findViewById(R.id.tv_desc);
        tvArea = (TextView) myDialog.findViewById(R.id.tv_area);

        tvName.setText(firstName + " " + middleName + " " + lastName);
        tvMobNo.setText(mobileno);
        tvType.setText(complaintType);
        tvTitle.setText(title);
        tvDesc.setText(desc);
        tvArea.setText(area);

        Button btnCancel = (Button) myDialog.findViewById(R.id.btn_cancel);
        Button btnSave = (Button) myDialog.findViewById(R.id.btn_add);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (complaintTypeId) {
                    case 1:
                        //hospital bed
                        addHospitalBed();
                        break;
                    case 2:
                        //oxygen cylinder
                        break;
                    case 3:
                        //plasma
                        addPlasma();
                        break;
                    case 4:
                        //icu & ventilator
                        addICUVentilator();
                        break;
                }
//                addComplaint();
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void addICUVentilator() {

        if (commonCode.checkConnection(AddComplaintActivity.this)) {
            String jsonurl = urllink.url + "resource/saveICUData";
            progressDialog = new ProgressDialog(AddComplaintActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(AddComplaintActivity.this);

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("patient_name", patient_name);
                jsonParams.put("patient_age", patient_age);
                jsonParams.put("patient_address", patient_address);
                jsonParams.put("ward", ward);
                jsonParams.put("rtpcr_test_date", rtpcr_test_date);
                jsonParams.put("test_result", rtpcr_test_result);
                jsonParams.put("current_situation", patient_condition);
                jsonParams.put("oxygen", o2room_level);
                jsonParams.put("oxygen_with_supprt", o2with_support);
                jsonParams.put("other_disease", any_comorbidity);
                jsonParams.put("hospital_name", hospital_name);
                jsonParams.put("doc_name", drName);
                jsonParams.put("doc_mobile", dr_mob_no);
                jsonParams.put("contact_person_name", contact_person_name);
                jsonParams.put("contact_person_mobie", contact_person_mob_no);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, jsonurl,

                    jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Added...!!!")) {
//                            sendIcuRequestToWP();

                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.requestGeneratedSuccessfully), Toast.LENGTH_LONG).show();

                            onBackPressed();
                        } else if (message.equals("Failed to add...!!!")) {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AddComplaintActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //   finish();
                    } else {
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AddComplaintActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    private void addPlasma() {

        if (commonCode.checkConnection(AddComplaintActivity.this)) {
            String jsonurl = urllink.url + "complaint/add";
            progressDialog = new ProgressDialog(AddComplaintActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(AddComplaintActivity.this);

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("patient_name", patient_name);
                jsonParams.put("age", patient_age);
                jsonParams.put("blood_group", patient_blood_group);
                // jsonParams.put("oxygen_level", oxygenscore);
                jsonParams.put("hrct_score", hrct_score);
                jsonParams.put("units_required", units_required);
                jsonParams.put("hospital_name", hospital_name);
                jsonParams.put("hospital_address", hospital_address);
                jsonParams.put("contact_person_name", contact_person_name);
                jsonParams.put("relative_mobile", contact_person_mob_no);
//                 JSONObject resource=jsonParams.getJSONObject("resource");
//                 resource.put("id",complaintTypeId);
//                JSONObject user_id=jsonParams.getJSONObject("user");
//                user_id.put("id",userId);

                JSONObject resource = new JSONObject();
                resource.put("id", complaintTypeId);
                jsonParams.put("resource", resource);

                JSONObject user = new JSONObject();
                user.put("id", userId);
                jsonParams.put("user", user);
//                String toNumber = "+91 9975868788";
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + Form_Message));
//                startActivity(intent);
//                finish();

//                JSONObject adminObj = new JSONObject();
//                adminObj.put("id", userId);
//                jsonParams.put("localUser", adminObj);
//
//                JSONObject comp = new JSONObject();
//                comp.put("id", complaintTypeId);
//                jsonParams.put("complaintType", comp);
//
//                JSONArray jsonArray = new JSONArray();
//                for (int i = 0; i < imagearray.size(); i++) {
//                    jsonArray.put(imagearray.get(i));
//                }
//                jsonParams.put("images", jsonArray);
//
//                jsonParams.put("status", "Open");

                //jsonParams.put("token", newToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, jsonurl,

                    jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Added...!!!")) {
//                            sendIcuRequestToWP();

                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.requestGeneratedSuccessfully), Toast.LENGTH_LONG).show();

                            onBackPressed();
                        } else if (message.equals("Failed to add...!!!")) {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AddComplaintActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //   finish();
                    } else {
                        Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AddComplaintActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    private void addHospitalBed() {

    }
}
