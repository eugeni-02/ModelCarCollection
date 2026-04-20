package com.example.modelcarcollection.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cars")
public class Car {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;           // e.g. "Ferrari 250 GTO"
    public String brand;          // e.g. "Hot Wheels"
    public String scale;          // e.g. "1:64"
    public String year;           // e.g. "1962" (year of the real car)
    public String color;          // e.g. "Red"
    public String yearOfRelease;  // e.g. "2003" (year the model was released)
    public String marketValue;    // e.g. "$340"
    public String trend;          // e.g. "+12%"
    public long dateAdded;        // timestamp
    public String imageUrl;
    public String imageQuery;
}