package zhan.slidezoomview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.SeekBar
import java.text.DecimalFormat

class SlideZoomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener {

    var numberStringArray = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    private var mNormalTextColor: Int = 0
    private var mSelectTextColor: Int = 0

    private var mNormalTextSize: Int = 0
    private var mSelectTextSize: Int = 0

    private var mNumberMax: Int = 0

    //set seekBar max value
    var seekBar: SeekBar? = null
        set(seekBar) {
            field = seekBar
            val maxProgress = (numberStringArray.size - 1) * 10
            this.seekBar!!.max = maxProgress
        }

    private var mItemHeight: Int = 0
    private var mItemWidth: Float = 0.toFloat()

    private var mOffset: Float = 0.toFloat()
    private var mCurrentProgress = 0
    private var mBeforeProgress = 0

    private var mCurrentIndex = 1

    private var mTempValue = 0
    private var mLastScore = 0.0
    private var mLastProgressValue = 0

    private var mCurrentValue: Double = 0.toDouble()

    private var mPaint: Paint? = null
    private var mSelectPaint: Paint? = null
    private var mBounds: Rect? = null

    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null

    private var mSlipTask: SlipTask? = null

    private var isFirst: Boolean = false
    private var isSlipTaskRunning: Boolean = false

    private var isStopSlip: Boolean = false
    private var isCancelSlipTask: Boolean = false

    private var mDecimalFormat: DecimalFormat? = null

    private val currentValue: String
        get() {
            if (mDecimalFormat == null) {
                mDecimalFormat = DecimalFormat("0.0")
            }
            return mDecimalFormat!!.format(mCurrentValue)
        }

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SlideZoomView, 0, 0)
        mNormalTextColor = a.getColor(R.styleable.SlideZoomView_number_view_normal_text_color,
                resources.getColor(R.color.number_view_normal_text_color))
        mSelectTextColor = a.getColor(R.styleable.SlideZoomView_number_view_select_text_color,
                resources.getColor(R.color.number_view_select_text_color))
        mNormalTextSize = a.getInteger(R.styleable.SlideZoomView_number_view_normal_text_size, NORMAL_TEXT_SIZE)
        mSelectTextSize = a.getInteger(R.styleable.SlideZoomView_number_view_select_text_size, SELECT_TEXT_SIZE)
        a.recycle()

        mNumberMax = numberStringArray.size + SPACE_NUM

        //init text paint
        mPaint = Paint()
        mPaint!!.color = mNormalTextColor
        mPaint!!.isAntiAlias = true
        mPaint!!.textSize = mNormalTextSize.toFloat()

        //init color paint
        mSelectPaint = Paint()
        mSelectPaint!!.color = mSelectTextColor
        mSelectPaint!!.isAntiAlias = true
        mSelectPaint!!.textSize = mSelectTextSize.toFloat()

        //init text bound rect
        mBounds = Rect()

        mSlipTask = SlipTask()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //init text width and height
        mItemHeight = measuredHeight
        mItemWidth = (measuredWidth / mNumberMax).toFloat()

        //check set seek bar
        if (seekBar == null) {
            throw RuntimeException("you must call set seekBar method!")
        }
        seekBar!!.setOnSeekBarChangeListener(this)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!isCancelSlipTask) {
                    startSlipTask()
                }
                viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
    }

    fun cancelSlipTask() {
        isCancelSlipTask = true
        isFirst = true
        isSlipTaskRunning = false
        isStopSlip = true
    }

    private fun startSlipTask() {
        if (!isSlipTaskRunning) {
            isSlipTaskRunning = true
            mLastProgressValue = (mLastScore * 10.0f).toInt() - 10
            mTempValue = 0
            postDelayed(mSlipTask, 300)
        }
    }

    inner class SlipTask : Runnable {

        override fun run() {

            if (seekBar != null && isSlipTaskRunning) {
                seekBar!!.progress = mTempValue

                mCurrentProgress = mTempValue
                mCurrentIndex = mCurrentProgress / numberStringArray.size + 1
                mCurrentValue = (mCurrentIndex + mCurrentProgress % 10 / 10.0f).toDouble()

                mTempValue++
                if (mTempValue <= mLastProgressValue) {
                    postDelayed(this, ITEM_SLIP_TIME.toLong())
                } else {

                    //init anim finish
                    isFirst = true
                    isSlipTaskRunning = false

                    isStopSlip = true
                }
            }
        }
    }

    fun removeTask() {
        removeCallbacks(mSlipTask)
        isSlipTaskRunning = false
    }

    override fun onDraw(canvas: Canvas) {
        //start draw text
        for (i in numberStringArray.indices) {
            val text = numberStringArray[i]
            val textIndex = i + 1

            if (isStopSlip) {
                if (mCurrentIndex == textIndex) {

                    if (mCurrentIndex != 1) {
                        mOffset += mItemWidth
                    }
                }

                if (textIndex - mCurrentIndex == 1) {
                    mOffset += mItemWidth
                }

                setDrawTextValue(text, i, textIndex, canvas)
                continue
            }

            if (mCurrentProgress > 0 && mCurrentProgress > mBeforeProgress) {//left to right slip
                //item % value
                val value = mCurrentProgress % 10 / 10.0f

                if (textIndex - mCurrentIndex == 0) {//current text
                    mOffset = mOffset + mItemWidth - value * mItemWidth
                }

                if (textIndex - mCurrentIndex == 1) {//current+1
                    mOffset += mItemWidth
                }

                if (textIndex - mCurrentIndex == 2) {//current+2
                    mOffset += value * mItemWidth
                }
            } else if (mCurrentProgress in 1 until mBeforeProgress) {//right to left slip

                //item % value
                val value = 1 - mCurrentProgress % 10 / 10.0f

                if (textIndex - mCurrentIndex == 0 && mCurrentIndex != 1) {//current text
                    mOffset += mItemWidth
                }

                if (textIndex - mCurrentIndex == -1) {//current - 1
                    mOffset += value * mItemWidth
                }

                if (textIndex - mCurrentIndex == 1 && mCurrentIndex != 1) {//current + 1
                    mOffset = mOffset + mItemWidth - value * mItemWidth
                }

                if (mCurrentIndex == 1 && textIndex - mCurrentIndex == 1) {
                    mOffset += mItemWidth
                }
            }

            if (mCurrentProgress == 0) {
                if (mCurrentIndex == 1 && textIndex - mCurrentIndex == 1) {
                    mOffset += mItemWidth
                }
            }

            setDrawTextValue(text, i, textIndex, canvas)
        }

        //reset params
        mOffset = 0f
        mBeforeProgress = mCurrentProgress
    }

    private fun setDrawTextValue(text: String, i: Int, textIndex: Int, canvas: Canvas) {
        //text width
        val textX = (mItemWidth * 0.5f + i * mItemWidth + mOffset).toInt()
        mPaint!!.getTextBounds(text, 0, text.length, mBounds)

        //text height
        val textHeight = mBounds!!.height().toFloat()
        val textY = (mItemHeight * 0.5f + textHeight * 0.5f).toInt()
        val fontHeight = mPaint!!.fontMetrics.bottom - mPaint!!.fontMetrics.top
        val posValue = fontHeight / 2 - mPaint!!.fontMetrics.descent - 10f

        //text draw
        if (textIndex == mCurrentIndex) {
            canvas.drawText(text, textX.toFloat(), textY + posValue, mSelectPaint!!)
        } else {
            canvas.drawText(text, textX.toFloat(), textY + posValue, mPaint!!)
        }
    }

    fun setNormalTextColor(color: Int) {
        mNormalTextColor = color
    }

    fun setSelectTextColor(color: Int) {
        mSelectTextColor = color
    }

    fun setNormalTextSize(size: Int) {
        mNormalTextSize = size
    }

    fun setSelectTextSize(size: Int) {
        mSelectTextSize = size
    }

    fun setScore(score: Double) {
        mLastScore = score
        stopSlip()

        if (isCancelSlipTask) {
            mLastProgressValue = (mLastScore * 10.0f).toInt() - 10
            seekBar!!.progress = mLastProgressValue

            mCurrentProgress = mLastProgressValue
            mCurrentIndex = mCurrentProgress / numberStringArray.size + 1
            mCurrentValue = (mCurrentIndex + mCurrentProgress % 10 / 10.0f).toDouble()
        }
    }

    fun setNumberEnable(isEnable: Boolean) {
        seekBar!!.isEnabled = isEnable

        isStopSlip = true
    }

    private fun stopSlip() {
        if (isFirst) {
            isStopSlip = true
        }
    }

    private fun startSlip() {
        if (isFirst) {
            isStopSlip = false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        startSlip()

        mCurrentProgress = progress
        mCurrentIndex = mCurrentProgress / numberStringArray.size + 1
        mCurrentValue = (mCurrentIndex + mCurrentProgress % 10 / 10.0f).toDouble()

        if (mCurrentProgress % 2 == 0) {
            postInvalidate()
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener!!.onProgressChanged(seekBar, progress, fromUser)
                mOnSeekBarChangeListener!!.onStateChangeBack(mCurrentProgress, mCurrentIndex,
                        currentValue)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener!!.onStartTrackingTouch(seekBar)
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        stopSlip()

        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener!!.onStopTrackingTouch(seekBar)
        }
    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        mOnSeekBarChangeListener = listener
    }

    interface OnSeekBarChangeListener {

        fun onStateChangeBack(progress: Int, currentIndex: Int, currentValue: String)

        fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)

        fun onStartTrackingTouch(seekBar: SeekBar)

        fun onStopTrackingTouch(seekBar: SeekBar)
    }

    companion object {

        private const val ITEM_SLIP_TIME = 10

        private const val NORMAL_TEXT_SIZE = 40
        private const val SELECT_TEXT_SIZE = 75

        private const val SPACE_NUM = 2
    }
}