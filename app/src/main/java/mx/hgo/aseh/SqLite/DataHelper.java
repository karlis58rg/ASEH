package mx.hgo.aseh.SqLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataHelper extends SQLiteOpenHelper {
    public static final String DataBase_Name = "AppAseh";
    public static final String Table_Aseh = "CatCategoria";
    public static final int Database_Version = 1;
    public static final String Create_Aseh="CREATE TABLE IF NOT EXISTS " + Table_Aseh +"(id INTEGER PRIMARY KEY AUTOINCREMENT, asehCargo TEXT NOT NULL UNIQUE)";
    public static final String Delete_Aseh = "DROP TABLE IF EXISTS "+Table_Aseh;

    public DataHelper(Context context){
        super(context,DataBase_Name,null,Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Aseh);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Delete_Aseh);
        onCreate(db);
    }
    public void insertCargo(String asehCargo){
        SQLiteDatabase dbSqLiteDatabase = this.getWritableDatabase();
        dbSqLiteDatabase.beginTransaction();
        ContentValues values;
        try {
            values = new ContentValues();
            values.put("asehCargo",asehCargo);
            dbSqLiteDatabase.insert(Table_Aseh,null,values);
            dbSqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){e.printStackTrace();}
        finally {
            dbSqLiteDatabase.endTransaction();
            dbSqLiteDatabase.close();
        }
    }
    public ArrayList<String> getAllCargo(){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase dbDatabase = this.getReadableDatabase();
        dbDatabase.beginTransaction();
        try{
            String selectQuery = "SELECT * FROM " + Table_Aseh;
            Cursor cursor = dbDatabase.rawQuery(selectQuery, null);
            if(cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    String asehCargo = cursor.getString(cursor.getColumnIndex("asehCargo"));
                    list.add(asehCargo);
                }
            }
            dbDatabase.setTransactionSuccessful();
        }catch (Exception e) {e.printStackTrace();}
        finally {
            {
                dbDatabase.endTransaction();
                dbDatabase.close();
            }
        }
        return  list;
    }
}
