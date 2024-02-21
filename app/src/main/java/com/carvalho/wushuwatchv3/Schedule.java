package com.carvalho.wushuwatchv3;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    public int uid;

   @ColumnInfo(name="Evento")
   public String evento;

    @ColumnInfo(name="Dia")
    public String dia;

    @ColumnInfo(name="Mes")
    public String mes;

    @ColumnInfo(name="Hora")
    public String hora;

    @ColumnInfo(name="Minutos")
    public String min;


}
