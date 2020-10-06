package com.myapp.pawdopter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class AnimalProfile extends AppCompatActivity {

    private Animal animal;
    private static final String TAG = "AnimalProfile";
    private LinearLayout shelterLayout;
    private TextView name, breed, age, gender, personality, shelterView;
    private ImageView picture;
    private Button sendEmail;
    private DatabaseReference shelterRef, userRef;
    private FirebaseDatabase database;
    private Shelter shelter;
    private FirebaseAuth mAuth;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        shelterRef = database.getReference("Shelter");

        sendEmail = (Button) findViewById(R.id.sendIntent);

        if(getIntent().hasExtra("selectedAnimal")){
            animal = getIntent().getParcelableExtra("selectedAnimal");
            Log.d(TAG, "onCreate"+animal.toString());
        }

        shelterLayout = (LinearLayout) findViewById(R.id.shelterLink);


        shelterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = (TextView) findViewById(R.id.name);
                breed = (TextView) findViewById(R.id.breed);
                age = (TextView) findViewById(R.id.age);
                gender = (TextView) findViewById(R.id.gender);
                personality = (TextView) findViewById(R.id.personality);
                shelterView = (TextView) findViewById(R.id.shelter);
                picture = (ImageView) findViewById(R.id.picture);

                final String animalName = animal.getName();
                final String animalBreed = animal.getBreed();
                String animalAge = animal.getAge();
                String animalGender = animal.getGender();
                final String animalCategory = animal.getCategory();
                String animalPersonality = animal.getPersonality();
                final String animalShelter = animal.getShelter();
                String animalPicture = animal.getPicture();

                name.setText(animalName);
                breed.setText(animalBreed);
                age.setText(animalAge);
                gender.setText(animalGender);
                personality.setText(animalPersonality);
                Picasso.get().load(animalPicture).resize(650,650).into(picture);

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    shelter = ds.getValue(Shelter.class);
                    final String shelterID = ds.getKey();
                    if(shelterID.equals(animalShelter)){
                        final String shelterName = shelter.getName();
                        final String emailTo = shelter.getEmail();
                        final String shelterNumber = shelter.getPhone();
                        final String shelterAddress = shelter.getAddress();
                        shelterView.setText(shelterName);

                        shelterLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), ShelterProfile.class);
                                Animal selectedAnimal = animal;
                                intent.putExtra("selectedAnimal", selectedAnimal);

                                intent.putExtra("shelterName", shelterName);
                                intent.putExtra("shelterPhone", shelterNumber);
                                intent.putExtra("shelterAddress", shelterAddress);
                                intent.putExtra("shelterEmail", emailTo);
                                startActivity(intent);
                            }
                        });

                        sendEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String emailSubject = "Intention of animal adoption";
                                //Initialise FirebaseAuth instance
                                mAuth = FirebaseAuth.getInstance();
                                currentUserID = mAuth.getCurrentUser().getUid();
                                database = FirebaseDatabase.getInstance();
                                userRef = database.getReference("User").child(currentUserID);

                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {
                                            //Retrieving info from user table
                                            String userName = dataSnapshot.child("name").getValue().toString();

                                            String categoryToLowercase = animalCategory.toLowerCase();
                                            //Composing e-mail body
                                            String emailBody = "To whom it may concern, \n\nMy name is " + userName + " and I am interested in adopting the " + animalBreed + " " + categoryToLowercase + " called " + animalName + ".\nPlease, contact me so we can arrange a date and time for a visit to the shelter.\n\nThank you,\n" + userName;

                                            sendAdoptionIntent(emailTo, emailSubject, emailBody);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendAdoptionIntent(String emailTo, String emailSubject, String emailBody){
        Log.d(TAG, "sendemail");
        String[] recipient = new String[]{emailTo};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, recipient);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make sure that when user returns to the app, pawdopter app is displayed, instead of the email app.
        startActivity(Intent.createChooser(intent, "Choose an e-mail client"));
    }
}