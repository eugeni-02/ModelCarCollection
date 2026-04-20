package com.example.modelcarcollection.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.modelcarcollection.model.Car;

@Database(entities = {Car.class}, version = 2)
public abstract class CarDatabase extends RoomDatabase {

    public abstract CarDao carDao();

    private static CarDatabase instance;

    public static synchronized CarDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CarDatabase.class,
                    "car_database"
            ).addMigrations(MIGRATION_1_2).build();
        }
        return instance;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE cars ADD COLUMN imageUrl TEXT");
            database.execSQL("ALTER TABLE cars ADD COLUMN imageQuery TEXT");
        }
    };
}