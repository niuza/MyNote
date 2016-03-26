package cn.ahcme.bean;

import android.text.format.Time;

public class Note {
	private int iD;
	private String dateTimeStr;
	private String noteData;
	private String imgUrl;
	private String dateStr;
	private String timeStr;
	private Time time = new Time();
	/**
	 * 得到该记录的id
	 * 
	 * @return
	 */
	public int getID() {
		return iD;
	}

	/**
	 * 设置该记录的id
	 * 
	 * @param iD
	 */
	public void setID(int iD) {
		this.iD = iD;
	}

	/**
	 * 得到日记数据
	 * 
	 * @return
	 */
	public String getNoteData() {
		return noteData;
	}

	/**
	 * 设置日记数据
	 * 
	 * @param noteData
	 */
	public void setNoteData(String noteData) {
		this.noteData = noteData;
	}

	/**
	 * 得到图片的地址
	 * 
	 * @return
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * 设置图片地址
	 * 
	 * @param imgUrl
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * 得到日期时间
	 * @return
	 */
	public String getDateTimeStr() {
		return dateTimeStr;
	}

	/**
	 * 设置日期时间
	 * @param dateTimeStr
	 */
	public void setDateTimeStr(String dateTimeStr) {
		this.dateTimeStr = dateTimeStr;
		initDateAndTime();
	}
	
	/**
	 * 初始化日期和时间字符串
	 */
	private void initDateAndTime(){
		try{
			String[] dateTimeStrs = dateTimeStr.split(" ");
			String[] dates = dateTimeStrs[0].split("-");
			String[] times = dateTimeStrs[1].split(":");
			this.time.year = Integer.parseInt(dates[0]);
			this.time.month = Integer.parseInt(dates[1]);
			this.time.monthDay = Integer.parseInt(dates[2]);
			this.time.hour = Integer.parseInt(times[0]);
			this.time.minute = Integer.parseInt(times[1]);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(this.time.month < 10 ){
			this.dateStr = "0" + time.month + "/";
		}else{
			this.dateStr = time.month + "/";
		}
		if(this.time.monthDay < 10){
			this.dateStr = this.dateStr +"0" + this.time.monthDay;
		}else{
			this.dateStr = this.dateStr + this.time.monthDay;
		}
		if(this.time.hour < 10){
			this.timeStr = "0" + this.time.hour + ":";
		} else {
			this.timeStr = this.time.hour + ":";
		}
		if(this.time.minute < 10){
			this.timeStr = this.timeStr + "0" + this.time.minute;
		} else {
			this.timeStr = this.timeStr + this.time.minute;
		}
	}

	/**
	 * 得到日期字符
	 * @return
	 */
	public String getDateStr() {
		return dateStr;
	}

	/**
	 * 得到时间字符
	 * @return
	 */
	public String getTimeStr() {
		return timeStr;
	}

	/**
	 * 得到时间
	 * @return
	 */
	public Time getTime() {
		return time;
	}

	
}
