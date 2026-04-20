package com.example.modelcarcollection.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.modelcarcollection.model.Car;

@Dao
public interface CarDao {

    @Insert
    void insert(Car car);

    @Query("SELECT * FROM cars ORDER BY dateAdded DESC")
    List<Car> getAllCars();

    @Query("SELECT COUNT(*) FROM cars")
    int getCarCount();

    @Query("DELETE FROM cars WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM cars WHERE id = :id")
    Car getCarById(int id);

    @Update
    void update(Car car);

    @Delete
    void delete(Car car);
}