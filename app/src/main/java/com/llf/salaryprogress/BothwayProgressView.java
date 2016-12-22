package com.llf.salaryprogress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by llf on 2016/12/20.
 * 类拉勾薪资双向选择的View
 */

public class BothwayProgressView extends RelativeLayout {
    private LinearLayout slideLeft, slideRight;
    private TextView salaryLower, salaryUpper;
    private View viewProgress;
    private ViewDragHelper mViewDragHelper;

    private int width;
    private int max = 34;//总共是35个点
    private float unitLong;//单位薪资长度
    private int currentPositionLeft, currentPotionRight;//滑动时的左右位置范围
    private int currentSalaryLeft = 1, currentSalaryRight = 100;//薪资
    private boolean isFirst = true;
    private int mLeftLower, mTopLower, mLeftUpper, mTopUpper;//滑动时确定位置用

    private Paint mPaint;
    private Path mPath, dstPath;
    private PathMeasure mPathMeasure;
    private int pathLeft, pathRight;//绿色线条左右范围

    private SalaryProgressListener salaryProgressListener;

    public BothwayProgressView(Context context) {
        this(context, null);
    }

    public BothwayProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BothwayProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.progress_nomal));
        mPaint.setStrokeWidth(dpTpPx(4));
        mPaint.setStyle(Paint.Style.STROKE);
        mPath = new Path();
        dstPath = new Path();
        initViewDrag();
    }

    private void initViewDrag() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f,new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == slideLeft || child == slideRight;
            }

            /**
             * 对移动的边界进行控制
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                int leftBound = currentPositionLeft;
                int rightBound = currentPotionRight == 0 ? getWidth() - slideRight.getWidth() : currentPotionRight - slideRight.getWidth();
                int newLeft = Math.min(Math.max(left, leftBound), rightBound);
                return newLeft;
            }

            /**
             * 设置上下不能滑动
             */
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }

            /**
             * 返回拖拽的范围，不对拖拽进行真正的限制，仅仅决定了动画执行速度
             * 需要子view中有点击事件时添加
             */
            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);

                if (capturedChild == slideLeft) {
                    currentPositionLeft = 0;
                    bringChildToFront(slideLeft);//改变层级关系
                }

                if (capturedChild == slideRight) {
                    currentPotionRight = 0;
                    bringChildToFront(slideRight);
                }

            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView == slideLeft) {
                    int valuesLeft = (int) (left / unitLong);
                    if (valuesLeft < 25) {
                        currentSalaryLeft = valuesLeft + 1;
                    } else if (valuesLeft < 30) {
                        switch (valuesLeft){
                            case 25:
                                currentSalaryLeft = 30;
                                break;
                            case 26:
                                currentSalaryLeft = 35;
                                break;
                            case 27:
                                currentSalaryLeft = 40;
                                break;
                            case 28:
                                currentSalaryLeft = 45;
                                break;
                            case 29:
                                currentSalaryLeft = 50;
                                break;
                        }
                    } else {
                        switch (valuesLeft){
                            case 30:
                                currentSalaryLeft = 60;
                                break;
                            case 31:
                                currentSalaryLeft = 70;
                                break;
                            case 32:
                                currentSalaryLeft = 80;
                                break;
                            case 33:
                                currentSalaryLeft = 90;
                                break;
                            case 34:
                                currentSalaryLeft = 100;
                                break;
                        }
                    }
                    pathLeft = left;
                    mLeftLower = slideLeft.getLeft();
                    salaryLower.setText(currentSalaryLeft + "K");
                }

                if (changedView == slideRight) {
                    int valuesRight = (int) (left / unitLong);
                    if (valuesRight < 25) {
                        currentSalaryRight = valuesRight + 1;
                    } else if (valuesRight < 30) {
                        switch (valuesRight){
                            case 25:
                                currentSalaryRight = 30;
                                break;
                            case 26:
                                currentSalaryRight = 35;
                                break;
                            case 27:
                                currentSalaryRight = 40;
                                break;
                            case 28:
                                currentSalaryRight = 45;
                                break;
                            case 29:
                                currentSalaryRight = 50;
                                break;
                        }
                    } else {
                        switch (valuesRight){
                            case 30:
                                currentSalaryRight = 60;
                                break;
                            case 31:
                                currentSalaryRight = 70;
                                break;
                            case 32:
                                currentSalaryRight = 80;
                                break;
                            case 33:
                                currentSalaryRight = 90;
                                break;
                            case 34:
                                currentSalaryRight = 100;
                                break;
                        }
                    }
                    pathRight = left;
                    mLeftUpper = slideRight.getLeft();
                    salaryUpper.setText(currentSalaryRight + "K");
                }

                if (salaryProgressListener != null) {
                    salaryProgressListener.salaryProgress(currentSalaryLeft, currentSalaryRight);
                }
                postInvalidate();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);

                if (releasedChild == slideLeft) {
                    currentPositionLeft = slideLeft.getLeft();
                }

                if (releasedChild == slideRight) {
                    currentPotionRight = slideRight.getRight();
                }

            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);

                switch (state) {
                    //不拖拽状态
                    case ViewDragHelper.STATE_IDLE:
                        salaryLower.setBackgroundResource(R.drawable.bg_number_unselect);
                        salaryUpper.setBackgroundResource(R.drawable.bg_number_unselect);
                        break;
                    //拖拽中
                    case ViewDragHelper.STATE_DRAGGING:
                        if (mViewDragHelper.getCapturedView() == slideLeft) {
                            salaryLower.setBackgroundResource(R.drawable.bg_number_seleted);

                        } else {
                            salaryUpper.setBackgroundResource(R.drawable.bg_number_seleted);
                        }
                        break;
                    //view设置中
                    case ViewDragHelper.STATE_SETTLING:
                        break;
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        unitLong = (float) ((width - dpTpPx(40)) / max);
        pathLeft = 0;
        pathRight = viewProgress.getMeasuredWidth();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        slideLeft = (LinearLayout) findViewById(R.id.slide_left);
        slideRight = (LinearLayout) findViewById(R.id.slide_right);
        salaryLower = (TextView) findViewById(R.id.tv_salary_left);
        salaryUpper = (TextView) findViewById(R.id.tv_salary_right);
        viewProgress = findViewById(R.id.view_progress);
    }

    /**
     * 是否该拦截当前事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 处理事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (isFirst) {
            mLeftLower = slideLeft.getLeft();
            mTopLower = slideLeft.getTop();
            mLeftUpper = slideRight.getLeft();
            mTopUpper = slideRight.getTop();
            isFirst = false;
        }

        //设置view的位置
        slideLeft.layout(mLeftLower, mTopLower, mLeftLower + slideLeft.getMeasuredWidth(), mTopLower + slideLeft.getMeasuredHeight());
        slideRight.layout(mLeftUpper, mTopUpper, mLeftUpper + slideRight.getMeasuredWidth(), mTopUpper + slideRight.getMeasuredHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mPath.reset();
        dstPath.reset();
        mPath.moveTo(viewProgress.getLeft(), viewProgress.getBottom() - viewProgress.getMeasuredHeight() / 2);
        mPath.lineTo(viewProgress.getRight(), viewProgress.getBottom() - viewProgress.getMeasuredHeight() / 2);
        mPathMeasure = new PathMeasure(mPath, false);
        mPathMeasure.getSegment(pathLeft, pathRight, dstPath, true);

        canvas.drawPath(dstPath, mPaint);
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

    /**
     * 设置当钱薪资范围的监听
     */
    public void setSalaryProgressListener(SalaryProgressListener salaryProgressListener) {
        this.salaryProgressListener = salaryProgressListener;
    }

    public interface SalaryProgressListener {
        void salaryProgress(int salaryLeft, int salaryRight);
    }
}
