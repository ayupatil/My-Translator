package com.example.mytranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {
    //Db Version
    private static final int Db_Version=1;
    //Db Name
    private static final String Db_Name="MyTranslator";
    //table name
    private static final String Table_Name="Content";
    //Creating mycontacts Columns
    private static final String cid="cid";
    private static final String original_content="original_content";
    private static final String translated_content="translated_content";

    //constructor here
    public DbHandler(Context context)
    {
        super(context,Db_Name,null,Db_Version);
    }

    //creating table
    @Override
    public void onCreate(SQLiteDatabase db) {
        // writing command for sqlite to create table with required columns
        String Create_Table="CREATE TABLE " + Table_Name + "(" + cid
                + " INTEGER PRIMARY KEY," + original_content + " TEXT," + translated_content + " TEXT" + ")";
        db.execSQL(Create_Table);
    }
    //Upgrading the Db
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop table if exists
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        //create the table again
        onCreate(db);
    }
    //Add new User by calling this method
    public void addContent(Content c)
    {

            // getting db instance for writing the user
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(original_content, c.getOriginal_content());
            cv.put(translated_content, c.getTranslated_content());
            //inserting row
            db.insert(Table_Name, null, cv);
            //close the database to avoid any leak
            db.close();

    }
    public int checkContent(Content c)
    {
        int id=-1;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT cid FROM Content WHERE original_content=?",new String[]{c.getOriginal_content()});
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            id=cursor.getInt(0);
            cursor.close();
        }
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from Content",null);
        return  res;
    }

    public void delete(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + Table_Name + " WHERE " + cid + "= " + id;
        db.execSQL(query);
        db.close();
    }



}
