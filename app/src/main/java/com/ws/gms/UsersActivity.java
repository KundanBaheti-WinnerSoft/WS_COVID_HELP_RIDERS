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
import com.ws.gms.Adapters.UsersAdapter;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UsersActivity extends AppCompatActivity implements ItemClickListener {
    CommonCode commonCode = new CommonCode();

    private RecyclerView rvUsers;
    final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
    private UsersAdapter madapter;

    Urllink urllink = new Urllink();
    ProgressDialog progressDialog;
    String SecurityToken, userName, userRole, userId;

    private SharedPreferences sharedPreferences;
    EditText edtSearch;
    String searchKey;
    ImageButton imgSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        commonCode.updateLocaleIfNeeded(UsersActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.users));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userRole = sharedPreferences.getString("role", "");
        userName = sharedPreferences.getString("username", "");
        userId = sharedPreferences.getString("userId", "");

        rvUsers = findViewById(R.id.rv_user);
        edtSearch = findViewById(R.id.edt_search);
        imgSearch = findViewById(R.id.img_search);

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchKey = edtSearch.getText().toString();
                if (!commonCode.isValidString(UsersActivity.this, searchKey)) {
                    edtSearch.setError(getResources().getString(R.string.plsEnterSearch));
                    edtSearch.requestFocus();
                } else {
                    getAllSearchUser();
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    edtSearch.getText().clear();
                    getAllusers();
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
        getAllusers();
    }

//    @Override
//    protected void onStart() {
//        getAllusers();
//        super.onStart();
//    }

    //http://192.168.0.130:8081/hrms/regLocalUser/getAll
    private void getAllusers() {
        arraymap.clear();
        if (commonCode.checkConnection(UsersActivity.this)) {
            String jsonurl = urllink.url + "regLocalUser/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(UsersActivity.this);
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
                                hashMap.put("mobileno", object.getString("mobileno"));
                                hashMap.put("emailid", object.getString("emailid"));
                                hashMap.put("address", object.getString("address"));
                                hashMap.put("area", object.getString("area"));
                                hashMap.put("city", object.getString("city"));
                                hashMap.put("pincode", object.getString("pincode"));
                                hashMap.put("state", object.getString("state"));
                                hashMap.put("country", object.getString("country"));

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
                        commonCode.AlertDialog_Pbtn(UsersActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.usersNotFound), getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(UsersActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void getAllSearchUser() {
        arraymap.clear();
        if (commonCode.checkConnection(UsersActivity.this)) {
            String jsonurl = urllink.url + "regLocalUser/getByUserNameOrMobNo/" + searchKey;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(UsersActivity.this);
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
                                hashMap.put("address", object.getString("address"));
                                hashMap.put("area", object.getString("area"));
                                hashMap.put("city", object.getString("city"));
                                hashMap.put("pincode", object.getString("pincode"));
                                hashMap.put("country", object.getString("country"));
                                hashMap.put("state", object.getString("state"));
                                hashMap.put("mobileno", object.getString("mobileno"));
                                hashMap.put("emailid", object.getString("emailid"));
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
                        commonCode.AlertDialog_Pbtn(UsersActivity.this, getResources().getString(R.string.notFound), getResources().getString(R.string.usersNotFound), getResources().getString(R.string.ok));
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
            commonCode.AlertDialog_Pbtn(UsersActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    private void showdata(ArrayList<HashMap<String, String>> arraymap) {
        madapter = new UsersAdapter(getApplicationContext(), arraymap);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setAdapter(madapter);
        madapter.setClickListener(this);
    }

    @Override
    public void onClick(View view, int position) {

        ProfileDetails profileDetails = new ProfileDetails();
        profileDetails = (ProfileDetails) view.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt("id", profileDetails.getId());
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


        Intent intent = new Intent(getApplicationContext(), UserProfileDetailsActivity.class);
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
