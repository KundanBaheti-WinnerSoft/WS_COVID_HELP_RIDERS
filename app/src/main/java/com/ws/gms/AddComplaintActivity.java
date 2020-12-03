package com.ws.gms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.GridView;
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
import com.ws.gms.Adapters.GalleryAdapter;
import com.ws.gms.Consatants.CommonCode;
import com.ws.gms.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddComplaintActivity extends AppCompatActivity {

    CommonCode commonCode = new CommonCode();
    Urllink urllink = new Urllink();
    ProgressDialog progressDialog;
    String SecurityToken, firstName, middleName, lastName, userName, userRole, userId, mobileno;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);

        commonCode.updateLocaleIfNeeded(AddComplaintActivity.this);
        myDialog = new Dialog(AddComplaintActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.addComplaint));
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
                    if (complaintTypeId == 0) {
                        spnCompType.setError(getResources().getString(R.string.plsSelectComplaintType));
                        spnCompType.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, title)) {
                        edtTitle.setError(getResources().getString(R.string.plsEnterTitle));
                        edtTitle.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, desc)) {
                        edtDesc.setError(getResources().getString(R.string.plsEnterDesc));
                        edtDesc.requestFocus();
                    } else if (!commonCode.isValidString(AddComplaintActivity.this, area)) {
                        edtArea.setError(getResources().getString(R.string.plsEnterArea));
                        edtArea.requestFocus();
                    } else {
                        confirmCommentPopup(v);
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
                jsonParams.put("title", title);
                jsonParams.put("complaintDescription", desc);
                jsonParams.put("area", area);

                JSONObject adminObj = new JSONObject();
                adminObj.put("id", userId);
                jsonParams.put("localUser", adminObj);

                JSONObject comp = new JSONObject();
                comp.put("id", complaintTypeId);
                jsonParams.put("complaintType", comp);

                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < imagearray.size(); i++) {
                    jsonArray.put(imagearray.get(i));
                }
                jsonParams.put("images", jsonArray);

                jsonParams.put("status", "Open");

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
                            Toast.makeText(AddComplaintActivity.this, getResources().getString(R.string.complaintAddedSuccess), Toast.LENGTH_LONG).show();
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
                addComplaint();
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
