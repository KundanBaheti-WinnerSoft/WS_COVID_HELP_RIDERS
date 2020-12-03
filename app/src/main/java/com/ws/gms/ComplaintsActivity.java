package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ws.gms.Adapters.ComplaintsAdapter;
import com.ws.gms.Adapters.ComplaintsDetails;
import com.ws.gms.Adapters.ItemClickListener;
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

public class ComplaintsActivity extends AppCompatActivity implements ItemClickListener {

    CommonCode commonCode = new CommonCode();

    private RecyclerView rvGrievances;
    final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
    private ComplaintsAdapter madapter;

    EditText edtSearch, edtSearchDate;

    Urllink urllink = new Urllink();
    ProgressDialog progressDialog;

    String SecurityToken, userName, userRole, userId;

    private SharedPreferences sharedPreferences;
    String complaintStatus, tokenNo;
    int complaintId;
    ImageButton imgBtnSearch;
    String status, searchType, complaintType = "", searchDate = "";
    Spinner spnSearchType;
    String jsonurl;
    private int mYear, mMonth, mDay, mHour, mMinute;


    int complaintTypeId = 0;
    final ArrayList<HashMap<String, String>> arraymapClass = new ArrayList<>();
    ArrayList<String> nameListClass = new ArrayList<>();

    MaterialSpinner spnCompType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        commonCode.updateLocaleIfNeeded(ComplaintsActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.complaints));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userName = sharedPreferences.getString("username", "");
        userRole = sharedPreferences.getString("role", "");
        userId = sharedPreferences.getString("userId", "");

        rvGrievances = findViewById(R.id.rv_grievances);
        edtSearch = findViewById(R.id.edt_search);
        spnCompType = (MaterialSpinner) findViewById(R.id.spn_comp_type);
        edtSearchDate = findViewById(R.id.edt_search_date);
        imgBtnSearch = findViewById(R.id.img_search);

        spnSearchType = (Spinner) findViewById(R.id.spinner);
        String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.area), getResources().getString(R.string.complaintType), getResources().getString(R.string.date)};

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnSearchType.setAdapter(aa);
        spnSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                searchType = parent.getItemAtPosition(position).toString();

                if (searchType.equals(getResources().getString(R.string.tokenNo))) {
                    edtSearchDate.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.VISIBLE);
                    spnCompType.setVisibility(View.GONE);
                    edtSearch.setHint(getResources().getString(R.string.tokenNo));
                } else if (searchType.equals(getResources().getString(R.string.area))) {
                    edtSearchDate.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.VISIBLE);
                    spnCompType.setVisibility(View.GONE);
                    edtSearch.setHint(getResources().getString(R.string.area));
                } else if (searchType.equals(getResources().getString(R.string.complaintType))) {
                    getAllComplaintsType();
                    edtSearchDate.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.GONE);
                    spnCompType.setVisibility(View.VISIBLE);
                    edtSearch.setHint(getResources().getString(R.string.complaintType));
                } else if (searchType.equals(getResources().getString(R.string.date))) {
                    edtSearchDate.setVisibility(View.VISIBLE);
                    edtSearch.setVisibility(View.GONE);
                    spnCompType.setVisibility(View.GONE);
                    edtSearch.setHint(getResources().getString(R.string.date));
                    edtSearchDate.setHint(getResources().getString(R.string.date));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenNo = edtSearch.getText().toString();

                if (searchType.equals(getResources().getString(R.string.tokenNo))) {
                    if (!commonCode.isValidString(ComplaintsActivity.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {

                        jsonurl = urllink.url + "complaint/getByTokebNosuperadmin/" + tokenNo;

                        getSearchedComplaints();
                    }
                } else if (searchType.equals(getResources().getString(R.string.area))) {
                    if (!commonCode.isValidString(ComplaintsActivity.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {

                        jsonurl = urllink.url + "complaint/SearchByCompAreaAdminAndSuperAdmin/" + tokenNo;

                        getSearchedComplaints();
                    }
                } else if (searchType.equals(getResources().getString(R.string.complaintType))) {
                    if (!commonCode.isValidInt(ComplaintsActivity.this, complaintTypeId)) {
                        spnCompType.setError(getResources().getString(R.string.plsSelectComplaintType));
                        spnCompType.requestFocus();
                    } else {

                        jsonurl = urllink.url + "complaint/SearchByCompTypeAdminSuperAdmin/" + complaintTypeId;

                        getSearchedComplaints();
                    }
                } else if (searchType.equals(getResources().getString(R.string.date))) {
                    searchDate = edtSearchDate.getText().toString();

                    if (!commonCode.isValidString(ComplaintsActivity.this, searchDate)) {
                        edtSearchDate.setError(getResources().getString(R.string.plsSelDate));
                        edtSearchDate.requestFocus();
                    } else {

                        jsonurl = urllink.url + "complaint/SearchByCompDateAdminAndSuperadmin/" + searchDate;

                        getSearchedComplaints();
                    }
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    edtSearch.getText().clear();
                    getAllComplaints();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        edtSearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDispatchCalendar();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(ComplaintsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                            String monthString = String.valueOf(monthOfYear + 1);
                            if (monthString.length() == 1) {
                                monthString = "0" + monthString;
                            }

                            String dayOfMonthString = String.valueOf(dayOfMonth);
                            if (dayOfMonthString.length() == 1) {
                                dayOfMonthString = "0" + dayOfMonthString;
                            }

                            edtSearchDate.setText(year + "-" + (monthString) + "-" + dayOfMonthString);                            //  layoutIssueDate.setErrorEnabled(false);
                            //    edtDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            //    BirthDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.plsEnterValidBirthdate), Toast.LENGTH_LONG).show();
                            edtSearchDate.setText(null);
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
                datePickerDialog.show();

                //Set Max Date
                Date today = new Date();
                Calendar c = Calendar.getInstance();

                c.setTime(today);
                c.add(Calendar.MONTH, 0);
                long mixDate = c.getTime().getTime();
                datePickerDialog.getDatePicker().setMaxDate(mixDate);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            status = bundle.getString("status");
            getComplaintByStatus();
        } else {
            getAllComplaints();
        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            status = bundle.getString("status");
//            getComplaintByStatus();
//        } else {
//            getAllComplaints();
//        }
//    }

    private void getAllComplaintsType() {
        arraymapClass.clear();
        nameListClass = new ArrayList<>();
        if (commonCode.checkConnection(ComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaintType/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(ComplaintsActivity.this);
            progressDialog = new ProgressDialog(ComplaintsActivity.this);
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

                    ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(ComplaintsActivity.this, android.R.layout.simple_spinner_item, nameListClass);
                    spnCompType.setAdapter(stringArrayAdapter2);
                    spnCompType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String valuename = parent.getItemAtPosition(position).toString();
                            if (!valuename.equals(getResources().getString(R.string.compType))) {
                                complaintTypeId = Integer.parseInt(arraymapClass.get(position).get("id"));
                                complaintType = (arraymapClass.get(position).get("complaintType"));
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
                        Intent intent = new Intent(ComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.serverError), "", getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getComplaintByStatus() {
        arraymap.clear();
        if (commonCode.checkConnection(ComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaint/getByStatus/" + status;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(ComplaintsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    arraymap.clear();
                    progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = response.getJSONObject(i);
                                hashMap.put("complaintId", object.getString("id"));
                                hashMap.put("area", object.getString("area"));
                                hashMap.put("title", object.getString("title"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("complaintDescription", object.getString("complaintDescription"));
                                hashMap.put("status", object.getString("status"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));

                                JSONObject objName = object.getJSONObject("localUser");
                                hashMap.put("firstname", objName.getString("firstname"));
                                hashMap.put("middlename", objName.getString("middlename"));
                                hashMap.put("lastname", objName.getString("lastname"));
                                hashMap.put("mobileno", objName.getString("mobileno"));
                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("complaintType");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));

                                if (object.has("assingVolunteer") && !object.isNull("assingVolunteer")) {
                                    JSONObject assignToObj = object.getJSONObject("assingVolunteer");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);
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
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllComplaints() {
        arraymap.clear();
        if (commonCode.checkConnection(ComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaint/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(ComplaintsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    arraymap.clear();
                    progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = response.getJSONObject(i);
                                hashMap.put("complaintId", object.getString("id"));
                                hashMap.put("area", object.getString("area"));
                                hashMap.put("title", object.getString("title"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("complaintDescription", object.getString("complaintDescription"));
                                hashMap.put("status", object.getString("status"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));

                                JSONObject objName = object.getJSONObject("localUser");
                                hashMap.put("firstname", objName.getString("firstname"));
                                hashMap.put("middlename", objName.getString("middlename"));
                                hashMap.put("lastname", objName.getString("lastname"));
                                hashMap.put("mobileno", objName.getString("mobileno"));
                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("complaintType");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));

                                if (object.has("assingVolunteer") && !object.isNull("assingVolunteer")) {
                                    JSONObject assignToObj = object.getJSONObject("assingVolunteer");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }

                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }

                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);
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
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getSearchedComplaints() {
        arraymap.clear();
        if (commonCode.checkConnection(ComplaintsActivity.this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(ComplaintsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    arraymap.clear();
                    progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = response.getJSONObject(i);
                                hashMap.put("complaintId", object.getString("id"));
                                hashMap.put("area", object.getString("area"));
                                hashMap.put("title", object.getString("title"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("complaintDescription", object.getString("complaintDescription"));
                                hashMap.put("status", object.getString("status"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));

                                JSONObject objName = object.getJSONObject("localUser");
                                hashMap.put("firstname", objName.getString("firstname"));
                                hashMap.put("middlename", objName.getString("middlename"));
                                hashMap.put("lastname", objName.getString("lastname"));
                                hashMap.put("mobileno", objName.getString("mobileno"));
                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("complaintType");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));

                                if (object.has("assingVolunteer") && !object.isNull("assingVolunteer")) {
                                    JSONObject assignToObj = object.getJSONObject("assingVolunteer");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);
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
            requestQueue.add(jsonArrayRequest);
        } else {
            commonCode.AlertDialog_Pbtn(ComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showdata(ArrayList<HashMap<String, String>> arraymap) {
        madapter = new ComplaintsAdapter(getApplicationContext(), arraymap);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvGrievances.setLayoutManager(mLayoutManager);
        rvGrievances.setAdapter(madapter);
        madapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {

        ComplaintsDetails complaintsDetails = new ComplaintsDetails();
        complaintsDetails = (ComplaintsDetails) view.getTag();
        complaintId = complaintsDetails.getComplaintId();
        complaintStatus = complaintsDetails.getStatus();
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(complaintsDetails.getComplaintId()));
        bundle.putString("firstName", complaintsDetails.getFirstname());
        bundle.putString("middleName", complaintsDetails.getMiddlename());
        bundle.putString("lastName", complaintsDetails.getLastname());
        bundle.putString("mobileno", complaintsDetails.getMobileno());
        bundle.putString("tokennumber", complaintsDetails.getTokenNumber());
        bundle.putString("complaintType", complaintsDetails.getComplaintType());
        bundle.putString("title", complaintsDetails.getTitle());
        bundle.putString("description", complaintsDetails.getDesc());
        bundle.putString("Area", complaintsDetails.getArea());
        bundle.putString("date", complaintsDetails.getComplaintDate());
        bundle.putString("vComment", complaintsDetails.getVcomment());
        bundle.putString("uComment", complaintsDetails.getUcomment());
        bundle.putString("userImage", complaintsDetails.getUserImage());

        bundle.putString("assignToId", complaintsDetails.getAssignToId());
        bundle.putString("assignFname", complaintsDetails.getAssignFname());
        bundle.putString("assignMname", complaintsDetails.getAssignMname());
        bundle.putString("assignLname", complaintsDetails.getAssignLname());
        bundle.putString("assignMono", complaintsDetails.getAssignMono());
        bundle.putString("assignEmailid", complaintsDetails.getAssignEmailid());

        switch (view.getId()) {
            case R.id.tv_status:
                if (complaintStatus.equals("Closed")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alreadyColsed), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Open")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.open), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("In Progress")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.inProgress), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Resolved")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.resolved), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cv_complaint:
                Intent intent = new Intent(ComplaintsActivity.this, ComplaintDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getAllComplaints();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDispatchCalendar() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }
}