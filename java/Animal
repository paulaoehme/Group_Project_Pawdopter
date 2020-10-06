package com.myapp.pawdopter;

import android.os.Parcel;
import android.os.Parcelable;


public class Animal implements Parcelable {

    private String name;
    private String age;
    private String category;
    private String breed;
    private String gender;
    private String size;
    private String chipNumber;
    private String personality;
    private String picture;
    private String shelter;

    public Animal(String name, String age, String category, String breed, String gender, String size, String chipNumber, String personality, String picture, String shelter) {

        this.name = name;
        this.age = age;
        this.category = category;
        this.breed = breed;
        this.gender = gender;
        this.size = size;
        this.chipNumber = chipNumber;
        this.personality = personality;
        this.picture = picture;
        this.shelter = shelter;
    }
    public Animal(){

    }

    protected Animal(Parcel in){
        name = in.readString();
        age = in.readString();
        category = in.readString();
        breed = in.readString();
        gender = in.readString();
        size = in.readString();
        chipNumber = in.readString();
        personality = in.readString();
        picture = in.readString();
        shelter = in.readString();
    }

    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel source) {
            return new Animal(source);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChipNumber() {
        return chipNumber;
    }

    public void setChipNumber(String chipNumber) {
        this.chipNumber = chipNumber;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", breed='" + breed + '\'' +
                ", gender='" + gender + '\'' +
                ", size='" + size + '\'' +
                ", chipNumber='" + chipNumber + '\'' +
                ", personality='" + personality + '\'' +
                ", shelter='" + shelter + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(category);
        dest.writeString(breed);
        dest.writeString(gender);
        dest.writeString(size);
        dest.writeString(chipNumber);
        dest.writeString(personality);
        dest.writeString(picture);
        dest.writeString(shelter);
    }
}
