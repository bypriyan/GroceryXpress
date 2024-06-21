package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.togocart.R;
import com.bypriyan.togocart.filter.FilterProducts;
import com.bypriyan.togocart.filter.FilterSearchProduct;
import com.bypriyan.togocart.models.ModelOrderItems;
import com.bypriyan.togocart.models.ModelProducts;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterSearchItem extends RecyclerView.Adapter<AdapterSearchItem.HolderCartItem> implements Filterable {

    public Context context;
    public ArrayList<ModelProducts> orderItemsArrayList;
    public ArrayList<ModelProducts> productsListFilter;
    private FilterSearchProduct filter;

    public AdapterSearchItem(Context context, ArrayList<ModelProducts> orderItemsArrayList) {
        this.context = context;
        this.orderItemsArrayList = orderItemsArrayList;
        this.productsListFilter = orderItemsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_search_product, parent, false);

        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterSearchItem.HolderCartItem holder, int position) {

        ModelProducts modelCartitem = orderItemsArrayList.get(position);

        String getpId = modelCartitem.getProductId();
        String pName = modelCartitem.getProductName();
        String pImg = modelCartitem.getProductImg();
        String productQuentitys = modelCartitem.getProductQuantity();

        try {
            Glide.with(context).load(pImg)
                    .centerInside().placeholder(R.drawable.togo_notification).into(holder.productImage);
        }catch (Exception e){
        }

        holder.productName.setText(""+pName);
        holder.quentity.setText(""+productQuentitys);

    }

    @Override
    public int getItemCount() {
        return orderItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterSearchProduct(this, productsListFilter);
        }
        return filter;
    }

    class HolderCartItem extends RecyclerView.ViewHolder {

        TextView productName, quentity;
        ImageView productImage;

        public HolderCartItem(@NonNull @NotNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quentity = itemView.findViewById(R.id.quentity);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }

}
