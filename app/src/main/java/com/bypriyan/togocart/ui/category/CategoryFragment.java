package com.bypriyan.togocart.ui.category;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.togocart.activity.SearchByTypeActivity;
import com.bypriyan.togocart.adapter.AdapterCategory;
import com.bypriyan.togocart.adapter.AdapterCouponCodes;
import com.bypriyan.togocart.databinding.FragmentCategoryBinding;
import com.bypriyan.togocart.models.ModelCategories;
import com.bypriyan.togocart.models.ModelCoupons;
import com.bypriyan.togocart.showCart.ShowCategories;
import com.bypriyan.togocart.utilities.CartItems;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private ArrayList<ModelCoupons> couponsArrayList;
    private CartItems cartItems;
    private int cartItemCount = 0;
    private ArrayList<ModelCategories> categoriesArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(getLayoutInflater());

        cartItems = new CartItems(getActivity());
        cartItemCount = cartItems.loadCartItemsCount();
        categoriesArrayList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        if (cartItemCount <= 0) {
            binding.above.setVisibility(View.GONE);
        } else {
            binding.above.setVisibility(View.VISIBLE);
        }

        executorService.execute(new Runnable() {
            public void run() {
                ShowCategories showCategories = new ShowCategories(categoriesArrayList);
                int i=showCategories.getCategoriesArraylist().size();
                //
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (i > 0) {
                            binding.recyclearCategories.setAdapter(new AdapterCategory(getActivity(), categoriesArrayList));
                        }
                    }
                });
                //
            }
        });

        executorService.shutdown();

        loadCouponCodes();

        return binding.getRoot();

    }

    private void loadCouponCodes() {
        couponsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_COUPON_CODES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                couponsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCoupons modelCoupons = ds.getValue(ModelCoupons.class);
                    couponsArrayList.add(modelCoupons);
                }
                binding.recyclearCouponCodes.setAdapter(new AdapterCouponCodes(getContext(), couponsArrayList, "false"));

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void starActiv(String searchType) {
        Intent intent = new Intent(getContext(), SearchByTypeActivity.class);
        intent.putExtra("searchType", searchType);
        startActivity(intent);
    }

}