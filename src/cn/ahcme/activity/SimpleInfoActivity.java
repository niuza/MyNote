package cn.ahcme.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import cn.ahcme.dao.DatabaseManager;

import com.ahcme.mynote.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SimpleInfoActivity extends Activity {

	private Button btnCamera = null;
	private Button btnSave = null;
	private ImageView ivHorizontal = null;
	private ImageView ivVertical = null;
	/** 所要保存的图片 **/
	private Bitmap bitmap = null;
	/** 图片所保存文件夹的路径 **/
	private String imgDirPath = "/sdcard/mynote/";
	/** 图片所保存文件的路径 **/
	private String imgPath = "null";
	private DatabaseManager dbManager = null;
	private EditText etNoteData = null;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbManager = new DatabaseManager(this);
		setContentView(R.layout.activity_write_note);
		initComponent();
	}
	
	/**
	 * 初始化界面上的组件
	 */
	private void initComponent(){
		btnCamera = (Button) findViewById(R.id.activity_write_note_btn_camera);
		btnSave = (Button) findViewById(R.id.activity_write_note_btn_save);
		ivHorizontal 
			= (ImageView) findViewById(R.id.activity_write_note_show_iv_horizontal);
		ivVertical 
			= (ImageView) findViewById(R.id.activity_write_note_show_iv_vertical);
		etNoteData = (EditText) findViewById(R.id.activity_write_note_data_et);
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
			}
		});
		//======TODO 用来测试文件是否可以保存
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveNote();
			}
		});
		//TODO 取消图片
	}
	/**
	 * 接收拍照获得的结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 0){
			Toast.makeText(SimpleInfoActivity.this, "拍照已取消", Toast.LENGTH_SHORT).show();
			return;
		}
		if(data == null) return;
		Bundle bundler = data.getExtras();
		bitmap = (Bitmap)bundler.get("data");
		if(bitmap == null) return;
		int imgWidth = bitmap.getWidth();
		int imgHeight = bitmap.getHeight();
		if(imgWidth > imgHeight){
			ivVertical.setImageBitmap(bitmap);
			ivVertical.setVisibility(View.VISIBLE);
			ivHorizontal.setVisibility(View.GONE);
		} else {
			ivHorizontal.setImageBitmap(bitmap);
			ivVertical.setVisibility(View.GONE);
			ivHorizontal.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 保存图像到sdcard中去
	 */
	private boolean saveImgToSdcard(){
		String sdCard = Environment.getExternalStorageState();
		if(!sdCard.equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "未检测到存储卡!", Toast.LENGTH_SHORT).show();
			return false;
		}
		Time time = new Time();
		time.setToNow();
		Random r = new Random();
		int imgRandom = r.nextInt();
		String imgName = time.year + "" + 
						 (time.month + 1) + "" +
						 time.monthDay + "" +
						 time.hour + "" +
						 time.minute +"" +
						 time.second  +"" +
						 imgRandom +".jpg";
		imgPath = imgDirPath + imgName;
		File imgFile = new File(imgPath);
		//如果文件不存在就创建
		if(!imgFile.exists()){
			try{
				imgFile.createNewFile();
			} catch(Exception e){
				Toast.makeText(this, "保存图片的时候出错!", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(imgPath);
			if(bitmap == null) return false;
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (Exception e) {
			Toast.makeText(this, "保存图片的时候出错!", Toast.LENGTH_SHORT).show();
			return false;
		}finally{
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	/**
	 * 检测文件夹是否存在，不存在的话就创建
	 * @param dirPath 所要操作的文件夹
	 */
	private boolean dirIsExistsAndCreate(String dirPath){
		File imgPath = new File(dirPath);
		if(imgPath.exists()){
			return true;
		}
		boolean isSuccess = imgPath.mkdirs();
		if(!isSuccess) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * 保存日记到数据库中
	 */
	private void saveNote(){
		Time time = new Time();
		time.setToNow();
		String dateStr = time.year + "-" + time.month + "-" + time.monthDay;
		String timeStr = time.hour + ":" + time.minute;
		String dateTimeStr = dateStr +" " +timeStr;
		String noteStr = etNoteData.getText().toString();
		if(bitmap == null){
			dbManager.executeWriteMsg(dateTimeStr, noteStr);
			}else{
			if(dirIsExistsAndCreate(imgDirPath)){
				if(saveImgToSdcard()){
					//TODO 保存记录到数据库
					dbManager.executeWriteMsg(dateTimeStr, noteStr);
				}
			}
		}
	}

}
