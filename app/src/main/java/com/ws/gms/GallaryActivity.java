package com.ws.gms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ws.gms.Adapters.GlidAdapter;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;
import com.github.clans.fab.FloatingActionButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
/*import android.media.Image;*/
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GallaryActivity extends AppCompatActivity {
    String SecurityToken;
    View view;
    String urlimage;
    private CommonCode commonCode = new CommonCode();
    private Urllink urllink = new Urllink();
    private ProgressDialog progressDialog;
    private String url = urllink.url;
    private SharedPreferences sharedPreferences;
    private String userRole = "", schoolid = "", divid = "", classid = "";
    private ArrayList<Image> images;
    private GlidAdapter mAdapter;
    private RecyclerView recyclerView;
    FloatingActionButton fabAddImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);
        commonCode.updateLocaleIfNeeded(GallaryActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.gallery));
        getSupportActionBar().setSubtitle("");

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        userRole = sharedPreferences.getString("role", "");
        classid = sharedPreferences.getString("classid", "");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        progressDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GlidAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GlidAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GlidAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        fabAddImages = findViewById(R.id.fab_add_image);
        fabAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddImagesActivity.class);
                startActivity(i);
            }
        });


        if (userRole.equals("Administrator")) {
            fabAddImages.setVisibility(View.VISIBLE);
        } else {
            fabAddImages.setVisibility(View.GONE);
        }
    }

    private void getAllImages() {
        images.clear();

        if (commonCode.checkConnection(getApplicationContext())) {
            String jsonurl = urllink.url + "uploadImage/getAll";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(GallaryActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    progressDialog.dismiss();
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                HashMap<String, String> hashMap = new HashMap<>();

                                JSONObject jsonObject = response.getJSONObject(i);
                                hashMap.put("id", jsonObject.getString("id"));
                                String image = jsonObject.getString("image");

                                Image imagee = new Image();
                                imagee.setName(jsonObject.getString("image"));
                                imagee.setImageId(Integer.parseInt(jsonObject.getString("id")));

                                urlimage = urllink.downloadGalleryImg + image;
                                hashMap.put("urlimage", urlimage);
                                imagee.setMedium(urlimage);
                                images.add(imagee);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.imagesNotAddedYet), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {//com.android.volley.AuthFailureError
                    progressDialog.dismiss();
                    String err = error.toString();
                    if (err.equals("com.android.volley.AuthFailureError")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();
                        Intent intent = new Intent(GallaryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
        } else {
            commonCode.AlertDialog_Pbtn(GallaryActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
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

    @Override
    protected void onStart() {
        super.onStart();
        getAllImages();
    }
}
