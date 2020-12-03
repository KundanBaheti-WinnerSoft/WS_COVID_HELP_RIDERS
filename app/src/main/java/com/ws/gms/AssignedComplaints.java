package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AssignedComplaints extends AppCompatActivity implements ItemClickListener {

    CommonCode commonCode = new CommonCode();

    private RecyclerView rvComplaints;

    String SecurityToken, userName, userRole, volunteerId, fName, mName, lName, mobNo;
    private ComplaintsAdapter madapter;
    ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;
    String complaintStatus, tComment;
    int complaintId;
    final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
    Urllink urllink = new Urllink();

    TextView tvTotalCount, tvNotResolvedCount, tvOpenCount, tvInProgressCount, tvResolveCount, tvClosedCount;

    Dialog myDialog;
    String tokenNo;
    EditText edtSearch, edtSearchDate;
    int totalCount = 0, openCount = 0, inProgressCount = 0, resolveCount = 0, closeCount = 0;

    CardView cvOpen, cvInProgress, cvResolved, cvClosed;

    String status, selectedStatus;

    ImageButton imgBtnSearch;
    String searchType, complaintType = "", searchDate = "";
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
        setContentView(R.layout.activity_assigned_complaints);
        myDialog = new Dialog(AssignedComplaints.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.assignedComplaints));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userName = sharedPreferences.getString("username", "");
        userRole = sharedPreferences.getString("role", "");
        volunteerId = sharedPreferences.getString("userId", "");

        cvOpen = findViewById(R.id.cv_open);
        cvInProgress = findViewById(R.id.cv_inprogress);
        cvResolved = findViewById(R.id.cv_resolved);
        cvClosed = findViewById(R.id.cv_closed);

        rvComplaints = findViewById(R.id.rv_complaints);
        imgBtnSearch = findViewById(R.id.img_search);
        tvTotalCount = findViewById(R.id.tvtotalCount);
        tvNotResolvedCount = findViewById(R.id.tv_not_resolved);
        tvOpenCount = findViewById(R.id.tvOpenCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvResolveCount = findViewById(R.id.tvResolveCount);
        tvClosedCount = findViewById(R.id.tvClosedCount);
        edtSearch = findViewById(R.id.edt_search);
        spnCompType = (MaterialSpinner) findViewById(R.id.spn_comp_type);
        edtSearchDate = findViewById(R.id.edt_search_date);
        spnSearchType = findViewById(R.id.spinner);

        //  String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.subject), getResources().getString(R.string.date), getResources().getString(R.string.status)};
        String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.area), getResources().getString(R.string.complaintType), getResources().getString(R.string.date)};

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(AssignedComplaints.this, android.R.layout.simple_spinner_item, country);
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
                    if (!commonCode.isValidString(AssignedComplaints.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/getByTokebNoVolunteer/" + tokenNo + "/" + volunteerId;
                        getAllSerachedOfVol();
                    }
                } else if (searchType.equals(getResources().getString(R.string.area))) {
                    if (!commonCode.isValidString(AssignedComplaints.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByComp/" + tokenNo + "/" + volunteerId;
                        getAllSerachedOfVol();
                    }
                } else if (searchType.equals(getResources().getString(R.string.complaintType))) {
                    if (!commonCode.isValidInt(AssignedComplaints.this, complaintTypeId)) {
                        spnCompType.setError(getResources().getString(R.string.plsSelectComplaintType));
                        spnCompType.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByCompType/" + complaintTypeId + "/" + volunteerId;
                        getAllSerachedOfVol();
                    }
                } else if (searchType.equals(getResources().getString(R.string.date))) {
                    searchDate = edtSearchDate.getText().toString();
                    if (searchDate.equals("")) {
                        edtSearchDate.setError(getResources().getString(R.string.plsSelDate));
                        edtSearchDate.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByCompDate/" + searchDate + "/" + volunteerId;
                        getAllSerachedOfVol();
                    }
                }
            }
        });
//http://192.168.0.135:8081/hrms/complaint/SearchByCompDate/1561401000000/15
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    edtSearch.getText().clear();
                    getAllOfVol();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

        edtSearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDispatchCalendar();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(AssignedComplaints.this, new DatePickerDialog.OnDateSetListener() {
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

                            edtSearchDate.setText(year + "-" + (monthString) + "-" + dayOfMonthString);                             //    edtDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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

        //Count CardView
        cvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openCount > 0) {
                    status = "Open";
                    getAllOfVolByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.complaintsNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inProgressCount > 0) {
                    status = "In Progress";
                    getAllOfVolByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.complaintsNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resolveCount > 0) {
                    status = "Resolved";
                    getAllOfVolByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.complaintsNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (closeCount > 0) {
                    status = "Closed";
                    getAllOfVolByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.complaintsNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showDispatchCalendar() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getComplaintCountOfVol();
    }

    private void getAllOfVol() {
        arraymap.clear();
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            String jsonurl = urllink.url + "complaint/getAllCompByVolunteer/" + volunteerId;
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
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

                        commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(AssignedComplaints.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AssignedComplaints.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllOfVolByStatus() {
        arraymap.clear();
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            String jsonurl = urllink.url + "complaint/getByCompStatusVoulunter/" + status + "/" + volunteerId;
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
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

                        commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(AssignedComplaints.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AssignedComplaints.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllSerachedOfVol() {
        arraymap.clear();
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
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

                        commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(AssignedComplaints.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AssignedComplaints.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllComplaintsType() {
        arraymapClass.clear();
        nameListClass = new ArrayList<>();
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            String jsonurl = urllink.url + "complaintType/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
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

                    ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(AssignedComplaints.this, android.R.layout.simple_spinner_item, nameListClass);
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
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.serverError), "", getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showdata(ArrayList<HashMap<String, String>> arraymap) {
        madapter = new ComplaintsAdapter(AssignedComplaints.this, arraymap);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AssignedComplaints.this);
        rvComplaints.setLayoutManager(mLayoutManager);
        rvComplaints.setAdapter(madapter);
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
                    Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.alreadyColsed), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Resolved")) {
                    Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.compIsResolved), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Not Resolved")) {
                    Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.compNotResolved), Toast.LENGTH_LONG).show();
                } else {
                    addCommentPopup(view);

                   /* if (complaintStatus.equals("Open")) {
                        complaintStatus = "In Progress";
                        addCommentPopup(view);
                    } else if (complaintStatus.equals("In Progress")) {
                        complaintStatus = "Resolved";
                        addCommentPopup(view);
                    } else if (complaintStatus.equals("Resolved")) {
                        // complaintStatus = "Closed";
                        Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.onlyUserCan), Toast.LENGTH_LONG).show();
                    }*/
                }
                break;
            case R.id.cv_complaint:
                Intent intent = new Intent(AssignedComplaints.this, ComplaintDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void getComplaintCountOfVol() {
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "complaint/getVolunteerCompCount/" + volunteerId;
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
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

                                tvTotalCount.setText(getResources().getString(R.string.totalComplaints) + " : " + response.getString("Total"));
                                tvNotResolvedCount.setText(getResources().getString(R.string.notResolved) + " : " + response.getString("NotResolved"));
                                tvOpenCount.setText(response.getString("Open"));
                                tvInProgressCount.setText(response.getString("Inprogress"));
                                tvResolveCount.setText(response.getString("resolve"));
                                tvClosedCount.setText(response.getString("Closed"));

                                totalCount = Integer.parseInt(response.getString("Total"));
                                openCount = Integer.parseInt(response.getString("Open"));
                                inProgressCount = Integer.parseInt(response.getString("Inprogress"));
                                resolveCount = Integer.parseInt(response.getString("resolve"));
                                closeCount = Integer.parseInt(response.getString("Closed"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                    getAllOfVol();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                        //  finish();
                    } else {
                        Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void updateStatus() {
        if (commonCode.checkConnection(AssignedComplaints.this)) {
            String jsonurl = urllink.url + "complaint/update/" + complaintId;
            RequestQueue requestQueue = Volley.newRequestQueue(AssignedComplaints.this);
            progressDialog = new ProgressDialog(AssignedComplaints.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject object = new JSONObject();
            try {

                object.put("status", selectedStatus);
                object.put("vcomment", tComment);

                JSONObject ob = new JSONObject();
                ob.put("id", volunteerId);
                object.put("resoved_by", ob);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl,

                    object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Updated...!!!")) {
                            Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.statusChanged), Toast.LENGTH_LONG).show();
                            getAllOfVol();
                        } else {
                            Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();

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
                        Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(AssignedComplaints.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AssignedComplaints.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(AssignedComplaints.this, getResources().getString(R.string.noInternetConnection), "", getResources().getString(R.string.ok));
        }
    }

    public void addCommentPopup(View v) {
        TextView tvClose, tvTitle;
        myDialog.setContentView(R.layout.add_comment_popup);

        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        final MaterialSpinner spnCompStatus = myDialog.findViewById(R.id.spn_status_type);

        List<String> spinnararray = new ArrayList<>();
        if (complaintStatus.equals("Open")) {
            spinnararray.add(getResources().getString(R.string.inProgress));
            spinnararray.add(getResources().getString(R.string.resolved));
            spinnararray.add(getResources().getString(R.string.notResolved));
        } else if (complaintStatus.equals("In Progress")) {
            spinnararray.add(getResources().getString(R.string.resolved));
            spinnararray.add(getResources().getString(R.string.notResolved));
        } else if (complaintStatus.equals("Resolved")) {
            spnCompStatus.setVisibility(View.GONE);
        }

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, spinnararray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCompStatus.setAdapter(adapter);

        spnCompStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                if (name.equals(getResources().getString(R.string.selCompStatus))) {
                    selectedStatus = "Select Complaint Status";
                } else if (name.equals(getResources().getString(R.string.open))) {
                    selectedStatus = "Open";
                } else if (name.equals(getResources().getString(R.string.inProgress))) {
                    selectedStatus = "In Progress";
                } else if (name.equals(getResources().getString(R.string.resolved))) {
                    selectedStatus = "Resolved";
                } else if (name.equals(getResources().getString(R.string.notResolved))) {
                    selectedStatus = "Not Resolved";
                } else {
                    selectedStatus = "Select Complaint Status";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvClose = (TextView) myDialog.findViewById(R.id.txtclose);
        tvClose.setText("X");
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        tvTitle = (TextView) myDialog.findViewById(R.id.tv_title);
        // tvTitle.setText(getResources().getString(R.string.changeStatusQue) + " " + complaintStatus + " ? ");
        tvTitle.setText(getResources().getString(R.string.complaintStatus) + " : " + complaintStatus);


        final EditText edtAddcomment = (EditText) myDialog.findViewById(R.id.edt_addComment);


        Button btnCancel = (Button) myDialog.findViewById(R.id.btn_cancel);
        Button btnSave = (Button) myDialog.findViewById(R.id.btn_save);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tComment = edtAddcomment.getText().toString();

                if (selectedStatus.equals("Select Complaint Status")) {
                    spnCompStatus.setError(getResources().getString(R.string.pleaSelCompStatus));
                    spnCompStatus.requestFocus();
                } else if (!commonCode.isValidString(AssignedComplaints.this, tComment)) {
                    edtAddcomment.setError(getResources().getString(R.string.pleaseAddComment));
                    edtAddcomment.requestFocus();
                } else {
//                    if (selectedStatus.equals("Open")) {
//                        complaintStatus = "In Progress";
//                    } else if (selectedStatus.equals("Resolved")) {
//                        complaintStatus = "Resolved";
//                    }
                    updateStatus();
                    myDialog.dismiss();
                }

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
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
            getComplaintCountOfVol();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
