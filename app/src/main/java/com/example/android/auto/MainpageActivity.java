package com.example.android.auto;

import android.Manifest;
import android.app.PendingIntent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class MainpageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks
,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    FirebaseAuth auth;
    FirebaseUser user;
    String user_id;
    TextView t1,t2;
    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng startLatLng,endLT;
    Marker current_marker;
    Marker dest_marker;
    Button b4_s;
    Button b5_d;
    PermissionManager permissionManager;

    DatabaseReference reference ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        auth=FirebaseAuth.getInstance();
        permissionManager =new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        b4_s=(Button) findViewById(R.id.button4);
        b5_d=(Button) findViewById(R.id.button5);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent i=new Intent(MainpageActivity.this,MainActivity.class);
            startActivity(i);

        }
        else{
            user_id= user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name=dataSnapshot.child("name").getValue(String.class);
                    String email=dataSnapshot.child("email").getValue(String.class);
                    t1 =(TextView) findViewById(R.id.name_text);
                    t2 =(TextView) findViewById(R.id.email_text);
                    t1.setText(name);
                    email="temp";
                    t2.setText(email);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        b4_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i=new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainpageActivity.this);
                    startActivityForResult(i,200);
                }catch (GooglePlayServicesNotAvailableException e){
                    e.printStackTrace();
                }catch (GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                }

            }
        });
        b5_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i=new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MainpageActivity.this);
                    startActivityForResult(i,400);
                }catch (GooglePlayServicesNotAvailableException e){
                    e.printStackTrace();
                }catch (GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                }

            }
        });


    }
    @Override
    public void onConnectionSuspended(int i){

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request= new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(500);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        //LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        if(location==null){
            Toast.makeText(getApplicationContext(),"location could not be found",Toast.LENGTH_LONG).show();

        }
        else{
            startLatLng= new LatLng(location.getLatitude(),location.getLongitude());


            Geocoder geocoder= new Geocoder(this, Locale.getDefault());
            try{
                List<Address> myaddress= geocoder.getFromLocation(startLatLng.latitude,startLatLng.longitude,1);
                String address = myaddress.get(0).getAddressLine(0);
                String city = myaddress.get(0).getLocality();
                b4_s.setText(address+ " "+ city);
            }catch(IOException e){
                e.printStackTrace();

            }


            if(current_marker==null){




                MarkerOptions options= new MarkerOptions();
                options.position(startLatLng);
                options.title("Current Position");
                current_marker= mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng,15));
            }
            else{
                current_marker.setPosition(startLatLng);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> denied_array = permissionManager.getStatus().get(0).denied;
        if(denied_array.isEmpty()){
            Toast.makeText(getApplicationContext(),"user granted permission",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

        // Add a marker in Sydney and move the camera

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {
            // Handle the camera action
        } else if (id == R.id.nav_urtrips) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_freerides) {

        } else if (id == R.id.nav_signout) {
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                auth.signOut();
                Intent myintent=new Intent(MainpageActivity.this,MainActivity.class);
                startActivity(myintent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "User is already signed out", Toast.LENGTH_SHORT).show();
            }

        }
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            if(requestCode==RESULT_OK){
                Place place = PlaceAutocomplete.getPlace(this,data);
                String name = place.getName().toString();
                startLatLng = place.getLatLng();

                b4_s.setText(name);
                if(current_marker==null){
                    MarkerOptions options1= new MarkerOptions();
                    options1.title("Pickup locatio");
                    options1.position(startLatLng);
                    current_marker= mMap.addMarker(options1);
                }else{
                    current_marker.setPosition(startLatLng);
                }

            }
        }
        else if(requestCode==400){
            if(requestCode==RESULT_OK){
                Place myplace= PlaceAutocomplete.getPlace(this,data);
                String name = myplace.getName().toString();
                endLT= myplace.getLatLng();
                b5_d.setText(name);
                if(dest_marker==null){
                    MarkerOptions options1= new MarkerOptions();
                    options1.title("destination");
                    options1.position(endLT);
                    dest_marker= mMap.addMarker(options1);
                }
                else {
                    dest_marker.setPosition(endLT);
                }

            }
        }
    }
}
