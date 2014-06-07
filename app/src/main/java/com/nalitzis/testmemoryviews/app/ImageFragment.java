package com.nalitzis.testmemoryviews.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by ado on 06/06/14.
 */
public class ImageFragment extends Fragment {

    public static final String TAG = "ImageFragment";

    public static final String ARGS_IS_SUBSAMPLED = "ARGS_IS_SUBSAMPLED";
    public static final String ARGS_SUBSAMPLE = "ARGS_SUBSAMPLE";

    private FrameLayout mRootLayout;


    private static final float BYTES_PER_PX = 4.0f; //32 bit

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        mRootLayout = (FrameLayout)inflater.inflate(R.layout.image_layout, viewGroup, false);
        loadImage(getArguments());
        return mRootLayout;
    }

    private void loadImage(Bundle bundle) {
        final boolean isSubSampled = bundle.getBoolean(ARGS_IS_SUBSAMPLED);
        if(isSubSampled) {
            final int subsample = bundle.getInt(ARGS_SUBSAMPLE);
            readBitmapInfo();
            subSampleImage(subsample);
            logHeapStats();
        } else {
            displayNormalImage();
            logHeapStats();
        }
    }

    private void readBitmapInfo() {
        final Resources res = getActivity().getResources();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, R.drawable.brasil, options);
        final float imageHeight = options.outHeight;
        final float imageWidth = options.outWidth;
        final String imageMimeType = options.outMimeType;
        Log.d(TAG, "w,h, type:"+imageWidth+", "+imageHeight+", "+imageMimeType);
        Log.d(TAG, "estimated memory required in MB: "+imageWidth * imageHeight * BYTES_PER_PX/MemUtils.BYTES_IN_MB);
    }


    private void displayNormalImage() {
        final Resources res = getActivity().getResources();
        mRootLayout.setBackgroundResource(R.drawable.brasil);
    }

    private void logHeapStats() {
        final float mbTotal = MemUtils.megabytesAvailable();
        final String totAvailable = "total MB available: "+mbTotal;
        Log.d(TAG, totAvailable);
        final float mbFree = MemUtils.megabytesFree();
        final String totFree = "free MB available: "+mbFree;
        Log.d(TAG, totFree);
        final Toast toast = Toast.makeText(getActivity(), totAvailable+ "\n" +totFree, Toast.LENGTH_SHORT);
        toast.show();
        //logActivityHeapStats();
    }

    private void logActivityHeapStats() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.d(TAG, "getMemoryClass: "+memoryClass);
    }



    private void subSampleImage(int powerOf2) {
        if(powerOf2 < 1 || powerOf2 > 8) {
            Log.e(TAG, "trying to apply upscale or excessive downscale: "+powerOf2);
            return;
        }
        final Resources res = getActivity().getResources();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = powerOf2;
        final Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.brasil, options);
        final BitmapDrawable bmpDrawable = new BitmapDrawable(res, bmp);
        mRootLayout.setBackground(bmpDrawable);
    }

}
