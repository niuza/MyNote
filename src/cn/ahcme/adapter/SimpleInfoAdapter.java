package cn.ahcme.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.ahcme.activity.EditNoteActivity;
import cn.ahcme.activity.MainActivity;
import cn.ahcme.bean.Note;

import com.ahcme.mynote.R;

public class SimpleInfoAdapter extends BaseAdapter{
	private List<Note> datas = null;
	private Context context = null;
	//记录在数据中position中checkbox的状态
	public boolean isPosChecked[];
	/**判断选择框是否被选中**/
	public boolean isCheckBoxVisibile = false;
	/**要操作的activity*/
	private MainActivity activity;
	public SimpleInfoAdapter(Context context,List<Note> datas){
		this.context = context; 
		this.datas = datas;
		activity = (MainActivity)context;
		//this.btnChoice = btnChoice;
		isPosChecked = new boolean[datas.size()];
		for(int i = 0; i < datas.size(); i++){
			isPosChecked[i] = false;
		}
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView 
				= LayoutInflater.from(context).inflate(R.layout.simple_info_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvDate 
				= (TextView)convertView.findViewById(R.id.simple_info_item_tv_date);
			viewHolder.tvContent
				= (TextView)convertView.findViewById(R.id.simple_info_item_tv_content);
			viewHolder.checkBox 
				= (CheckBox)convertView.findViewById(R.id.simple_info_item_check);
			viewHolder.tvNoteID
			    = (TextView)convertView.findViewById(R.id.simple_info_item_tv_note_id);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Time time = new Time();
		time.setToNow();
		Time timeItem = datas.get(position).getTime();
		if((time.year == timeItem.year) 
			&&(time.month+1 == timeItem.month)
			&&(time.monthDay == timeItem.monthDay)){
			viewHolder.tvDate.setText(datas.get(position).getTimeStr());
		}else{
			viewHolder.tvDate.setText(datas.get(position).getDateStr());
		}
		viewHolder.tvNoteID.setText(datas.get(position).getID() +"");
		viewHolder.tvContent.setText(delPathForContent(datas.get(position).getNoteData()));
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isPosChecked[position] = isChecked;
				activity.judegeChecked();
			}
		});
		viewHolder.checkBox.setChecked(isPosChecked[position]);
		if(isCheckBoxVisibile){
			viewHolder.checkBox.setVisibility(View.VISIBLE);
		}else{
			viewHolder.checkBox.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	/**
	 * 将内容里面的图片路删去
	 */
	private String delPathForContent(String content){
		String patternStr = Environment.getExternalStorageDirectory() 
				+ "/" +EditNoteActivity.IMG_DIR + "/.+?\\.\\w{3}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher m = pattern.matcher(content);
		content = m.replaceAll("");
		patternStr = "\\n";
		pattern = Pattern.compile(patternStr);
		m = pattern.matcher(content);
		content = m.replaceAll("");
		return content;
	}
	/**
	 * 计算出有多条被选中
	 * @return 被选中的条目
	 */
	public int calIsChecked(){
		int num = 0;
		for(int i = 0; i < isPosChecked.length; i++){
			if(isPosChecked[i]){
				num++;
			}
		}
		return num;
	}
	
	public static class ViewHolder{
		public TextView tvNoteID;
		public TextView tvDate;
		public TextView tvContent;
		public CheckBox checkBox;
	}
	
	
	
}
