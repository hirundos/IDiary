package com.example.dttt.idiary.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dttt.idiary.Database.DBHelper;
import com.example.dttt.idiary.Database.DataContract;
import com.example.dttt.idiary.R;
import com.example.dttt.idiary.utill.CheckValid;
import com.example.dttt.idiary.utill.Encrypt;

import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    Encrypt encrypt;
    EditText mname;
    EditText memail;
    EditText mpassword;
    EditText mpassword_confirm;
    TextView messageView;
    Button regist_btn;
    CheckValid checkValid;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mname = (EditText) findViewById(R.id.regist_username);
        memail = (EditText) findViewById(R.id.regist_useremail);
        mpassword = (EditText) findViewById(R.id.regist_password);
        mpassword_confirm = (EditText) findViewById(R.id.password_confirm);
        messageView = (TextView) findViewById(R.id.regist_message);
        regist_btn = (Button) findViewById(R.id.regist_btn);
        checkValid = new CheckValid();
        encrypt = new Encrypt();
        dbHelper = new DBHelper(this);

        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkEmail = memail.getText().toString();
                if(idOverlap(checkEmail)) {
                    if(RegistDataChanged(checkEmail, mpassword.getText().toString(), mpassword_confirm.getText().toString())) {
                        try {
                            saveInformation();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RegistDataChanged(memail.getText().toString(), mpassword.getText().toString(), mpassword_confirm.getText().toString());
            }
        };
        memail.addTextChangedListener(afterTextChangedListener);
        mpassword.addTextChangedListener(afterTextChangedListener);
        mpassword_confirm.addTextChangedListener(afterTextChangedListener);

    }

    //아이디 중복확인
    private boolean idOverlap(String email) {
        Cursor cursor = null;
        sqLiteDatabase = dbHelper.getWritableDatabase();
        cursor = sqLiteDatabase.query(DataContract.RegistEntry.TABLE_NAME, null,
                DataContract.RegistEntry.COLUMN_NAME_USEREMAIL+" = '"+email+"'", null, null, null,null);
        if(cursor.getCount() >0){
            messageView.setText("해당 아이디가 이미 존재합니다.");
            return false;
        }
        return true;
    }

    //회원정보 추가
    private void saveInformation() throws NoSuchAlgorithmException {
        String name = mname.getText().toString();
        String email = memail.getText().toString();
        String password = mpassword.getText().toString();
        password = encrypt.sha256(password);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.RegistEntry.COLUMN_NAME_USERNAME,name);
        contentValues.put(DataContract.RegistEntry.COLUMN_NAME_USEREMAIL, email);
        contentValues.put(DataContract.RegistEntry.COLUMN_NAME_PASSWORD, password);

        sqLiteDatabase = dbHelper.getWritableDatabase();
        long newRowId = sqLiteDatabase.insert(DataContract.RegistEntry.TABLE_NAME, null, contentValues);
        if(newRowId == -1){
            Toast.makeText(this,"저장에 문제가 발생하였습니다.",Toast.LENGTH_SHORT).show();
        }else {
            Log.d(TAG, "저장이 완료되었습니다");
        }
        sqLiteDatabase.close();
        finish();
    }

    public boolean RegistDataChanged(String username, String password, String pwConfirm) {
        if (!checkValid.isUserNameValid(username)) {
            messageView.setText(R.string.invalid_username);
            return false;
        } else if (!checkValid.isPasswordValid(password)) {
            messageView.setText("비밀번호가 5자 미만입니다.");
            return false;
        } else if(!checkValid.isPasswordComfirm(password, pwConfirm)){
            messageView.setText("비밀번호가 일치하지 않습니다.");
            return false;
        } else {
            messageView.setText("");
            return true;
        }
    }

}
