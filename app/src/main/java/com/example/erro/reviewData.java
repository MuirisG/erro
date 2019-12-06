package com.example.erro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.erro.Adapter.SpinnerAdapter;
import com.example.erro.Utils.GlideToast;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class reviewData extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinnerZone, spinnerDirectorate, spinnerSurface;
    String[] zonelist={"zone1", "zone2", "zone3","zone4"};
    String[] directoratelist={"youghal", "cobh", "glanmire","blarney"};
    String[] surfacelist={"tarmac", "grass verge", "concrete foothpath","hra"};
    String selectedZone, selectedDirectorate, selectedSurface, selectedLocationX, selectedLocationY;
    TextView tvlocationX, tvlocationY, tvResult, btnResult;
    EditText etLength, etBreadth;
    Button btnAttachPhoto;
    ImageView ivPhoto;

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

        btnResult.setOnClickListener(this);
        btnAttachPhoto.setOnClickListener(this);

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
        }
    }


}
