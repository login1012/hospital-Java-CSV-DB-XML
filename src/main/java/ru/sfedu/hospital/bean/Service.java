/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital.bean;

import com.opencsv.bean.CsvBindByName;
import ru.sfedu.hospital.enums.ServiceStatus;

/**
 *
 * @author Liza
 */
public class Service {
    @CsvBindByName
    private long id;
    @CsvBindByName
    private long patientId;
    @CsvBindByName
    private long doctorId;
    @CsvBindByName
    private ServiceStatus status; 
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

    public Service() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
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
        int hash = 7;
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
        final Service other = (Service) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.patientId != other.patientId) {
            return false;
        }
        if (this.doctorId != other.doctorId) {
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
        if (this.status != other.status) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Service{" + "id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", status=" + status + ", day=" + day + ", month=" + month + ", year=" + year + ", hour=" + hour + ", minute=" + minute + '}';
    }
 
    
    
}
