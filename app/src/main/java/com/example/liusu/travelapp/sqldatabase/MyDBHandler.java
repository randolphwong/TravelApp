package com.example.liusu.travelapp.sqldatabase;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by junhaochiew on 6/11/2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=6;
    private static final String DATABASE_NAME="route.db";
    public static final String TABLE_LOCATIONS="locations";

    public static final String COLUMN_ID="_id";
    public static final String COLUMN_ROUTENAME="routename";
    public static final String COLUMN_LOCATION1NAME="location1name";
    public static final String COLUMN_LOCATION2NAME="location2name";
    public static final String COLUMN_LOCATIONXCOORDINATES ="locationxcoordinates";
    public static final String COLUMN_LOCATIONYCOORDINATES ="locationycoordinates";
    public static final String COLUMN_WALKTIME="walktime";
    public static final String COLUMN_BUSTIME="bustime";
    public static final String COLUMN_TAXITIME="taxitime";
    public static final String COLUMN_BUSCOST="buscost";
    public static final String COLUMN_TAXICOST="taxicost";
    public static final String COLUMN_LOCATIONDESCRIPTION="locationdescription";



    @Override
    public void onCreate(SQLiteDatabase db) {
        String query= "CREATE TABLE " + TABLE_LOCATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                COLUMN_ROUTENAME + " TEXT, " +
                COLUMN_LOCATION1NAME + " TEXT, " + COLUMN_LOCATION2NAME + " TEXT, " +
                COLUMN_LOCATIONXCOORDINATES + " TEXT, " +
                COLUMN_LOCATIONYCOORDINATES + " TEXT, "+ COLUMN_WALKTIME + " INTEGER, " +
                COLUMN_BUSTIME + " INTEGER, " + COLUMN_TAXITIME + " INTEGER, " +
                COLUMN_BUSCOST + " REAL, " + COLUMN_TAXICOST + " REAL, " +
                COLUMN_LOCATIONDESCRIPTION + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public void addLocations(DBRoute location){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ROUTENAME,location.get_Route());
        cv.put(COLUMN_LOCATION1NAME,location.get_location1Name());
        cv.put(COLUMN_LOCATION2NAME,location.get_location2Name());
        cv.put(COLUMN_LOCATIONXCOORDINATES,location.get_locationXCoordinates());
        cv.put(COLUMN_LOCATIONYCOORDINATES,location.get_locationYCoordinates());
        cv.put(COLUMN_WALKTIME, location.get_walkTime());
        cv.put(COLUMN_BUSTIME, location.get_busTime());
        cv.put(COLUMN_BUSCOST, location.get_busCost());
        cv.put(COLUMN_TAXITIME, location.get_taxiTime());
        cv.put(COLUMN_TAXICOST, location.get_taxiCost());
        cv.put(COLUMN_LOCATIONDESCRIPTION,location.get_locationDescription());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LOCATIONS,null,cv);
        db.close();
    }

    public void deleteLocation(long id){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_LOCATIONS, COLUMN_ID + "=" + id, null);
    }

    public String databaseToString(){
        String dbString="";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '" +TABLE_LOCATIONS + "' WHERE 1";

        Cursor c= db.rawQuery(query, null);

        c.moveToFirst();

        while (!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_ROUTENAME))!=null){
                dbString += c.getString(c.getColumnIndex(COLUMN_ROUTENAME)) + " ";
                dbString += c.getLong(c.getColumnIndex(COLUMN_LOCATIONXCOORDINATES)) + " ";
                dbString += c.getLong(c.getColumnIndex(COLUMN_LOCATIONYCOORDINATES)) + " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_LOCATIONDESCRIPTION)) + " ";
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    // returns detail of the route in String[] format, 0-columnid, 1-longcoord, 2-latcoord, 3-walktime, 4-bustime, 5-taxitime, 6-buscost, 7-taxicost, 8-description
    public String[] getRouteDetails(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID,COLUMN_ROUTENAME, COLUMN_LOCATIONXCOORDINATES,COLUMN_LOCATIONYCOORDINATES, COLUMN_WALKTIME,
                COLUMN_BUSTIME, COLUMN_TAXITIME, COLUMN_BUSCOST, COLUMN_TAXICOST, COLUMN_LOCATIONDESCRIPTION};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        long i=1;
        Cursor c = db.query(TABLE_LOCATIONS, columns, COLUMN_ROUTENAME +" =?", new String[]{route},null,null,null);
        String[] s = new String[]{null,"location do not exist in database",null,null,null,null,null,null,null};
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            s[0] = c.getString(0);
            s[1] = c.getString(2);
            s[2]=c.getString(3);
            s[3]=c.getString(4);
            s[4]=c.getString(5);
            s[5]=c.getString(6);
            s[6]=c.getString(7);
            s[7]=c.getString(8);
            s[8]=c.getString(9);

        }
        return s;
    }
    public String[] getRouteLongLat(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID,COLUMN_ROUTENAME, COLUMN_LOCATIONXCOORDINATES,COLUMN_LOCATIONYCOORDINATES};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        long i=1;
        Cursor c = db.query(TABLE_LOCATIONS, columns, COLUMN_ROUTENAME +" =?", new String[]{route},null,null,null);
        String[] s = new String[]{null,"location do not exist in database",null,null};
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            s[0] = c.getString(2);
            s[1] = c.getString(3);
        }
        return s;
    }

    public Double getBusRouteCost(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID,COLUMN_ROUTENAME, COLUMN_BUSCOST};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        Cursor c = db.query(TABLE_LOCATIONS, columns, COLUMN_ROUTENAME +" =?", new String[]{route},null,null,null);
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            return c.getDouble(2);
        }
        return null;
    }
    public Double getTaxiRouteCost(String location1, String location2){
        String[] columns = new String[]{COLUMN_ID,COLUMN_ROUTENAME, COLUMN_TAXICOST};
        SQLiteDatabase db = getWritableDatabase();
        String route = location1 + " to " + location2;
        Cursor c = db.query(TABLE_LOCATIONS, columns, COLUMN_ROUTENAME +" =?", new String[]{route},null,null,null);
        if(c!=null && !c.isAfterLast()){
            c.moveToFirst();
            return c.getDouble(2);
        }
        return null;
    }

    public void updateData(long id, String name, String name2, long coordx, long coordy, String walktime,
                           String bustime, String taxitime, String buscost, String taxicost, String desc){
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ROUTENAME, name + " to " + name2);
        cv.put(COLUMN_LOCATION1NAME, name);
        cv.put(COLUMN_LOCATION2NAME, name2);
        cv.put(COLUMN_LOCATIONXCOORDINATES, coordx);
        cv.put(COLUMN_LOCATIONYCOORDINATES,coordy);
        cv.put(COLUMN_WALKTIME, walktime);
        cv.put(COLUMN_BUSTIME, bustime);
        cv.put(COLUMN_TAXITIME, taxitime);
        cv.put(COLUMN_BUSCOST, buscost);
        cv.put(COLUMN_TAXICOST, taxicost);
        cv.put(COLUMN_LOCATIONDESCRIPTION, desc);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_LOCATIONS,cv,COLUMN_ID + "=" + id, null);

    }



    public  String[] searchDB(String location1, String location2){
        SQLiteDatabase db = getWritableDatabase();
        //String[] columns = new String[]{COLUMN_ID,COLUMN_ROUTENAME, COLUMN_LOCATIONXCOORDINATES,COLUMN_LOCATIONYCOORDINATES, COLUMN_LOCATIONDESCRIPTION};
        //Cursor c = db.query(TABLE_LOCATIONS, columns, COLUMN_ROUTENAME +" =?", new String[]{route},null,null,null);
        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_LOCATIONS+" WHERE " + COLUMN_ROUTENAME + " = '" + location1+" to "+location2 + "'", null);

        String[] dbString = new String[3];

        if(c.getCount()!=0){
            int i=0;
            c.moveToFirst();
            do {
                dbString[i] += c.getString(c.getColumnIndex(COLUMN_ROUTENAME)) + " ";
                dbString[i] += c.getLong(c.getColumnIndex(COLUMN_LOCATIONXCOORDINATES)) + " ";
                dbString[i] += c.getLong(c.getColumnIndex(COLUMN_LOCATIONYCOORDINATES)) + " ";
                dbString[i] += c.getString(c.getColumnIndex(COLUMN_LOCATIONDESCRIPTION)) + " ";

                i++;
            } while (c.moveToNext() && i<3);

            c.close();
            return dbString;
        }
        else {
            return null;
        }
    }
}




















