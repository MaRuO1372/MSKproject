package com.sasa.mskrpoject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuizDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quiz.db";
    private static final int DB_VERSION = 2;

    public QuizDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QuizContact.QuizEntry.CREATE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QuizContact.QuizEntry.DROP_COMMAND);
        onCreate(db);
    }
}
