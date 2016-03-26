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
	 * �õ�һ���ɶ������ݿ�
	 * @return
	 */
	public SQLiteDatabase getDbReadable() {
		return dbReadable;
	}
	/**
	 * �õ�һ����д�����ݿ�
	 * @return
	 */
	public SQLiteDatabase getDbWritable() {
		return dbWritable;
	}
	
	/**
	 * ��ȡ����
	 */
	public void executeWriteMsg(String dateTimeStr, String note){
		ContentValues cv = new ContentValues();
		cv.put("DateTime", dateTimeStr);
		cv.put("Note", note);
		dbWritable.insert("Note", null, cv);
	}
	/**
	 * �����ݿ��е����ݶ�����
	 * @return �����ļ���
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
	 * ����һ����¼
	 * @param noteID Ҫ�����ռǵ�id
	 * @param dateTimeStr ��Ҫ���µ����ں�ʱ��
	 * @param note ��Ҫ���µ��ռ�����
	 */
	public void updateNote(int noteID, String dateTimeStr, String note){
		ContentValues cv = new ContentValues();
		cv.put("DateTime", dateTimeStr);
		cv.put("Note", note);
		dbWritable.update("Note", cv, "ID = ?", new String[]{noteID +""});
	}
	
	/**
	 * ����id��ѯ����
	 * @param noteID Ҫ��ѯ���ռǵ�id
	 * @return ����ѯ���Ľ��
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
	 * ����idɾ��������¼
	 * @param noteID Ҫɾ����¼��id
	 */
	public void deleteNote(int noteID){
		dbWritable.delete("Note", "ID = ?", new String[]{noteID +""});
	}
}
