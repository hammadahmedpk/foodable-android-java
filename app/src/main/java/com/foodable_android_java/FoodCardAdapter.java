package com.foodable_android_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodCardAdapter extends RecyclerView.Adapter<FoodCardAdapter.myViewHolder>  {
    List<FoodCardModel> ls;
    Context c;
    public FoodCardAdapter(List<FoodCardModel> ls, Context c) {
        this.ls = ls;
        this.c = c;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(c).inflate(R.layout.card_row,parent,false);
        return new myViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.itemName.setText(ls.get(position).getItemName());
        holder.donnerDistance.setText(ls.get(position).getItemDistance());
        holder.donnerName.setText(ls.get(position).getDonate_Name());
        Glide.with(c).load(ls.get(position).getDonate_Img()).circleCrop().into(holder.donnerProfileImg);
        Glide.with(c).load(ls.get(position).getItemImg()).into(holder.itemImg);
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg, donnerProfileImg;
        TextView itemName,donnerName, donnerDistance;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            itemName = itemView.findViewById(R.id.itemName);
            donnerName = itemView.findViewById(R.id.userName);
            donnerProfileImg= itemView.findViewById(R.id.userImg);
            donnerDistance = itemView.findViewById(R.id.distance);
        }
    }

}
