package com.example.se2_exploding_kittens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.Network.TCP.Lobby;

import java.util.ArrayList;

public class Lobby_RecyclerViewAdapter extends RecyclerView.Adapter<Lobby_RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Lobby> lobbyList;
    private Context context;

    public Lobby_RecyclerViewAdapter(Context context, ArrayList<Lobby> lobbyList){
        this.context = context;
        this.lobbyList = lobbyList;
    }

    @NonNull
    @Override
    public Lobby_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.text_row_item, parent, false);
        return new Lobby_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Lobby_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.textViewName.setText(lobbyList.get(position).getName());
        holder.textViewAddress.setText(lobbyList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return lobbyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewAddress;
        TextView textViewName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}
