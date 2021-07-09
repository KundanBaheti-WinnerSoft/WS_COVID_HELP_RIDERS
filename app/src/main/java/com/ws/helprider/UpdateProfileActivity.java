package com.ws.helprider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.ws.helprider.Consatants.CommonCode;
import com.ws.helprider.Consatants.Urllink;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText edtFname, edtMName, edtLname, edtMob, edtEmailid, edtAddress, edtArea, edtDob, edtPincode, edtCity;
    Button btnCancel, btnUpdate;
    private ProgressDialog progressDialog;
    Dialog myDialog;
    private CommonCode commonCode = new CommonCode();
    private Urllink urllink = new Urllink();
    private String role = "", imgName, firstname = "", middlename = "", lastname = "", mobileno = "", emailId = "", birthDate = "", address = "", area = "", cityval = "", pincodeval = "", stateval = "", countryval = "";
    private String radiovalue = "";
    private int mYear, mMonth, mDay, mHour, mMinute;
    String imageUrl = "";

    private String url = urllink.url;

    private ArrayList<String> imagearray = new ArrayList<>();
    private String encodedUploadedFile = "";
    private String bitImageString = "";
    private Bitmap bitmap;
    private int REQUEST_CAMERA = 5, SELECT_FILE = 5;//emp_reg so 1 1
    private static final int SELECT_PICTURE = 5;

    String jsonurl = "", userId;

    private SharedPreferences sharedPreferences;
    String SecurityToken;
    RelativeLayout rlProfilePic;
    CircleImageView ivProfilePic;
    String userName, password;
    String otp = "", rspotp;

    boolean removePic = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        commonCode.updateLocaleIfNeeded(UpdateProfileActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.myProfile));
        getSupportActionBar().setSubtitle("");
        myDialog = new Dialog(this);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        SecurityToken = sharedPreferences.getString("securitytoken", "");

        userName = sharedPreferences.getString("userName", "");
        password = sharedPreferences.getString("password", "");

        role = sharedPreferences.getString("role", "");
        userId = sharedPreferences.getString("userId", "");
        imgName = sharedPreferences.getString("image", "");
        firstname = sharedPreferences.getString("firstName", "");
        middlename = sharedPreferences.getString("middleName", "");
        lastname = sharedPreferences.getString("lastName", "");
        radiovalue = sharedPreferences.getString("gender", "");
        mobileno = sharedPreferences.getString("mobileno", "");
        emailId = sharedPreferences.getString("emailId", "");
        birthDate = sharedPreferences.getString("birthdate", "");
        address = sharedPreferences.getString("address", "");
        area = sharedPreferences.getString("area", "");
        cityval = sharedPreferences.getString("city", "");
        pincodeval = sharedPreferences.getString("pincode", "");
        stateval = sharedPreferences.getString("state", "");
        countryval = sharedPreferences.getString("country", "");



        ivProfilePic = findViewById(R.id.img_profile);

        edtFname = findViewById(R.id.edt_fname);

        edtMName = findViewById(R.id.edt_mname);

        edtLname = findViewById(R.id.edt_lname);

        edtMob = findViewById(R.id.edt_mob);
        edtEmailid = findViewById(R.id.edt_emailid);
        edtDob = findViewById(R.id.edt_birthdate);
        edtAddress = findViewById(R.id.edt_address);
        edtArea = findViewById(R.id.edt_area);

        edtCity = findViewById(R.id.edt_city);

        edtPincode = findViewById(R.id.edt_pincode);

        btnCancel = findViewById(R.id.btn_cancel);
        btnUpdate = findViewById(R.id.btn_update);
        rlProfilePic = findViewById(R.id.rl_profile_pic);

//        rlProfilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ediProfilePicPopup(v);
//            }
//        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation(role, v);
            }
        });


        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDispatchCalendar();
                final DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
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


        //Set Vallues From SharedPrefrences

        String imageUrl = urllink.downloadProfilePic + imgName; // profileImageUrl
        Picasso.with(getApplicationContext()).load(imageUrl).into(ivProfilePic);

        if (ivProfilePic.getDrawable() == null) {
            ivProfilePic.setImageResource(R.mipmap.avatar);
        }
        edtFname.setText(firstname);
        edtMName.setText(middlename);
        edtLname.setText(lastname);
        edtMob.setText(mobileno);
        edtEmailid.setText(emailId);

//        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
//        long complaintDate = Long.parseLong(birthDate);
//        String date = formatter.format(new Date(complaintDate));
//
//        edtDob.setText(date);
        edtAddress.setText(address);
        edtArea.setText(area);
        edtCity.setText(cityval);
        edtPincode.setText(pincodeval);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void showDispatchCalendar() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    public void validation(String UserRole, View v) {
        firstname = edtFname.getText().toString();
        middlename = edtMName.getText().toString();
        lastname = edtLname.getText().toString();
        mobileno = edtMob.getText().toString();
        emailId = edtEmailid.getText().toString();
        birthDate = edtDob.getText().toString();
        address = edtAddress.getText().toString();
        area = edtArea.getText().toString();
        pincodeval = edtPincode.getText().toString();
        cityval = edtCity.getText().toString();

        if (!commonCode.isValidString(UpdateProfileActivity.this, firstname)) {
            edtFname.setError(getResources().getString(R.string.plsEnterFirstName));
            edtFname.requestFocus();
        } else if (!commonCode.isValidString(UpdateProfileActivity.this, middlename)) {
            edtMName.setError(getResources().getString(R.string.plsEnterMiddleName));
            edtMName.requestFocus();
        } else if (!commonCode.isValidString(UpdateProfileActivity.this, lastname)) {
            edtLname.setError(getResources().getString(R.string.plsEnterLastName));
            edtLname.requestFocus();
        } else if (!commonCode.isValidMobileNo(UpdateProfileActivity.this, mobileno)) {
            edtMob.setError(getResources().getString(R.string.plsEnterMobNo));
            edtMob.requestFocus();
        } else if (!edtEmailid.getText().toString().trim().matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$")) {
            edtEmailid.setError(getResources().getString(R.string.validMailid));
            edtEmailid.requestFocus();
        }
//        else if (!commonCode.isValidString(UpdateProfileActivity.this, birthDate)) {
//            edtDob.setError(getResources().getString(R.string.plsEnterBirthdate));
//            edtDob.requestFocus();
//        }
        else if (!commonCode.isValidString(UpdateProfileActivity.this, address)) {
            edtAddress.setError(getResources().getString(R.string.plsEnterAddress));
            edtAddress.requestFocus();
        } else if (!commonCode.isValidString(UpdateProfileActivity.this, area)) {
            edtArea.setError(getResources().getString(R.string.plsEnterArea));
            edtArea.requestFocus();
        } else if (!commonCode.isValidString(UpdateProfileActivity.this, cityval)) {
            edtCity.setError(getResources().getString(R.string.plsEnterCity));
            edtCity.requestFocus();
        } else if (!commonCode.isValidPincode(UpdateProfileActivity.this, pincodeval)) {
            edtPincode.setError(getResources().getString(R.string.plsEnterPincode));
            edtPincode.requestFocus();
        } else {
            if (commonCode.isValidString(UpdateProfileActivity.this, UserRole)) {


                    if (role.equals("SuperAdmin")) {
                        jsonurl = urllink.url + "super_admin_reg/superAdminUpdate/" + userId;
                    } else if (role.equals("Administrator")) {
                        jsonurl = urllink.url + "regAdmin/adminupdate/" + userId;
                    } else if (role.equals("volunteer")) {
                        jsonurl = urllink.url + "regVolunteer/volunteerUpdate/" + userId;
                    } else if (role.equals("user")) {
                        jsonurl = urllink.url + "regLocalUser/localUserUpdate/" + userId;
                    }
                    update();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void update() {
        if (commonCode.checkConnection(UpdateProfileActivity.this)) {
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JSONObject jsonParams = new JSONObject();
            try {
                if (!imagearray.isEmpty()) {
                    String[] namesArr = imagearray.toArray(new String[imagearray.size()]);
                    jsonParams.put("image", namesArr[0]);
                } else if (removePic) {
                    jsonParams.put("image", "remove");
                }

                jsonParams.put("firstname", firstname);
                jsonParams.put("middlename", middlename);
                jsonParams.put("lastname", lastname);
                jsonParams.put("mobileno", mobileno);
                jsonParams.put("emailid", emailId);
//                jsonParams.put("birthdate", birthDate);
                jsonParams.put("country", countryval);
                jsonParams.put("state", stateval);
                jsonParams.put("city", cityval);
                jsonParams.put("gender", radiovalue);
                jsonParams.put("pincode", pincodeval);
                jsonParams.put("address", address);
                jsonParams.put("area", area);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PUT, jsonurl,

                    jsonParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        String message = response.getString("message");
                        if (message.equals("Record Updated...!!!")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.updatedSuccessfullyPleaseLoginAgain), Toast.LENGTH_LONG).show();
                            //getLoginDetails();
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("userId", userId);
//                            editor.putString("firstName", firstname);
//                            editor.putString("middleName", middlename);
//                            editor.putString("lastName", lastname);
//                            editor.putString("mobileno", mobileno);
//                            editor.putString("emailId", emailId);
//                            editor.putString("address", address);
//
////                            editor.putString("birthdate", birthDate);
//                            editor.putString("country", countryval);
//                            editor.putString("state", stateval);
//                            editor.putString("city", cityval);
//                            editor.putString("gender", radiovalue);
//                            editor.putString("pincode", pincodeval);
//                            editor.putString("area", area);
//
//                            editor.apply();
                            SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                            sp.edit().clear().apply();
                            Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
//                            onBackPressed();
                        } else if (message.equals("MobNo Already exists")) {
                            mobNoAlreadyPopup();
                        } else if (message.equals("Sorry Email Already Exist...!!!")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.emailIdAlreadyExist), Toast.LENGTH_LONG).show();
                            onBackPressed();
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
                        Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.serverError), Toast.LENGTH_LONG).show();
                    }
                }
            })        //This is for Headers If You Needed
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
            commonCode.AlertDialog_Pbtn(UpdateProfileActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
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


    private void getLoginDetails() {
        if (commonCode.checkConnection(UpdateProfileActivity.this)) {
            final ArrayList<HashMap<String, String>> arraymap = new ArrayList<>();
            String jsonurl = urllink.url + "user/getByLoginName";
            RequestQueue requestQueue = Volley.newRequestQueue(UpdateProfileActivity.this);
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            progressDialog.setCancelable(true);

            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, jsonurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject data = response;
                    progressDialog.dismiss();
                    try {

                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        sp.edit().clear().commit();

                        String userId = response.getString("id");
                        String mobileno = response.getString("mobileno");
                        String firstName = response.getString("firstname");
                        String middleName = response.getString("middlename");
                        String lastName = response.getString("lastname");
                        String emailId = response.getString("emailid");
                        String address = response.getString("address");

                        String birthdate = response.getString("birthdate");
                        String country = response.getString("country");
                        String state = response.getString("state");
                        String city = response.getString("city");
                        String gender = response.getString("gender");
                        String pincode = response.getString("pincode");
                        String image = response.getString("image");
                        String area = response.getString("area");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("securitytoken", SecurityToken);
                        editor.putString("username", userName);
                        editor.putString("password", password);
                        editor.putString("role", role);
                        editor.putString("userid", userId);

                        //userInfo
                        editor.putString("userId", userId);
                        editor.putString("firstName", firstName);
                        editor.putString("middleName", middleName);
                        editor.putString("lastName", lastName);
                        editor.putString("mobileno", mobileno);
                        editor.putString("emailId", emailId);
                        editor.putString("address", address);

                        editor.putString("birthdate", birthdate);
                        editor.putString("country", country);
                        editor.putString("state", state);
                        editor.putString("city", city);
                        editor.putString("gender", gender);
                        editor.putString("pincode", pincode);
                        editor.putString("image", image);
                        editor.putString("area", area);
                        editor.commit();

                        onBackPressed();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    String err = error.toString();

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.tokenExpire), Toast.LENGTH_LONG).show();
                    SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                    sp.edit().clear().commit();
                    Intent intent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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
            commonCode.AlertDialog_Pbtn(UpdateProfileActivity.this, getResources().getString(R.string.noInternetConnection), getResources().getString(R.string.plsConnectToInternet), getResources().getString(R.string.ok));
        }
    }

    public void ediProfilePicPopup(View v) {
        ImageView ivShowImage;
        TextView btnRemove, btnEdit;
        myDialog.setContentView(R.layout.edit_profile_pic);
        ViewGroup.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        btnRemove = (TextView) myDialog.findViewById(R.id.btn_remove);
        btnEdit = (TextView) myDialog.findViewById(R.id.btn_edit);
        ivShowImage = (ImageView) myDialog.findViewById(R.id.imageView);

        String imageUrl = urllink.downloadProfilePic + imgName; // profileImageUrl
        Picasso.with(getApplicationContext()).load(imageUrl).into(ivShowImage);

        if (ivShowImage.getDrawable() == null) {
            ivShowImage.setImageResource(R.mipmap.avatar);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ivProfilePic.setImageResource(R.mipmap.avatar);
                myDialog.dismiss();
                removePic = true;
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//selectImage();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    String data1 = selectedImageUri.getPath();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImageUri);
                        ivProfilePic.setImageBitmap(bitmap);

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        if (bitmap == null) {
                            bitImageString = getStringImage(BitmapFactory.decodeResource(getResources(), R.mipmap.avatar));
                        } else {
                            bitImageString = getStringImage(bitmap);
                        }
                        imagearray.add(bitImageString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedUploadedFile = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedUploadedFile;
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
