package com.example.dttt.idiary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dttt.idiary.Database.DBHelper;
import com.example.dttt.idiary.Database.DataContract;
import com.example.dttt.idiary.ui.login.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DiaryDetailActivity extends AppCompatActivity {

    final static int REQUEST_CODE = 0;
    private static final String TAG = DiaryDetailActivity.class.getSimpleName();
    int mDiaryId = -1;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText mtitle;
    EditText mcontents;
    ImageView mimageview;
    TextView textDate;
    String imageUrl, useremail;
    String date;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        dbHelper = new DBHelper(DiaryDetailActivity.this);
        mtitle = (EditText) findViewById(R.id.add_title);
        mcontents = (EditText) findViewById(R.id.add_content);
        mimageview = (ImageView) findViewById(R.id.add_image);
        textDate = (TextView) findViewById(R.id.add_date);
        imageUrl = null;
        date = LocalDate.now().format(formatter);
        textDate.setText(date);

        //넘어온 데이터 저장 및 적용
        Intent intent = getIntent();
        if(intent != null){
            mDiaryId = intent.getIntExtra("id",-1);
            String title = intent.getStringExtra("title");
            String contents = intent.getStringExtra("contents");
            useremail = intent.getStringExtra("email");
            imageUrl = intent.getStringExtra("image");
            date = intent.getStringExtra("date");
            mtitle.setText(title);
            mcontents.setText(contents);

            if(date!= null){
                textDate.setText(date);
            }

            if(imageUrl != null){
                Uri iuri = Uri.parse(imageUrl);
                setImageSizeBig(iuri);
            }
        }

        //이미지 추가하기
        mimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionDeniedFun();
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(imageIntent, REQUEST_CODE);
            }
        });

        //이미지 삭제
        mimageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryDetailActivity.this);
                builder.setMessage("이미지를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileDelete(imageUrl);
                        setImageSizeSmall();
                    }
                });
                builder.setNegativeButton("취소",null);
                builder.show();

                return false;
            }
        });


        textDate.setOnClickListener(new View.OnClickListener() {
            LocalDate todayDate;

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                todayDate = LocalDate.parse(textDate.getText(), formatter);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DiaryDetailActivity.this, dateSetListener,
                        todayDate.getYear(),todayDate.getMonthValue(),todayDate.getDayOfMonth());
                datePickerDialog.show();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            LocalDate today = LocalDate.of(year, month, dayOfMonth);
            textDate.setText(today.format(formatter));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_delete:
                deleteDiary();
                break;
            case R.id.menu_save:
                saveDiary();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    imageUrl = saveBitmaptoJpeg(img);
                    setImageSizeBig(Uri.parse(imageUrl));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "사진 선택 취소");
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DiaryDetailActivity.this);
        builder.setMessage("확인 버튼을 누르시면 현재 다이어리를 저장하지 않고 이전 화면으로 이동합니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(imageUrl!=null){
                    fileDelete(imageUrl);
                }
                DiaryDetailActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }



    public void deleteDiary(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DiaryDetailActivity.this);
        builder.setTitle("삭제");
        builder.setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sqLiteDatabase = dbHelper.getWritableDatabase();

                //파일 경로 알아오기
                Cursor cursorT = sqLiteDatabase.query(DataContract.DiaryEntry.TABLE_NAME, null,
                        DataContract.DiaryEntry._ID+" = '"+mDiaryId+"'", null, null, null,null);
                cursorT.moveToNext();
                String path = cursorT.getString(cursorT.getColumnIndexOrThrow(DataContract.DiaryEntry.COLUMN_NAME_IMAGE));
                fileDelete(path);

                //메모 삭제
                int deleteCount = sqLiteDatabase.delete(DataContract.DiaryEntry.TABLE_NAME,
                        DataContract.DiaryEntry._ID+" = "+mDiaryId,null);
                if(deleteCount == 0){
                    Toast.makeText(DiaryDetailActivity.this, "삭제에 문제가 발생했습니다.",Toast.LENGTH_SHORT).show();
                }else {

                    Log.d(TAG, "삭제가 완료되었습니다.");
                    sqLiteDatabase.close();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        builder.setNegativeButton("취소",null);
        builder.show();
    }

    public void saveDiary(){
        String title = mtitle.getText().toString();
        String contents = mcontents.getText().toString();
        String ldate = textDate.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.DiaryEntry.COLUMN_NAME_TITLE, title);
        contentValues.put(DataContract.DiaryEntry.COLUMN_NAME_CONTENTS, contents);
        contentValues.put(DataContract.DiaryEntry.COLUMN_NAME_IMAGE, imageUrl);
        contentValues.put(DataContract.DiaryEntry.COLUMN_NAME_EMAIL, useremail);
        contentValues.put(DataContract.DiaryEntry.COLUMN_NAME_DATE, ldate);

        sqLiteDatabase = dbHelper.getWritableDatabase();

        //새 글 작성
        if(mDiaryId == -1){
            long newRowId = sqLiteDatabase.insert(DataContract.DiaryEntry.TABLE_NAME,null,contentValues);

            if(newRowId == -1){
                Toast.makeText(DiaryDetailActivity.this,"저장에 문제가 발생하였습니다.",Toast.LENGTH_SHORT).show();
            }else {
                Log.d(TAG, "저장이 완료되었습니다.");
                setResult(RESULT_OK);
            }

            //글 수정
        }else {
            int count = sqLiteDatabase.update(DataContract.DiaryEntry.TABLE_NAME, contentValues,
                    DataContract.DiaryEntry._ID+" = "+mDiaryId ,null);
            if(count == 0){
                Toast.makeText(DiaryDetailActivity.this, "수정에 문제가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }else {
                Log.d(TAG, "수정이 완료되었습니다");
                setResult(RESULT_OK);
            }
        }

        sqLiteDatabase.close();
        finish();
    }

    public String saveBitmaptoJpeg(Bitmap bitmap){
        String storagePath = this.getFilesDir().getAbsolutePath();
        String TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Diary"+TimeStamp;

        try{
            FileOutputStream out = openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(this.getFileStreamPath(fileName));
    }

    public void fileDelete(String filePath){
        try{
            File file = new File(filePath);
            //파일이 존재하는지 체크
            if(file.exists()){
                file.delete();
                Log.d(TAG,"파일 삭제 완료");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setImageSizeBig(Uri uri){
        mimageview.getLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
        mimageview.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
        mimageview.setAdjustViewBounds(true);
        mimageview.requestLayout();
        mimageview.setImageURI(uri);
    }

    public void setImageSizeSmall(){
        imageUrl = null;
        mimageview.getLayoutParams().width = WindowManager.LayoutParams.WRAP_CONTENT;
        mimageview.getLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
        mimageview.requestLayout();
        mimageview.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_box_black_24dp));
    }

    private void PermissionDeniedFun() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            android.app.AlertDialog.Builder localBuilder = new android.app.AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    .setMessage("권한 거절로 인해 일부 기능이 제한됩니다.")
                    .setPositiveButton("권한설정하러 가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package :"+getPackageName()));
                                startActivity(intent);
                            }catch (ActivityNotFoundException e){
                                e.printStackTrace();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
        }
    }
}
