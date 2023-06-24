package com.example.diy_project_interface_app.Communication.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diy_project_interface_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice item);
    }

    List<BluetoothDevice> items;

    Context context;

    OnItemClickListener listener;
    public BluetoothDeviceAdapter(Context context, Set<BluetoothDevice> set, OnItemClickListener listener) {
        if(set != null)
            this.items = new ArrayList<>(set);
        else
            this.items = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bluetooth_devices, parent, false);
        return new BluetoothViewHolder(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {
        BluetoothDevice item = items.get(position);
        if(item.getName() != null)
            holder.textView.setText(item.getName());
        else
            holder.textView.setText(item.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

class BluetoothViewHolder extends RecyclerView.ViewHolder{
//TODO

    TextView textView;
    private BluetoothDeviceAdapter adapter;


    public BluetoothViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.bt_name);

        //TODO connect
    }

    public BluetoothViewHolder linkAdapter(BluetoothDeviceAdapter adapter){
        this.adapter = adapter;
        return this;
    }
}