package com.example.relate.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.relate.ChatActivity;
import com.example.relate.R;
import com.example.relate.util.Match;

import java.util.List;

public class MatchRecyclerAdapter extends RecyclerView.Adapter<MatchRecyclerAdapter.ViewHolder> {
    Context context;
    List<Match> mMatchList;

    public MatchRecyclerAdapter(Context context, List<Match> mMatchList) {
        this.context = context;
        this.mMatchList = mMatchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_match_user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mName.setText(mMatchList.get(position).getName());
        Glide.with(context).load(mMatchList.get(position).getImg_url()).into(holder.mImg);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("doc_id",mMatchList.get(holder.getAdapterPosition()).getUser_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        ImageView mImg;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            mName = itemView.findViewById(R.id.match_name);
            mImg = itemView.findViewById(R.id.match_img);
        }
    }
}
