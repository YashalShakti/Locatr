package com.yashal.locatr.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yashal.locatr.R;

/**
 * Created by yashal on 16/4/16.
 */
public class ContactSelector extends FrameLayout {
    private Context mContext;

    public ContactSelector(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ContactSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    public ContactSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {


        mContext = getContext();
        inflate(getContext(), R.layout.contact_selector, this);
    }
}