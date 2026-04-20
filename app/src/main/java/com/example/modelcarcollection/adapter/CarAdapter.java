package com.example.modelcarcollection.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import com.example.modelcarcollection.EditCarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.modelcarcollection.R;
import com.example.modelcarcollection.model.Car;
import com.example.modelcarcollection.utils.ImageUtils;
import android.graphics.Bitmap;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Car> cars;

    public CarAdapter(List<Car> cars) {
        this.cars = cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = cars.get(position);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditCarActivity.class);
            intent.putExtra("car_id", car.id);
            v.getContext().startActivity(intent);
        });
        holder.name.setText(car.name);
        holder.meta.setText(car.year + " · " + car.brand + " · " + car.color);
        holder.scale.setText(car.scale);
        holder.brand.setText(car.brand);
        holder.yearOfRelease.setText(car.yearOfRelease);
        holder.value.setText(car.marketValue);
        holder.trend.setText(car.trend);
        if (car.imageUrl != null && !car.imageUrl.isEmpty()) {
            Bitmap bitmap = ImageUtils.loadCorrectedBitmap(car.imageUrl);
            if (bitmap != null) {
                holder.carImage.setImageBitmap(bitmap);
            } else {
                holder.carImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.carImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return cars != null ? cars.size() : 0;
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView name, meta, scale, brand, yearOfRelease, value, trend;
        android.widget.ImageView carImage;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.car_name);
            meta = itemView.findViewById(R.id.car_meta);
            scale = itemView.findViewById(R.id.car_scale);
            brand = itemView.findViewById(R.id.car_brand);
            yearOfRelease = itemView.findViewById(R.id.car_year_of_release);
            value = itemView.findViewById(R.id.car_value);
            trend = itemView.findViewById(R.id.car_trend);
            carImage = itemView.findViewById(R.id.car_image);
        }
    }
}