package com.ws.helprider;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ws.helprider.Adapters.GalleryAdapter;
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddImagesActivity extends AppCompatActivity {
    String SecurityToken;
    View view;
    String AddImagesFor;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ArrayList<String> imagearray = new ArrayList<>();
    private String bitImageString = "";
    private String schoolid = "";
    private CommonCode commonCode = new CommonCode();
    private Urllink urllink = new Urllink();
    private String url = urllink.url;
    private String encodedUploadedFile = "";
    private Bitmap bitmap;
    private String userChoosenTask = "";
    private Button btn_browse_image, btnUploadImage, btnCancel;

    //Pick images
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images_fragment);
        commonCode.updateLocaleIfNeeded(AddImagesActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setSubtitle("To parent's");
        getSupportActionBar().setTitle(R.string.uploadImage);

        commonCode.setupUI(findViewById(R.id.layout), this);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");
        schoolid = sharedPreferences.getString("schoolid", "");

        Intent oIntent = getIntent();
        Bundle bd = oIntent.getExtras();
        if (bd != null) {
            AddImagesFor = oIntent.getExtras().getString("For");
        }

        gvGallery = (GridView) findViewById(R.id.gvGallary);

        btnCancel = findViewById(R.id.btn_cancel);
        btnUploadImage = findViewById(R.id.btn_uploadImage);

        btn_browse_image = findViewById(R.id.btn_selectImage);
        btn_browse_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selImages)), PICK_IMAGE_MULTIPLE);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagearray.isEmpty()) {
                    commonCode.AlertDialog_Pbtn(AddImagesActivity.this, getResources().getString(R.string.browseImage), getResources().getString(R.string.plsBrowseImgForGlry), getResources().getString(R.string.ok));
                } else {
                    uploadImage();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void uploadImage() {
        if (commonCode.checkConnection(getApplicationContext())) {
            String jsonurl = urllink.url + "uploadImage/add";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            progressDialog = new ProgressDialog(AddImagesActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            JSONObject jsonParams = new JSONObject();
            //    String[] namesArr = imagearray.toArray(new String[imagearray.size()]);
            try {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < imagearray.size(); i++) {
                    jsonArray.put(imagearray.get(i));
                }
                jsonParams.put("images", jsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, jsonurl,

                    jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try {
                                String message = response.getString("message");
                                if (message.equals("Photo Updated sucessfully...!!!")) {
                                    imagearray.clear();
                                    Toast.makeText(getApplicationContext(), R.string.addedSuccess, Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String err = error.toString();
                            if (err.equals("com.android.volley.AuthFailureError")) {
                                Toast.makeText(getApplicationContext(), R.string.tokenExpire, Toast.LENGTH_LONG).show();

                                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                sp.edit().clear().commit();
                                Intent intent = new Intent(AddImagesActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.serverError, Toast.LENGTH_LONG).show();
                            }
                            //   Handle Error
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
            commonCode.AlertDialog_Pbtn(AddImagesActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

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
                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        bitImageString = getStringImage(bitmap);
                        imagearray.add(bitImageString);
                    } catch (Exception e) {
                        Toast.makeText(this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            if (i >= 10) {
                                Toast.makeText(this, "You can't share more than 10 media items",
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

                                galleryAdapter = new GalleryAdapter(getApplicationContext(), mArrayUri);
                                gvGallery.setAdapter(galleryAdapter);
                                gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                        .getLayoutParams();
                                mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);


                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
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
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
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
}
