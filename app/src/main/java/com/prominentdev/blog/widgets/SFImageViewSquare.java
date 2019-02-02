package com.prominentdev.blog.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SFImageViewSquare extends ImageView {

    public SFImageViewSquare(Context context) {
        super(context);
    }

    public SFImageViewSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SFImageViewSquare(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec); // This is the key that will make the height equivalent to its width
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}