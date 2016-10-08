package app.sms.com.smslooper;

import android.text.format.Time;

public class TimeClass {

    private Time time;
    public TimeClass()
    {
        this.time = new Time();
        this.time.setToNow();
    }

    public String getCurrentDateAndTime()
    {
        return time.year + "/" + (time.month + 1) + "/" + time.monthDay
                + " " + time.hour + ":" + time.minute + ":" + time.second;
    }
}
