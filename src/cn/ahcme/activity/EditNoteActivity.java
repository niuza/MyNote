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
	/** 所保存图片的文件夹 **/
	public final static String IMG_DIR = "MyNoteImgs";
	/** 当前图片的路径 **/
	private String currImgPath = "";
	/** 保存日记的按钮 */
	private Button btnSave = null;
	/** 数据库管理类，用于将数据进行保存或者更改 **/
	private DatabaseManager dm = null;
	/**返回键，用于放弃日记编写**/
	private Button btnBack = null;
	/**用显示时间的textview**/
	private TextView tvTime = null;
	/**获取时间的操作**/
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
	 * 初始化界面上的组件
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
			Toast.makeText(getApplicationContext(), "拍照未完成", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(currImgPath);
		Bitmap resizeBitmap = BitmapTools.getScaleBitmap(bitmap, 0.2f, 0.2f);
		// 如果bitmap非空，进行销毁操作
		if (bitmap != null) {
			bitmap.recycle();
		}
		stringSpanEdit.insertImgForText(resizeBitmap, currImgPath);
	}

	/**
	 * 判断文件是否存在，如果不存在，则创建文件夹
	 * 
	 * @return 是否创建成功，或者是否存在
	 */
	private boolean dirIsExistAndMkdir(String dirPath) {
		String sdCard = Environment.getExternalStorageState();
		if (!sdCard.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "未检测到存储卡!", Toast.LENGTH_SHORT).show();
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
	 * 当按返回键的时候，跳转到主界面
	 */
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定放弃编辑?");
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				toMainActivity();
			}
		});
		builder.setNegativeButton("否", null);
		builder.show();
	}

	/**
	 * 跳转到主界面
	 */
	private void toMainActivity() {
		Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
		startActivity(intent);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.finish();
	}

	/**
	 * 将日记保存起来
	 * 
	 * @param content
	 *            要保存的内容
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
	 * 将日记进行更新操作
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
	 * 使用相机操作
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
	 * 使用相册
	 */
	private void useGallery(){
		
	}
	
	/***
	 * 将要更新的数据从数据库中读出来，放到界面上
	 */
	private void fillNoteData(int id){
		Note note = dm.readData(id);
		this.stringSpanEdit.setSpanContent(note.getNoteData());
		this.tvTime.setText(note.getDateTimeStr());
	}
}
