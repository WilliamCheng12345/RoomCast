package com.williamcheng.roomcast.classes;

import android.app.AlarmManager;

public class Interval {
   public static final long ONCE = 0;
   public static final long HOURLY = AlarmManager.INTERVAL_HOUR;
   public static final long DAILY = AlarmManager.INTERVAL_DAY;
   public static final long WEEKLY = AlarmManager.INTERVAL_DAY*7;
   public static final long MONTHLY_START = 1;
   public static final long MONTHLY_END = 2;
}
