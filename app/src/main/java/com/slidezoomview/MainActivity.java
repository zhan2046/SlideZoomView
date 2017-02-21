package com.slidezoomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import zhan.slidezoomview.SlideZoomView;

public class MainActivity extends AppCompatActivity {

  private SlideZoomView mHnv1;
  private SeekBar mSeekBar1;

  private SlideZoomView mHnv2;
  private SeekBar mSeekBar2;

  private SlideZoomView mHnv3;
  private SeekBar mSeekBar3;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mHnv1 = (SlideZoomView) findViewById(R.id.hnv1);
    mSeekBar1 = (SeekBar) findViewById(R.id.seek_bar1);

    mHnv2 = (SlideZoomView) findViewById(R.id.hnv2);
    mSeekBar2 = (SeekBar) findViewById(R.id.seek_bar2);

    mHnv3 = (SlideZoomView) findViewById(R.id.hnv3);
    mSeekBar3 = (SeekBar) findViewById(R.id.seek_bar3);

    //you must call this method
    mHnv1.setNumberStringArray(new String[]{"1","2","3","4","5","6","7","8","9","10"});
    mHnv2.setNumberStringArray(new String[]{"A","B","C","D","E","F","G","H","I","J"});
    mHnv3.setNumberStringArray(new String[]{"我","要","放","大","我","要","滑","动","啊","呀"});

    mHnv1.setSeekBar(mSeekBar1);
    mHnv2.setSeekBar(mSeekBar2);
    mHnv3.setSeekBar(mSeekBar3);
  }
}
