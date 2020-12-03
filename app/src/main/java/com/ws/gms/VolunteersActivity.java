package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ws.gms.Adapters.ItemClickListener;
import com.ws.gms.Adapters.ProfileDetails;
import com.ws.gms.Adapters.VolunteersAdapter;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VolunteersActivity extends AppCompatActivity implements ItemClickListener {
    CommonCode commonCode = new CommonCode();

    private RecyclerView rvVolunteers;
    final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
    private VolunteersAdapter madapter;
    Button btnAddVolunteers;

    Urllink urllink = new Urllink();
    ProgressDialog progressDialog;
    String SecurityToken, userName, userRole, userId;

    private SharedPreferences sharedPreferences;
    String jsonurl;
    EditText edtSearch;
    ImageButton imgSearch;
    String searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers);
        commonCode.updateLocaleIfNeeded(VolunteersActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.volunteers));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userRole = sharedPreferences.getString("role", "");
        userName = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getString("userId", "");

        rvVolunteers = findViewById(R.id.rv_volunteers);

        btnAddVolunteers = findViewById(R.id.btn_add_volunteers);


        if (userRole.equals("Administrator")) {
            btnAddVolunteers.setVisibility(View.VISIBLE);
        } else {
            btnAddVolunteers.setVisibility(View.GONE);
        }

        btnAddVolunteers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                intent.putExtra("role", "Volunteer");
                intent.putExtra("userId", userId);
                startActivity(intent);
                // getContext().finish();
            }
        });


        edtSearch = findViewById(R.id.edt_search);
        imgSearch = findViewById(R.id.img_search);

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKey = edtSearch.getText().toString();
                if (!commonCode.isValidString(VolunteersActivity.this, searchKey)) {
                    edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                    edtSearch.requestFocus();
                } else {
                    getAllSearchVolunteers();
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    edtSearch.getText().clear();


                    getAllvolunteers();
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

        getAllvolunteers();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        getAllvolunteers();
//    }

    //http://192.168.0.122:8081/hrms/regVolunteer/getAll
    private void getAllvolunteers() {

        if (userRole.equals("SuperAdmin")) {
            jsonurl = urllink.url + "regVolunteer/getAll";
        } else {
            jsonurl = urllink.url + "regVolunteer/VolunteerGetAllByAddminID/" + userId;
        }

        arraymap.clear();
        if (commonCode.checkConnection(VolunteersActivity.this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(VolunteersActivity.this);
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
                                hashMap.put("id", object.getString("id"));
                                hashMap.put("role", object.getString("role"));
                                hashMap.put("firstname", object.getString("firstname"));
                                hashMap.put("middlename", object.getString("middlename"));
                                hashMap.put("lastname", object.getString("lastname"));
                                hashMap.put("birthdate", object.getString("birthdate"));
                                hashMap.put("gender", object.getString("gender"));
                                hashMap.put("country", object.getString("country"));
                                hashMap.put("state", object.getString("state"));
                                hashMap.put("city", object.getString("city"));
                                hashMap.put("pincode", object.getString("pincode"));
                                hashMap.put("mobileno", object.getString("mobileno"));
                                hashMap.put("emailid", object.getString("emailid"));
                                hashMap.put("address", object.getString("address"));
                                hashMap.put("image", object.getString("image"));

                                JSONObject userObj = object.getJSONObject("userId");
                                hashMap.put("userId", userObj.getString("id"));
                                hashMap.put("activeDeactive", String.valueOf(userObj.getBoolean("inactive_deactivate")));

                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(VolunteersActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.volunteersNotFound), getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(VolunteersActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllSearchVolunteers() {
        jsonurl = urllink.url + "regVolunteer/getByVolutNameOrMobNo/" + searchKey;
        arraymap.clear();
        if (commonCode.checkConnection(VolunteersActivity.this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(VolunteersActivity.this);
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
                                hashMap.put("id", object.getString("id"));
                                hashMap.put("role", object.getString("role"));
                                hashMap.put("firstname", object.getString("firstname"));
                                hashMap.put("middlename", object.getString("middlename"));
                                hashMap.put("lastname", object.getString("lastname"));
                                hashMap.put("birthdate", object.getString("birthdate"));
                                hashMap.put("gender", object.getString("gender"));
                                hashMap.put("country", object.getString("country"));
                                hashMap.put("state", object.getString("state"));
                                hashMap.put("city", object.getString("city"));
                                hashMap.put("pincode", object.getString("pincode"));
                                hashMap.put("mobileno", object.getString("mobileno"));
                                hashMap.put("emailid", object.getString("emailid"));
                                hashMap.put("address", object.getString("address"));
                                hashMap.put("image", object.getString("image"));

                                JSONObject userObj = object.getJSONObject("userId");
                                hashMap.put("userId", userObj.getString("id"));
                                hashMap.put("activeDeactive", String.valueOf(userObj.getBoolean("inactive_deactivate")));

                                arraymap.add(hashMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        commonCode.AlertDialog_Pbtn(VolunteersActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.volunteersNotFound), getResources().getString(R.string.ok));
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
                        onBackPressed();
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
            commonCode.AlertDialog_Pbtn(VolunteersActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showdata(ArrayList<HashMap<String, String>> arraymap) {
        madapter = new VolunteersAdapter(getApplicationContext(), arraymap);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvVolunteers.setLayoutManager(mLayoutManager);
        rvVolunteers.setAdapter(madapter);
        madapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {

        ProfileDetails profileDetails = new ProfileDetails();
        profileDetails = (ProfileDetails) view.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("id", profileDetails.getId());
        bundle.putString("role", profileDetails.getRole());
        bundle.putString("firstName", profileDetails.getFirstName());
        bundle.putString("middleName", profileDetails.getMiddleName());
        bundle.putString("lastName", profileDetails.getLastName());
        bundle.putString("mobileno", profileDetails.getMobileno());
        bundle.putString("emailid", profileDetails.getEmailId());
        bundle.putString("address", profileDetails.getAddress());
        bundle.putString("area", profileDetails.getArea());
        bundle.putString("city", profileDetails.getCity());
        bundle.putString("pincode", profileDetails.getPincode());
        bundle.putString("state", profileDetails.getState());
        bundle.putString("country", profileDetails.getCountry());

        bundle.putString("image", profileDetails.getImageName());

        bundle.putBoolean("activeDeactive", profileDetails.getActiveDeactive());
        bundle.putInt("userId", profileDetails.getUserId());

        Intent intent = new Intent(getApplicationContext(), ProfilesDetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
}
