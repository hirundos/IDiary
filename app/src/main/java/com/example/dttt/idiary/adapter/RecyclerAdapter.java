package com.example.dttt.idiary.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dttt.idiary.Database.DataContract;
import com.example.dttt.idiary.MainActivity;
import com.example.dttt.idiary.R;

public class RecyclerAdapter extends CursorRecyclerViewAdapter<RecyclerAdapter.ViewHolder>  {

    private MyRecyclerViewClickListener mRecyclerViewClickListener;

    public RecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public interface MyRecyclerViewClickListener{
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(MyRecyclerViewClickListener listener){
        mRecyclerViewClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor, int position) {
        holder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_TITLE)));
        holder.contents.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_CONTENTS)));
        holder.date.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_DATE)));

        String photo = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_IMAGE));
        if(photo == null){
            holder.image.setImageResource(R.drawable.img_basic);
        }else {
            holder.image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_IMAGE))));
        }

        if(mRecyclerViewClickListener != null){
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerViewClickListener.onItemClicked(pos);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView contents;
        ImageView image;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_text);
            contents = itemView.findViewById(R.id.contents_text);
            image = itemView.findViewById(R.id.image_diary);
            date = itemView.findViewById(R.id.date_text);
        }
    }


}
