package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.CartActivity;
import com.bypriyan.togocart.models.ModelCartitem;
import com.bypriyan.togocart.models.ModelOrderItems;
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.HolderCartItem> {

    public Context context;
    public ArrayList<ModelOrderItems> orderItemsArrayList;

    public AdapterOrderItem(Context context, ArrayList<ModelOrderItems> orderItemsArrayList) {
        this.context = context;
        this.orderItemsArrayList = orderItemsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false);

        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterOrderItem.HolderCartItem holder, int position) {

        ModelOrderItems modelCartitem = orderItemsArrayList.get(position);

        String getpId = modelCartitem.getpId();
        String pName = modelCartitem.getName();
        String totalCost = modelCartitem.getCost();
        String eachPrise = modelCartitem.getPrice();
        String quentity = modelCartitem.getQuantity();
        String pImg = modelCartitem.getpImg();
        String productQuentitys = modelCartitem.getpQuantity();

        try {
            Glide.with(context).load(pImg)
                    .centerInside().placeholder(R.drawable.togo_notification).into(holder.productImage);
        }catch (Exception e){
        }

        holder.productName.setText(""+pName);
        holder.totalPrise.setText("= ₹"+totalCost);
        holder.productQuentity.setText(""+quentity+"unit");
        holder.quentity.setText(""+productQuentitys);
        holder.sellingPrise.setText("₹"+eachPrise);

        holder.remove.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return orderItemsArrayList.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder {

        TextView productName, quentity, sellingPrise, productQuentity, totalPrise;
        ImageView productImage, remove;

        public HolderCartItem(@NonNull @NotNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quentity = itemView.findViewById(R.id.quentity);
            sellingPrise = itemView.findViewById(R.id.sellingPrise);
            productQuentity = itemView.findViewById(R.id.productQuentity);
            totalPrise = itemView.findViewById(R.id.totalPrise);
            remove = itemView.findViewById(R.id.imageMinus);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }

}
