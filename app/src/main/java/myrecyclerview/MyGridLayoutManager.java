package myrecyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by user on 2018/9/28.
 */

public class MyGridLayoutManager extends GridLayoutManager {
    private boolean canScroll=true;
    public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setCanScroll(boolean flag){
        this.canScroll=flag;
    }
    @Override
    public boolean canScrollVertically() {
        return canScroll&&super.canScrollVertically();
    }
}
