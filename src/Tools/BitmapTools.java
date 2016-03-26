package Tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapTools {
	/**
	 * ��ͼƬ���а���������
	 * @param bitmap Ҫ�����ͼƬ
	 * @return ���ش���õ�ͼƬ
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
