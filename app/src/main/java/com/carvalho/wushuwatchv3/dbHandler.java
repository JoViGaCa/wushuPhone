package com.carvalho.wushuwatchv3;

import androidx.room.*;
@Database(entities={Schedule.class}, version=1)
public abstract class dbHandler extends RoomDatabase{
    public abstract ScheduleDAO schDAO();


}