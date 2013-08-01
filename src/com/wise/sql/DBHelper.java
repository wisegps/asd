package com.wise.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper{
	private static final int VERSION = 2;
	private static final String DB_NAME = "wise_unicom";
	//≥µ¡æ–≈œ¢
	private static final String CREATE_CAR_INFO = "create table wise_unicom_zwc(_id integer primary key autoincrement,serial text,obj_name text,sim text,accessory text,is_sound boolean,is_start text,is_lockdoor boolean,phone text)";
	public DBHelper(Context context){
		super(context,DB_NAME,null,VERSION);
	}
	public DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CAR_INFO);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1){
			db.execSQL("drop table if exists wise_unicom");
		}
		this.onCreate(db);
	}
}