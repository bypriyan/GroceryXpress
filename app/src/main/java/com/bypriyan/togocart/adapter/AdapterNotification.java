package com.bypriyan.togocart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bypriyan.togocart.R;
import com.bypriyan.togocart.models.ModelCoupons;
import com.bypriyan.togocart.models.ModelNotifications;
import com.bypriyan.togocart.utilities.Constant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.HolderNotification> {

    public Context context;
    public ArrayList<ModelNotifications> notificationsArrayList;

    public AdapterNotification(Context context, ArrayList<ModelNotifications> notificationsArrayList) {
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
    }


    @NonNull
    @NotNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notifications, parent, false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterNotification.HolderNotification holder, int position) {
        ModelNotifications modelNotifications = notificationsArrayList.get(position);
        String message = modelNotifications.getMessage();
        String id = modelNotifications.getMessageReplyId();
        String isRead = modelNotifications.getIsSeen();

        holder.message.setText(message);

        if(isRead.equals("true")){
            holder.readBtn.setVisibility(View.GONE);
        }else{
            holder.readBtn.setVisibility(View.VISIBLE);
        }

        holder.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.readBtn.setVisibility(View.GONE);
                setIsReadFalse(id);
            }
        });
    }

    private void setIsReadFalse(String id) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_IS_SEEN,"true");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_MESSAGE_REPLY);
        reference.child(id).updateChildren(hashMap);
    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public class HolderNotification extends RecyclerView.ViewHolder{
        TextView message, readBtn;
        public HolderNotification(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            readBtn = itemView.findViewById(R.id.readBtn);

        }
    }

}
