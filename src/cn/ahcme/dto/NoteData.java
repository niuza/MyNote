package cn.ahcme.dto;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import cn.ahcme.bean.Note;
import cn.ahcme.dao.DatabaseManager;

/**
 * �����ռǵ�����
 * @author Administrator kang
 *
 */
public class NoteData {
	/**�����ռ����ݵļ���**/
	private List<Note> noteDataList = new ArrayList<Note>();
	private Context context = null;
	private DatabaseManager dm = null;
	public NoteData(Context context) {
		this.context = context;
		dm =  new DatabaseManager(context);
		dm.readData(noteDataList);
	}
	/**
	 * �õ��ռǼ�¼
	 * @return
	 */
	public List<Note> getNoteDataList() {
		return noteDataList;
	}
	
	/**
	 * ���´����ݿ��ж�ȡ����
	 */
	public void refreshNoteData(){
		noteDataList.clear();
		dm.readData(noteDataList);
	}
}
