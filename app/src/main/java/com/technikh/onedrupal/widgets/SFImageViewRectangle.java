package com.technikh.onedrupal.widgets;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SFImageViewRectangle extends ImageView {

    public SFImageViewRectangle(Context context) {
        super(context);
    }

    public SFImageViewRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SFImageViewRectangle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec); // This is the key that will make the height equivalent to its width
        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() / 1.3)); //Snap to width
    }
}