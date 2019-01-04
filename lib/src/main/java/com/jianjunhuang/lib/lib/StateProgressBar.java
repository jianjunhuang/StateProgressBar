package com.jianjunhuang.lib.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StateProgressBar extends View {

  private static final int FINISH_COLOR = Color.parseColor("#FF22AC38");
  private static final int UNFINISH_COLOR = Color.parseColor("#FFD2D2D2");
  private static final int TEXT_FINISH_COLOR = Color.BLACK;
  private static final int TEXT_UNFINISH_COLOR = Color.parseColor("#FFCCCCCC");
  private static final int RING_RADIUS = 20;
  private static final int DOT_RADIUS = 10;
  private static final int TEXT_SIZE = 24;
  private static final float TEXT_MARGIN = 16;
  private Paint mStatePaint;

  private Paint mStateLinePaint;

  private Paint mStateTextPaint;

  private Paint mDotPaint;

  private int mFinishColor = FINISH_COLOR;
  private int mUnFinishColor = UNFINISH_COLOR;
  private int mTextFinishColor = TEXT_FINISH_COLOR;
  private int mTextUnFinishColor = TEXT_UNFINISH_COLOR;

  private float mStateRadius = RING_RADIUS;
  private float mDotRadius = DOT_RADIUS;
  private float mTextSize = TEXT_SIZE;

  private float mMargin = TEXT_MARGIN;

  private int mMinWidth = 0;

  private int mStatePos = 0;

  private @Orientation
  int mOrientation = Orientation.HORIZONTAL;

  private List<String> mStates = new ArrayList<>();
  private List<Rect> mTextRect = new ArrayList<>();

  public StateProgressBar(Context context) {
    this(context, null);

  }

  public StateProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public StateProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mStatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    mStateLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mStateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mDotPaint.setColor(Color.WHITE);

    if (attrs != null) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar);
      if (typedArray != null) {
        mStateRadius = typedArray
            .getDimension(R.styleable.StateProgressBar_RingRadius, RING_RADIUS);
        mDotRadius = typedArray
            .getDimension(R.styleable.StateProgressBar_centerDotRadius, DOT_RADIUS);
        mMargin = typedArray.getDimension(R.styleable.StateProgressBar_textMarginTop, TEXT_MARGIN);
        mTextSize = typedArray.getDimension(R.styleable.StateProgressBar_stateTextSize, TEXT_SIZE);
        mTextFinishColor = typedArray
            .getColor(R.styleable.StateProgressBar_textFinishColor, TEXT_FINISH_COLOR);
        mTextUnFinishColor = typedArray
            .getColor(R.styleable.StateProgressBar_textUnFinishColor, TEXT_UNFINISH_COLOR);
        mFinishColor = typedArray.getColor(R.styleable.StateProgressBar_finishColor, FINISH_COLOR);
        mUnFinishColor = typedArray
            .getColor(R.styleable.StateProgressBar_unFinishColor, UNFINISH_COLOR);
        typedArray.recycle();
      }
    }

    mStatePaint.setColor(mUnFinishColor);

    mStateLinePaint.setColor(mUnFinishColor);
    mStateLinePaint.setStyle(Style.STROKE);

    mStateTextPaint.setTextSize(mTextSize);
    mStateTextPaint.setColor(Color.BLACK);

    mStateTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, 500, false));

    Path path = new Path();
    path.addCircle(0, 0, 3, Direction.CCW);
    PathDashPathEffect mEffects = new PathDashPathEffect(path, 12, 12,
        PathDashPathEffect.Style.ROTATE);
    mStateLinePaint.setPathEffect(mEffects);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    setMeasuredDimension(measuredWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  private int measureHeight(int heightMeasureSpec) {
    int size = MeasureSpec.getSize(heightMeasureSpec);
    int mode = MeasureSpec.getMode(heightMeasureSpec);

    int height =
        (int) ((mStateRadius * 2) + mMargin + mTextSize + getPaddingTop() + getPaddingBottom());

    if (mode == MeasureSpec.EXACTLY) {
      return size > height ? size : height;
    }

    return height;
  }

  private int measuredWidth(int widthMeasureSpec) {
    int size = MeasureSpec.getSize(widthMeasureSpec);
    int mode = MeasureSpec.getMode(widthMeasureSpec);

    if (mode == MeasureSpec.EXACTLY) {
      return size > mMinWidth ? size : mMinWidth;
    }

    return mMinWidth;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (int i = 0; i < mStates.size(); i++) {
      int x = getXByPos(i);

      if (i <= mStatePos) {
        mStateTextPaint.setColor(mTextFinishColor);
        mStatePaint.setColor(mFinishColor);
        mStateLinePaint.setColor(mFinishColor);
      } else {
        mStateLinePaint.setColor(mUnFinishColor);
        mStatePaint.setColor(mUnFinishColor);
        mStateTextPaint.setColor(mTextUnFinishColor);
      }
      //draw line
      if ((i - 1) >= 0) {
        canvas.drawLine(x, mStateRadius, getXByPos(i - 1) + mStateRadius, mStateRadius,
            mStateLinePaint);
      }
      //draw circle
      canvas.drawCircle(x, mStateRadius, mStateRadius, mStatePaint);
      if (i <= mStatePos) {
        canvas.drawCircle(x, mStateRadius, mDotRadius, mDotPaint);
      }
      //draw text
      String text = mStates.get(i);
      Rect rect = mTextRect.get(i);
      canvas.drawText(text, x - (rect.width() >> 1), getHeight() - getPaddingBottom() - 10,
          mStateTextPaint);
    }
  }

  private int getXByPos(int pos) {
    return getWidth() / mStates.size() * (pos + 1) - getFirstX();
  }

  private int getFirstX() {
    return getWidth() / (mStates.size() << 1);
  }

  public void addState(String... state) {
    mStates.addAll(Arrays.asList(state));
    //get all text bounds
    mMinWidth = 0;
    for (String s : mStates) {
      Rect rect = new Rect();
      mStateTextPaint.getTextBounds(s, 0, s.length(), rect);
      mTextRect.add(rect);
      mMinWidth += rect.width();
    }
    requestLayout();
  }

  public void refreshState(String... state) {
    mStates.clear();
    mStates.addAll(Arrays.asList(state));
    //get all text bounds
    mMinWidth = 0;
    for (String s : mStates) {
      Rect rect = new Rect();
      mStateTextPaint.getTextBounds(s, 0, s.length(), rect);
      mTextRect.add(rect);
      mMinWidth += rect.width();
    }
    requestLayout();
  }

  @IntDef({Orientation.HORIZONTAL, Orientation.VERTICAL})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Orientation {

    int HORIZONTAL = 0;
    int VERTICAL = 1;
  }

  public void setStatePos(int pos) {
    if (pos < 0 || pos >= mStates.size()) {
      throw new IndexOutOfBoundsException(
          String.format("state list size = %d ,index = %d", mStates.size(), pos));
    }
    this.mStatePos = pos;
    invalidate();
  }

}
