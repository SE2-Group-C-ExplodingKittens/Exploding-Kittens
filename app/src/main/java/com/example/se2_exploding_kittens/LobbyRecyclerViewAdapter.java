package com.example.se2_exploding_kittens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_exploding_kittens.Network.LobbyLogic.JoinLobbyCallback;
import com.example.se2_exploding_kittens.Network.LobbyLogic.Lobby;

import java.util.ArrayList;

public class LobbyRecyclerViewAdapter extends RecyclerView.Adapter<LobbyRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Lobby> lobbyList;
    private Context context;
    private JoinLobbyCallback joinLobbyCallback;

    public LobbyRecyclerViewAdapter(Context context, ArrayList<Lobby> lobbyList, JoinLobbyCallback joinLobbyCallback){
        this.context = context;
        this.lobbyList = lobbyList;
        this.joinLobbyCallback = joinLobbyCallback;
    }

    @NonNull
    @Override
    public LobbyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.text_row_item, parent, false);
        return new LobbyRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.textViewName.setText(lobbyList.get(position).getName());
        holder.textViewAddress.setText(lobbyList.get(position).getAddress());
        holder.buttonJoinLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    joinLobbyCallback.JoinLobby(lobbyList.get(holder.getAdapterPosition()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    holder.buttonJoinLobby.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lobbyList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewAddress;
        TextView textViewName;
        Button buttonJoinLobby;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewName = itemView.findViewById(R.id.textViewName);
            buttonJoinLobby = itemView.findViewById(R.id.buttonJoinLobby);
        }
    }
}
