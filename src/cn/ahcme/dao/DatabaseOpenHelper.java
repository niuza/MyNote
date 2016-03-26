package cn.ahcme.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static String dbName = "Note";
	private static int dbVersion = 4;

	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public DatabaseOpenHelper(Context context) {
		super(context, dbName, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String dbStr = "CREATE TABLE Note("
				+ "ID integer PRIMARY KEY AUTOINCREMENT," 
				+ "DateTime text," 
				+ "Note text)";
		db.execSQL(dbStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS Note");
		onCreate(db);
	}

}
