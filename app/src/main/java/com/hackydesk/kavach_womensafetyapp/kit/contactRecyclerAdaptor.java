package com.hackydesk.kavach_womensafetyapp.kit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.hackydesk.kavach_womensafetyapp.R;


import java.util.ArrayList;

public class contactRecyclerAdaptor extends RecyclerView.Adapter<contactRecyclerAdaptor.ViewHolder>{

    ArrayList<contactModel> contacts;
    Context context;
    Activity activity;
    public contactRecyclerAdaptor(Context context, ArrayList<contactModel> contacts , Activity activity)
    {
        this.context = context;
        this.contacts = contacts;
        this.activity = activity;
    }

    @NonNull
    @Override
    public contactRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(context).inflate(R.layout.contact_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull contactRecyclerAdaptor.ViewHolder holder, int position) {
        holder.name.setText(contacts.get(position).name);
        holder.number.setText(contacts.get(position).number);
        holder.imageView.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},323);
            }
            else
            {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contacts.get(position).number));
                context.startActivity(callIntent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            number = itemView.findViewById(R.id.contatc_number);
            imageView = itemView.findViewById(R.id.dial_number);
        }
    }
}
