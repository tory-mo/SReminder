package sremind.torymo.by;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


public class CalendarView extends LinearLayout {

    private TextView tvPrevious;
    private TextView tvNext;
    private TextView tvMonth;
    private GridView gvCalendar;

    private EventHandler eventHandler = null;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    private static final SimpleDateFormat mMonthTitleFormat = new SimpleDateFormat("MMMM yyyy");

    static final int FIRST_DAY_OF_WEEK = 1; // Sunday = 0, Monday = 1

    public CalendarView(Context context)
    {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }

    private void initControl(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.calendar_view, this);

        tvPrevious = findViewById(R.id.tvPrevious);
        tvNext = findViewById(R.id.tvNext);
        tvMonth = findViewById(R.id.tvMonth);
        GridView gvHeader = findViewById(R.id.gvHeader);
        gvCalendar = findViewById(R.id.gvCalendar);

        gvHeader.setAdapter(new ArrayAdapter<>(context, R.layout.day_name, R.id.name_day, getResources().getStringArray(R.array.weekDays)));

        assignClickHandlers();

        updateCalendar();
    }

    private void assignClickHandlers()
    {
        tvNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                if(month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH)) {
//                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
//                } else {
//                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
//                }

                currentDate.add(Calendar.MONTH, 1);

                updateCalendar();
                if(eventHandler != null){
                    Calendar calendar = (Calendar)currentDate.clone();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date startDate = new Date(calendar.getTimeInMillis());
                    final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DATE,lastDay);
                    Date endDate = calendar.getTime();
                    eventHandler.onMonthChanged(startDate, endDate);
                }
            }
        });

        tvPrevious.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
//                    month.set((month.get(Calendar.YEAR) - 1), month.getActualMaximum(Calendar.MONTH), 1);
//                } else {
//                    month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
//                }

                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
                if(eventHandler != null){
                    Calendar calendar = (Calendar)currentDate.clone();
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    Date startDate = new Date(calendar.getTimeInMillis());
                    final int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    calendar.set(Calendar.DATE,lastDay);
                    Date endDate = calendar.getTime();
                    eventHandler.onMonthChanged(startDate, endDate);
                }
            }
        });

        gvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // handle long-press
                if (eventHandler == null)
                    return;

                Date date = (Date)parent.getItemAtPosition(position);
                eventHandler.onDayPress(date);
            }
        });
    }


    public void updateCalendar()
    {
        updateCalendar(null);
    }

    public void updateCalendar(HashSet<Date> events)
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        currentDate.set(Calendar.DATE, 1);
        int firstDay = currentDate.get(Calendar.DAY_OF_WEEK) - 1 - FIRST_DAY_OF_WEEK; //convert to start with zero
        final int lastDay = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);


        for(int i = 0; i < firstDay; i++) {
            cells.add(null);
        }

        for(int i = firstDay; i < (lastDay + firstDay); i++) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        gvCalendar.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        tvMonth.setText(mMonthTitleFormat.format(currentDate.getTime()));

    }

    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        private Context mContext;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.day, days);
            mContext = context;
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);



            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.day, parent, false);

            TextView dayView = view.findViewById(R.id.date);

            if(date == null) {
                dayView.setClickable(false);
                dayView.setFocusable(false);
            }else {
                // mark current day as focused
                // today
                int day = date.getDate();
                int month = date.getMonth();
                int year = date.getYear();

                Date today = new Date();
                int todayDay = today.getDate();
                int todayMonth = today.getMonth();
                int todayYear = today.getYear();
                boolean event = false;
                if (eventDays != null){
                    for (Date eventDate : eventDays){
                        int eventDay = eventDate.getDate();
                        int eventMonth = eventDate.getMonth();
                        int eventYear = eventDate.getYear();
                        if (eventDay == day && eventMonth == month && eventYear == year){
                            if(todayDay == day && todayMonth == month && todayYear == year) {
                                dayView.setTextColor(mContext.getResources().getColor(R.color.light_bg));
                                dayView.setBackgroundResource(R.drawable.today_episode_day);
                            }else {
                                // mark this day for event
                                dayView.setBackgroundResource(R.drawable.episode_day);
                            }
                            event = true;
                            break;
                        }
                    }
                }
                if(!event && (todayDay == day && todayMonth == month && todayYear == year)) {
                    dayView.setBackgroundResource(R.drawable.today_day);
                }else if(!event){
                    dayView.setClickable(false);
                    dayView.setFocusable(false);
                }

//                if(currDay.event){
//                    view.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v)
//                        {
//                            TextView date = v.findViewById(R.id.date);
//                            if(date != null) {
//                                String day = date.getText().toString();
//                                currentDate.set(Calendar.DATE,Integer.valueOf(day));
//                                showEpisodesForDay(mChosenMonth.getTime(), ((Activity)mContext));
//                            }
//                        }
//                    });
//                }



                // set text
                dayView.setText(String.valueOf(date.getDate()));
            }

            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onDayPress(Date date);

        void onMonthChanged(Date start, Date end);
    }

}
