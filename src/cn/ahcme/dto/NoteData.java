package cn.ahcme.dto;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import cn.ahcme.bean.Note;
import cn.ahcme.dao.DatabaseManager;

/**
 * 保存日记的数据
 * @author Administrator kang
 *
 */
public class NoteData {
	/**保存日记数据的集合**/
	private List<Note> noteDataList = new ArrayList<Note>();
	private Context context = null;
	private DatabaseManager dm = null;
	public NoteData(Context context) {
		this.context = context;
		dm =  new DatabaseManager(context);
		dm.readData(noteDataList);
	}
	/**
	 * 得到日记记录
	 * @return
	 */
	public List<Note> getNoteDataList() {
		return noteDataList;
	}
	
	/**
	 * 重新从数据库中读取数据
	 */
	public void refreshNoteData(){
		noteDataList.clear();
		dm.readData(noteDataList);
	}
}
