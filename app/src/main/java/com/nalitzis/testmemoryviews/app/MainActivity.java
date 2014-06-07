package com.nalitzis.testmemoryviews.app;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button mNormalImageButton;
        private Button mSubSampleImageButton;

        private Button mMinusBtn;
        private Button mPlusBtn;
        private int mSubsampleSize;
        private TextView mTextView;

        private Button mCauseGc;

        private TextView mFreeRamTextView;
        private float mFreeMemory;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mNormalImageButton = (Button)rootView.findViewById(R.id.normalButton);
            mSubSampleImageButton = (Button)rootView.findViewById(R.id.subsampleButton);
            mNormalImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageFragment(false);
                }
            });
            mSubSampleImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageFragment(true);
                }
            });
            mMinusBtn = (Button)rootView.findViewById(R.id.minusBtn);
            mMinusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSubsampleSize--;
                    mTextView.setText(String.valueOf(mSubsampleSize));
                }
            });
            mPlusBtn = (Button)rootView.findViewById(R.id.plusBtn);
            mPlusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSubsampleSize++;
                    mTextView.setText(String.valueOf(mSubsampleSize));
                }
            });
            mTextView = (TextView)rootView.findViewById(R.id.textView);
            mSubsampleSize = Integer.parseInt(mTextView.getText().toString());

            mCauseGc = (Button)rootView.findViewById(R.id.gcButton);
            mCauseGc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.gc();
                    mFreeMemory = MemUtils.megabytesFree();
                    mFreeRamTextView.setText(String.valueOf(mFreeMemory));
                }
            });

            mFreeRamTextView = (TextView)rootView.findViewById(R.id.freeRamValueTv);
            mFreeMemory = MemUtils.megabytesFree();
            mFreeRamTextView.setText(String.valueOf(mFreeMemory));
            return rootView;
        }

        private void showImageFragment(boolean subSample) {
            final ImageFragment imageFragment = new ImageFragment();
            final Bundle args = new Bundle();
            args.putBoolean(ImageFragment.ARGS_IS_SUBSAMPLED, subSample);
            if(subSample) {
                args.putInt(ImageFragment.ARGS_SUBSAMPLE, mSubsampleSize);
            }
            imageFragment.setArguments(args);

            final FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, imageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
