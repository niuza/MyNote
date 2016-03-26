package cn.ahcme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ahcme.adapter.SimpleInfoAdapter;
import cn.ahcme.dao.DatabaseManager;
import cn.ahcme.dto.NoteData;

import com.ahcme.mynote.R;
/**
 * 主界面，显示日记列表
 * @author Administrator kang qq:2733791207 
 *
 */
public class MainActivity extends Activity {
	
	/**用于显示日记的数据**/
	private ListView lvNoteDataShow = null;
	private NoteData noteData = null;
	private SimpleInfoAdapter adaShowNoteData = null;
	/**跳转到写笔记的界面的按钮**/
	private ImageButton btnAddNewNote = null;
	/**隐藏的头部布局**/
	private RelativeLayout rlTop = null;
	/**隐藏在底部的布局,用于删除操作**/
	private RelativeLayout rlBottomDel = null;
	/**是否在进行批量删除操作*/
	private boolean isMassDeleteOpera = false;
	/**已经有多少个item被选中**/
	private int hasCheckedSum = 0;
	/**一共有多少个item**/
	private int hasItemSum = 0;
	/**选择按钮，置为全选或者全不选**/
	private Button btnChoice = null;
	/**取消按钮，取消批量操作**/
	private Button btnCancle = null;
	/**用于数据库操作*/
	private DatabaseManager dm = null;
	/**用于计算两次按返回键的时间差，进行退出操作*/
	private long oldTime = 0;
	/**规定两次按返回键的时间**/
	private static final int EXIT_TIME = 2000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		dm = new DatabaseManager(this);
		initComonment();
	}
	/***
	 * listview长按监听器，可以进行批量删除
	 * @author Administrator
	 *
	 */
	private class EditNoteLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			if(!isMassDeleteOpera){
				chanageToAllNoteChecked();
				judegeChecked();
				showMassDeleteOpera();
			}
			return true;
		}

	}
	private class EditNoteClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if(isMassDeleteOpera){
				if(adaShowNoteData.isPosChecked[position]){
					adaShowNoteData.isPosChecked[position] = false;
				}else{
					adaShowNoteData.isPosChecked[position] = true;
				}
				adaShowNoteData.notifyDataSetChanged();
			}else{
				SimpleInfoAdapter.ViewHolder viewHolder 
					= (SimpleInfoAdapter.ViewHolder)view.getTag();
				String noteId = viewHolder.tvNoteID.getText().toString().trim();
				toEditNoteActivity(Integer.parseInt(noteId));
			}
			
		}
	}
	
	/***
	 * 初始化界面上的组件
	 */
	private void initComonment(){
		lvNoteDataShow = (ListView) findViewById(R.id.activity_main_lv_note_data);
		noteData = new NoteData(this);
		hasItemSum = noteData.getNoteDataList().size();
		adaShowNoteData = new SimpleInfoAdapter(this, noteData.getNoteDataList());
		lvNoteDataShow.setAdapter(adaShowNoteData);
		//长按监听，用于批量操作
		lvNoteDataShow.setOnItemLongClickListener(new EditNoteLongClickListener());
		//单击事件，跳转到编辑日记的界面，用于修改该条日记
		lvNoteDataShow.setOnItemClickListener(new EditNoteClickListener());
		btnAddNewNote 
			= (ImageButton)findViewById(R.id.activity_main_btn_add_new_note);
		btnAddNewNote.setOnClickListener(new AddNewNoteListener());
		initHiddenComponent();
	}
	
	/***
	 * 初始化隐藏布局
	 */
	private void initHiddenComponent(){
		//初始化隐藏的布局
		rlTop = (RelativeLayout)findViewById(R.id.activity_main_hidden_top);
		rlBottomDel = (RelativeLayout)findViewById(R.id.activity_main_hidden_bottom);
		btnChoice = (Button)findViewById(R.id.activity_main_top_hidden_btn_choice);
		btnCancle = (Button)findViewById(R.id.activity_main_top_hidden_btn_cancel);
		btnChoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(hasCheckedSum == hasItemSum && hasItemSum != 0){
					//如果全被选中，则置为全不选中
					chanageToAllNoteChecked();
				}else{
					changeToAllChecked();
				}
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hiddenMassDeleteOpera();
			}
		});
		rlBottomDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteCheckedItem();
				noteData.refreshNoteData();
				adaShowNoteData.notifyDataSetChanged();
				hiddenMassDeleteOpera();
			}
		});
	}
	/**
	 * 跳转到写日记的界面
	 */
	private class AddNewNoteListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,EditNoteActivity.class);
			startActivity(intent);
			MainActivity.this.finish();
		}
	}
	
	/**
	 * 跳转到修改日记的界面
	 */
	private void toEditNoteActivity(int id){
		Intent intent = new Intent(this,EditNoteActivity.class);
		intent.putExtra("id" , id);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();
	}
	/**
	 * 隐藏批量操作
	 */
	public void hiddenMassDeleteOpera(){
		rlTop.setVisibility(View.GONE);
		rlBottomDel.setVisibility(View.GONE);
		isMassDeleteOpera = false;
		hiddenAllCheckBox();
	}
	/**
	 * 显示批量删除条目
	 */
	public void showMassDeleteOpera(){
		rlTop.setVisibility(View.VISIBLE);
		rlBottomDel.setVisibility(View.VISIBLE);
		isMassDeleteOpera = true;
		adaShowNoteData.isCheckBoxVisibile = true;
		adaShowNoteData.notifyDataSetChanged();
	}
	
	/**
	 * 判断选中的条目，对控件进行变换
	 */
	public void judegeChecked(){
		hasCheckedSum = adaShowNoteData.calIsChecked();
		if(hasCheckedSum == 0){
			btnChoice.setText("全选");
			rlBottomDel.setClickable(false);
		}else if(hasCheckedSum == hasItemSum){
			btnChoice.setText("全不选");
			rlBottomDel.setClickable(true);
		}else if(hasCheckedSum < hasItemSum){
			btnChoice.setText("全选");
			rlBottomDel.setClickable(true);
		}
	}
	/***
	 * 将checkbox置为全选
	 */
	private void changeToAllChecked(){
		for(int i = 0; i < hasItemSum;i++){
			adaShowNoteData.isPosChecked[i] = true;
		}
		adaShowNoteData.notifyDataSetChanged();
	}
	
	/**
	 * 将checkbox置为全不选
	 */
	private void chanageToAllNoteChecked(){
		for(int i = 0; i < hasItemSum;i++){
			adaShowNoteData.isPosChecked[i] = false;
		}
		adaShowNoteData.notifyDataSetChanged();
	}
	/**
	 * 隐藏全部的checkBox
	 */
	private void hiddenAllCheckBox(){
		adaShowNoteData.isCheckBoxVisibile = false;
	}
	
	/**
	 * 删除所有选择的item
	 */
	private  void deleteCheckedItem() {
		for(int i = 0; i < hasItemSum;i++){
			if(adaShowNoteData.isPosChecked[i]){
				dm.deleteNote(noteData.getNoteDataList().get(i).getID());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			 long currentTime=System.currentTimeMillis();
			 if(currentTime - oldTime <= EXIT_TIME){
				 this.finish();
			 }else{
				 Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				 oldTime = currentTime;
			 }
		}
		return true;
	}
	
}
