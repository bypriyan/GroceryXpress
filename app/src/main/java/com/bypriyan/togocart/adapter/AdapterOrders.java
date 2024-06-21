package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.OrdeDetailsActivity;
import com.bypriyan.togocart.models.ModelOrders;
import com.bypriyan.togocart.models.ModelProducts;
import com.bypriyan.togocart.utilities.Constant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.HolderOrders>  {

    public Context context;
    public ArrayList<ModelOrders> ordersArrayList;

    public AdapterOrders(Context context, ArrayList<ModelOrders> ordersArrayList) {
        this.context = context;
        this.ordersArrayList = ordersArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderOrders onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_orders, parent, false);
        return new HolderOrders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterOrders.HolderOrders holder, int position) {
        ModelOrders modelOrders = ordersArrayList.get(position);

        String orderId = modelOrders.getOrderId();
        String OrderNumber = modelOrders.getOrderNumber();
        String area= modelOrders.getArea();
        String city = modelOrders.getCity();
        String mobileNumber= modelOrders.getMobileNumber();
        String orderBy = modelOrders.getOrderBy();
        String orderCost = modelOrders.getOrderCost();
        String orderTime = modelOrders.getOrderTime();
        String orderStatus= modelOrders.getOrderStatus();
        String phoneNumber = modelOrders.getPhoneNumber();
        String pinCode= modelOrders.getPinCode();
        String propertyName = modelOrders.getPropertyName();
        String propertyLocation = modelOrders.getPropertyLocation();
        String reciverName = modelOrders.getReciverName();
        String state = modelOrders.getState();
        String paymentStatus = modelOrders.getPaymentStatus();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(orderTime));
        String dateTime = DateFormat.format("dd MMM yyyy hh:mm aa", cal).toString();

        holder.orderStatus.setText(orderStatus);
        holder.orderNumber.setText("Order No: "+OrderNumber);
        holder.dateTime.setText(dateTime);
        holder.totalAmount.setText("₹"+orderCost);
        holder.paymentMode.setText(paymentStatus);
        holder.reciverName.setText(reciverName);
        holder.reciverAddress.setText(propertyName+", "+propertyLocation+", "+area+", "+city);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrdeDetailsActivity.class);
                intent.putExtra(Constant.KEY_TIMESTAMP, orderId);
                intent.putExtra("fromActivity","otherActivity");
                intent.putExtra(Constant.KEY_ORDERSTATUS, orderStatus);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersArrayList.size();
    }

    public class HolderOrders extends RecyclerView.ViewHolder {

        TextView orderStatus, orderNumber, dateTime, totalItems,
                totalAmount, paymentMode, reciverName, reciverAddress;

        public HolderOrders(@NonNull @NotNull View itemView) {
            super(itemView);

            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            dateTime = itemView.findViewById(R.id.dateTime);
            totalItems = itemView.findViewById(R.id.totalItems);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            paymentMode = itemView.findViewById(R.id.paymentMode);
            reciverName = itemView.findViewById(R.id.reciverName);
            reciverAddress = itemView.findViewById(R.id.reciverAddress);
        }
    }

}
