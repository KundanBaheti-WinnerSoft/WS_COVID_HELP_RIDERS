package com.ws.helprider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.ws.helprider.Adapters.ComplaintsAdapter;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolunteerDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    CommonCode commonCode = new CommonCode();

    ArrayList<Entry> entries1, entries2;
    PieDataSet pieDataSet1, pieDataSet2;
    PieData pieData1, pieData2;
    PieChart chart1, chart2;
    ArrayList colorarray1, colorarray2;
    List<String> list1 = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    private RecyclerView rvComplaints;

    String SecurityToken, userName, userRole, userId, fName, mName, lName, mobNo;
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
    int totalCount = 0, openCount = 0, inProgressCount = 0, resolveCount = 0, closeCount = 0, notResolveCount = 0;
    String imageName;
    String imageUrl;
    ImageView ivProfilePic;

    CardView cvOpen, cvInProgress, cvResolved, cvClosed, cvAssignedComplaints;

    boolean activeDeactive;

    int jan, feb, mar, apr, may, jun, july, aug, sept, oct, nov, dec;
    BarChart barChart;

    Spinner spnYear;
    String selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dashboard);
        commonCode.updateLocaleIfNeeded(VolunteerDashboardActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        activeDeactive = sharedPreferences.getBoolean("activeDeactive", false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        ivProfilePic = header.findViewById(R.id.iv_drawer);
        imageUrl = urllink.downloadProfilePic + imageName; // profileImageUrl
        Picasso.with(getApplicationContext()).load(imageUrl).into(ivProfilePic);
        if (ivProfilePic.getDrawable() == null) {
            ivProfilePic.setImageResource(R.mipmap.avatar);
        }
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfilePicPopup(v);
            }
        });

        TextView tvName = header.findViewById(R.id.tv_drawer_name);
        TextView tvDetails = header.findViewById(R.id.tv_drawer_details);
        tvName.setText(fName + " " + lName);
        tvDetails.setText(mobNo);

        myDialog = new Dialog(VolunteerDashboardActivity.this);

        cvOpen = findViewById(R.id.cv_open);
        cvInProgress = findViewById(R.id.cv_inprogress);
        cvResolved = findViewById(R.id.cv_resolved);
        cvClosed = findViewById(R.id.cv_closed);

        rvComplaints = findViewById(R.id.rv_complaints);
        tvTotalCount = findViewById(R.id.tvtotalCount);
        tvNotResolvedCount = findViewById(R.id.tv_not_resolved);
        tvOpenCount = findViewById(R.id.tvOpenCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvResolveCount = findViewById(R.id.tvResolveCount);
        tvClosedCount = findViewById(R.id.tvClosedCount);
        cvAssignedComplaints = findViewById(R.id.cv_assigned_complaints);
        barChart = findViewById(R.id.BarChart);

        cvAssignedComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activeDeactive) {
                    Intent intent = new Intent(VolunteerDashboardActivity.this, AssignedComplaints.class);
                    startActivity(intent);
                } else {
                    commonCode.AlertDialog_Pbtn(VolunteerDashboardActivity.this, getResources().getString(R.string.deactivited), getResources().getString(R.string.notAllowToSee), getResources().getString(R.string.ok));
                }
            }
        });

        spnYear = findViewById(R.id.spn_year);
        //  String[] country = {getResources().getString(R.string.tokenNo), getResources().getString(R.string.subject), getResources().getString(R.string.date), getResources().getString(R.string.status)};
        String[] country = {"2019", "2020", "2021"};

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(VolunteerDashboardActivity.this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnYear.setAdapter(aa);

        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedYear = parent.getItemAtPosition(position).toString();
                getMonthwiseCount();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void barChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(jan, 0));
        entries.add(new BarEntry(feb, 1));
        entries.add(new BarEntry(mar, 2));
        entries.add(new BarEntry(apr, 3));
        entries.add(new BarEntry(may, 4));
        entries.add(new BarEntry(jun, 5));
        entries.add(new BarEntry(july, 6));
        entries.add(new BarEntry(aug, 7));
        entries.add(new BarEntry(sept, 8));
        entries.add(new BarEntry(oct, 9));
        entries.add(new BarEntry(nov, 10));
        entries.add(new BarEntry(dec, 11));

        BarDataSet bardataset = new BarDataSet(entries, "Complaint Count");

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");
        labels.add("Jul");
        labels.add("Aug");
        labels.add("Sep");
        labels.add("Oct");
        labels.add("Nov");
        labels.add("Dec");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the edt_yeardata and list of lables into chart

        barChart.setDescription("");  // set the description
        bardataset.setColors(ColorTemplate.JOYFUL_COLORS);
        bardataset.setValueTextSize(18f);

        barChart.animateY(2000);
        //data.setValueFormatter(new MyValueFormatter());

        //hide background grid lines
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        // In percentage Term
        // data.setValueFormatter(new PercentFormatter());
        // Default value
        data.setDrawValues(false);
        data.setValueFormatter(new MyValueFormatter());
        bardataset.setValueFormatter(new MyValueFormatter());
    }

    public class MyValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            if(value > 0) {
                return mFormat.format(value);
            } else {
                return "";
            }
        }
    }

    public void chardData() {
        org.eazegraph.lib.charts.PieChart mPieChart = (org.eazegraph.lib.charts.PieChart) findViewById(R.id.piechart);
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel(getResources().getString(R.string.inProgress), inProgressCount, getResources().getColor(R.color.startblue)));
        mPieChart.addPieSlice(new PieModel(getResources().getString(R.string.resolved), resolveCount, getResources().getColor(R.color.green)));
        mPieChart.addPieSlice(new PieModel(getResources().getString(R.string.closed), closeCount, getResources().getColor(R.color.orange)));
        mPieChart.addPieSlice(new PieModel(getResources().getString(R.string.notResolved), notResolveCount, getResources().getColor(R.color.red)));
        mPieChart.addPieSlice(new PieModel(getResources().getString(R.string.open), openCount, getResources().getColor(R.color.background)));

        mPieChart.startAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllComplaintCount();

        imageName = sharedPreferences.getString("image", "");

        imageUrl = urllink.downloadProfilePic + imageName; // profileImageUrl
        Picasso.with(getApplicationContext()).load(imageUrl).into(ivProfilePic);
        if (ivProfilePic.getDrawable() == null) {
            ivProfilePic.setImageResource(R.mipmap.avatar);
        }
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfilePicPopup(v);
            }
        });

    }

    private void getMonthwiseCount() {
        if (commonCode.checkConnection(VolunteerDashboardActivity.this)) {
            String jsonurl = urllink.url + "complaint/getAllAdminandSuprAdminComplaintCountYearly/" + selectedYear;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            //  progressDialog = new ProgressDialog(VolunteerDashboardActivity.this);
            //  progressDialog.setMessage(getResources().getString(R.string.loading));
            //   progressDialog.show();
            //   progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    //   progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                if (response.has("1")) {
                                    jan = Integer.parseInt(response.getString("1"));
                                }
                                if (response.has("2")) {
                                    feb = Integer.parseInt(response.getString("2"));
                                }
                                if (response.has("3")) {
                                    mar = Integer.parseInt(response.getString("3"));
                                }
                                if (response.has("4")) {
                                    apr = Integer.parseInt(response.getString("4"));
                                }
                                if (response.has("5")) {
                                    may = Integer.parseInt(response.getString("5"));
                                }
                                if (response.has("6")) {
                                    jun = Integer.parseInt(response.getString("6"));
                                }
                                if (response.has("7")) {
                                    july = Integer.parseInt(response.getString("7"));
                                }
                                if (response.has("8")) {
                                    aug = Integer.parseInt(response.getString("8"));
                                }
                                if (response.has("9")) {
                                    sept = Integer.parseInt(response.getString("9"));
                                }
                                if (response.has("10")) {
                                    oct = Integer.parseInt(response.getString("10"));
                                }
                                if (response.has("11")) {
                                    nov = Integer.parseInt(response.getString("11"));
                                }
                                if (response.has("12")) {
                                    dec = Integer.parseInt(response.getString("12"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        barChart();
                    } else {
                        commonCode.AlertDialog_Pbtn(VolunteerDashboardActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //    progressDialog.dismiss();
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

            //setValuesToChart2();

        } else {
            commonCode.AlertDialog_Pbtn(VolunteerDashboardActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllComplaintCount() {
        if (commonCode.checkConnection(VolunteerDashboardActivity.this)) {
            String jsonurl = urllink.url + "complaint/getAllStatusCount";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(VolunteerDashboardActivity.this);
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

                                //  tvAdminCount.setText(response.getString("TotalAdmin"));
                                //   tvVolCount.setText(response.getString("TotalVolunteer"));
                                //    tvUsersCount.setText(response.getString("TotalUser"));
                                //    tvCompCount.setText(response.getString("Total"));

                                totalCount = Integer.parseInt(response.getString("Total"));
                                openCount = Integer.parseInt(response.getString("Open"));
                                inProgressCount = Integer.parseInt(response.getString("Inprogress"));
                                resolveCount = Integer.parseInt(response.getString("resolve"));
                                closeCount = Integer.parseInt(response.getString("Closed"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        chardData();
                    } else {
                        commonCode.AlertDialog_Pbtn(VolunteerDashboardActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.complaintsNotFound), getResources().getString(R.string.ok));
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

            //   setValuesToChart1();

        } else {
            commonCode.AlertDialog_Pbtn(VolunteerDashboardActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.volunteer_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getAllComplaintCount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        if (id == R.id.nav_gallary) {
//            Intent intent = new Intent(VolunteerDashboardActivity.this, GallaryActivity.class);
//            startActivity(intent);
//        } else
            if (id == R.id.nav_settings) {
            Intent intent = new Intent(VolunteerDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {


            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String shareMessage = "\nAndroid App link\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerDashboardActivity.this, R.style.MyDialogTheme);
            builder.setTitle(getResources().getString(R.string.logoutQue));
            builder.setIcon(R.drawable.warning_sign);
            builder.setMessage(getResources().getString(R.string.doYouWantLogout));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    sp.edit().clear().commit();
                    Intent intent = new Intent(VolunteerDashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.cancel();
                }
            }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create();
            builder.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}