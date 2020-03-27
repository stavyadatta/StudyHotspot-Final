package com.example.studyhotspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
//

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowLongClickListener {


    //widgets
    private Button mSearchText;
    private FloatingActionButton homeButton;
    private FloatingActionButton directions;
    private static GoogleMap mMap;
    private BottomAppBar bottomAppBar;


    private static GeoJsonLayer layerShop = null;
    private static GeoJsonLayer layerFB = null;
    private static GeoJsonLayer layerComm = null;
    private static GeoJsonLayer fullLayer = null;
    static Map<String, GeoJsonLayer> layers = new HashMap<String, GeoJsonLayer>();

    private Marker mMarker;
    private static final String TAG = "MapsActivity";

    Chip chip1, chip2, chip3;
    static Map<String, Boolean> chipStatus = new HashMap<String, Boolean>();

    private List<Place.Field> fields;
    private List<LatLng> loc_gov = new ArrayList<LatLng>();
    private List<LatLng> loc_fb = new ArrayList<LatLng>();
    private List<LatLng> loc_comm = new ArrayList<LatLng>();/**/
    final int place_picker_req_code = 1;
    final LatLng Sg = new LatLng(1.353791, 103.818145);

    String name;
    String userID;
    LatLng latLng;
    JSONObject jsonObject = null;

    JSONObject HotspotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpBottomAppBar();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Get Hotspot Data
        getData();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d("GET USER ID","UID: "+ userID);
            }
        });

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyCo7BtlsuOVcER0l-THnPurg5v1RjBXXtU");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchText = findViewById(R.id.input_search);
        homeButton = findViewById(R.id.homeButton);
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
                        Uri.parse("https://www.google.com/maps/dir///@1.3553318,103.6941784,15z/data=!4m2!4m1!3e0"));
                startActivity(intent);

                /*Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MapsActivity.this);

                startActivityForResult(intent,directions_req_code);
                System.out.println("HELLO");*/
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
            }
        });

        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        chip3 = findViewById(R.id.chip3);

        chipStatus.put("Community", false);
        chipStatus.put("Cafes", false);
        chipStatus.put("Shopping", false);

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String index = buttonView.getText().toString();
                    chipStatus.put(index,true);
                    layers.get(index).addLayerToMap();

                }
                else{
                    String index = buttonView.getText().toString();
                    chipStatus.put(buttonView.getText().toString(),false);
                    layers.get(index).removeLayerFromMap();
                }
            }
        };
        chip1.setOnCheckedChangeListener(checkedChangeListener);
        chip2.setOnCheckedChangeListener(checkedChangeListener);
        chip3.setOnCheckedChangeListener(checkedChangeListener);


    }
    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Fav")){
                    //
                }
                else if (title.contentEquals("Activities")){
                    Intent intent = new Intent(MapsActivity.this, ActivityPageMain.class);
                    startActivity(intent);
                }
                else if (title.contentEquals("Settings")){
                    //Intent intent = new Intent(MapsActivity.this, )
                }

                return false;
            }});
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

            boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                    .getString(R.string.style_json)));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

            try {
                getData();
                JSONObject emptyGeoJson = new JSONObject();
                layerShop = new GeoJsonLayer(mMap, emptyGeoJson);
                layerFB = new GeoJsonLayer(mMap, emptyGeoJson);
                layerComm = new GeoJsonLayer(mMap, emptyGeoJson);
                fullLayer = new GeoJsonLayer(mMap, HotspotData);


                Iterable<GeoJsonFeature> geoJsonFeature = fullLayer.getFeatures();

                for(GeoJsonFeature cur : geoJsonFeature) {
                    String s = cur.getProperty("Description");

                    int begin = s.indexOf("<th>LOCATION_TYPE</th> <td>");
                    int end = s.indexOf("</td>",begin);
                    String category = s.substring(begin+27,end);

                    if (category.contentEquals("F&B")){
                        layerFB.addFeature(cur);
                    }
                    else if (category.contentEquals("Shopping Mall")){
                        layerShop.addFeature(cur);
                    }
                    else if (category.contentEquals("Community")){
                        layerComm.addFeature(cur);
                    }
                }
                layers.put("Community", layerComm);
                layers.put("Cafes", layerFB);
                layers.put("Shopping", layerShop);
            } catch (Exception e) {
                e.printStackTrace();
            }



            GeoJsonPointStyle pointStyle = layerFB.getDefaultPointStyle();
            pointStyle.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi));
            pointStyle.setTitle("Information (Long Click)");

            pointStyle = layerShop.getDefaultPointStyle();
            pointStyle.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi));
            pointStyle.setTitle("Information (Long Click)");

            pointStyle = layerComm.getDefaultPointStyle();
            pointStyle.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi));
            pointStyle.setTitle("Information (Long Click)");

            mMap.setTrafficEnabled(true);
            mMap.setOnInfoWindowLongClickListener(this);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Sg));
            mMap.setMinZoomPreference(11);
        }

        private void resetView(){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Sg));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(Sg, 11);
            mMap.animateCamera(update);
        }

        private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }

        @Override
        public void onInfoWindowLongClick(Marker marker) {
            Iterable<GeoJsonFeature> geoJsonFeature = fullLayer.getFeatures();

            String target = marker.getPosition().toString();
            int begin = target.indexOf("(");
            int end = target.indexOf(")");
            String targetCoord = target.substring(begin+1, end);

            System.out.println(targetCoord);
            System.out.println("^TARGET");

            String name = null;
            String s = null;
            String match = null;

            for(GeoJsonFeature cur : geoJsonFeature) {

                match = cur.getGeometry().toString();

                if (match.contains(target)){
                    System.out.println("MATCH");
                    s = cur.getProperty("Description");
                    begin = s.indexOf("<th>LOCATION_NAME</th> <td>");
                    end = s.indexOf("</td>",begin);
                    name = s.substring(begin+27,end);
                    break;
                }
            }

            System.out.println(name);



            StringBuilder sb = new StringBuilder();
            sb.append("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=");
            sb.append(name);
            sb.append("&inputtype=textquery&fields=place_id&key=AIzaSyCo7BtlsuOVcER0l-THnPurg5v1RjBXXtU&locationbias=ipbias");
            String url = sb.toString();

            System.out.println(url);

            try {
                String jsonstring = URLReader.readUrl(sb.toString());
                jsonObject = new JSONObject(jsonstring);
            } catch (Exception e) {
                e.printStackTrace();
            }


            Intent intent = new Intent(MapsActivity.this, LocationInformationActivity.class);
            intent.putExtra("Name", name);
            intent.putExtra("Coord", targetCoord);
            try {
                intent.putExtra("PlaceID", jsonObject.get("candidates").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            marker.hideInfoWindow();
        }

        private void getData(){
            try {

                String dataGovURL = "https://data.gov.sg/api/action/package_show?id=6b3f1e1b-257d-4d49-8142-1b2271d20143";

                String packageURLData = URLReader.readUrl(dataGovURL);


                JSONObject Jobject = new JSONObject(packageURLData);
                String dataURL = Jobject.getJSONObject("result").getJSONArray("resources").getJSONObject(1).get("url").toString();

                String data = URLReader.readUrl(dataURL);
                HotspotData = new JSONObject(data);

            } catch (Exception e){}
        }
    }
