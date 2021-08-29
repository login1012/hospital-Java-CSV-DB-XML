/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital.bean;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

/**
 *
 * @author Liza
 */
public class Appointment extends Service{
    @CsvBindByName
    private String specialization;

    public Appointment() {
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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
        final Appointment other = (Appointment) obj;
        if (!Objects.equals(this.specialization, other.specialization)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Appointment{" + "specialization=" + specialization + '}';
    }
    
    
    
    
    
}
