package com.carvalho.wushuwatchv3;

import androidx.room.*;

import java.util.List;
@Dao
public interface ScheduleDAO {

    @Query("SELECT * FROM schedule")
    List<Schedule> getAll();

    @Query("SELECT * FROM schedule WHERE Dia LIKE :dia AND Mes LIKE :mes")
    List<Schedule> getByDay(int dia, int mes);

    @Insert
    void insertSchedule(Schedule... schedule);

    // Delete all
    @Query("DELETE FROM schedule")
    void deleteSchedule();

}


