package cn.ahcme.activity;

import java.io.File;
import java.util.Random;

import Tools.BitmapTools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.ahcme.bean.Note;
import cn.ahcme.dao.DatabaseManager;
import cn.ahcme.ui.StringSpanEdit;

import com.ahcme.mynote.R;

public class EditNoteActivity extends Activity {

	private ImageButton btnCamera = null;
	private StringSpanEdit stringSpanEdit = null;
	/** ������ͼƬ���ļ��� **/
	public final static String IMG_DIR = "MyNoteImgs";
	/** ��ǰͼƬ��·�� **/
	private String currImgPath = "";
	/** �����ռǵİ�ť */
	private Button btnSave = null;
	/** ���ݿ�����࣬���ڽ����ݽ��б�����߸��� **/
	private DatabaseManager dm = null;
	/**���ؼ������ڷ����ռǱ�д**/
	private Button btnBack = null;
	/**����ʾʱ���textview**/
	private TextView tvTime = null;
	/**��ȡʱ��Ĳ���**/
	private Time time = new Time();
	private int noteID = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		time.setToNow();
		Intent intent = getIntent();
		noteID = intent.getIntExtra("id", -1);
		dm = new DatabaseManager(this);
		initComponent();
		if(noteID != -1){
			fillNoteData(noteID);
		}
	}

	/**
	 * ��ʼ�������ϵ����
	 */
	private void initComponent() {
		btnCamera = (ImageButton) findViewById(R.id.activity_edit_note_btn_camera);
		stringSpanEdit = (StringSpanEdit) findViewById(R.id.activity_edit_note_data_et);
		btnCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				useCamera();
			}
		});
		btnSave = (Button) findViewById(R.id.activity_edit_note_btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(noteID == -1){
					saveNote(stringSpanEdit.getText().toString());
				}else{
					updateNote(noteID, stringSpanEdit.getText().toString());
				}
				toMainActivity();
			}
		});
		btnBack = (Button) findViewById(R.id.activity_edit_note_btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		tvTime = (TextView)findViewById(R.id.activity_edit_note_tv_time);
		if(noteID == -1){
			String timeStr = time.year + "-" + 
							 (time.month+1) + "-" +
					         time.monthDay +" " +
							 time.hour + ":" + time.minute;
			tvTime.setText(timeStr);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK != resultCode) {
			Toast.makeText(getApplicationContext(), "����δ���", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(currImgPath);
		Bitmap resizeBitmap = BitmapTools.getScaleBitmap(bitmap, 0.2f, 0.2f);
		// ���bitmap�ǿգ��������ٲ���
		if (bitmap != null) {
			bitmap.recycle();
		}
		stringSpanEdit.insertImgForText(resizeBitmap, currImgPath);
	}

	/**
	 * �ж��ļ��Ƿ���ڣ���������ڣ��򴴽��ļ���
	 * 
	 * @return �Ƿ񴴽��ɹ��������Ƿ����
	 */
	private boolean dirIsExistAndMkdir(String dirPath) {
		String sdCard = Environment.getExternalStorageState();
		if (!sdCard.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "δ��⵽�洢��!", Toast.LENGTH_SHORT).show();
			return false;
		}
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ dirPath);
		if (f.exists()) {
			return true;
		}
		boolean isSuccess = f.mkdir();
		if (isSuccess) {
			return true;
		}
		return false;
	}

	/**
	 * �������ؼ���ʱ����ת��������
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ȷ�������༭?");
		builder.setPositiveButton("��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				toMainActivity();
			}
		});
		builder.setNegativeButton("��", null);
		builder.show();
	}

	/**
	 * ��ת��������
	 */
	private void toMainActivity() {
		Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
		startActivity(intent);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.finish();
	}

	/**
	 * ���ռǱ�������
	 * 
	 * @param content
	 *            Ҫ���������
	 */
	private void saveNote(String content) {
		Time time = new Time();
		time.setToNow();
		String dateTimeStr = time.year + "-" + 
						 (time.month + 1) + "-" + 
				         time.monthDay +" " + 
						 time.hour +":" + time.minute;
		dm.executeWriteMsg(dateTimeStr, content);
	}
	/**
	 * ���ռǽ��и��²���
	 */
	private void updateNote(int id, String content){
		Time time = new Time();
		time.setToNow();
		String dateTimeStr = time.year + "-" + 
						 (time.month + 1) + "-" + 
				         time.monthDay +" " + 
						 time.hour +":" + time.minute;
		dm.updateNote(id, dateTimeStr, content);
	}
	/**
	 * ʹ���������
	 **/
	private void useCamera(){
		if (!dirIsExistAndMkdir(IMG_DIR)) {
			return;
		}
		Time time = new Time();
		time.setToNow();
		Random r = new Random();
		String imgName = time.year + "" + (time.month + 1) + ""
				+ time.monthDay + "" + time.minute + "" + time.second
				+ "" + r.nextInt(1000) + ".jpg";
		currImgPath = Environment.getExternalStorageDirectory() + "/"
				+ IMG_DIR + "/" + imgName;
		File f = new File(currImgPath);
		Uri uri = Uri.fromFile(f);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, 1);
	}
	
	/**
	 * ʹ�����
	 */
	private void useGallery(){
		
	}
	
	/***
	 * ��Ҫ���µ����ݴ����ݿ��ж��������ŵ�������
	 */
	private void fillNoteData(int id){
		Note note = dm.readData(id);
		this.stringSpanEdit.setSpanContent(note.getNoteData());
		this.tvTime.setText(note.getDateTimeStr());
	}
}
