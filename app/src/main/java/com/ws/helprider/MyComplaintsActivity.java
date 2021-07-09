package com.ws.helprider;

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
import android.widget.ImageView;
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
import com.ws.helprider.Adapters.ComplaintsAdapter;
import com.ws.helprider.Adapters.ComplaintsDetails;
import com.ws.helprider.Adapters.ItemClickListener;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;
import com.squareup.picasso.Picasso;

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

public class MyComplaintsActivity extends AppCompatActivity implements ItemClickListener {

    CommonCode commonCode = new CommonCode();
    Urllink urllink = new Urllink();
    private RecyclerView rvComplaints;
    final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
    private ComplaintsAdapter madapter;
    ProgressDialog progressDialog;
    EditText edtSearch, edtSearchDate;
    String SecurityToken, firstName, middleName, lastName, userName, userRole, userId, mobileno, fName, mName, lName, mobNo;
    private String url = urllink.url;
    private SharedPreferences sharedPreferences;
    String complaintStatus, selectedStatus, tComment;
    int complaintId;
    TextView tvTotalCount, tvNotResolvedCount, tvResolveCount, tvPendingCount, tvOpenCount, tvInProgressCount, tvClosedCount;
    String tokenNo;
    Dialog myDialog;
    String imageName;
    String imageUrl;


    ImageButton imgBtnSearch;
    String status, searchType, complaintType = "", searchDate = "";
    Spinner spnSearchType;
    String jsonurl;
    private int mYear, mMonth, mDay, mHour, mMinute;


    int complaintTypeId = 0;
    final ArrayList<HashMap<String, String>> arraymapClass = new ArrayList<>();
    ArrayList<String> nameListClass = new ArrayList<>();

    MaterialSpinner spnCompType;
    CardView cvOpen, cvInProgress, cvResolved, cvClosed;
    int totalCount = 0, openCount = 0, inProgressCount = 0, resolveCount = 0, closeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.myRequest));
        getSupportActionBar().setSubtitle("");

        commonCode.updateLocaleIfNeeded(MyComplaintsActivity.this);
        myDialog = new Dialog(MyComplaintsActivity.this);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userName = sharedPreferences.getString("username", "");
        userRole = sharedPreferences.getString("role", "");
        userId = sharedPreferences.getString("userId", "");
        fName = sharedPreferences.getString("firstName", "");
        mName = sharedPreferences.getString("middleName", "");
        lName = sharedPreferences.getString("lastName", "");
        mobNo = sharedPreferences.getString("mobileno", "");
        imageName = sharedPreferences.getString("image", "");

        rvComplaints = findViewById(R.id.rv_complaints);
        edtSearch = findViewById(R.id.edt_search);
        imgBtnSearch = findViewById(R.id.img_search);
        spnCompType = (MaterialSpinner) findViewById(R.id.spn_comp_type);
        edtSearchDate = findViewById(R.id.edt_search_date);
        tvTotalCount = findViewById(R.id.tvtotalCount);
        tvNotResolvedCount = findViewById(R.id.tv_not_resolved);
        tvOpenCount = findViewById(R.id.tvOpenCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvResolveCount = findViewById(R.id.tvResolveCount);
        tvClosedCount = findViewById(R.id.tvClosedCount);
        spnSearchType = findViewById(R.id.spinner);

        cvOpen = findViewById(R.id.cv_open);
        cvInProgress = findViewById(R.id.cv_inProgress);
        cvResolved = findViewById(R.id.cv_resolved);
        cvClosed = findViewById(R.id.cv_closed);


        //  String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.subject), getResources().getString(R.string.date), getResources().getString(R.string.status)};
        String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.serviceType), getResources().getString(R.string.date)};

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(MyComplaintsActivity.this, android.R.layout.simple_spinner_item, country);
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
                }else if (searchType.equals(getResources().getString(R.string.serviceType))) {
                    getAllComplaintsType();
                    edtSearchDate.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.GONE);
                    spnCompType.setVisibility(View.VISIBLE);
                    edtSearch.setHint(getResources().getString(R.string.serviceType));
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
                    if (!commonCode.isValidString(MyComplaintsActivity.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/getByTokebNoUser/" + tokenNo + "/" + userId;
                        getAllSerachedOfUser();
                    }
                } else if (searchType.equals(getResources().getString(R.string.area))) {
                    if (!commonCode.isValidString(MyComplaintsActivity.this, tokenNo)) {
                        edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                        edtSearch.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByComp/" + tokenNo + "/" + userId;
                        getAllSerachedOfUser();
                    }
                } else if (searchType.equals(getResources().getString(R.string.serviceType))) {
                    if (!commonCode.isValidInt(MyComplaintsActivity.this, complaintTypeId)) {
                        spnCompType.setError(getResources().getString(R.string.plsSelectServiceType));
                        spnCompType.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByCompTypebyUser/" + complaintTypeId + "/" + userId;
                        getAllSerachedOfUser();
                    }
                } else if (searchType.equals(getResources().getString(R.string.date))) {
                    searchDate = edtSearchDate.getText().toString();
                    if (!commonCode.isValidString(MyComplaintsActivity.this, searchDate)) {
                        edtSearchDate.setError(getResources().getString(R.string.plsSelDate));
                        edtSearchDate.requestFocus();
                    } else {
                        jsonurl = urllink.url + "complaint/SearchByCompDateUser/" + searchDate + "/" + userId;
                        getAllSerachedOfUser();
                    }
                }
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    edtSearch.getText().clear();
                    getMyComplaints();
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(MyComplaintsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                    getAllOfByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.requestNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inProgressCount > 0) {
                    status = "In Progress";
                    getAllOfByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.requestNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resolveCount > 0) {
                    status = "Resolved";
                    getAllOfByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.requestNotFound), Toast.LENGTH_LONG).show();
                }
            }
        });
        cvClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (closeCount > 0) {
                    status = "Closed";
                    getAllOfByStatus();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.requestNotFound), Toast.LENGTH_LONG).show();
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
        getMyComplaints();
    }

    private void getMyComplaints() {
        arraymap.clear();
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaint/getByLocalUser/" + userId;
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
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
                                hashMap.put("vCommDate", object.getString("vCommDate"));
                                hashMap.put("uCommDate", object.getString("uCommDate"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));
                                hashMap.put("status", object.getString("status"));
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                hashMap.put("patient_name", object.getString("patient_name"));
                                hashMap.put("age", object.getString("age"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("location", object.getString("location"));
                                hashMap.put("bed_required", object.getString("bed_required"));
                                hashMap.put("date_of_test", object.getString("date_of_test"));
                                hashMap.put("rtpcr_or_antigen", object.getString("rtpcr_or_antigen"));
                                hashMap.put("govt_or_private", object.getString("govt_or_private"));
                                hashMap.put("oxy_room_level", object.getString("oxy_room_level"));
                                hashMap.put("oxy_with_support", object.getString("oxy_with_support"));
                                hashMap.put("hrct_score", object.getString("hrct_score"));
                                hashMap.put("current_situation", object.getString("current_situation"));
                                hashMap.put("blood_group", object.getString("blood_group"));
                                hashMap.put("other_disease", object.getString("other_disease"));
                                hashMap.put("help_detail", object.getString("help_detail"));
                                hashMap.put("hospital_name", object.getString("hospital_name"));
                                hashMap.put("hospital_address", object.getString("hospital_address"));
                                hashMap.put("doc_mobile", object.getString("doc_mobile"));
                                hashMap.put("relative_mobile", object.getString("relative_mobile"));
                                hashMap.put("ward", object.getString("ward"));
                                hashMap.put("doc_name", object.getString("doc_name"));
                                hashMap.put("contact_person_name", object.getString("contact_person_name"));
                                hashMap.put("oxygen", object.getString("oxygen"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("units_required", object.getString("units_required"));
                                hashMap.put("contact_person_mobile", object.getString("contact_person_mobile"));





//                                JSONObject objName = object.getJSONObject("localUser");
//                                hashMap.put("firstname", objName.getString("firstname"));
//                                hashMap.put("middlename", objName.getString("middlename"));
//                                hashMap.put("lastname", objName.getString("lastname"));
//                                hashMap.put("mobileno", objName.getString("mobileno"));
//                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("resource");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));
                                hashMap.put("complaintdescription", cmpObj.getString("complaintdescription"));
                                JSONObject user = object.getJSONObject("user");
                                hashMap.put("firstname", user.getString("firstname"));




                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.requestNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);
                    getComplaintCount();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(MyComplaintsActivity.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getComplaintCount() {
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "complaint/getAllStatusCountByLocalUser/" + userId;
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
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

                                tvTotalCount.setText(getResources().getString(R.string.totalRequests) + " : " + response.getString("Total"));
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
                        commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.requestNotFound), getResources().getString(R.string.ok));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //  finish();
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllSerachedOfUser() {
        arraymap.clear();
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
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
                                hashMap.put("vCommDate", object.getString("vCommDate"));
                                hashMap.put("uCommDate", object.getString("uCommDate"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));
                                hashMap.put("status", object.getString("status"));
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                hashMap.put("patient_name", object.getString("patient_name"));
                                hashMap.put("age", object.getString("age"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("location", object.getString("location"));
                                hashMap.put("bed_required", object.getString("bed_required"));
                                hashMap.put("date_of_test", object.getString("date_of_test"));
                                hashMap.put("rtpcr_or_antigen", object.getString("rtpcr_or_antigen"));
                                hashMap.put("govt_or_private", object.getString("govt_or_private"));
                                hashMap.put("oxy_room_level", object.getString("oxy_room_level"));
                                hashMap.put("oxy_with_support", object.getString("oxy_with_support"));
                                hashMap.put("hrct_score", object.getString("hrct_score"));
                                hashMap.put("current_situation", object.getString("current_situation"));
                                hashMap.put("blood_group", object.getString("blood_group"));
                                hashMap.put("other_disease", object.getString("other_disease"));
                                hashMap.put("help_detail", object.getString("help_detail"));
                                hashMap.put("hospital_name", object.getString("hospital_name"));
                                hashMap.put("hospital_address", object.getString("hospital_address"));
                                hashMap.put("doc_mobile", object.getString("doc_mobile"));
                                hashMap.put("relative_mobile", object.getString("relative_mobile"));
                                hashMap.put("ward", object.getString("ward"));
                                hashMap.put("doc_name", object.getString("doc_name"));
                                hashMap.put("contact_person_name", object.getString("contact_person_name"));
                                hashMap.put("oxygen", object.getString("oxygen"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("units_required", object.getString("units_required"));
                                hashMap.put("contact_person_mobile", object.getString("contact_person_mobile"));


//                                JSONObject objName = object.getJSONObject("localUser");
//                                hashMap.put("firstname", objName.getString("firstname"));
//                                hashMap.put("middlename", objName.getString("middlename"));
//                                hashMap.put("lastname", objName.getString("lastname"));
//                                hashMap.put("mobileno", objName.getString("mobileno"));
//                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("resource");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));
                                hashMap.put("complaintdescription", cmpObj.getString("complaintdescription"));
                                JSONObject user = object.getJSONObject("user");
                                hashMap.put("firstname", user.getString("firstname"));


                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.requestNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);
                    getComplaintCount();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(MyComplaintsActivity.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllOfByStatus() {
        arraymap.clear();
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaint/getByCompStatusUser/" + status + "/" + userId;
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
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
                                hashMap.put("vCommDate", object.getString("vCommDate"));
                                hashMap.put("uCommDate", object.getString("uCommDate"));
                                hashMap.put("vcomment", object.getString("vcomment"));
                                hashMap.put("ucomment", object.getString("ucomment"));
                                hashMap.put("status", object.getString("status"));
                                if (object.has("adminAssign") && !object.isNull("adminAssign")) {
                                    JSONObject assignToObj = object.getJSONObject("adminAssign");
                                    hashMap.put("assignToId", assignToObj.getString("id"));
                                    hashMap.put("assignFname", assignToObj.getString("firstname"));
                                    hashMap.put("assignMname", assignToObj.getString("middlename"));
                                    hashMap.put("assignLname", assignToObj.getString("lastname"));
                                    hashMap.put("assignMono", assignToObj.getString("mobileno"));
                                    hashMap.put("assignEmailid", assignToObj.getString("emailid"));
                                }
                                hashMap.put("patient_name", object.getString("patient_name"));
                                hashMap.put("age", object.getString("age"));
                                hashMap.put("created_date", object.getString("created_date"));
                                hashMap.put("location", object.getString("location"));
                                hashMap.put("bed_required", object.getString("bed_required"));
                                hashMap.put("date_of_test", object.getString("date_of_test"));
                                hashMap.put("rtpcr_or_antigen", object.getString("rtpcr_or_antigen"));
                                hashMap.put("govt_or_private", object.getString("govt_or_private"));
                                hashMap.put("oxy_room_level", object.getString("oxy_room_level"));
                                hashMap.put("oxy_with_support", object.getString("oxy_with_support"));
                                hashMap.put("hrct_score", object.getString("hrct_score"));
                                hashMap.put("current_situation", object.getString("current_situation"));
                                hashMap.put("blood_group", object.getString("blood_group"));
                                hashMap.put("other_disease", object.getString("other_disease"));
                                hashMap.put("help_detail", object.getString("help_detail"));
                                hashMap.put("hospital_name", object.getString("hospital_name"));
                                hashMap.put("hospital_address", object.getString("hospital_address"));
                                hashMap.put("doc_mobile", object.getString("doc_mobile"));
                                hashMap.put("relative_mobile", object.getString("relative_mobile"));
                                hashMap.put("ward", object.getString("ward"));
                                hashMap.put("doc_name", object.getString("doc_name"));
                                hashMap.put("contact_person_name", object.getString("contact_person_name"));
                                hashMap.put("oxygen", object.getString("oxygen"));
                                hashMap.put("tokennumber", object.getString("tokennumber"));
                                hashMap.put("units_required", object.getString("units_required"));
                                hashMap.put("contact_person_mobile", object.getString("contact_person_mobile"));





//                                JSONObject objName = object.getJSONObject("localUser");
//                                hashMap.put("firstname", objName.getString("firstname"));
//                                hashMap.put("middlename", objName.getString("middlename"));
//                                hashMap.put("lastname", objName.getString("lastname"));
//                                hashMap.put("mobileno", objName.getString("mobileno"));
//                                hashMap.put("userImage", objName.getString("image"));

                                JSONObject cmpObj = object.getJSONObject("resource");
                                hashMap.put("complaintTypeId", cmpObj.getString("id"));
                                hashMap.put("complaintType", cmpObj.getString("complaintType"));
                                hashMap.put("complaintdescription", cmpObj.getString("complaintdescription"));
                                JSONObject user = object.getJSONObject("user");
                                hashMap.put("firstname", user.getString("firstname"));
                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {

                        commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.requestNotFound), getResources().getString(R.string.ok));
                    }
                    showdata(arraymap);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(MyComplaintsActivity.this, R.string.tokenExpire, Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, R.string.serverError, Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }
    private void getAllComplaintsType() {
        arraymapClass.clear();
        nameListClass = new ArrayList<>();
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaintType/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
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

                    ArrayAdapter<String> stringArrayAdapter2 = new ArrayAdapter<>(MyComplaintsActivity.this, android.R.layout.simple_spinner_item, nameListClass);
                    spnCompType.setAdapter(stringArrayAdapter2);
                    spnCompType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String valuename = parent.getItemAtPosition(position).toString();
                            if (!valuename.equals(getResources().getString(R.string.serviceType))) {
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
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.serverError), "", getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showdata(ArrayList<HashMap<String, String>> arraymap) {
        madapter = new ComplaintsAdapter(MyComplaintsActivity.this, arraymap);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyComplaintsActivity.this);
        rvComplaints.setLayoutManager(mLayoutManager);
        rvComplaints.setAdapter(madapter);
        madapter.setClickListener(this);
    }



    @Override
    public void onClick(View view, int position) {

        ComplaintsDetails complaintsDetails = new ComplaintsDetails();
        complaintsDetails = (ComplaintsDetails) view.getTag();
        complaintId = complaintsDetails.getRequestId();
        complaintStatus = complaintsDetails.getStatus();

        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(complaintsDetails.getRequestId()));
        bundle.putString("vCommDate", complaintsDetails.getvCommDate());
        bundle.putString("uCommDate", complaintsDetails.getuCommDate());
        bundle.putString("status", complaintsDetails.getStatus());
        bundle.putString("vComment", complaintsDetails.getVcomment());
        bundle.putString("uComment", complaintsDetails.getUcomment());
        bundle.putString("patient_name", complaintsDetails.getPatient_name());
        bundle.putString("age", complaintsDetails.getAge());
        bundle.putString("created_date", complaintsDetails.getCreated_date());
        bundle.putString("location", complaintsDetails.getLocation());
        bundle.putString("bed_required", complaintsDetails.getBed_required());
        bundle.putString("date_of_test", complaintsDetails.getDate_of_test());
        bundle.putString("complaintTypeId", String.valueOf(complaintsDetails.getRequestTypeId()));
        bundle.putString("complaintType", complaintsDetails.getComplaintType());
        bundle.putString("complaintdescription", complaintsDetails.getComplaintdescription());
        bundle.putString("rtpcr_or_antigen", complaintsDetails.getRtpcr_or_antigen());
        bundle.putString("govt_or_private", complaintsDetails.getGovt_or_private());
        bundle.putString("oxy_room_level", complaintsDetails.getOxy_room_level());
        bundle.putString("oxy_with_support", complaintsDetails.getOxy_with_support());
        bundle.putString("hrct_score", complaintsDetails.getHrct_score());
        bundle.putString("current_situation", complaintsDetails.getCurrent_situation());
        bundle.putString("blood_group", complaintsDetails.getBlood_group());
        bundle.putString("other_disease", complaintsDetails.getOther_disease());
        bundle.putString("help_detail", complaintsDetails.getHelp_detail());
        bundle.putString("hospital_name", complaintsDetails.getHospital_name());
        bundle.putString("hospital_address", complaintsDetails.getHospital_address());
        bundle.putString("doc_mobile", complaintsDetails.getDoc_mobile());
        bundle.putString("relative_mobile", complaintsDetails.getRelative_mobile());
        bundle.putString("ward", complaintsDetails.getWard());
        bundle.putString("doc_name", complaintsDetails.getDoc_name());
        bundle.putString("contact_person_name", complaintsDetails.getContact_person_name());
        bundle.putString("oxygen", complaintsDetails.getOxygen());
        bundle.putString("tokennumber", complaintsDetails.getTokennumber());
        bundle.putString("units_required", complaintsDetails.getUnits_required());
        bundle.putString("contact_person_mobile", complaintsDetails.getContact_person_mobile());
        bundle.putString("assignToId", complaintsDetails.getAssignToId());
        bundle.putString("assignFname", complaintsDetails.getAssignFname());
        bundle.putString("assignMname", complaintsDetails.getAssignMname());
        bundle.putString("assignLname", complaintsDetails.getAssignLname());
        bundle.putString("assignMono", complaintsDetails.getAssignMono());
        bundle.putString("assignEmailid", complaintsDetails.getAssignEmailid());
        bundle.putString("firstname", complaintsDetails.getFirstname());

        switch (view.getId()) {
            case R.id.tv_status:

                if (complaintStatus.equals("Closed")) {
                    Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.requestAlreadyColsed), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Open")) {
                    Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.requestNotResolved), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("In Progress")) {
                    Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.requestNotResolved), Toast.LENGTH_LONG).show();
                } else if (complaintStatus.equals("Not Resolved")) {
                    Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.requestNotResolved), Toast.LENGTH_LONG).show();
                } else {
                    if (complaintStatus.equals("Resolved")) {
                        addCommentPopup(view);
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.requestNotResolved), Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.cv_complaint:
                Intent intent = new Intent(MyComplaintsActivity.this, ComplaintDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
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
        spinnararray.add(getResources().getString(R.string.closed));
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, spinnararray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCompStatus.setAdapter(adapter);

        spnCompStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                if (name.equals(getResources().getString(R.string.selServiceStatus))) {
                    selectedStatus = "Select Service Status";
                } else if (name.equals(getResources().getString(R.string.closed))) {
                    selectedStatus = "Closed";
                } else {
                    selectedStatus = "Select Service Status";
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
        //  tvTitle.setText(getResources().getString(R.string.changeStatusQue) + " " + complaintStatus + " ? ");
        tvTitle.setText(getResources().getString(R.string.serviceStatus) + " : " + complaintStatus);

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

                if (selectedStatus.equals("Select Service Status")) {
                    spnCompStatus.setError(getResources().getString(R.string.selServiceStatus));
                    spnCompStatus.requestFocus();
                } else if (!commonCode.isValidString(MyComplaintsActivity.this, tComment)) {
                    edtAddcomment.setError(getResources().getString(R.string.pleaseAddComment));
                    edtAddcomment.requestFocus();
                } else {
                    updateStatus();
                    myDialog.dismiss();
                }

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void updateStatus() {
        if (commonCode.checkConnection(MyComplaintsActivity.this)) {
            String jsonurl = urllink.url + "complaint/update/" + complaintId;
            RequestQueue requestQueue = Volley.newRequestQueue(MyComplaintsActivity.this);
            progressDialog = new ProgressDialog(MyComplaintsActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject object = new JSONObject();
            try {
                object.put("status", selectedStatus);
                object.put("ucomment", tComment);

                JSONObject ob = new JSONObject();
                ob.put("id", userId);
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
                            Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.statusChanged), Toast.LENGTH_LONG).show();
                            getMyComplaints();
                        } else {
                            Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.failedTryAgain), Toast.LENGTH_LONG).show();

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
                        Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(MyComplaintsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MyComplaintsActivity.this, getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
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
            commonCode.AlertDialog_Pbtn(MyComplaintsActivity.this, getResources().getString(R.string.noInternetConnection), "", getResources().getString(R.string.ok));
        }
    }


    public void viewProfilePicPopup(View v) {
        ImageView imgviewShowImage;
        myDialog.setContentView(R.layout.profile_pic_preview);

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            getMyComplaints();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
