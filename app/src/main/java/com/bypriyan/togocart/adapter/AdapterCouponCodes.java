package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.SavedAddressActivity;
import com.bypriyan.togocart.models.ModelCoupons;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterCouponCodes extends RecyclerView.Adapter<AdapterCouponCodes.HolderCouponCodes> {

    public Context context;
    public ArrayList<ModelCoupons> couponsArrayList;
    public String isShowApply;

    public AdapterCouponCodes(Context context, ArrayList<ModelCoupons> couponsArrayList, String isShowApply) {
        this.context = context;
        this.couponsArrayList = couponsArrayList;
        this.isShowApply = isShowApply;
    }

    @NonNull
    @NotNull
    @Override
    public HolderCouponCodes onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_coupon_codes, parent, false);
        return new HolderCouponCodes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterCouponCodes.HolderCouponCodes holder, int position) {
        ModelCoupons modelCoupons = couponsArrayList.get(position);

        String couponId = modelCoupons.getCouponId();
        String couponCode = modelCoupons.getCouponCode();
        String couponDescription = modelCoupons.getCouponDescription();
        String discountPrice = modelCoupons.getCouponDiscount();
        String minimumOrderPrice = modelCoupons.getCouponMinimumOrder();
        String couponTimeUse = modelCoupons.getCouponTimeUsage();

        holder.discountCoupenCode.setText(couponCode);
        if(isShowApply.equals("true")){
            holder.applyBtn.setVisibility(View.VISIBLE);
            holder.applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isValid(minimumOrderPrice)){
                        if(couponTimeUse.equals("Multitime Use")){
                            int discountPer = Integer.parseInt(discountPrice);
                            double orderTotalAmount = Double.parseDouble(((SavedAddressActivity)context).orderTotalAmountFees);
                            double discountPrise = Math.ceil(orderTotalAmount*discountPer/100);
                            String totalAmountAterDiscount = String.valueOf(orderTotalAmount-discountPrise);
                            ((SavedAddressActivity)context).binding.couponDiscount.setText("-₹"+discountPrise);
                            ((SavedAddressActivity)context).binding.allTotalPriseTv.setText("₹"+totalAmountAterDiscount);
                            ((SavedAddressActivity)context).binding.couponCode.setText(couponCode + " " + "applied");
                            ((SavedAddressActivity)context).binding.couponCodeSaved.setText("You saved ₹" + discountPrise + " with this coupon");
                            ((SavedAddressActivity)context).couponCodeId = couponId;
                            ((SavedAddressActivity)context).alertDialog.dismiss();
                        }else if(couponTimeUse.equals("Single time use")){
                            checkCouponIsUsed(couponId, couponCode, couponDescription, discountPrice, minimumOrderPrice);
                        }
                    }

                }
            });
        }else{
            holder.applyBtn.setVisibility(View.GONE);
        }

        holder.discriptionCoupn.setText(couponDescription);

    }

    private void checkCouponIsUsed(String id, String couponCode, String couponDescription, String couponDiscountPercentage, String couponMinimumOrder) {
        String uid = FirebaseAuth.getInstance().getUid().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COUPON_CODES).child(id).child(Constant.KEY_COUPON_USED_USERS);

        Query query = reference.orderByChild(Constant.KEY_UID).equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(context, "You already used this Coupon Code", Toast.LENGTH_LONG).show();
                }else{
                        int discountPer = Integer.parseInt(couponDiscountPercentage);
                        double orderTotalAmount = Double.parseDouble(((SavedAddressActivity)context).orderTotalAmountFees);
                        double discountPrise = Math.ceil(orderTotalAmount*discountPer/100);
                        String totalAmountAterDiscount = String.valueOf(orderTotalAmount-discountPrise);
                        ((SavedAddressActivity)context).binding.couponDiscount.setText("-₹"+discountPrise);
                        ((SavedAddressActivity)context).binding.allTotalPriseTv.setText("₹"+totalAmountAterDiscount);
                        ((SavedAddressActivity)context).binding.couponCode.setText(couponCode + " " + "applied");
                        ((SavedAddressActivity)context).binding.couponCodeSaved.setText("You saved ₹" + discountPrise + " with this coupon");
                        ((SavedAddressActivity)context).couponCodeId = id;
                        ((SavedAddressActivity)context).alertDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private boolean isValid(String minimumOrderPrice) {

        String totalAmount = ((SavedAddressActivity) context).binding.sTotalTv.getText().toString().replace("₹", "");
        Double totalPrise = Double.parseDouble(totalAmount);
        Double minOrderAmountPrise = Double.parseDouble(minimumOrderPrice);
        if (totalPrise >= minOrderAmountPrise) {
            return true;
        } else {
            Toast.makeText(context, "Minimum cart total amount to apply this coupon code is " + minimumOrderPrice, Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public int getItemCount() {
        return couponsArrayList.size();
    }

    public class HolderCouponCodes extends RecyclerView.ViewHolder {

        TextView discriptionCoupn, discountCoupenCode, applyBtn;

        public HolderCouponCodes(@NonNull @NotNull View itemView) {
            super(itemView);

            discriptionCoupn = itemView.findViewById(R.id.discriptionCoupn);
            discountCoupenCode = itemView.findViewById(R.id.discountCoupenCode);
            applyBtn = itemView.findViewById(R.id.applyBtn);
        }
    }

}
