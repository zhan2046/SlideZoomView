package zhan.slidezoomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import java.text.DecimalFormat;

/**
 * Created by zhan on 2017/2/21.
 */
public class SlideZoomView extends View implements SeekBar.OnSeekBarChangeListener {

  private static final int ITEM_SLIP_TIME = 10;

  private static final int NORMAL_TEXT_SIZE = 40;
  private static final int SELECT_TEXT_SIZE = 75;

  private static final int SPACE_NUM = 2;

  private String[] NUMBERS = new String[] {
      "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
  };

  private int mNormalTextColor;
  private int mSelectTextColor;

  private int mNormalTextSize;
  private int mSelectTextSize;

  private int mNumberMax;

  private SeekBar mSeekBar;

  private int mItemHeight;
  private float mItemWidth;

  private float mOffset;
  private int mCurrentProgress = 0;
  private int mBeforeProgress = 0;

  private int mCurrentIndex = 1;

  private int mTempValue = 0;
  private double mLastScore = 0.0f;
  private int mLastProgressValue = 0;

  private double mCurrentValue;

  private Paint mPaint;
  private Paint mSelectPaint;
  private Rect mBounds;

  private OnSeekBarChangeListener mOnSeekBarChangeListener;

  private SlipTask mSlipTask;

  private boolean isFirst;
  private boolean isSlipTaskRunning;

  private boolean isStopSlip;
  private boolean isCancelSlipTask;

  private DecimalFormat mDecimalFormat;

  public SlideZoomView(Context context) {
    this(context, null);
  }

  public SlideZoomView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SlideZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SlideZoomView, 0, 0);
    mNormalTextColor = a.getColor(R.styleable.SlideZoomView_number_view_normal_text_color,
        getResources().getColor(R.color.number_view_normal_text_color));
    mSelectTextColor = a.getColor(R.styleable.SlideZoomView_number_view_select_text_color,
        getResources().getColor(R.color.number_view_select_text_color));
    mNormalTextSize =
        a.getInteger(R.styleable.SlideZoomView_number_view_normal_text_size, NORMAL_TEXT_SIZE);
    mSelectTextSize =
        a.getInteger(R.styleable.SlideZoomView_number_view_select_text_size, SELECT_TEXT_SIZE);
    a.recycle();

    mNumberMax = NUMBERS.length + SPACE_NUM;

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

    mSlipTask = new SlipTask();
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

    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        if (!isCancelSlipTask) {
          startSlipTask();
        }
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    });
  }

  public void cancelSlipTask() {
    isCancelSlipTask = true;
    isFirst = true;
    isSlipTaskRunning = false;
    isStopSlip = true;
  }

  private void startSlipTask() {
    if (!isSlipTaskRunning) {
      isSlipTaskRunning = true;
      mLastProgressValue = (int) (mLastScore * 10.0f) - 10;
      mTempValue = 0;
      postDelayed(mSlipTask, 300);
    }
  }

  public class SlipTask implements Runnable {

    @Override public void run() {

      if (mSeekBar != null && isSlipTaskRunning) {
        mSeekBar.setProgress(mTempValue);

        mCurrentProgress = mTempValue;
        mCurrentIndex = (mCurrentProgress / NUMBERS.length) + 1;
        mCurrentValue = mCurrentIndex + ((mCurrentProgress % 10) / 10.0f);

        mTempValue++;
        if (mTempValue <= mLastProgressValue) {
          postDelayed(this, ITEM_SLIP_TIME);
        } else {

          //init anim finish
          isFirst = true;
          isSlipTaskRunning = false;

          isStopSlip = true;
        }
      }
    }
  }

  public void removeTask() {
    removeCallbacks(mSlipTask);
    isSlipTaskRunning = false;
  }

  @Override protected void onDraw(Canvas canvas) {
    //start draw text
    for (int i = 0; i < NUMBERS.length; i++) {
      String text = NUMBERS[i];
      int textIndex = i + 1;

      if (isStopSlip) {
        if (mCurrentIndex == textIndex) {

          if (mCurrentIndex != 1) {
            mOffset += mItemWidth;
          }
        }

        if (textIndex - mCurrentIndex == 1) {
          mOffset += mItemWidth;
        }

        setDrawTextValue(text, i, textIndex, canvas);
        continue;
      }

      if (mCurrentProgress > 0 && mCurrentProgress > mBeforeProgress) {//left to right slip
        //item % value
        float value = (mCurrentProgress % 10) / 10.0f;

        if (textIndex - mCurrentIndex == 0) {//current text
          mOffset = mOffset + mItemWidth - value * mItemWidth;
        }

        if (textIndex - mCurrentIndex == 1) {//current+1
          mOffset += mItemWidth;
        }

        if (textIndex - mCurrentIndex == 2) {//current+2
          mOffset = mOffset + value * mItemWidth;
        }
      } else if (mCurrentProgress > 0 && mCurrentProgress < mBeforeProgress) {//right to left slip

        //item % value
        float value = 1 - ((mCurrentProgress % 10) / 10.0f);

        if (textIndex - mCurrentIndex == 0 && mCurrentIndex != 1) {//current text
          mOffset += mItemWidth;
        }

        if (textIndex - mCurrentIndex == -1) {//current - 1
          mOffset = mOffset + value * mItemWidth;
        }

        if (textIndex - mCurrentIndex == 1 && mCurrentIndex != 1) {//current + 1
          mOffset = mOffset + mItemWidth - value * mItemWidth;
        }

        if (mCurrentIndex == 1 && textIndex - mCurrentIndex == 1) {
          mOffset = mOffset + mItemWidth;
        }
      }

      if (mCurrentProgress == 0) {
        if (mCurrentIndex == 1 && textIndex - mCurrentIndex == 1) {
          mOffset = mOffset + mItemWidth;
        }
      }

      setDrawTextValue(text, i, textIndex, canvas);
    }

    //reset params
    mOffset = 0;
    mBeforeProgress = mCurrentProgress;
  }

  private void setDrawTextValue(String text, int i, int textIndex, Canvas canvas) {
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
    int maxProgress = (NUMBERS.length - 1) * 10;
    mSeekBar.setMax(maxProgress);//set seekBar max value
  }

  public SeekBar getSeekBar() {
    return mSeekBar;
  }

  public void setScore(double score) {
    mLastScore = score;
    stopSlip();

    if (isCancelSlipTask) {
      mLastProgressValue = (int) (mLastScore * 10.0f) - 10;
      mSeekBar.setProgress(mLastProgressValue);

      mCurrentProgress = mLastProgressValue;
      mCurrentIndex = (mCurrentProgress / NUMBERS.length) + 1;
      mCurrentValue = mCurrentIndex + ((mCurrentProgress % 10) / 10.0f);
    }
  }

  public String getCurrentValue() {
    if (mDecimalFormat == null) {
      mDecimalFormat = new DecimalFormat("0.0");
    }
    return mDecimalFormat.format(mCurrentValue);
  }

  public void setNumberEnable(boolean isEnable) {
    mSeekBar.setEnabled(isEnable);

    isStopSlip = true;
  }

  private void stopSlip() {
    if (isFirst) {
      isStopSlip = true;
    }
  }

  private void startSlip() {
    if (isFirst) {
      isStopSlip = false;
    }
  }

  @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    startSlip();

    mCurrentProgress = progress;
    mCurrentIndex = (mCurrentProgress / NUMBERS.length) + 1;
    mCurrentValue = mCurrentIndex + ((mCurrentProgress % 10) / 10.0f);

    if (mCurrentProgress % 2 == 0) {
      postInvalidate();
      if (mOnSeekBarChangeListener != null) {
        mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        mOnSeekBarChangeListener.onStateChangeBack(mCurrentProgress, mCurrentIndex,
            getCurrentValue());
      }
    }
  }

  @Override public void onStartTrackingTouch(SeekBar seekBar) {
    if (mOnSeekBarChangeListener != null) {
      mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
    }
  }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    stopSlip();

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