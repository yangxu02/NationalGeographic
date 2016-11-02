package com.linkx.wallpaper.view.components.timeline;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.linkx.wallpaper.R;
import com.linkx.wallpaper.view.components.TextDrawable;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/2.
 */
public class TextTimelineView extends View {

    private TextDrawable mTextDrawable;
    private Drawable mStartLine;
    private Drawable mEndLine;
    private int mTextSize;
    private int mLineSize;
    private int mLineOrientation;
    private boolean mMarkerInCenter;

    private Rect mBounds;
    private Context mContext;


    public TextTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.timeline_style);
        String text = typedArray.getString(R.styleable.timeline_style_text);
        int textColor = typedArray.getColor(R.styleable.timeline_style_textColor, Color.WHITE);
        int textSize = typedArray.getColor(R.styleable.timeline_style_textSize, 30);
        int textBackgroundColor = typedArray.getColor(R.styleable.timeline_style_textBackgroundColor, Color.RED);
        Log.w("WP", text);
        mTextDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize(textSize)
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(text, textBackgroundColor, 10);
        mStartLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mEndLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_line_size, 2);
        mLineOrientation = typedArray.getInt(R.styleable.timeline_style_line_orientation, 1);
        mMarkerInCenter = typedArray.getBoolean(R.styleable.timeline_style_markerInCenter, true);
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Width measurements of the width and height and the inside view of child controls
        int w = mTextDrawable.getMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int h = mTextDrawable.getMinimumHeight() + getPaddingTop() + getPaddingBottom();

        // Width and height to determine the final view through a systematic approach to decision-making
        int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        initDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // When the view is displayed when the callback
        // Positioning Drawable coordinates, then draw
        initDrawable();
    }

    private void initDrawable() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();// Width of current custom view
        int height = getHeight();

        int cWidth = width - pLeft - pRight;// Circle width
        int cHeight = height - pTop - pBottom;

        int textWidth = mTextDrawable.getBounds().width();
        int textHeight = mTextDrawable.getBounds().height();

        if(mMarkerInCenter) { //Marker in center is true

            if(mTextDrawable != null) {
                mTextDrawable.setBounds((width/2) - (textWidth/2),(height/2) - (textHeight/2), (width/2) + (textWidth/2),(height/2) + (textHeight/2));
                mBounds = mTextDrawable.getBounds();
            }

        } else { //Marker in center us false

            if(mTextDrawable != null) {
                mTextDrawable.setBounds(pLeft,pTop,pLeft + textWidth,pTop + textHeight);
                mBounds = mTextDrawable.getBounds();
            }
        }

        int centerX = mBounds.centerX();
        int lineLeft = centerX - (mLineSize >> 1);

        if(mLineOrientation==0) {

            //Horizontal Line

            if(mStartLine != null) {
                mStartLine.setBounds(0, pTop + (mBounds.height()/2), mBounds.left, (mBounds.height()/2) + pTop + mLineSize);
            }

            if(mEndLine != null) {
                mEndLine.setBounds(mBounds.right, pTop + (mBounds.height()/2), width, (mBounds.height()/2) + pTop + mLineSize);
            }

        } else {

            //Vertical Line

            if(mStartLine != null) {
                mStartLine.setBounds(lineLeft, 0, mLineSize + lineLeft, mBounds.top);
            }

            if(mEndLine != null) {
                mEndLine.setBounds(lineLeft, mBounds.bottom, mLineSize + lineLeft, height);
            }

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mTextDrawable != null) {
            mTextDrawable.draw(canvas);
        }

        if(mStartLine != null) {
            mStartLine.draw(canvas);
        }

        if(mEndLine != null) {
            mEndLine.draw(canvas);
        }
    }

    public void setStartLine(Drawable startline) {
        mStartLine = startline;
        initDrawable();
    }

    public void setEndLine(Drawable endLine) {
        mEndLine = endLine;
        initDrawable();
    }

    public void setLineSize(int lineSize) {
        mLineSize = lineSize;
        initDrawable();
    }

    public void initLine(int viewType) {

        if(viewType == LineType.BEGIN) {
            setStartLine(null);
        } else if(viewType == LineType.END) {
            setEndLine(null);
        } else if(viewType == LineType.ONLYONE) {
            setStartLine(null);
            setEndLine(null);
        }

        initDrawable();
    }

    public static int getTimeLineViewType(int position, int total_size) {

        if(total_size == 1) {
            return LineType.ONLYONE;
        } else if(position == 0) {
            return LineType.BEGIN;
        } else if(position == total_size - 1) {
            return LineType.END;
        } else {
            return LineType.NORMAL;
        }
    }
}
