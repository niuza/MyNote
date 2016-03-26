package Tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapTools {
	/**
	 * 对图片进行按比例设置
	 * @param bitmap 要处理的图片
	 * @return 返回处理好的图片
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, float widthScale, float heightScale){
		Matrix matrix = new Matrix();
		matrix.postScale(widthScale, heightScale);
		if(bitmap == null){
			return null;
		}
		Bitmap resizeBmp  = 
				Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
		return resizeBmp;
	}
}
