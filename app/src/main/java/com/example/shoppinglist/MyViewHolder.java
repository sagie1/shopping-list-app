package com.example.shoppinglist;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    View v;
    public MyViewHolder(View itemView){
        super(itemView);
        v = itemView;
    }
    public void setType(String type){
        TextView mytype =(TextView) v.findViewById(R.id.type);
        mytype.setText(type);
    }
    public void setNote(String note){
        TextView mynote =(TextView) v.findViewById(R.id.note);
        mynote.setText(note);
    }

    public void setDate(String date){
        TextView myDate =(TextView) v.findViewById(R.id.date);
        myDate.setText(date);
    }

    public void setAmount(double amount){
        TextView myAmount = (TextView)v.findViewById(R.id.amount);
        String stam = String.valueOf(amount);
        myAmount.setText(stam);
    }
}