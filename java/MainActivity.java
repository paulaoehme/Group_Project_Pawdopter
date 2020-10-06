package com.myapp.pawdopter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final Uri APP_URI = Uri.parse("android-app://com.myapp.pawdopter/MainActivity");

    private DatabaseReference animalRef, categoryRef;
    private FirebaseDatabase database;
    private ListView animalsList;
    private ArrayList<Animal> arrayOfAnimals;
    private ArrayList<String> categoryList, breedList, sizeList;
    private AnimalAdapter animalAdapter;
    private ArrayAdapter<String> categoryAdapter, breedAdapter, sizeAdapter;
    private Animal animal;
    private Spinner category, breed, size;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        animalsList = (ListView) findViewById(R.id.animalsListView);
        database = FirebaseDatabase.getInstance();
        animalRef = database.getReference("Animal");
        categoryRef = database.getReference("AnimalCategory");

        //Construct data source
        arrayOfAnimals = new ArrayList<>();
        categoryList = new ArrayList<>();
        breedList = new ArrayList<>();
        sizeList = new ArrayList<String>();
        sizeList.add(0, "Size");
        sizeList.add(1, "Small");
        sizeList.add(2, "Medium");
        sizeList.add(3, "Large");

        //Setting default value for dropdown list
        categoryList.add(0,"Category");
        breedList.add(0,"Breed");

        //Create the adapter to convert the array to views
        animalAdapter = new AnimalAdapter(this, arrayOfAnimals);
        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, breedList);
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sizeList);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // ########## Filters ##########

        //Category
        category = (Spinner) findViewById(R.id.categoryFilter);
        category.setAdapter(categoryAdapter);
        retrieveCategory();

        //Breed
        breed = (Spinner) findViewById(R.id.breedFilter);
        breed.setAdapter(breedAdapter);
        breed.setEnabled(false);

        //Size
        size = (Spinner) findViewById(R.id.sizeFilter);
        size.setAdapter(sizeAdapter);
        size.setEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Displaying data
        animalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //Create temporary ArrayList
                ArrayList<Animal> updateList = new ArrayList<Animal>();
                //Loop through the collection
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Store animal to temporary ArrayList
                    animal = ds.getValue(Animal.class);
                    updateList.add(animal);
                }
                //Clear current list of animals and notify change
                arrayOfAnimals.clear();
                animalAdapter.notifyDataSetChanged();

                //Attach the adapter to a ListView
                animalsList.setAdapter(animalAdapter);
                //Add entries in the temporary ArrayList to the current list of animals
                arrayOfAnimals.addAll(updateList);
                animalAdapter.notifyDataSetInvalidated();

                category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        final String categorySelected = parent.getSelectedItem().toString();

                        //Call selected category
                        selectCategory(dataSnapshot, parent, view, position, id, categorySelected);

                        //Display animal within selected size
                        size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final String sizeSelected = parent.getSelectedItem().toString();

                                //Call selected size
                                selectSize(dataSnapshot, parent, view, position, id, categorySelected, sizeSelected);

                                //Display animals within selected breed
                                breed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        final String breedSelected = parent.getSelectedItem().toString();

                                        //Call selected breed
                                        selectBreed(dataSnapshot, parent, view, position, id, categorySelected, sizeSelected, breedSelected);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                animalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), AnimalProfile.class);
                        Animal selectedAnimal = animalAdapter.getItem(position);
                        intent.putExtra("selectedAnimal", selectedAnimal);
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    //Create options menu (three dots)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Determine what item is selected in menu bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.updateProfile:
                //Go to update profile screen
                Intent intent = new Intent(this, UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.logout:
                //Logout
                Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_LONG).show();
                logout();
                break;
            default:
                //unknown error
        }
        return super.onOptionsItemSelected(item);
    }

    //Execute logout from app and return to login screen
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }

    //Retrieve animal categories from Firebase
    public void retrieveCategory(){
        listener = categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    categoryList.add(ds.getValue().toString());
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Retrieve animal categories from Firebase
    public void retrieveBreed(final String category, final String size){
        listener = animalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> list = new HashSet<String>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    animal = ds.getValue(Animal.class);
                    String animalCateg = animal.getCategory();
                    String animalSize = animal.getSize();

                    if(category.equals(animalCateg) && size.equals(animalSize)){
                        String breed = animal.getBreed();
                        list.add(breed);
                    }
                }
                breedList.clear();
                breedList.add(0,  "Breed");
                breedList.addAll(list);
                breedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void selectCategory(@NotNull DataSnapshot dataSnapshot, AdapterView<?> parent, View view, int position, long id, String categorySelected) {
        this.size.setEnabled(false);
        this.breed.setEnabled(false);

        //Create temporary ArrayList
        ArrayList<Animal> updateList = new ArrayList<Animal>();
        //Loop through the collection
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            this.animal = ds.getValue(Animal.class);
            String categ = this.animal.getCategory();
            //Store animal to temporary ArrayList
            if (position > 0) {
                if (categorySelected.equals(categ)) {
                    this.animal = ds.getValue(Animal.class);
                    updateList.add(this.animal);

                    //Enable size selection
                    size.setEnabled(true);
                    size.setSelection(0);

                }
            } else {
                this.animal = ds.getValue(Animal.class);
                updateList.add(this.animal);

                size.setEnabled(false);
            }

        }

        //Clear current list of animals and notify change
        arrayOfAnimals.clear();
        animalAdapter.notifyDataSetChanged();

        //Attach the adapter to a ListView
        animalsList.setAdapter(animalAdapter);
        //Add entries in the temporary ArrayList to the current list of animals
        arrayOfAnimals.addAll(updateList);
        animalAdapter.notifyDataSetInvalidated();

    }

    public void selectSize(@NotNull DataSnapshot dataSnapshot, AdapterView<?> parent, View view, int position, long id, String categorySelected, String sizeSelected) {
        //Create temporary ArrayList
        ArrayList<Animal> updateList = new ArrayList<Animal>();
        //Loop through the collection
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            animal = ds.getValue(Animal.class);
            String categ = animal.getCategory();
            String animalSize = animal.getSize();
            //Store animal to temporary ArrayList
            if (position > 0) {
                if (categorySelected.equals(categ)) {
                    if (sizeSelected.equals(animalSize)) {
                        animal = ds.getValue(Animal.class);
                        updateList.add(animal);

                        //Enable breed selection
                        breed.setEnabled(true);
                        breed.setSelection(0);

                        //Populate breed spinner with compatible breeds according to selected category and size
                        retrieveBreed(categorySelected, sizeSelected);
                    }
                }

            } else {
                if (categorySelected.equals(categ)) {
                    animal = ds.getValue(Animal.class);
                    updateList.add(animal);

                    breed.setEnabled(false);
                }
            }

        }

        //Clear current list of animals and notify change
        arrayOfAnimals.clear();
        animalAdapter.notifyDataSetChanged();
        sizeAdapter.notifyDataSetChanged();

        //Attach the adapter to a ListView
        animalsList.setAdapter(animalAdapter);
        //Add entries in the temporary ArrayList to the current list of animals
        arrayOfAnimals.addAll(updateList);
        animalAdapter.notifyDataSetInvalidated();
    }

    public void selectBreed(@NotNull DataSnapshot dataSnapshot, AdapterView<?> parent, View view, int position, long id, String categorySelected, String sizeSelected, String breedSelected) {
        //Create temporary ArrayList
        ArrayList<Animal> updateList = new ArrayList<Animal>();
        //Loop through the collection
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            animal = ds.getValue(Animal.class);
            String categ = animal.getCategory();
            String animalSize = animal.getSize();
            String animalBreed = animal.getBreed();

            //Store animal to temporary ArrayList
            if(position>0){
                if(categorySelected.equals(categ)){
                    if(sizeSelected.equals(animalSize)){
                        if(breedSelected.equals(animalBreed)){
                            animal = ds.getValue(Animal.class);
                            updateList.add(animal);
                        }
                    }
                }
            }
            else{
                if(categorySelected.equals(categ)) {
                    if (sizeSelected.equals(animalSize)) {
                        animal = ds.getValue(Animal.class);
                        updateList.add(animal);
                    }
                }
            }

        }
        //Clear current list of animals and notify change
        arrayOfAnimals.clear();
        animalAdapter.notifyDataSetChanged();

        //Attach the adapter to a ListView
        animalsList.setAdapter(animalAdapter);
        //Add entries in the temporary ArrayList to the current list of animals
        arrayOfAnimals.addAll(updateList);
        animalAdapter.notifyDataSetInvalidated();
    }

}
