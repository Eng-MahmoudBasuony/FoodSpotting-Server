package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 11101;
    private SupportMapFragment mapFragment;
    private Switch btnSwitch;
    private Button btnCallClint,btnCallShipper;
    //----------------------------
    private FirebaseDatabase database;
    private DatabaseReference refDriversLocation;


    //-------------
    //-------------
   private   Marker markerYorShipper;
   private   Marker markerClintLocation;
   private   Marker markerServerAppLocation;

   private LocationRequest locationRequest;
   private LocationCallback locationCallback;
   private FusedLocationProviderClient fusedLocationProviderClient;
   private GeoFire geoFireDataBase; //هخزن فيها خطوط الطول والعرض للمكان

    //---------Direction--------//
   private LatLng shipperCurrentLocation, clintCurrentLocation,serverCurrentLocation;
    //-----------------
  private   String shipperPhone;
  private   String clientPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);



            initViews();

            //-------init--------//
            database = FirebaseDatabase.getInstance();
            refDriversLocation = database.getReference(Common.DRIVER_LOCATION);



            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            geoFireDataBase = new GeoFire(refDriversLocation);

            //--Get Constant Location Clint
            if (getIntent() != null)
            {

                String latLngClint = getIntent().getStringExtra("Latlng");
                shipperPhone = getIntent().getStringExtra("shipperPhone");
                clientPhone=getIntent().getStringExtra("clientPhone");

                String[] separatedLocation = latLngClint.split(",");

                separatedLocation[0] = separatedLocation[0].trim();
                separatedLocation[1] = separatedLocation[1].trim();

                clintCurrentLocation = new LatLng(Double.parseDouble(separatedLocation[0]), Double.parseDouble(separatedLocation[1]));


            }

            //--------Check open GPS ------------//
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            } else {
                showGPSDisabledAlertToUser();
            }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }



    private void initViews() {
        //-------------Id------------------//
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnSwitch = (Switch) findViewById(R.id.pin);
        btnCallClint = (Button)findViewById(R.id.btnCallClient);
        btnCallShipper=(Button)findViewById(R.id.btnCallShipper);


        //------Event---------------//

        //---Run Map and Gel your Location and Draw Rout to ClintLocation

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) //True
                {
                    startGettingLocation();
                } else //False
                {

                    stopGettingLocation();
                }


            }
        });





        btnCallShipper.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                String uri = "tel:" + shipperPhone.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));

                if (ActivityCompat.checkSelfPermission(TrackingOrder.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });


        btnCallClint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                String uri = "tel:" + clientPhone.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));

                if (ActivityCompat.checkSelfPermission(TrackingOrder.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });


    }


    //---------Get Your Location-------//
    private void startGettingLocation()
    {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this
                    ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}
                    ,MY_PERMISSIONS_REQUEST_LOCATION);
        }else
        {
            prepareLocationRequest();
            prepareCallBack();
            fusedLocationProviderClientPermission();
        }


    }

    private  void stopGettingLocation()
    {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        fusedLocationProviderClient.removeLocationUpdates(locationCallback); //هوقف الresponse اللى راجع من السيرفر
        mMap.setMyLocationEnabled(false);

        Snackbar.make(mapFragment.getView(),"Your OffLine",Snackbar.LENGTH_SHORT).show();


    }

    private void fusedLocationProviderClientPermission()
    {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

        Snackbar.make(mapFragment.getView(),"Your Online",Snackbar.LENGTH_SHORT).show();

    }

    private void prepareLocationRequest()
    {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setSmallestDisplacement(.00001f);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void prepareCallBack()
    {
        locationCallback=new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {super.onLocationResult(locationResult);

                //Your Location
                List<Location>locations=locationResult.getLocations();

                if (locations.size()>0)
                {
                    final Location mLastLocationServerApp = locations.get(locations.size() - 1);//First Rout Storage in List

                    //---get Storage Location In Firebase
                    geoFireDataBase
                            .getLocation(shipperPhone, new com.firebase.geofire.LocationCallback() {
                                @Override
                                public void onLocationResult(String key, GeoLocation locationShipper) {

                                //--------------------------------Server App----------------------------------//
                                    //Marker Clint
                                    if (markerServerAppLocation != null)//يعنى موجود على الخريطه وواخد location
                                        markerServerAppLocation.remove(); //احذف العلامه ديه عشان هديله الlocation الاحدث لموقعى

                                    //Add Clint Marker
                                    markerServerAppLocation = mMap.addMarker(new MarkerOptions()
                                            .title("Food Spotting")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.storeonline))
                                            .position(new LatLng(mLastLocationServerApp.getLatitude(), mLastLocationServerApp.getLongitude())));

                                    //Add Circle Around Location Clint
                                    CircleOptions circleOptionsServer = new CircleOptions();
                                    circleOptionsServer.center(new LatLng(mLastLocationServerApp.getLatitude(), mLastLocationServerApp.getLongitude()));
                                    circleOptionsServer.radius(20);
                                    circleOptionsServer.fillColor(Color.GREEN);
                                    circleOptionsServer.strokeColor(Color.RED);
                                    circleOptionsServer.strokeWidth(4);

                                    mMap.addCircle(circleOptionsServer);

                                    //-------------------------------Shipper-----------------------------------------//

                                    //Marker Your Location Shipper
                                    if (markerYorShipper != null)//يعنى موجود على الخريطه وواخد location
                                        markerYorShipper.remove(); //احذف العلامه ديه عشان هديله الlocation الاحدث لموقعى

                                    //Add Marker On Your Location
                                    markerYorShipper = mMap.addMarker(new MarkerOptions()
                                            .title("Shipper")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markershipper))
                                            .position(new LatLng(locationShipper.latitude, locationShipper.longitude)));

                                    //markerYorShipper.setDraggable(true);//عشان محدش يحركه بأيده

                                    //معنى انك تضيف الanimatCamer هنا انك هتعملzome على الyourLocation بالمسافه اللى حددتها
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationShipper.latitude, locationShipper.longitude), 15));

                                    //-------------------------------------------Client-----------------------------------//
                                    //Marker Clint
                                    if (markerClintLocation != null)//يعنى موجود على الخريطه وواخد location
                                        markerClintLocation.remove(); //احذف العلامه ديه عشان هديله الlocation الاحدث لموقعى

                                    //Add Clint Marker
                                    markerClintLocation = mMap.addMarker(new MarkerOptions()
                                            .title("Your Location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerclint))
                                            .position(new LatLng(clintCurrentLocation.latitude, clintCurrentLocation.longitude)));

                                    //Add Circle Around Location Clint
                                    CircleOptions circleOptionsClient = new CircleOptions();
                                    circleOptionsClient.center(new LatLng(clintCurrentLocation.latitude, clintCurrentLocation.longitude));
                                    circleOptionsClient.radius(50);
                                    //circleOptionsClient.fillColor(Color.BLUE);
                                    circleOptionsClient.strokeColor(Color.RED);
                                    circleOptionsClient.strokeWidth(4);

                                    mMap.addCircle(circleOptionsClient);

                                 //-----------------------------------------------------------------------------------//

                                    //---After Call Back Draw And Chose Route between 2 Points
                                    shipperCurrentLocation = new LatLng(locationShipper.latitude, locationShipper.longitude);
                                    serverCurrentLocation=new LatLng(mLastLocationServerApp.getLatitude(),mLastLocationServerApp.getLongitude());

                                    // getDirectionAndDraw();
                                    if (shipperCurrentLocation != null && clintCurrentLocation != null && serverCurrentLocation !=null) {

                                        getDirectionAndDraw(shipperCurrentLocation, clintCurrentLocation,serverCurrentLocation);
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }
            }
        };


    }

    /*
    * holder.btnTrackingShipper.setVisibility(View.GONE); //will hide the button and space acquired by button
                    //// holder.btnTrackingShipper.setVisibility(View.INVISIBLE); //will hide button only.
                    if (adapter.getItem(position).getStatus().equals("2")) //shipping
                    {

                        holder.btnTrackingShipper.setVisibility(View.VISIBLE); //Shaw Button

                        //open Map for Shaw RealTime Location to Shipper
                        holder.btnTrackingShipper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent intentTracing=new Intent(OrderStatus.this,TrackingShipper.class);
                                       intentTracing.putExtra("Latlng",model.getLatlng());
                                       intentTracing.putExtra("shipperPhone",model.getPhoneShipper());
                                       startActivity(intentTracing);
                            }
                        });
                    }else
                        {
                            holder.btnTrackingShipper.setVisibility(View.GONE); //will hide button only.
                        }
    *
    * */


    //Method Open Gps
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:

                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                {
                    prepareLocationRequest();
                    prepareCallBack();
                    fusedLocationProviderClientPermission();

                }else
                {
                    Toast.makeText(this, "محتاجين السماحيات يابيه ", Toast.LENGTH_SHORT).show();
                }

                break;




        }



    }

    private void getDirectionAndDraw(LatLng shipperCurrentLocation, LatLng clintCurrentLocation,LatLng serverCurrentLocation)
    {
        //المكتبه هتحددلك الاتجاهات فقط وبعدين انت ترسم بأيدك
        GoogleDirection.withServerKey(getResources().getString(R.string.google_directions_key))
                .from(shipperCurrentLocation)
                .and(clintCurrentLocation)
                .to(serverCurrentLocation)
                .transportMode(TransportMode.DRIVING) //بتحددله انك راكب عشان يقترحلك الطرق المناسبه
                //.avoid(AvoidType.HIGHWAYS) //بتقوله هنا تجنب الطرق العاليه
                //.avoid(AvoidType.FERRIES)
                .transitMode(TransitMode.BUS)
                .language("AR")
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override//فى حال حدد الاتجاه بين المنطقتين هيخش هنا
                    public void onDirectionSuccess(Direction direction, String rawBody) //وطبعا أنت لا تحدد الاتجاهات فقط لمجرد تحديد الاتجاهات بل لتقوم بامر اخر والذى يكون فى اغلب الاحيان رسم خط على الخريطة يوضح هذه الاتجاهات
                    {
                        //نتيجة الاتصال السابق على هيئة List تحتوى على Route أو اكثر حيث أحيانا يكون هناك عدة طرق للوصول لمكان اخر من هذا المكان
                        //يحتوى الـ Route على Leg أو أكثر  وهو المسافه بين مكان واخر
                        //ويحتوى الـ Leg على step وهى المسافة بين مكان واخر داخل نفس الـ Leg
                        //ذلك نستدعى ذلك من النتيجة ونقوم بناءا عليه بإنشاء Polyline ونرسمه على الخريطة بناءا على بيانات الاتجاهات الى حصلنا عليها كما يتضح من الكود التالى

                        if (direction.isOK()) {
/*

                            Leg leg = direction.getRouteList().get(0).getLegList().get(0);

                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(TrackingOrder.this, directionPositionList, 5, Color.RED);//.jointType(JointType.ROUND);

                            mMap.addPolyline(polylineOptions);
*/


                            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();

                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(TrackingOrder.this, stepList, 5, Color.RED, 3, Color.BLUE);

                            for (PolylineOptions polylineOption : polylineOptionList)
                            {
                                mMap.addPolyline(polylineOption);
                            }




                            Log.e("Direction", "onDirectionSuccess: ");
                        } else {

                            Log.e("Direction", direction.getErrorMessage());

                        }

                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("Direction", "faild" + t.getLocalizedMessage());
                    }
                });
    }




}
