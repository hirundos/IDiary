package com.example.dttt.idiary.Database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DataContract {
    private DataContract(){

    }

    public static class DiaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "tb_diary";
        public static final String COLUMN_NAME_TITLE ="title";
        public static final String COLUMN_NAME_CONTENTS = "contents";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static class RegistEntry implements BaseColumns {
        public static final String TABLE_NAME = "tb_register";
        public static final String COLUMN_NAME_USEREMAIL = "email";
        public static final String COLUMN_NAME_USERNAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }
}
