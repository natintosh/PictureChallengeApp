package org.gdhote.gdhotecodegroup.pixcha.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageView extends AppCompatImageView {
    
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getContext().getResources().getDisplayMetrics().widthPixels;
//        int height = getContext().getResources().getDisplayMetrics().heightPixels;
//
//        int dim = Math.min(width, height);
//        setMeasuredDimension(dim, dim);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
