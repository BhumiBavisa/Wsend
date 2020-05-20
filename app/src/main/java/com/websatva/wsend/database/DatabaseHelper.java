package com.websatva.wsend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.websatva.wsend.model.Model;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static String DATABASE = "database.db";
    public static String TABLE ="mytable";
    public static String NAME ="name";
    public static String NUMBER ="number";

    String br;

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, 1);

    }


    //==============Create table in sqlite database=========
    @Override
    public void onCreate(SQLiteDatabase db) {
         //  br= "CREATE TABLE mytable(name TEXT,company TEXT,city TEXT,country TEXT);";
         br = "CREATE TABLE "+TABLE+"("+NAME+ " Text, "+NUMBER+ " Text);";
        db.execSQL(br);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS "+TABLE+" ;");
    }

//=========This method used to insert data into database table=========
    public void insertdata(String name,String number){
        System.out.print("Hello "+br);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();


            contentValues.put(NAME, name);
            contentValues.put(NUMBER, number);

            db.insert(TABLE,null,contentValues);


    }

    //Get all data from sqlite database

    public List<Model> getdata(){
       // DataModel dataModel = new DataModel();
        List<Model> data=new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE+" ;",null);
         StringBuffer stringBuffer = new StringBuffer();
        Model dataModel = null;
        while (cursor.moveToNext()) {
            dataModel= new Model();
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));

            dataModel.setName(name);
            dataModel.setNumber(number);

            stringBuffer.append(dataModel);
           // stringBuffer.append(dataModel);
            data.add(dataModel);
        }

        for (Model mo:data ) {

            Log.i("Hellomo",""+mo.getName());
        }

        //

        return data;
    }



}
