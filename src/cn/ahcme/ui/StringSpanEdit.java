package cn.ahcme.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tools.BitmapTools;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import cn.ahcme.activity.EditNoteActivity;

public class StringSpanEdit extends EditText {
	
	public StringSpanEdit(Context context){
		super(context);
	}
	
	public StringSpanEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 往文本里面插入图片
	 */
	public void insertImgForText(Bitmap bitmap, String imgPath){
		ImageSpan imageSpan = new ImageSpan(bitmap,ImageSpan.ALIGN_BASELINE);
		SpannableString ss = new SpannableString(imgPath);
		ss.setSpan(imageSpan, 0, imgPath.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		append(ss);
		append("\n");
		setSelection(this.getText().toString().length());
		setSpanContent(this.getText().toString());
	}
	
	/**
	 * 为文本框设置带图文混排的内容
	 * @param content 要设置的内容
	 */
	public void setSpanContent(String content){
		String patternStr = Environment.getExternalStorageDirectory() 
							+ "/" +EditNoteActivity.IMG_DIR + "/.+?\\.\\w{3}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher m = pattern.matcher(content);
		SpannableString ss = new SpannableString(content); 
		while(m.find()){
			Bitmap bmp = BitmapFactory.decodeFile(m.group());
			Bitmap bitmap = BitmapTools.getScaleBitmap(bmp, 0.2f, 0.2f);
			if(bmp != null){
				bmp.recycle();
			}
			ImageSpan imgSpan = new ImageSpan(bitmap, ImageSpan.ALIGN_BASELINE);
			ss.setSpan(imgSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		this.setText(ss);
	}
}
