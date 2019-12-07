package com.example.erro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erro.API.ApiClient;
import com.example.erro.API.GetMessageForm;
import com.example.erro.API.GetMessageResponse;
import com.example.erro.API.HttpHandler;
import com.example.erro.Adapter.SpinnerAdapter;
import com.example.erro.Utils.GlideToast;
import com.example.erro.Utils.WeiboDialogUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class reviewData extends AppCompatActivity implements View.OnClickListener{

    String base_url = "https://mycouncil.net";
    private Spinner spinnerZone, spinnerDirectorate, spinnerSurface;
    String[] zonelist={"zone1", "zone2", "zone3","zone4"};
    String[] directoratelist={"youghal", "cobh", "glanmire","blarney"};
    String[] surfacelist={"tarmac", "grass verge", "concrete foothpath","hra"};
    String selectedZone, selectedDirectorate, selectedSurface, selectedLocationX, selectedLocationY, selectedArea = "0";
    TextView tvlocationX, tvlocationY, tvResult, btnResult;
    EditText etLength, etBreadth;
    Button btnAttachPhoto, btnSend, btnGetID, btnUploadPhoto;
    ImageView ivPhoto;
    protected Dialog loadingDialog;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;

    public static final int PICK_IMAGE = 1;
    private String upLoadServerUri = null;
    private String imagepath="";
    private int serverResponseCode = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    String uploadPhotoName = "";
    String uploadPhotoNamewithoutExtention = "";

    private GetMessageForm getMessageForm;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_data);
        
        tvlocationX = findViewById(R.id.location_x);
        tvlocationY = findViewById(R.id.location_y);
        etLength = findViewById(R.id.et_length);
        etBreadth = findViewById(R.id.et_breadth);
        tvResult = findViewById(R.id.tv_result);
        btnResult = findViewById(R.id.btn_result);
        spinnerZone = findViewById(R.id.spinner_zone);
        spinnerDirectorate = findViewById(R.id.spinner_directorate);
        spinnerSurface = findViewById(R.id.spinner_surface);
        btnAttachPhoto = findViewById(R.id.btn_attach_photo);
        ivPhoto = findViewById(R.id.attached_photo);
        btnSend = findViewById(R.id.btn_send);
        btnGetID = findViewById(R.id.btn_getPhotoID);
        btnUploadPhoto = findViewById(R.id.btn_uploadPhoto);

        btnResult.setOnClickListener(this);
        btnAttachPhoto.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnGetID.setOnClickListener(this);
        btnUploadPhoto.setOnClickListener(this);

        SpinnerAdapter customAdapter1 = new SpinnerAdapter(getApplicationContext(),zonelist);
        spinnerZone.setAdapter(customAdapter1);
        SpinnerAdapter customAdapter2 = new SpinnerAdapter(getApplicationContext(),directoratelist);
        spinnerDirectorate.setAdapter(customAdapter2);
        SpinnerAdapter customAdapter3 = new SpinnerAdapter(getApplicationContext(),surfacelist);
        spinnerSurface.setAdapter(customAdapter3);

        spinnerZone.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedZone = zonelist[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerDirectorate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDirectorate = directoratelist[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinnerSurface.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSurface = surfacelist[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        init();
        restoreValuesFromBundle(savedInstanceState);
        startLocationButtonClick();
    }
/////////////////////////////////// start of get location coordination ///////////////////////////////////////
    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }
    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            selectedLocationX = Double.toString(mCurrentLocation.getLatitude());
            selectedLocationY = Double.toString(mCurrentLocation.getLongitude());
//            String[] separatedX = str1.split("\\.");
//            String[] separatedY = str2.split("\\.");
            tvlocationX.setText(selectedLocationX);
            tvlocationY.setText(selectedLocationY);
//            tvXInt.setText(separatedX[0]);
//            tvXFloat.setText("."+separatedX[1].substring(0,4));
//            tvYInt.setText(separatedY[0]);
//            tvYFloat.setText("."+separatedY[1].substring(0,4));
        }
    }
    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        new GlideToast.makeToast(reviewData.this,"Started Location Updates");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(reviewData.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                new GlideToast.makeToast(reviewData.this,errorMessage);
                        }
                        updateLocationUI();
                    }
                });
    }
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
/////////////////////////////////// end of get location coordination /////////////////////////////////////////

/////////////////////////////////// start of load picture ////////////////////////////////////////////////////
    private void getImageFromAlbum(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                imagepath =getPath(imageUri);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivPhoto.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri.substring(sourceFileUri.lastIndexOf("/")+1);
        fileName = "1" + "__" + fileName;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            WeiboDialogUtils.closeDialog(loadingDialog);
            runOnUiThread(new Runnable() {
                public void run() {
                    new GlideToast.makeToast(reviewData.this,"Choose one of image file");
                }
            });
            return 0;
        }
        else{
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    new GlideToast.makeToast(reviewData.this, "uploaded");
                } else {
                    WeiboDialogUtils.closeDialog(loadingDialog);
                    new GlideToast.makeToast(reviewData.this, "fault");
                }
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                ex.printStackTrace();
            } catch (Exception e) {
                WeiboDialogUtils.closeDialog(loadingDialog);
                e.printStackTrace();
            }
            return serverResponseCode;
        }
    }

    /////////////////////////////////// end of load picture //////////////////////////////////////////////////////
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_result) {
            int a, b, c;
            if(!etLength.getText().toString().equals("") && !etBreadth.getText().toString().equals("")) {
                a = Integer.valueOf(etLength.getText().toString());
                b = Integer.valueOf(etBreadth.getText().toString());
                c = a * b;
                tvResult.setText(String.valueOf(c));
                selectedArea = String.valueOf(c);
            } else {
                new GlideToast.makeToast(reviewData.this, "input two number");
            }
        } else if(view.getId() == R.id.btn_attach_photo) {
            int newHeight = 200, newWidth = 200;
            ivPhoto.requestLayout();
            ivPhoto.getLayoutParams().height = newHeight;
            ivPhoto.getLayoutParams().width = newWidth;
            ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
            getImageFromAlbum();
        } else if(view.getId() == R.id.btn_send) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Sending data");
            loadingDialog.show();
            RequestSend();
        } else if(view.getId() == R.id.btn_getPhotoID) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "getting latest photo id");
            loadingDialog.show();
            new GetLatestPhotoID().execute();
        } else if(view.getId() == R.id.btn_uploadPhoto) {
//            upLoadServerUri = "https://mycouncil.net/api/upload.php";
            upLoadServerUri = "https://urban.network/Api/upload/photoupload.php";
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "uploading photo");
            loadingDialog.show();
            uploadFile(imagepath);
        }
    }
/////////////////////////////////// send data //////////////////////////////////////////////////////
    public void RequestSend() {
        String code,  gmail;
        code = "erro";
        gmail = "test@gmail";
        selectedLocationX = "aaaa";
        selectedLocationY = "bbbb";


        getMessageForm = new GetMessageForm(code, selectedZone, selectedDirectorate, selectedSurface, selectedArea, selectedLocationX, selectedLocationY, gmail);
        Call<GetMessageResponse> mService =
                ApiClient.getInstance()
                        .getApi()
                        .getMessage(
                                getMessageForm.getCode(),
                                getMessageForm.getZone(),
                                getMessageForm.getDirectorate(),
                                getMessageForm.getSurface(),
                                getMessageForm.getArea(),
                                getMessageForm.getLocationX(),
                                getMessageForm.getLocationY(),
                                getMessageForm.getGmail());
        mService.enqueue(
                new Callback<GetMessageResponse>() {
                    @Override
                    public void onResponse(
                            Call<GetMessageResponse> call,
                            Response<GetMessageResponse> response) {
                        WeiboDialogUtils.closeDialog(loadingDialog);
                        try {
                            if (response.isSuccessful()) {
                                new GlideToast.makeToast(reviewData.this,"Success");


                            } else {
                                String s = response.errorBody().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    new GlideToast.makeToast(reviewData.this,"Send Fault Error 401");
                                } catch (JSONException e) {
                                    new GlideToast.makeToast(reviewData.this,"Send Fault Error 404");
                                }
                            }
                        } catch (IOException e) {
                            new GlideToast.makeToast(reviewData.this,"Connect Server Error");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetMessageResponse> call, Throwable t) {
                        call.cancel();
                        WeiboDialogUtils.closeDialog(loadingDialog);
                        new GlideToast.makeToast(reviewData.this,"Network Error");
                    }
                });
    }
/////////////////////////////////// upload photo ///////////////////////////////////////////////////

    private class GetLatestPhotoID extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "/api/profilePicture.php";
            String parameters = "";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String id = jsonObj.getString("id");
                    int a = Integer.parseInt(id);
                    a = a+1;
                    uploadPhotoName = String.valueOf(a);
                    uploadPhotoNamewithoutExtention = uploadPhotoName;
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error photoID: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            new GlideToast.makeToast(reviewData.this, uploadPhotoName + "  " + imagepath);

        }
    }
}
