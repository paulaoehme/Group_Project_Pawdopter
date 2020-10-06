package com.myapp.pawdopter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ShelterProfile extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ShelterProfile";
    private TextView shelterName, shelterPhone, shelterAddress, shelterEmail;
    private String name, phone, address, email;
    private Animal animal;
    private Button goBack;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        goBack = (Button) findViewById(R.id.goBackBttn);

        final Intent in = getIntent();

        if(getIntent().hasExtra("selectedAnimal")){
            animal = getIntent().getParcelableExtra("selectedAnimal");
            Log.d(TAG, "onCreate"+animal.toString());
        }

        name = in.getStringExtra("shelterName");
        phone = in.getStringExtra("shelterPhone");
        address = in.getStringExtra("shelterAddress");
        email = in.getStringExtra("shelterEmail");

        shelterName = (TextView) findViewById(R.id.shelterName);
        shelterPhone = (TextView) findViewById(R.id.shelterPhone);
        shelterAddress = (TextView) findViewById(R.id.shelterAddress);
        shelterEmail = (TextView) findViewById(R.id.shelterEmail);

        shelterName.setText(name);
        shelterPhone.setText(phone);
        shelterAddress.setText(address);
        shelterEmail.setText(email);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnimalProfile.class);
                Animal selectedAnimal = animal;
                intent.putExtra("selectedAnimal", selectedAnimal);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = 0;
        double lon = 0;
        if((shelterEmail.getText().toString()).equals("cspcashelter@yahoo.com")){
            lat = 51.885516;
            lon = -8.405689;
        }
        if((shelterEmail.getText().toString()).equals("ispca.shelter@gmail.com")){
            lat = 53.739253;
            lon = -7.700096;
        }
        if((shelterEmail.getText().toString()).equals("lawshelter.limerick@gmail.com")){
            lat = 52.374025;
            lon = -8.448726;
        }
        if((shelterEmail.getText().toString()).equals("serashelter@hotmail.com")){
            lat = 52.469020;
            lon = -9.580914;
        }

        LatLng shelterLoc = new LatLng(lat,lon);
        mMap.addMarker(new MarkerOptions().position(shelterLoc).title("Shelter location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(shelterLoc));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setMinZoomPreference(7);
        mMap.setMaxZoomPreference(20);
    }
}
