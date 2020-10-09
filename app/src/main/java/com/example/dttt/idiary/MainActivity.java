package com.example.dttt.idiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.example.dttt.idiary.Database.DBHelper;
import com.example.dttt.idiary.Database.DataContract;
import com.example.dttt.idiary.adapter.RecyclerAdapter;
import com.example.dttt.idiary.model.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RecyclerAdapter.MyRecyclerViewClickListener {

    private static final int REQUEST_CODE_INSERT = 1000;
    private DBHelper mDbHelper;
    private ArrayList<Diary> mDatalist;
    private Diary mrecipe;
    private Cursor mCursor;
    private RecyclerAdapter adapter;
    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        useremail = intent.getStringExtra("email");

        mDbHelper = new DBHelper(this);
        mDatalist = new ArrayList<Diary>();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mCursor = mDbHelper.getReadableDatabase().query(DataContract.DiaryEntry.TABLE_NAME,
                null,DataContract.DiaryEntry.COLUMN_NAME_EMAIL + " = '"+useremail+"'",
                null,null,null,DataContract.DiaryEntry._ID+" DESC");
        adapter = new RecyclerAdapter(this, mCursor);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
                intent.putExtra("email",useremail);
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });
    }

    private Cursor getRecipeCursor(){
        mDbHelper = new DBHelper(this);
        return mDbHelper.getReadableDatabase().query(DataContract.DiaryEntry.TABLE_NAME,
                null,DataContract.DiaryEntry.COLUMN_NAME_EMAIL + " = '"+useremail+"'",
                null,null,null,DataContract.DiaryEntry._ID+" DESC");
    }
    @Override
    public void onItemClicked(int position) {
        Cursor cursor = (Cursor)adapter.getItem(position);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(
                        DataContract.DiaryEntry._ID
        ));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                DataContract.DiaryEntry.COLUMN_NAME_TITLE
        ));
        String contents = cursor.getString(cursor.getColumnIndexOrThrow(
                DataContract.DiaryEntry.COLUMN_NAME_CONTENTS
        ));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(
                DataContract.DiaryEntry.COLUMN_NAME_IMAGE
        ));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(
                DataContract.DiaryEntry.COLUMN_NAME_DATE
        ));

        Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("title",title);
        intent.putExtra("contents",contents);
        intent.putExtra("image", image);
        intent.putExtra("date",date);
        intent.putExtra("email", useremail);
        startActivityForResult(intent, REQUEST_CODE_INSERT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INSERT && resultCode == RESULT_OK){
            adapter.swapCursor(getRecipeCursor());
        }
    }
}
