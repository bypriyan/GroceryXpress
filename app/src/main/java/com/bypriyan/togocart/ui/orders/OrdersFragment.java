package com.bypriyan.togocart.ui.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.activity.SearchByTypeActivity;
import com.bypriyan.togocart.adapter.AdapterOrders;
import com.bypriyan.togocart.adapter.AdapterProductsGrid;
import com.bypriyan.togocart.databinding.FragmentHomeBinding;
import com.bypriyan.togocart.databinding.FragmentOrdersBinding;
import com.bypriyan.togocart.models.ModelOrders;
import com.bypriyan.togocart.models.ModelProducts;
import com.bypriyan.togocart.utilities.CartItems;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrders> ordersArrayList;
    private CartItems cartItems;
    private int cartItemCount=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrdersBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        //
        cartItems = new CartItems(getActivity());
        cartItemCount = cartItems.loadCartItemsCount();

        loadOrders();

        checkCartItems(cartItemCount);

        return binding.getRoot();

    }

    public void checkCartItems(int cartItemCount){
        if(cartItemCount<=0){
            binding.above.setVisibility(View.GONE);
        }else{
            binding.above.setVisibility(View.VISIBLE);
        }

    }

    private void loadOrders() {
        ordersArrayList = new ArrayList<>();
        String authId = firebaseAuth.getCurrentUser().getUid().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_ORDERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ordersArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String orderBy = ""+ds.child(Constant.KEY_ORDERBY).getValue().toString();
                    ModelOrders modelOrders = ds.getValue(ModelOrders.class);
                    if(authId.equals(orderBy))
                        ordersArrayList.add(modelOrders);
                }
                binding.recyclearOrders.setAdapter(new AdapterOrders(getActivity(), ordersArrayList));

                if(ordersArrayList.size()<=0){
                    binding.noOrderLayout.setVisibility(View.VISIBLE);
                    binding.recyclearOrders.setVisibility(View.GONE);
                }else{
                    binding.noOrderLayout.setVisibility(View.GONE);
                    binding.recyclearOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    
}