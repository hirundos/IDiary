package com.example.dttt.idiary.utill;

import android.util.Patterns;

public class CheckValid {

    // A placeholder username validation check
    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        if(password == null){
            return false;
        }
        if(password.trim().length() < 5){
            return false;
        }
        return true;
    }

    public boolean isPasswordComfirm(String pw, String pw_confirm){
        if(pw.equals(pw_confirm)){
            return true;
        }
        return false;
    }
}
