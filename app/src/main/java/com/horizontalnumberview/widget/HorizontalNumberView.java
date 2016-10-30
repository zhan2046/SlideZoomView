package com.horizontalnumberview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import com.horizontalnumberview.R;
import java.text.DecimalFormat;

/**
 * Created by hrz on 2016/10/30.
 */
public class HorizontalNumberView extends View implements SeekBar.OnSeekBarChangeListener {

  private static final int SPACE_NUM = 2;

  private static String[] NUMBERS = new String[] {
      "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
  };

  private static int mNormalTextColor;
  private static int mSelectTextColor;

  private int mNormalTextSize = 36;
  private int mSelectTextSize = 72;

  private int mNumberMax;

  private SeekBar mSeekBar;

  private int mItemHeight;
  private int mItemWidth;

  private int mOffset;
  private int mCurrentProgress = 0;
  private int mBeforeProgress = 0;

  private int mCurrentIndex = 1;

  private double mCurrentValue;

  private Paint mPaint;
  private Paint mSelectPaint;
  private Rect mBounds;

  private OnSeekBarChangeListener mOnSeekBarChangeListener;

  public HorizontalNumberView(Context context) {
    this(context, null);
  }

  public HorizontalNumberView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HorizontalNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mNumberMax = NUMBERS.length + SPACE_NUM;
    mNormalTextColor = getResources().getColor(R.color.number_view_normal_text_color);
    mSelectTextColor = getResources().getColor(R.color.number_view_select_text_color);

    //init text paint
    mPaint = new Paint();
    mPaint.setColor(mNormalTextColor);
    mPaint.setAntiAlias(true);
    mPaint.setTextSize(mNormalTextSize);

    //init color paint
    mSelectPaint = new Paint();
    mSelectPaint.setColor(mSelectTextColor);
    mSelectPaint.setAntiAlias(true);
    mSelectPaint.setTextSize(mSelectTextSize);

    //init text bound rect
    mBounds = new Rect();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    //init text width and height
    mItemHeight = getMeasuredHeight();
    mItemWidth = getMeasuredWidth() / mNumberMax;

    //check set seek bar
    if (mSeekBar == null) {
      throw new RuntimeException("you must call set seekBar method!");
    }
    mSeekBar.setOnSeekBarChangeListener(this);
  }

  @Override protected void onDraw(Canvas canvas) {
    //start draw text
    for (int i = 0; i < NUMBERS.length; i++) {
      String text = NUMBERS[i];
      int textIndex = i + 1;

      if (mCurrentProgress > 0 && mCurrentProgress > mBeforeProgress) {//left to right slip

        //item % value
        float value = (mCurrentProgress % 10) / 10.0f;

        if (textIndex - mCurrentIndex == 0) {//current text
          mOffset = (int) (mOffset + mItemWidth - value * mItemWidth);
        }

        if (textIndex - mCurrentIndex == 1) {//current+1
          mOffset += mItemWidth;
        }

        if (textIndex - mCurrentIndex == 2) {//current+2
          mOffset = (int) (mOffset + value * mItemWidth);
        }
      } else if (mCurrentProgress > 0 && mCurrentProgress < mBeforeProgress) {//right to left slip

        //item % value
        float value = 1 - ((mCurrentProgress % 10) / 10.0f);

        if (textIndex - mCurrentIndex == 0 && mCurrentIndex != 1) {//current text
          mOffset += mItemWidth;
        }

        if (textIndex - mCurrentIndex == -1) {//current - 1
          mOffset = (int) (mOffset + value * mItemWidth);
        }

        if (textIndex - mCurrentIndex == 1 && mCurrentIndex != 1) {//current + 1
          mOffset = (int) (mOffset + mItemWidth - value * mItemWidth);
        }
      }

      //text width
      int textX = (int) (mItemWidth * 0.5f + i * mItemWidth + mOffset);
      mPaint.getTextBounds(text, 0, text.length(), mBounds);

      //text height
      float textHeight = mBounds.height();
      int textY = (int) (mItemHeight * 0.5f + textHeight * 0.5f);
      float fontHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
      float posValue = fontHeight / 2 - mPaint.getFontMetrics().descent - 10;

      //text draw
      if (textIndex == mCurrentIndex) {
        canvas.drawText(text, textX, textY + posValue, mSelectPaint);
      } else {
        canvas.drawText(text, textX, textY + posValue, mPaint);
      }
    }

    //reset params
    mOffset = 0;
    mBeforeProgress = mCurrentProgress;
  }

  public String[] getNumberStringArray() {
    return NUMBERS;
  }

  public void setNumberStringArray(String[] stringArray) {
    NUMBERS = stringArray;
  }

  public void setNormalTextColor(int color) {
    mNormalTextColor = color;
  }

  public void setSelectTextColor(int color) {
    mSelectTextColor = color;
  }

  public void setNormalTextSize(int size) {
    mNormalTextSize = size;
  }

  public void setSelectTextSize(int size) {
    mSelectTextSize = size;
  }

  public void setSeekBar(SeekBar seekBar) {
    mSeekBar = seekBar;
    mSeekBar.setMax((NUMBERS.length - 1) * 10);//set seekBar max value
  }

  public String getCurrentValue() {
    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    return decimalFormat.format(mCurrentValue);
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    mCurrentProgress = progress;
    mCurrentIndex = (mCurrentProgress / NUMBERS.length) + 1;
    mCurrentValue = mCurrentIndex + ((mCurrentProgress % 10) / 10.0f);
    invalidate();

    if (mOnSeekBarChangeListener != null) {
      mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
      mOnSeekBarChangeListener.onStateChangeBack(mCurrentProgress, mCurrentIndex,
          getCurrentValue());
    }
  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {
    if (mOnSeekBarChangeListener != null) {
      mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
    }
  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    if (mOnSeekBarChangeListener != null) {
      mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
    }
  }

  public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
    mOnSeekBarChangeListener = listener;
  }

  public interface OnSeekBarChangeListener {

    void onStateChangeBack(int progress, int currentIndex, String currentValue);

    void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

    void onStartTrackingTouch(SeekBar seekBar);

    void onStopTrackingTouch(SeekBar seekBar);
  }
}

