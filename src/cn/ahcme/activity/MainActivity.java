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
 * �����棬��ʾ�ռ��б�
 * @author Administrator kang qq:2733791207 
 *
 */
public class MainActivity extends Activity {
	
	/**������ʾ�ռǵ�����**/
	private ListView lvNoteDataShow = null;
	private NoteData noteData = null;
	private SimpleInfoAdapter adaShowNoteData = null;
	/**��ת��д�ʼǵĽ���İ�ť**/
	private ImageButton btnAddNewNote = null;
	/**���ص�ͷ������**/
	private RelativeLayout rlTop = null;
	/**�����ڵײ��Ĳ���,����ɾ������**/
	private RelativeLayout rlBottomDel = null;
	/**�Ƿ��ڽ�������ɾ������*/
	private boolean isMassDeleteOpera = false;
	/**�Ѿ��ж��ٸ�item��ѡ��**/
	private int hasCheckedSum = 0;
	/**һ���ж��ٸ�item**/
	private int hasItemSum = 0;
	/**ѡ��ť����Ϊȫѡ����ȫ��ѡ**/
	private Button btnChoice = null;
	/**ȡ����ť��ȡ����������**/
	private Button btnCancle = null;
	/**�������ݿ����*/
	private DatabaseManager dm = null;
	/**���ڼ������ΰ����ؼ���ʱ�������˳�����*/
	private long oldTime = 0;
	/**�涨���ΰ����ؼ���ʱ��**/
	private static final int EXIT_TIME = 2000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		dm = new DatabaseManager(this);
		initComonment();
	}
	/***
	 * listview���������������Խ�������ɾ��
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
	 * ��ʼ�������ϵ����
	 */
	private void initComonment(){
		lvNoteDataShow = (ListView) findViewById(R.id.activity_main_lv_note_data);
		noteData = new NoteData(this);
		hasItemSum = noteData.getNoteDataList().size();
		adaShowNoteData = new SimpleInfoAdapter(this, noteData.getNoteDataList());
		lvNoteDataShow.setAdapter(adaShowNoteData);
		//����������������������
		lvNoteDataShow.setOnItemLongClickListener(new EditNoteLongClickListener());
		//�����¼�����ת���༭�ռǵĽ��棬�����޸ĸ����ռ�
		lvNoteDataShow.setOnItemClickListener(new EditNoteClickListener());
		btnAddNewNote 
			= (ImageButton)findViewById(R.id.activity_main_btn_add_new_note);
		btnAddNewNote.setOnClickListener(new AddNewNoteListener());
		initHiddenComponent();
	}
	
	/***
	 * ��ʼ�����ز���
	 */
	private void initHiddenComponent(){
		//��ʼ�����صĲ���
		rlTop = (RelativeLayout)findViewById(R.id.activity_main_hidden_top);
		rlBottomDel = (RelativeLayout)findViewById(R.id.activity_main_hidden_bottom);
		btnChoice = (Button)findViewById(R.id.activity_main_top_hidden_btn_choice);
		btnCancle = (Button)findViewById(R.id.activity_main_top_hidden_btn_cancel);
		btnChoice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(hasCheckedSum == hasItemSum && hasItemSum != 0){
					//���ȫ��ѡ�У�����Ϊȫ��ѡ��
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
	 * ��ת��д�ռǵĽ���
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
	 * ��ת���޸��ռǵĽ���
	 */
	private void toEditNoteActivity(int id){
		Intent intent = new Intent(this,EditNoteActivity.class);
		intent.putExtra("id" , id);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		this.finish();
	}
	/**
	 * ������������
	 */
	public void hiddenMassDeleteOpera(){
		rlTop.setVisibility(View.GONE);
		rlBottomDel.setVisibility(View.GONE);
		isMassDeleteOpera = false;
		hiddenAllCheckBox();
	}
	/**
	 * ��ʾ����ɾ����Ŀ
	 */
	public void showMassDeleteOpera(){
		rlTop.setVisibility(View.VISIBLE);
		rlBottomDel.setVisibility(View.VISIBLE);
		isMassDeleteOpera = true;
		adaShowNoteData.isCheckBoxVisibile = true;
		adaShowNoteData.notifyDataSetChanged();
	}
	
	/**
	 * �ж�ѡ�е���Ŀ���Կؼ����б任
	 */
	public void judegeChecked(){
		hasCheckedSum = adaShowNoteData.calIsChecked();
		if(hasCheckedSum == 0){
			btnChoice.setText("ȫѡ");
			rlBottomDel.setClickable(false);
		}else if(hasCheckedSum == hasItemSum){
			btnChoice.setText("ȫ��ѡ");
			rlBottomDel.setClickable(true);
		}else if(hasCheckedSum < hasItemSum){
			btnChoice.setText("ȫѡ");
			rlBottomDel.setClickable(true);
		}
	}
	/***
	 * ��checkbox��Ϊȫѡ
	 */
	private void changeToAllChecked(){
		for(int i = 0; i < hasItemSum;i++){
			adaShowNoteData.isPosChecked[i] = true;
		}
		adaShowNoteData.notifyDataSetChanged();
	}
	
	/**
	 * ��checkbox��Ϊȫ��ѡ
	 */
	private void chanageToAllNoteChecked(){
		for(int i = 0; i < hasItemSum;i++){
			adaShowNoteData.isPosChecked[i] = false;
		}
		adaShowNoteData.notifyDataSetChanged();
	}
	/**
	 * ����ȫ����checkBox
	 */
	private void hiddenAllCheckBox(){
		adaShowNoteData.isCheckBoxVisibile = false;
	}
	
	/**
	 * ɾ������ѡ���item
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
				 Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
				 oldTime = currentTime;
			 }
		}
		return true;
	}
	
}
