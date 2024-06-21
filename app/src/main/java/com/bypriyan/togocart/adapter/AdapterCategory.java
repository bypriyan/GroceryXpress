package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.SearchByTypeActivity;
import com.bypriyan.togocart.models.ModelCategories;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCateg> {

    private Context context;
    private ArrayList<ModelCategories> categoriesArrayList;

    public AdapterCategory(Context context, ArrayList<ModelCategories> categoriesArrayList) {
        this.context = context;
        this.categoriesArrayList = categoriesArrayList;
    }


    @NonNull
    @NotNull
    @Override
    public HolderCateg onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_category, parent, false);
        return new HolderCateg(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterCategory.HolderCateg holder, int position) {
        ModelCategories modelCategories = categoriesArrayList.get(position);
        String imagesUrl = modelCategories.getImageUrl();
        String categTitle = modelCategories.getCategTitle();

        try {
            Glide.with(context).load(imagesUrl)
                    .centerInside().placeholder(R.drawable.togo_notification).into(holder.categoryImage);
        }catch (Exception e){
        }

        holder.categoryName.setText(categTitle);
        holder.itemView.setOnClickListener(v -> {
            starActiv(categTitle);
        });

    }

    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

    public void starActiv(String searchType){
        Intent intent = new Intent(context, SearchByTypeActivity.class);
        intent.putExtra("searchType", searchType);
        context.startActivity(intent);
    }
    public class HolderCateg extends RecyclerView.ViewHolder{

        TextView categoryName;
        ImageView categoryImage;

        public HolderCateg(@NonNull @NotNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }

}
