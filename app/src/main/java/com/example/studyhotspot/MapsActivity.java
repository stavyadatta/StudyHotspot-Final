package com.example.studyhotspot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.studyhotspot.R.raw.wireless2;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    //widgets
    private Button mSearchText;
    private FloatingActionButton homeButton;
    private FloatingActionButton directions;
    private GoogleMap mMap;
    private GeoJsonLayer layerShop = null;
    private GeoJsonLayer layerFB = null;
    private GeoJsonLayer layerComm = null;
    static Map<String, GeoJsonLayer> layers = new HashMap<String, GeoJsonLayer>();


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
    final LatLng Sg = new LatLng(1.353791, 103.818145);

    String name;
    LatLng latLng;
    LatLng latLngStart;
    LatLng latLngEnd;

    JSONObject HotspotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Get Hotspot Data
        getData();

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDfUFca8a0bcE6Q4iog86Ud7lz6lig6WGc");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*GeoJsonLayer layer = new GeoJsonLayer(mMap, jObjek);
        layer.addLayerToMap();*/

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
                        Uri.parse("http://maps.google.com/maps?saddr=1.353791,103.818145&daddr=1.353791,103.818145"));
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

        GeoJsonLayer.GeoJsonOnFeatureClickListener geoJsonOnFeatureClickListener = new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();

                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("LOCATION_NAME"));
                System.out.println(geometry);
            }
        };

        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        chip3 = findViewById(R.id.chip3);

        chipStatus.put("Community", false);
        chipStatus.put("Cafes", false);
        chipStatus.put("Shopping", false);

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        boolean filter_gov = false;

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String index = buttonView.getText().toString();
                    chipStatus.put(index,true);
                    layers.get(index).addLayerToMap();
                    layers.get(index).setOnFeatureClickListener(geoJsonOnFeatureClickListener);
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


        /*OkHttp obj = new OkHttp();

        // id of the wireless hotspots on data.gov.sg is 6b3f1e1b-257d-4d49-8142-1b2271d20143
        try {
            System.out.println("CKAN Package Show");
            String dataURL = obj.getURL("6b3f1e1b-257d-4d49-8142-1b2271d20143");

            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.wireless.geojson, getApplicationContext());
            layer.addLayerToMap();

        }catch (Exception e){
            e.printStackTrace();
        }*/




        try {
            getData();
            JSONObject emptyGeoJson = new JSONObject();
            layerShop = new GeoJsonLayer(mMap, emptyGeoJson);
            layerFB = new GeoJsonLayer(mMap, emptyGeoJson);
            layerComm = new GeoJsonLayer(mMap, emptyGeoJson);
            GeoJsonLayer layer = new GeoJsonLayer(mMap, HotspotData);


            Iterable<GeoJsonFeature> geoJsonFeature = layer.getFeatures();

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
        pointStyle.setTitle("View Information");

        pointStyle = layerShop.getDefaultPointStyle();
        pointStyle.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi));
        pointStyle.setTitle("View Information");

        pointStyle = layerComm.getDefaultPointStyle();
        pointStyle.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi));
        pointStyle.setTitle("View Information");

        /*layerShop.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();

                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("Description"));
                String s = feature.getProperty("Description");
                int begin = s.indexOf("<th>LOCATION_NAME</th> <td>");
                int end = s.indexOf("</td>",begin);
                String name = s.substring(begin+27,end);

                begin = s.indexOf("<th>LOCATION_TYPE</th> <td>");
                end = s.indexOf("</td>",begin);
                String category = s.substring(begin+27,end);


                System.out.println(category+": "+name);
            }
        });
        layerFB.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();

                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("Description"));
                String s = feature.getProperty("Description");
                int begin = s.indexOf("<th>LOCATION_NAME</th> <td>");
                int end = s.indexOf("</td>",begin);
                String name = s.substring(begin+27,end);

                begin = s.indexOf("<th>LOCATION_TYPE</th> <td>");
                end = s.indexOf("</td>",begin);
                String category = s.substring(begin+27,end);


                System.out.println(category+": "+name);
            }
        });
        layerComm.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();

                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("Description"));
                String s = feature.getProperty("Description");
                int begin = s.indexOf("<th>LOCATION_NAME</th> <td>");
                int end = s.indexOf("</td>",begin);
                String name = s.substring(begin+27,end);


                begin = s.indexOf("<th>LOCATION_TYPE</th> <td>");
                end = s.indexOf("</td>",begin);
                String category = s.substring(begin+27,end);


                System.out.println(category+": "+name);
            }
        });


        for (int i=0; i<loc_gov.size(); i++){
            LatLng cur = loc_gov.get(i);
            mMap.addMarker(new MarkerOptions().title("Mark: "+Integer.toString(i)).position(cur)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_wifi)));
        }*/

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
