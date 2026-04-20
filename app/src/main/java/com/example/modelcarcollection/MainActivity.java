package com.example.modelcarcollection;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.modelcarcollection.adapter.CarAdapter;
import com.example.modelcarcollection.database.CarDatabase;
import com.example.modelcarcollection.model.Car;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private CarAdapter carAdapter;
    private TextView statTotal, statValue, statBrands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statTotal = findViewById(R.id.stat_total);
        statValue = findViewById(R.id.stat_value);
        statBrands = findViewById(R.id.stat_brands);

        RecyclerView recyclerView = findViewById(R.id.recycler_cars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(new ArrayList<>());
        recyclerView.setAdapter(carAdapter);

        Button btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        });

        loadCars();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCars();
    }

    private void loadCars() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Car> cars = CarDatabase.getInstance(this).carDao().getAllCars();
            runOnUiThread(() -> {
                carAdapter.setCars(cars);
                updateStats(cars);
            });
        });
    }

    private void updateStats(List<Car> cars) {
        int total = cars.size();
        Set<String> brands = new HashSet<>();
        int totalValue = 0;

        for (Car car : cars) {
            if (car.brand != null) brands.add(car.brand);
            if (car.marketValue != null) {
                try {
                    String cleaned = car.marketValue.replaceAll("[^0-9\\-]", "");
                    if (cleaned.contains("-")) {
                        String[] parts = cleaned.split("-");
                        int low = Integer.parseInt(parts[0].trim());
                        int high = Integer.parseInt(parts[1].trim());
                        totalValue += (low + high) / 2;
                    } else if (!cleaned.isEmpty()) {
                        totalValue += Integer.parseInt(cleaned);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        statTotal.setText(String.valueOf(total));
        statValue.setText("$" + totalValue);
        statBrands.setText(String.valueOf(brands.size()));
    }
}