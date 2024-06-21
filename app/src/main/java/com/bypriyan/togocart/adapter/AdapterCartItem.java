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
import com.bypriyan.togocart.utilities.Constant;
import com.bypriyan.togocart.utilities.preferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    public Context context;
    public ArrayList<ModelCartitem> cartitems;
    private preferenceManager preferenceManager;

    public AdapterCartItem(Context context, ArrayList<ModelCartitem> cartitems) {
        this.context = context;
        this.cartitems = cartitems;
        preferenceManager = new preferenceManager(context);
    }

    @NonNull
    @NotNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false);

        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterCartItem.HolderCartItem holder, int position) {

        ModelCartitem modelCartitem = cartitems.get(position);
        String id = modelCartitem.getId();
        String getpId = modelCartitem.getpId();
        String pName = modelCartitem.getName();
        String totalCost = modelCartitem.getCost();
        String eachPrise = modelCartitem.getPrice();
        String quentity = modelCartitem.getQuentity();
        String pImg = modelCartitem.getpImg();
        String productQuentitys = modelCartitem.getpQuentity();

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

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId= 1;
                itemId = Integer.parseInt(preferenceManager.getString(Constant.KEY_ITEM_ID));

                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("ITEM_ID", new String[]{"text", "unique"}))
                        .addColumn(new Column("ITEM_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_NAME", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_PRISE_EACH", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_PRISE", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_QUENTITY", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_P_IMG", new String[]{"text", "not null"}))
                        .addColumn(new Column("ITEM_P_QUENTITY", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();

                itemId--;
                preferenceManager.putString(Constant.KEY_ITEM_ID,""+itemId);

                cartitems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx = Double.parseDouble((((CartActivity)context).allTotalPriseTv.getText().toString().trim().replace("₹","")));
                double totalPrise = tx - Double.parseDouble(totalCost);
                double deliveryFee = 40.0;
                double sTotalPrice = Double.parseDouble(String.format("%.2f", totalPrise)) - Double.parseDouble(String.format("%.2f", deliveryFee));

                ((CartActivity)context).allTotalPrise = 0.00;
                ((CartActivity)context).sTotalTv.setText("₹"+String.format("%.2f", sTotalPrice));
                ((CartActivity)context).totalCostTv.setText("₹"+String.format("%.2f", sTotalPrice));
                ((CartActivity)context).allTotalPriseTv.setText("₹"+String.format("%.2f", Double.parseDouble(String.format("%.2f", totalPrise))));

                if(((CartActivity)context).cartCount()<=0){
                    preferenceManager.putString(Constant.KEY_ITEM_ID,""+1);
                    ((CartActivity)context).deleteCartData();
                    ((CartActivity)context).onBackPressed();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartitems.size();
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
