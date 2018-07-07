package com.example.eng_mahnoud83coffey.embeatitserver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Common.DirectionJSONParser;
import com.example.eng_mahnoud83coffey.embeatitserver.Remote.IGeoCoordinates;
import com.google.android.gms.location.LocationListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback
    ,GoogleApiClient.ConnectionCallbacks
    ,GoogleApiClient.OnConnectionFailedListener
    ,LocationListener
    {

    private GoogleMap mMap;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST=1000;
    private final static int LOCATION_PERMISSION_REQUEST=1001; //Constant Request Permission

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL=1000;
    private static int FATEST_INTERVAL=5000;
    private static int DISPLACEMENT=10;

    private IGeoCoordinates mServiceCoordinates; //Interface Retrofit

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);



           mServiceCoordinates= Common.getIGeoCodeService();


          if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                  ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
          {
            requestRunTimePermission();

          }else
              {
                  if (checkplayServices())
                  {
                       bulidGoogleApiClinet();
                       createLocationRequest();
                  }
              }


            displayLocation();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

        private void displayLocation()
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                requestRunTimePermission();

            }else
                {
                    mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (mLastLocation!=null)
                    {
                        double latuitud =mLastLocation.getLatitude();
                        double longtuitud=mLastLocation.getLongitude();



                        //add marker in your location and move the camera
                        LatLng yourLOcation=new LatLng(latuitud,longtuitud);

                        mMap.addMarker(new MarkerOptions().position(yourLOcation).title("Your Location"));
                        //This method repositions the camera according to the instructions defined in the update
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLOcation));
                        //This method Moves the map according to the update with an animation
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                        // after Add Marker on your Location , Add Marker for This Order and Draw Route
                        addRequestMarker(yourLOcation,Common.currentRequest.getAddress());

                    }else
                        {
                            Toast.makeText(this, "Could Not Get Location", Toast.LENGTH_SHORT).show();
                        }

                }


            }




         //Method Add Marker for This Order and Draw Route
        private void addRequestMarker(final LatLng yourLOcation, String address)
        {
            mServiceCoordinates.getGeoCode(address).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    try
                    {

                        JSONObject jsonObject=new JSONObject(response.body().toString());


                        String lat=((JSONArray)jsonObject.get("results"))
                                                                          .getJSONObject(0)
                                                                          .getJSONObject("geometry")
                                                                          .getJSONObject("location")
                                                                          .get("lat").toString();


                        String lng=((JSONArray)jsonObject.get("results"))
                                                                      .getJSONObject(0)
                                                                      .getJSONObject("geometry")
                                                                      .getJSONObject("location")
                                                                      .get("lng").toString();


                        LatLng  orderLocation=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));


                        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.box);
                        bitmap=Common.scaleBitmap(bitmap,70,70);

                        MarkerOptions marker=new MarkerOptions()
                                                               .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                               .title("Order of"+Common.currentRequest.getPhone())
                                                                .position(orderLocation);
                        mMap.addMarker(marker);


                        //draw Route
                        mServiceCoordinates.getDirection(yourLOcation.latitude+","+yourLOcation.longitude
                                                                ,orderLocation.latitude+","+orderLocation.longitude)

                                           .enqueue(new Callback<String>() {
                                               @Override
                                               public void onResponse(Call<String> call, Response<String> response)
                                               {

                                                   new ParserTask().execute(response.body().toString());
                                               }

                                               @Override
                                               public void onFailure(Call<String> call, Throwable t) {

                                               }
                                           });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });

        }

        private void createLocationRequest()
        {
            mLocationRequest=new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        }



        protected synchronized void bulidGoogleApiClinet()
        {
            mGoogleApiClient =new GoogleApiClient.Builder(this)
                                                .addConnectionCallbacks(this)
                                                .addOnConnectionFailedListener(this)
                                                .addApi(LocationServices.API).build();


            mGoogleApiClient.connect();
        }


        private boolean checkplayServices()
        {

        int resuletCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resuletCode!=ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resuletCode))
            {

                GooglePlayServicesUtil.getErrorDialog(resuletCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else
                {
                    Toast.makeText(this, "This Device is not support", Toast.LENGTH_SHORT).show();
                    finish();
                }


        return false ;
        }

        return true;
    }


        // Request Permission
      private void requestRunTimePermission()
        {

            ActivityCompat.requestPermissions(this,
                                                      new String[]{
                                                                   Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                   Manifest.permission.ACCESS_FINE_LOCATION
                                                                   },
                                                      LOCATION_PERMISSION_REQUEST
                                             );



        }

           @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            switch(requestCode)
            {
                case LOCATION_PERMISSION_REQUEST:

                    if (grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    {

                        if (checkplayServices())
                        {
                            bulidGoogleApiClinet();
                            createLocationRequest();

                            displayLocation();
                        }

                    }

                    break;

            }


        }

        @Override
     public void onMapReady(GoogleMap googleMap)
        {
        mMap = googleMap;

           mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }





    //------------------------




        //لميثود onConnect يتم تنفيذها بعد الاتصال بالـ Google Api مباشرة
        @Override
        public void onConnected(@Nullable Bundle bundle)
        {

            displayLocation();
            startLocationUpdate();

        }


        private void startLocationUpdate()
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                       return;
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

        // onConnectionSuspended تستدعى عندما يتم ايقاف الاتصال
        @Override
        public void onConnectionSuspended(int i)
        {

            mGoogleApiClient.connect();
        }

        //الـ onConnectionFailed تستدعى فى حالة فشل الاتصال
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }



        @Override
        public void onLocationChanged(Location location)
        {

            mLastLocation=location;
            displayLocation();
        }


        @Override
        protected void onResume() {
            super.onResume();

            checkplayServices();

        }

        @Override
        protected void onStart() {


            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();

            super.onStart();
        }

        @Override
        protected void onStop() {

            mGoogleApiClient.disconnect();

            super.onStop();

        }

        private class ParserTask  extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{


               ProgressDialog mDialog=new ProgressDialog(TrackingOrder.this);

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                mDialog.setMessage("Please Waiting...");
                mDialog.show();
            }

            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... strings)
            {
                          JSONObject jsonObject;
                         List<List<HashMap<String,String>>>routes=null;


                         try
                         {
                             jsonObject=new JSONObject(strings[0]);

                             DirectionJSONParser parser=new DirectionJSONParser();

                             routes=parser.parse(jsonObject);


                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         return routes;
            }


            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> lists)
            {


                mDialog.dismiss();

                ArrayList points=null;

                PolylineOptions lineOptions=null;

                for (int i=0;i<lists.size();i++)
                {
                    points=new ArrayList();

                    lineOptions=new PolylineOptions();


                    List<HashMap<String,String>> path=lists.get(i);



                    for (int j=0; j<path.size();j++)
                    {
                        HashMap<String,String> point=path.get(j);


                      double lat=Double.parseDouble(point.get("lat"));
                      double lng=Double.parseDouble(point.get("lng"));

                      LatLng position=new LatLng(lat,lng);

                      points.add(position);


                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.BLUE);
                    lineOptions.geodesic(true);
                }

                mMap.addPolyline(lineOptions);
            }
        }



    }
