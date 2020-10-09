package com.example.dttt.idiary.ui.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dttt.idiary.utill.CheckValid;
import com.example.dttt.idiary.Database.DBHelper;
import com.example.dttt.idiary.Database.DataContract;
import com.example.dttt.idiary.MainActivity;
import com.example.dttt.idiary.R;
import com.example.dttt.idiary.utill.Encrypt;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private static final int STORTAGE_PERMISSION_CODE = 1001;
    CheckValid checkValid;
    EditText musername;
    EditText mpassword;
    TextView mlogin_message;
    Button mlogin_btn;
    Button mregist_btn;
    Intent intent;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    Cursor cursor;
    Encrypt encrypt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        musername = (EditText) findViewById(R.id.username);
        mpassword = (EditText) findViewById(R.id.password);
        mlogin_message = (TextView) findViewById(R.id.login_message);
        mlogin_btn = (Button) findViewById(R.id.login_btn);
        mregist_btn = (Button) findViewById(R.id.register_btn);
        checkValid = new CheckValid();
        encrypt = new Encrypt();
        dbHelper = new DBHelper(this);

        PermissionCheckFun();

        mlogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idCheck(musername.getText().toString())){
                    try {
                        if(passwordCheck(musername.getText().toString(), mpassword.getText().toString())){
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email",musername.getText().toString());
                            startActivity(intent);
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mregist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginDataChanged(musername.getText().toString(), mpassword.getText().toString());
            }
        };
        musername.addTextChangedListener(afterTextChangedListener);
        mpassword.addTextChangedListener(afterTextChangedListener);

    }
    public void loginDataChanged(String username, String password) {
        if (!checkValid.isUserNameValid(username)) {
            mlogin_message.setText("이메일 형식이 아닙니다");
        } else if (!checkValid.isPasswordValid(password)) {
            mlogin_message.setText("비밀번호가 5자 미만입니다.");
        } else {
            mlogin_message.setText("");
        }
    }

    private boolean idCheck(String email) {
        cursor = null;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursor = sqLiteDatabase.query(DataContract.RegistEntry.TABLE_NAME, null,
                DataContract.RegistEntry.COLUMN_NAME_USEREMAIL+" = '"+email+"'", null, null, null,null);
        if(!(cursor.getCount() >0)){
            mlogin_message.setText("존재하지 않는 아이디입니다");
            return false;
        }
        return true;
    }

    private boolean passwordCheck(String email, String password) throws NoSuchAlgorithmException {
        cursor = null;
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursor = sqLiteDatabase.query(DataContract.RegistEntry.TABLE_NAME, null,
                DataContract.RegistEntry.COLUMN_NAME_USEREMAIL+" = '"+email+"'", null, null, null,null);
        cursor.moveToNext();
        String localPw = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.RegistEntry.COLUMN_NAME_PASSWORD));

        password = encrypt.sha256(password);

        if(password.equals(localPw)){
            return true;
        }else {
            mlogin_message.setText("비밀번호가 틀렸습니다.");
            return false;
        }

        }

    private void PermissionCheckFun(){
        int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORTAGE_PERMISSION_CODE);
            }
        }
    }

}
