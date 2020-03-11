package com.example.studyhotspot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    //widgets
    private Button mSearchText;
    private FloatingActionButton directions;
    private GoogleMap mMap;

    private Marker mMarker;
    private static final String TAG = "MapsActivity";

    Chip chip1, chip2, chip3;
    static Map<String, Boolean> chipStatus = new HashMap<String, Boolean>();
    private ArrayList<String> selectedChips = new ArrayList<String>();

    private List<Place.Field> fields;
    private List<LatLng> loc_gov = new ArrayList<LatLng>();
    private List<LatLng> loc_fb = new ArrayList<LatLng>();
    private List<LatLng> loc_comm = new ArrayList<LatLng>();/**/
    final int place_picker_req_code = 1;
    final int directions_req_code = 2;

    String name;
    LatLng latLng;
    LatLng latLngStart;
    LatLng latLngEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDfUFca8a0bcE6Q4iog86Ud7lz6lig6WGc");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchText = findViewById(R.id.input_search);
        directions = findViewById(R.id.directions);

        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapsActivity.this);

                startActivityForResult(intent,place_picker_req_code);
            }
        });

        directions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=1.353791,103.818145&daddr=1.353791,103.818145"));
                startActivity(intent);

                /*Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapsActivity.this);

                startActivityForResult(intent,directions_req_code);
                System.out.println("HELLO");*/
            }
        });

        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        chip3 = findViewById(R.id.chip3);

        chipStatus.put("Government", false);
        chipStatus.put("Cafes", false);
        chipStatus.put("Community", false);

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        boolean filter_gov = false;

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    chipStatus.put(buttonView.getText().toString(),true);

                    System.out.println(chipStatus);
                }
                else{
                    chipStatus.put(buttonView.getText().toString(),false);

                    System.out.println(chipStatus);
                }
            }
        };
        GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng sel = marker.getPosition();
                System.out.println("HELLO");
                return false;
            }
        };
        chip1.setChecked(true);
        chip2.setChecked(true);
        chip3.setChecked(true);

        chip1.setOnCheckedChangeListener(checkedChangeListener);
        chip2.setOnCheckedChangeListener(checkedChangeListener);
        chip3.setOnCheckedChangeListener(checkedChangeListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == place_picker_req_code) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                name = place.getName();

                mSearchText.setText(name);

                latLng = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                mMap.animateCamera(update);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                onMapReady(mMap);
            } else if (resultCode == RESULT_CANCELED) {
                onMapReady(mMap);
            }
        }
        /*if (requestCode == directions_req_code){
            latLngStart = new LatLng(1.284261, 103.851051);

            if (resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                name = place.getName();

                mSearchText.setText(name);

                latLngEnd = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                mMap.animateCamera(update);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                onMapReady(mMap);
            } else if (resultCode == RESULT_CANCELED) {
                onMapReady(mMap);
            }
        }*/
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        ArrayList<LatLng> listPoints = new ArrayList<LatLng>();

        /*ActivityCompat.requestPermissions(this, new String[](Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST)*/;


        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        // Initialize the camera to Singapore
        LatLng Sg = new LatLng(1.353791, 103.818145);

        OkHttp obj = new OkHttp();

        System.out.println("CKAN Package Show");

        // id of the wireless hotspots on data.gov.sg is 6b3f1e1b-257d-4d49-8142-1b2271d20143
        String dataURL = null;
        String data = "";
        try {
            dataURL = obj.getURL("6b3f1e1b-257d-4d49-8142-1b2271d20143");
            data = obj.accessData(dataURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("HELLO");

        System.out.println(data.isEmpty());

        System.out.println("HELLO");
        loc_gov.add(new LatLng(1.284261, 103.851051));
        loc_gov.add(new LatLng(1.283580, 103.850946));
        loc_gov.add(new LatLng(1.283843, 103.852684));
        loc_gov.add(new LatLng(1.282406, 103.852620));


        /**
        for (int i = 0; i < dataset.get(); i++){
            LatLng cur = new LatLng(......);
            if (cur.category...== ....)
            loc_comm.add();
            loc_fb.add();
        }**/


        for (int i=0; i<loc_gov.size(); i++){
            LatLng cur = loc_gov.get(i);
            mMap.addMarker(new MarkerOptions().title("Mark: "+Integer.toString(i)).position(cur)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi)));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("Click");
                System.out.println(marker.getPosition());
                System.out.println(marker.getTitle());
                return true;
            }
        });

        mMap.setTrafficEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Sg));
        mMap.setMinZoomPreference(11);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void requestDirections(){
        StringBuilder sb = new StringBuilder();

        Object[] dataTransfer = new Object[4];

        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin="+latLngStart.latitude+","+latLngStart.longitude);
        sb.append("&destination="+latLngEnd.latitude+","+latLngEnd.longitude);
        sb.append("&key=AIzaSyDfUFca8a0bcE6Q4iog86Ud7lz6lig6WGc");

        GetDirectionsData directions = new GetDirectionsData(getApplicationContext());
        dataTransfer[0] = mMap;
        dataTransfer[1] = sb.toString();
        dataTransfer[2] = new LatLng(latLngStart.latitude, latLngStart.longitude);
        dataTransfer[3] = new LatLng(latLngEnd.latitude, latLngEnd.longitude);
        directions.execute(dataTransfer);

    }
}
