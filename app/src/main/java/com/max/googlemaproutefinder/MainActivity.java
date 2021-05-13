package com.max.googlemaproutefinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    protected LatLng start = null;
    protected LatLng end = null;
    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    boolean locationPermission = false;
    //google map object
    private GoogleMap mMap;
    //polyline object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //request location permission.
        requestPermision();

        //init google map fragment to show map.
        // SupportMapFragment mapFragment;
        //  MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // permission denied,

                }
                return;
            }
        }
    }

    //to get user location
    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(location -> {

            myLocation = location;
            LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    ltlng, 13f);
            mMap.animateCamera(cameraUpdate);
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getMyLocation();

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_address:
                EditText editText = findViewById(R.id.locationSearch);
                String myAddress = editText.getText().toString();

                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();

                if (myAddress.isEmpty() || myAddress == null) {
                    Toast.makeText(this, "Please enter any place name", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(myAddress, 6);

                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                Address userAddress = addressList.get(i);

                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                                markerOptions.position(latLng);
                                markerOptions.title(myAddress);
                                markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.location2));

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                        latLng, 14f);
                                mMap.addMarker(markerOptions);

                                mMap.animateCamera(cameraUpdate);
                            }
                        }else {
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                break;
        }
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
