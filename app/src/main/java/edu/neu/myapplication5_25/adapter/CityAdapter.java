package edu.neu.myapplication5_25.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.model.City;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private List<City> cities;
    private OnCityClickListener listener;

    public interface OnCityClickListener {
        void onCityClick(City city);
    }

    public CityAdapter(List<City> cities) {
        this.cities = cities;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<City> newCities) {
        this.cities = newCities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cities.get(position);
        holder.bind(city);
    }

    @Override
    public int getItemCount() {
        return cities != null ? cities.size() : 0;
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        private TextView textCityName;
        private TextView textProvince;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            textCityName = itemView.findViewById(R.id.text_city_name);
            textProvince = itemView.findViewById(R.id.text_province);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCityClick(cities.get(getAdapterPosition()));
                }
            });
        }

        public void bind(City city) {
            textCityName.setText(city.getName());
            textProvince.setText(city.getProvince());
        }
    }
} 