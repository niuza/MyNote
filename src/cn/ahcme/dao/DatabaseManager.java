package cn.ahcme.dao;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ahcme.bean.Note;

public class DatabaseManager {
	@SuppressWarnings("unused")
	private Context context = null;
	private DatabaseOpenHelper databaseOpenHelper = null;
	private SQLiteDatabase dbReadable = null;
	private SQLiteDatabase dbWritable = null;
	public DatabaseManager(Context context) {
		this.context = context;
		databaseOpenHelper = new DatabaseOpenHelper(context);
		databaseOpenHelper.getWritableDatabase();
		dbReadable = databaseOpenHelper.getReadableDatabase();
		dbWritable = databaseOpenHelper.getWritableDatabase();
	}
	/**
	 * 得到一个可读的数据库
	 * @return
	 */
	public SQLiteDatabase getDbReadable() {
		return dbReadable;
	}
	/**
	 * 得到一个可写的数据库
	 * @return
	 */
	public SQLiteDatabase getDbWritable() {
		return dbWritable;
	}
	
	/**
	 * 存取数据
	 */
	public void executeWriteMsg(String dateTimeStr, String note){
		ContentValues cv = new ContentValues();
		cv.put("DateTime", dateTimeStr);
		cv.put("Note", note);
		dbWritable.insert("Note", null, cv);
	}
	/**
	 * 将数据库中的数据读出来
	 * @return 读到的集合
	 */
	public void readData(List<Note> noteList){
		Cursor cursor 
		= dbReadable.rawQuery("SELECT * FROM Note ORDER BY DateTime DESC", null);
		try{
			while(cursor.moveToNext()){
				Note note = new Note();
				note.setID(cursor.getInt(cursor.getColumnIndex("ID")));
				note.setDateTimeStr(cursor.getString(cursor.getColumnIndex("DateTime")));
				note.setNoteData(cursor.getString(cursor.getColumnIndex("Note")));
				noteList.add(note);
			}
		}catch(Exception e){
		}
	}
	
	/**
	 * 更新一条记录
	 * @param noteID 要更新日记的id
	 * @param dateTimeStr 所要更新的日期和时间
	 * @param note 所要更新的日记内容
	 */
	public void updateNote(int noteID, String dateTimeStr, String note){
		ContentValues cv = new ContentValues();
		cv.put("DateTime", dateTimeStr);
		cv.put("Note", note);
		dbWritable.update("Note", cv, "ID = ?", new String[]{noteID +""});
	}
	
	/**
	 * 根据id查询数据
	 * @param noteID 要查询的日记的id
	 * @return 所查询到的结果
	 */
	public Note readData(int noteID){
		
		Cursor cursor = dbReadable.rawQuery("SELECT * FROM Note WHERE ID = ?", new String[]{noteID+""});
		cursor.moveToFirst();
		Note note = new Note();
		note.setID(cursor.getInt(cursor.getColumnIndex("ID")));
		note.setDateTimeStr(cursor.getString(cursor.getColumnIndex("DateTime")));
		note.setNoteData(cursor.getString(cursor.getColumnIndex("Note")));
		return note;
	}
	
	/**
	 * 根据id删除该条记录
	 * @param noteID 要删除记录的id
	 */
	public void deleteNote(int noteID){
		dbWritable.delete("Note", "ID = ?", new String[]{noteID +""});
	}
}
