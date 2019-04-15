package com.nishant.tictactoeonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "tic_tac_toe_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "local_data";
    private static final String PLAYER_NUMBER = "player_number";
    private static final String GAME_NUMBER = "game_number";
    private static final String WASTE = "waste";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            PLAYER_NUMBER + " INTEGER, " +
            GAME_NUMBER + " INTEGER, " +
            WASTE + " INTEGER);";
    private static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME;
    SQLiteDatabase reader;
    int gameNumber;
    int playerNumber;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void newPlayer()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GAME_NUMBER, -1);
        contentValues.put(PLAYER_NUMBER, -1);
        contentValues.put(WASTE, 0);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updatePlayer(int gameNumber, int playerNumber)
    {
        String whereClause = WASTE + "=0;";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GAME_NUMBER, gameNumber);
        contentValues.put(PLAYER_NUMBER, playerNumber);
        contentValues.put(WASTE, 0);
        sqLiteDatabase.update(TABLE_NAME, contentValues, whereClause, null);
    }

    public int getGameNumber()
    {
        getterHelper();
        return gameNumber;
    }

    public int getPlayerNumber()
    {
        getterHelper();
        return playerNumber;
    }

    public void getterHelper()
    {
        reader = this.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(cursor.moveToNext())
        {
            do {
                gameNumber = cursor.getInt(cursor.getColumnIndex(GAME_NUMBER));
                playerNumber = cursor.getInt(cursor.getColumnIndex(PLAYER_NUMBER));
            }while(cursor.moveToNext());
        }
    }
}
