package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RegistrationActivity extends AppCompatActivity {

    private EditText edtFname, edtMName, edtLname, edtMob, edtEmailid, edtAddress, edtArea, edtDob, edtPincode, edtCity;
    Button btnCancel, btnRegister;
    private ProgressDialog progressDialog;
    private RadioButton rbMale, rbFemale;
    private RadioGroup radioGroupGender;

    private CommonCode commonCode = new CommonCode();
    private Urllink urllink = new Urllink();
    private String role = "", firstname = "", middlename = "", lastname = "", mobileno = "", EmailId = "", BirthDate = "", address = "", area = "", cityval = "", pincodeval = "";
    private String radiovalue = "";
    private String SecurityToken;
    private int mYear, mMonth, mDay, mHour, mMinute;

    String jsonurl = "", userId;

    MaterialSpinner spnCountry, spnState, spnDistrict;

    //    final ArrayList<HashMap<String, String>> arraymapCountry = new ArrayList<>();
//    ArrayList<String> nameListCountry = new ArrayList<>();
//
    final ArrayList<HashMap<String, String>> arraymapState = new ArrayList<>();
    ArrayList<String> nameListState = new ArrayList<>();

    final ArrayList<HashMap<String, String>> arraymapDistrict = new ArrayList<>();
    ArrayList<String> nameListDistrict = new ArrayList<>();

    String countryName = "", stateName = "", districtName = "";
    int countryId = 0, stateId = 0, districtId = 0;

    private EditText edtNewPassword, edtConfirmPassword;
    private String newpass = "", confirmpass = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        commonCode.updateLocaleIfNeeded(RegistrationActivity.this);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");

        edtFname = findViewById(R.id.edt_fname);

        edtMName = findViewById(R.id.edt_mname);

        edtLname = findViewById(R.id.edt_lname);

        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        edtMob = findViewById(R.id.edt_mob);
        edtEmailid = findViewById(R.id.edt_emailid);
        edtNewPassword = findViewById(R.id.new_password);
        edtConfirmPassword = findViewById(R.id.confirm_password);


        edtDob = findViewById(R.id.edt_birthdate);
        edtAddress = findViewById(R.id.edt_address);
        edtArea = findViewById(R.id.edt_area);

        //  spnCountry = findViewById(R.id.spn_country);
        spnState = findViewById(R.id.spn_state);
        spnDistrict = findViewById(R.id.spn_district);

        edtCity = findViewById(R.id.edt_city);

        edtPincode = findViewById(R.id.edt_pincode);

        radioGroupGender = findViewById(R.id.radio_group);

        btnCancel = findViewById(R.id.btn_cancel);
        btnRegister = findViewById(R.id.btn_register);

        Intent oIntent = getIntent();
        Bundle bd = oIntent.getExtras();
        role = "User";
        if (bd != null) {
            role = oIntent.getExtras().getString("role", "");
            userId = oIntent.getExtras().getString("userId", "");
            getSupportActionBar().setTitle(role + " " + getResources().getString(R.string.registration));
            getSupportActionBar().setSubtitle("");
        } else {
            role = "User";
            getSupportActionBar().setTitle(getResources().getString(R.string.citizenRegistration));
            getSupportActionBar().setSubtitle("");
        }


        if (role.equalsIgnoreCase("Administrator") || role.equalsIgnoreCase("Volunteer")) {
            edtConfirmPassword.setVisibility(View.GONE);
            edtNewPassword.setVisibility(View.GONE);
        } else {
            edtConfirmPassword.setVisibility(View.VISIBLE);
            edtNewPassword.setVisibility(View.VISIBLE);
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  confirmOtp();
                validation();
            }
        });

        rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radiovalue = "";
                radiovalue = "Male";
                // Toast.makeText(getApplicationContext(), radiovalue, Toast.LENGTH_LONG).show();
            }
        });

        rbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radiovalue = "";
                radiovalue = "Female";
                //Toast.makeText(getApplicationContext(), radiovalue, Toast.LENGTH_LONG).show();
            }
        });

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDispatchCalendar();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //compare date is not previous
                        String s = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        SimpleDateFormat simpleformater = new SimpleDateFormat("MM-dd-yyyy");
                        Date selectedDate = null;
                        Date date = new Date();
                        String d = simpleformater.format(date);

                        Date currentDate = null;
                        try {
                            selectedDate = simpleformater.parse(s);
                            currentDate = simpleformater.parse(d);
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }

                        if (selectedDate.compareTo(currentDate) < 0 || selectedDate.compareTo(currentDate) == 0) {
                            //edtIssueDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            edtDob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            //  layoutIssueDate.setErrorEnabled(false);
                            //    edtDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            //    BirthDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.plsEnterValidBirthdate), Toast.LENGTH_LONG).show();
                            edtDob.setText(null);
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
                datePickerDialog.show();

                //Set Max Date
                Date today = new Date();
                Calendar c = Calendar.getInstance();

                c.setTime(today);
                c.add(Calendar.MONTH, -1);
                long mixDate = c.getTime().getTime();
                datePickerDialog.getDatePicker().setMaxDate(mixDate);
            }
        });

        getAllState();
    }


//    //http://192.168.0.135:8081/hrms/regLocalUser/countryList
//    private void getAllCountry() {
//        arraymapCountry.clear();
//        nameListCountry.clear();
//        if (commonCode.checkConnection(RegistrationActivity.this)) {
//            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
//            String jsonurl = urllink.url + "regLocalUser/countryList";
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            progressDialog = new ProgressDialog(RegistrationActivity.this);
//            progressDialog.setMessage(getResources().getString(R.string.loading));
//            progressDialog.show();
//            progressDialog.setCancelable(true);
//
//            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    JSONObject data = response;
//                    progressDialog.dismiss();
//                    if (response.length() > 0) {
//
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("geonames");
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject obj = jsonArray.getJSONObject(i);
//                                HashMap<String, String> hashMap = new HashMap<>();
//                                hashMap.put("geonameId", obj.getString("geonameId"));
//                                hashMap.put("countryName", obj.getString("countryName"));
//                                String name = obj.getString("countryName");
//                                nameListCountry.add(name);
//
//                                arraymapCountry.add(hashMap);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(RegistrationActivity.this, android.R.layout.simple_spinner_item, nameListCountry);
//                        spnCountry.setAdapter(stringArrayAdapter2);
//                        spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                String valuename = parent.getItemAtPosition(position).toString();
//                                if (!valuename.equals(getResources().getString(R.string.selCountry))) {
//                                    countryId = Integer.parseInt(arraymapCountry.get(position).get("geonameId"));
//                                    countryName = (arraymapCountry.get(position).get("countryName"));
//
//                                    getAllState();
//                                } else {
//                                    countryId = 0;
//                                    countryName = "";
//                                }
//
//                                //    Toast.makeText(RegistrationActivity.this, arraymapClass.get(position).get("id"), Toast.LENGTH_LONG).show();
//
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
//                    } else {
//                        commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    progressDialog.dismiss();
//                    String err = error.toString();
//                    if (err.equals("com.android.volley.AuthFailureError")) {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();
//
//                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
//                        sp.edit().clear().commit();
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);
//                        //  finish();
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//            requestQueue.add(jsonArrayRequest);
//        } else {
//            commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
//        }
//    }


    //http://192.168.0.135:8081/hrms/regLocalUser/StateList/1269750
    private void getAllState() {
        arraymapState.clear();
        nameListState.clear();
        countryId = 1269750;
        if (commonCode.checkConnection(RegistrationActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "regLocalUser/StateList/" + countryId;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    progressDialog.dismiss();
                    if (response.length() > 0) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("geonames");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("geonameId", obj.getString("geonameId"));
                                hashMap.put("name", obj.getString("name"));
                                String name = obj.getString("name");
                                nameListState.add(name);

                                arraymapState.add(hashMap);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(RegistrationActivity.this, android.R.layout.simple_spinner_item, nameListState);
                        spnState.setAdapter(stringArrayAdapter2);
                        // spnState.setSelection(21);
                        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String valuename = parent.getItemAtPosition(position).toString();
                                if (!valuename.equals(getResources().getString(R.string.selState))) {
                                    stateId = Integer.parseInt(arraymapState.get(position).get("geonameId"));
                                    stateName = (arraymapState.get(position).get("name"));
                                    getAllDistrict();
                                } else {
                                    stateId = 0;
                                    stateName = "";
                                }

                                //    Toast.makeText(RegistrationActivity.this, arraymapClass.get(position).get("id"), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
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
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    //http://192.168.0.135:8081/hrms/regLocalUser/districtList/1264418
    private void getAllDistrict() {
        arraymapDistrict.clear();
        nameListDistrict.clear();
        if (commonCode.checkConnection(RegistrationActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "regLocalUser/districtList/" + stateId;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    progressDialog.dismiss();
                    if (response.length() > 0) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("geonames");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("geonameId", obj.getString("geonameId"));
                                hashMap.put("name", obj.getString("name"));
                                String name = obj.getString("name");
                                nameListDistrict.add(name);

                                arraymapDistrict.add(hashMap);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(RegistrationActivity.this, android.R.layout.simple_spinner_item, nameListDistrict);
                        spnDistrict.setAdapter(stringArrayAdapter2);
                        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String valuename = parent.getItemAtPosition(position).toString();
                                if (!valuename.equals(getResources().getString(R.string.selDistrict))) {
                                    districtId = Integer.parseInt(arraymapDistrict.get(position).get("geonameId"));
                                    districtName = (arraymapDistrict.get(position).get("name"));
                                } else {
                                    districtId = 0;
                                    districtName = "";
                                }
                                //    Toast.makeText(RegistrationActivity.this, arraymapClass.get(position).get("id"), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
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
            });
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showDispatchCalendar() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    public void validation() {
        countryName = "India";
        firstname = edtFname.getText().toString();
        middlename = edtMName.getText().toString();
        lastname = edtLname.getText().toString();
        mobileno = edtMob.getText().toString();
        EmailId = edtEmailid.getText().toString();
        newpass = edtNewPassword.getText().toString().trim();
        confirmpass = edtConfirmPassword.getText().toString().trim();

        BirthDate = edtDob.getText().toString();
        address = edtAddress.getText().toString();
        area = edtArea.getText().toString();
        pincodeval = edtPincode.getText().toString();
        cityval = edtCity.getText().toString();

        if (!commonCode.isValidString(RegistrationActivity.this, firstname)) {
            edtFname.setError(getResources().getString(R.string.plsEnterFirstName));
            edtFname.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, middlename)) {
            edtMName.setError(getResources().getString(R.string.plsEnterMiddleName));
            edtMName.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, lastname)) {
            edtLname.setError(getResources().getString(R.string.plsEnterLastName));
            edtLname.requestFocus();
        } else if (radiovalue.equals("")) {
            rbMale.setError(getResources().getString(R.string.plsEnterGender));
            rbMale.requestFocus();
        } else if (!commonCode.isValidMobileNo(RegistrationActivity.this, mobileno)) {
            edtMob.setError(getResources().getString(R.string.plsEnterMobNo));
            edtMob.requestFocus();
        } else if (!edtEmailid.getText().toString().trim().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
            edtEmailid.setError(getResources().getString(R.string.validMailid));
            edtEmailid.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, BirthDate)) {
            edtDob.setError(getResources().getString(R.string.plsEnterBirthdate));
            edtDob.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, stateName)) {
            spnState.setError(getResources().getString(R.string.plsSelectState));
            spnState.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, districtName)) {
            spnDistrict.setError(getResources().getString(R.string.plsSelectDistrict));
            spnDistrict.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, address)) {
            edtAddress.setError(getResources().getString(R.string.plsEnterAddress));
            edtAddress.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, area)) {
            edtArea.setError(getResources().getString(R.string.plsEnterArea));
            edtArea.requestFocus();
        } else if (!commonCode.isValidString(RegistrationActivity.this, cityval)) {
            edtCity.setError(getResources().getString(R.string.plsEnterCity));
            edtCity.requestFocus();
        } else if (!commonCode.isValidPincode(RegistrationActivity.this, pincodeval)) {
            edtPincode.setError(getResources().getString(R.string.plsEnterPincode));
            edtPincode.requestFocus();
        } else {
            if (role.equals("Administrator") || role.equals("Volunteer")) {
                if (role.equals("Administrator")) {
                    jsonurl = urllink.url + "regAdmin";
                } else if (role.equals("Volunteer")) {
                    jsonurl = urllink.url + "regVolunteer";
                }
            } else if (role.equals("User") || role.equals("")) {
                if (!commonCode.isValidString(RegistrationActivity.this, newpass)) {
                    edtNewPassword.setError(getResources().getString(R.string.enterNewPswd));
                    edtNewPassword.requestFocus();
                } else if (!commonCode.isValidString(RegistrationActivity.this, confirmpass)) {
                    edtConfirmPassword.setError(getResources().getString(R.string.enterConfirmNewPswd));
                    edtConfirmPassword.requestFocus();
                } else if (!newpass.equals(confirmpass)) {
                    commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.incorrectPassword), getResources().getString(R.string.passwordDoesnMatched), getResources().getString(R.string.ok));
                } else {
                    jsonurl = urllink.url + "regLocalUser";
                }
            }
            checkExistOrNot();
        }
    }

    private void register() {
        if (commonCode.checkConnection(RegistrationActivity.this)) {
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("role", role);
                jsonParams.put("firstname", firstname);
                jsonParams.put("middlename", middlename);
                jsonParams.put("lastname", lastname);
                jsonParams.put("mobileno", mobileno);
                jsonParams.put("emailid", EmailId);
                jsonParams.put("birthdate", BirthDate);
                jsonParams.put("country", countryName);
                jsonParams.put("state", stateName);
                jsonParams.put("city", cityval);
                jsonParams.put("gender", radiovalue);
                jsonParams.put("pincode", pincodeval);
                jsonParams.put("address", address);
                jsonParams.put("area", area);

                if (role.equals("User") || role.equals("")) {
                    jsonParams.put("password", newpass);
                }

                if (role.equals("Volunteer")) {
                    JSONObject adminObj = new JSONObject();
                    adminObj.put("id", userId);
                    jsonParams.put("admin", adminObj);
                }
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
                        if (message.equals("Saved successfully")) {
                            // Toast.makeText(getApplicationContext(), getResources().getString(R.string.registeredSuccess), Toast.LENGTH_LONG).show();
                            regSuccessPopup();

                        } else if (message.equals("EmailId Already exists")) {
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
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
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
            commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    private void checkExistOrNot() {
        if (commonCode.checkConnection(RegistrationActivity.this)) {
            String jsonurl = urllink.url + "user/MobileNumberValidate";
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("emailId", EmailId);
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
                        if (message.equals("EmailId Already exists")) {
                            mobNoAlreadyPopup();
                        } else if (message.equals("EmailId not exists")) {
                            register();
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
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
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
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("x-Auth-token", SecurityToken);
//                return params;
//            }
//            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(postRequest);
        } else {
            commonCode.AlertDialog_Pbtn(RegistrationActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
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


    //This method would confirm the otp
    private void confirmOtp() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        AppCompatButton buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        EditText editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();
    }

    private void regSuccessPopup() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_reg_success, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        Button buttonConfirm = (Button) confirmDialog.findViewById(R.id.btn_ok);
        TextView tvSuccess = (TextView) confirmDialog.findViewById(R.id.tv_success);

        if (role.equals("User") || role.equals("")) {
            tvSuccess.setText(getResources().getString(R.string.userRegSuccess));
        } else {
            tvSuccess.setText(getResources().getString(R.string.regSuccess));
        }

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

    //This method would confirm the otp
    private void mobNoAlreadyPopup() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.mob_no_exist_dialog, null);

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
}
