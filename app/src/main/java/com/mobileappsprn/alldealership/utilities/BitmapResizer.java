package com.mobileappsprn.alldealership.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapResizer {
    public static String TAG = "Resizer";
    public static Bitmap decodeFile(File f,int requiredSize){
    try {
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);
       
        //Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE=requiredSize;
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=2;
        while(true){
            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }
       
        o.inJustDecodeBounds = false;
	    o.inPurgeable = true;
	    o.inInputShareable = true;
	    o.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    o.inDither = false;
	    
        //decode with inSampleSize
        //BitmapFactory.Options o2 = new BitmapFactory.Options();
        o.inSampleSize=scale;
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
    } catch (FileNotFoundException e) {
           
    }
    return null;
}
    public static Bitmap decodeSampledBitmapFromResource(String  filePath,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);
        Log.v("Resizer","options.outHeight "+options.outHeight+"options.outWidth "+options.outWidth);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        
        options.inJustDecodeBounds = false;
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    options.inDither = false;
	    
        Bitmap bm = BitmapFactory.decodeFile(filePath,options);
        int width = reqWidth;
        int newHeight = width * options.outHeight / options.outWidth;
       
      //  bm = getResizedBitmap(bm, newHeight, reqWidth);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, width, newHeight, false);//(bm, 0, 0, width, newHeight);
        bm = null;

        return resizedBitmap;
        
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
    
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
    	 
    	int width = bm.getWidth();
    	 
    	int height = bm.getHeight();
    	 
    	float scaleWidth = ((float) newWidth) / width;
    	 
    	float scaleHeight = ((float) newHeight) / height;
    	 
    	// create a matrix for the manipulation
    	 
    	Matrix matrix = new Matrix();
    	 
    	// resize the bit map
    	 
    	matrix.postScale(scaleWidth, scaleHeight);
    	 
    	// recreate the new Bitmap
    	 
    	Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    	 
    	return resizedBitmap;
    	 
    	}
   
}
