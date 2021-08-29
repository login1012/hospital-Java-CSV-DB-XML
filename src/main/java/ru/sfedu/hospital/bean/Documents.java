/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital.bean;
import com.opencsv.bean.CsvBindByName;

/**
 *
 * @author Liza
 */
public class Documents {
    @CsvBindByName
    private long id;
    @CsvBindByName
    private boolean signature;
    @CsvBindByName
    private long serviceId;
    @CsvBindByName
    private int day;
    @CsvBindByName
    private int month;
    @CsvBindByName
    private int year;
    @CsvBindByName
    private int hour;
    @CsvBindByName
    private int minute;

    public Documents() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSignature() {
        return signature;
    }

    public void setSignature(boolean signature) {
        this.signature = signature;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Documents other = (Documents) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.signature != other.signature) {
            return false;
        }
        if (this.serviceId != other.serviceId) {
            return false;
        }
        if (this.day != other.day) {
            return false;
        }
        if (this.month != other.month) {
            return false;
        }
        if (this.year != other.year) {
            return false;
        }
        if (this.hour != other.hour) {
            return false;
        }
        if (this.minute != other.minute) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Documents{" + "id=" + id + ", signature=" + signature + ", serviceId=" + serviceId + ", day=" + day + ", month=" + month + ", year=" + year + ", hour=" + hour + ", minute=" + minute + '}';
    }


    
 }
