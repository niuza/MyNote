package cn.ahcme.global;

import java.util.List;

import android.app.Application;
import cn.ahcme.bean.Note;

public class MyApplication extends Application {
	private List<Note> noteDataList = null;

	public List<Note> getNoteDataList() {
		return noteDataList;
	}

	public void setNoteDataList(List<Note> noteDataList) {
		this.noteDataList = noteDataList;
	}

}
