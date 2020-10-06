package com.myapp.pawdopter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnimalAdapter extends ArrayAdapter<Animal> {

    private static class ViewHolder{
        ImageView picture;
        TextView name;
        TextView age;
        TextView personality;
    }

    public AnimalAdapter(Context context, ArrayList<Animal> animal) {
        super(context, 0, animal);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Animal animal = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // View lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.animal, parent, false);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.animalPicture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.animalName);
            viewHolder.age = (TextView) convertView.findViewById(R.id.animalAge);
            viewHolder.personality = (TextView) convertView.findViewById(R.id.animalPersonality);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }
        else{
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object into the template view.
        viewHolder.name.setText(animal.getName());
        viewHolder.age.setText(animal.getAge());
        viewHolder.personality.setText(animal.getPersonality());
        String image = animal.getPicture();
        Picasso.get().load(image).resize(120,120).into(viewHolder.picture);


        // Return the completed view to render on screen
        return convertView;
    }

}
