package com.zerotoonelabs.pickerview.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import com.zerotoonelabs.pickerview.LoopScrollListener;
import com.zerotoonelabs.pickerview.LoopView;
import com.zerotoonelabs.pickerview.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * PopWindow for Date Pick
 */
public class DatePickerPopWin extends PopupWindow implements OnClickListener {

    private static final int DEFAULT_MIN_YEAR = 1900;
    public Button cancelBtn;
    public Button confirmBtn;
    public LoopView yearLoopView;
    public LoopView monthLoopView;
    public LoopView dayLoopView;
    public View pickerContainerV;
    public View contentView;//root view

    private int minYear; // min year
    private int maxYear; // max year
    private int minMonth;
    private int maxMonth;
    private int minDay;
    private int maxDay;

    private long minDate;
    private long maxDate;

    private int yearPos = 0;
    private int monthPos = 0;
    private int dayPos = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private boolean showDayMonthYear;

    List<String> yearList = new ArrayList();
    List<String> monthList = new ArrayList();
    List<String> dayList = new ArrayList();

    public static class Builder{

        //Required
        private Context context;
        private OnDatePickedListener listener;
        public Builder(Context context, OnDatePickedListener listener){
            this.context = context;
            this.listener = listener;
        }

        //Option
        private boolean showDayMonthYear = false;
        private int minYear = DEFAULT_MIN_YEAR;
        private int maxYear = Calendar.getInstance().get(Calendar.YEAR)+1;
        private int minMonth = 1;
        private int maxMonth = 12;
        private int minDay = 1;
        private int maxDay = 31;

        private long minDate = 0;
        private long maxDate = 0;

        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String dateChose = getStrDate();
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;

        public Builder minDate(long minDate){
            this.minDate = minDate;

            Date date = new Date(minDate);
            Calendar cal = Calendar.getInstance();

            cal.setTime(date);
            this.minYear = cal.get(Calendar.YEAR);
            this.minMonth = cal.get(Calendar.MONTH) + 1;
            this.minDay = cal.get(Calendar.DAY_OF_MONTH);

            return this;
        }

        public Builder maxDate(long maxDate){
            this.maxDate = maxDate;

            Date date = new Date(maxDate);
            Calendar cal = Calendar.getInstance();

            cal.setTime(date);
            this.maxYear = cal.get(Calendar.YEAR);
            this.maxMonth = cal.get(Calendar.MONTH) + 1;
            this.maxDay = cal.get(Calendar.DAY_OF_MONTH);

            return this;
        }

        public Builder textCancel(String textCancel){
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm){
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder dateChose(String dateChose){
            this.dateChose = dateChose;
            return this;
        }

        public Builder colorCancel(int colorCancel){
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm){
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize){
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize){
            this.viewTextSize = textSize;
            return this;
        }

        public DatePickerPopWin build(){
            if(minDate > maxDate){
                throw new IllegalArgumentException();
            }
            return new DatePickerPopWin(this);
        }

        public Builder showDayMonthYear(boolean useDayMonthYear) {
            this.showDayMonthYear = useDayMonthYear;
            return this;
        }
    }

    public DatePickerPopWin(Builder builder){
        this.minYear = builder.minYear;
        this.maxYear = builder.maxYear;
        this.minMonth = builder.minMonth;
        this.maxMonth = builder.maxMonth;
        this.minDay = builder.minDay;
        this.maxDay = builder.maxDay;
        this.minDate = builder.minDate;
        this.maxDate = builder.maxDate;

        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.showDayMonthYear = builder.showDayMonthYear;
        setSelectedDate(builder.dateChose);
        initView();
    }

    private OnDatePickedListener mListener;

    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(showDayMonthYear ? R.layout.layout_date_picker_inverted : R.layout.layout_date_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        cancelBtn.setTextColor(colorCancel);
        cancelBtn.setTextSize(btnTextsize);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        confirmBtn.setTextColor(colorConfirm);
        confirmBtn.setTextSize(btnTextsize);
        yearLoopView = (LoopView) contentView.findViewById(R.id.picker_year);
        monthLoopView = (LoopView) contentView.findViewById(R.id.picker_month);
        dayLoopView = (LoopView) contentView.findViewById(R.id.picker_day);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        //set checked listen
        yearLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                yearPos = item;
                //initDayPickerView();
            }
        });
        monthLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                monthPos = item;
                //initDayPickerView();
            }
        });
        dayLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                dayPos = item;
            }
        });

        initPickerViews(); // init year and month loop view
        //initDayPickerView(); //init day loop view

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        if(!TextUtils.isEmpty(textConfirm)){
            confirmBtn.setText(textConfirm);
        }

        if(!TextUtils.isEmpty(textCancel)){
            cancelBtn.setText(textCancel);
        }

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init year and month loop view,
     * Let the day loop view be handled separately
     */
    private void initPickerViews() {
        int yearCount = maxYear - minYear;
        int monthCount = 0;

        //year
        for (int i = 0; i <= yearCount; i++) {
            yearList.add(format2LenStr(minYear + i));
        }

        //month
        if (yearCount == 0) {
            for (int i = minMonth; i <= maxMonth; i++) {
                monthList.add(format2LenStr(i));
                monthCount++;
            }
        } else if (yearCount == 1) {
            for (int i = minMonth; i <= 12; i++) {
                monthList.add(format2LenStr(i));
                monthCount++;
            }
            for (int i = 1; i <= maxMonth; i++) {
                monthList.add(format2LenStr(i));
                monthCount++;
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                monthList.add(format2LenStr(i));
                monthCount++;
            }
        }

        //day
        Date date = new Date(minDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayMaxInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (monthCount == 1) {
            for (int i = minDay; i <= maxDay; i++) {
                dayList.add(format2LenStr(i));
            }
        } else if (monthCount == 2) {
            for (int i = minDay; i <= dayMaxInMonth; i++) {
                dayList.add(format2LenStr(i));
            }
            for (int i = 1; i <= maxDay; i++) {
                if (!dayList.contains(format2LenStr(i))) {
                    dayList.add(format2LenStr(i));
                }
            }
        } else {
            for (int i = 1; i <= 31; i++) {
                dayList.add(format2LenStr(i));
            }
        }

        yearLoopView.setDataList((ArrayList) yearList);
        yearLoopView.setInitPosition(yearPos);

        monthLoopView.setDataList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);

        dayLoopView.setDataList((ArrayList) dayList);
        dayLoopView.setInitPosition(dayPos);
    }

    /**
     * Init day item
     */
    private void initDayPickerView() {

        int dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
        dayList = new ArrayList<String>();

        calendar.set(Calendar.YEAR, minYear + yearPos);
        calendar.set(Calendar.MONTH, monthPos);

        //get max day in month
        dayMaxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < dayMaxInMonth; i++) {
            dayList.add(format2LenStr(i + 1));
        }

        dayLoopView.setDataList((ArrayList) dayList);
        dayLoopView.setInitPosition(dayPos);
    }

    /**
     * set selected date position value when initView.
     *
     * @param dateStr
     */
    public void setSelectedDate(String dateStr) {

        if (!TextUtils.isEmpty(dateStr)) {

            long milliseconds = getLongFromyyyyMMdd(dateStr);
            Calendar calendar = Calendar.getInstance(Locale.US);
            long currentTime = calendar.getTimeInMillis();

            if (milliseconds != -1) {
                calendar.setTimeInMillis(currentTime);

                monthPos = 0;
                dayPos = 0;

                if (currentTime > minDate && currentTime < maxDate) {
                    yearPos = calendar.get(Calendar.YEAR) - minYear;
                } else {
                    yearPos = 0;
                }
            }
        }
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {

        if (v == contentView || v == cancelBtn) {

            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {

                String year = yearList.get(yearPos);
                String month = monthList.get(monthPos);
                String day = dayList.get(dayPos);

                String date = year + "-" + month + "-" + day;

                mListener.onDatePickCompleted(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), date);
            }

            dismissPopWin();
        }
    }

    /**
     * get long from yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static long getLongFromyyyyMMdd(String date) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date parse = null;
        try {
            parse = mFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse != null) {
            return parse.getTime();
        } else {
            return -1;
        }
    }

    public static String getStrDate() {
        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return dd.format(new Date());
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {

        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnDatePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param year
         * @param month
         * @param day
         * @param dateDesc  yyyy-MM-dd
         */
        void onDatePickCompleted(int year, int month, int day,
                                 String dateDesc);
    }
}
