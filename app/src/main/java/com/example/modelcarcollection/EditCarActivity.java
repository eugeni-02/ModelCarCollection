package com.example.modelcarcollection;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.modelcarcollection.database.CarDatabase;
import com.example.modelcarcollection.model.Car;
import java.io.File;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import java.io.IOException;

public class EditCarActivity extends AppCompatActivity {

    private EditText editName, editBrand, editScale, editYear, editColor, editValue;
    private ImageView carImage;
    private int carId;
    private Car currentCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);

        editName = findViewById(R.id.edit_name);
        editBrand = findViewById(R.id.edit_brand);
        editScale = findViewById(R.id.edit_scale);
        editYear = findViewById(R.id.edit_year);
        editColor = findViewById(R.id.edit_color);
        editValue = findViewById(R.id.edit_value);
        carImage = findViewById(R.id.edit_car_image);

        carId = getIntent().getIntExtra("car_id", -1);
        if (carId == -1) {
            finish();
            return;
        }

        loadCar();

        findViewById(R.id.btn_save).setOnClickListener(v -> saveCar());
        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteCar());
    }

    private void loadCar() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentCar = CarDatabase.getInstance(this).carDao().getCarById(carId);
            runOnUiThread(() -> {
                if (currentCar != null) {
                    editName.setText(currentCar.name);
                    editBrand.setText(currentCar.brand);
                    editScale.setText(currentCar.scale);
                    editYear.setText(currentCar.year);
                    editColor.setText(currentCar.color);
                    editValue.setText(currentCar.marketValue);
                    if (currentCar.imageUrl != null && !currentCar.imageUrl.isEmpty()) {
                        File imgFile = new File(currentCar.imageUrl);
                        if (imgFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(currentCar.imageUrl);
                            try {
                                ExifInterface exif = new ExifInterface(currentCar.imageUrl);
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                Matrix matrix = new Matrix();
                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_90: matrix.postRotate(90); break;
                                    case ExifInterface.ORIENTATION_ROTATE_180: matrix.postRotate(180); break;
                                    case ExifInterface.ORIENTATION_ROTATE_270: matrix.postRotate(270); break;
                                }
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            carImage.setImageBitmap(bitmap);
                        }
                    }
                }
            });
        });
    }

    private void saveCar() {
        if (currentCar == null) return;
        currentCar.name = editName.getText().toString().trim();
        currentCar.brand = editBrand.getText().toString().trim();
        currentCar.scale = editScale.getText().toString().trim();
        currentCar.year = editYear.getText().toString().trim();
        currentCar.color = editColor.getText().toString().trim();
        currentCar.marketValue = editValue.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {
            CarDatabase.getInstance(this).carDao().update(currentCar);
            runOnUiThread(() -> {
                Toast.makeText(this, "Car updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteCar() {
        Executors.newSingleThreadExecutor().execute(() -> {
            CarDatabase.getInstance(this).carDao().delete(currentCar);
            runOnUiThread(() -> {
                Toast.makeText(this, "Car deleted", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}