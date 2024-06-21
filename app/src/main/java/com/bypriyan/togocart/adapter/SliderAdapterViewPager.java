package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.SearchByTypeActivity;
import com.bypriyan.togocart.showCart.SliderItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SliderAdapterViewPager extends RecyclerView.Adapter<SliderAdapterViewPager.SliderViewPagerViewHolder>{

    private ArrayList<SliderItems> sliderImages;
    private ViewPager2 viewPager2;
    private Context context;
    private ArrayList<String> clickData;

    public SliderAdapterViewPager(ArrayList<SliderItems> sliderImages, ViewPager2 viewPager2, Context context) {
        this.sliderImages = sliderImages;
        this.viewPager2 = viewPager2;
        this.context = context;
        clickData = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public SliderViewPagerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SliderViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slider_item_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SliderAdapterViewPager.SliderViewPagerViewHolder holder, int position) {
       SliderItems sliderItems = sliderImages.get(position);
       String images = sliderItems.getImages();
        try {
            Glide.with(context).load(images)
                    .centerInside().placeholder(R.drawable.togo_notification).into(holder.imageView);
        }catch (Exception e){
        }

        if(position == sliderImages.size()-2){
            viewPager2.post(runnable);
        }

        try {
            loadData(clickData);
        }catch (Exception e){ }

        holder.imageView.setOnClickListener(v -> {
            if(position==0 || position ==5 || position ==10){
                if(isValid(clickData.get(0))) startActive(clickData.get(0));
            }else if(position==1|| position ==6|| position ==11){
                if(isValid(clickData.get(1))) startActive(clickData.get(1));
            }else if(position==2|| position ==7|| position ==12){
                if(isValid(clickData.get(2))) startActive(clickData.get(2));
            }else if(position==3|| position ==8|| position ==13){
                if(isValid(clickData.get(3))) startActive(clickData.get(3));
            } else if(position==4|| position ==8|| position ==14){
                if(isValid(clickData.get(4))) startActive(clickData.get(4));
            }
        });

    }

    @Override
    public int getItemCount() {
        return sliderImages.size();
    }

    private boolean isValid(String s){
        if(s==null){
            return false;
        }else{
            return true;
        }
    }

    private void startActive(String s){
        Intent intent = new Intent(context, SearchByTypeActivity.class);
        intent.putExtra("searchType", s);
        context.startActivity(intent);
    }

    private void loadData(ArrayList<String> clickData) {
        clickData.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Clicks");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                clickData.add(""+snapshot.child("1").getValue().toString());
                clickData.add(""+snapshot.child("2").getValue().toString());
                clickData.add(""+snapshot.child("3").getValue().toString());
                clickData.add(""+snapshot.child("4").getValue().toString());
                clickData.add(""+snapshot.child("5").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public class SliderViewPagerViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        SliderViewPagerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderImages.addAll(sliderImages);
            notifyDataSetChanged();
        }
    };

}
