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
public class Employee extends Person{
    @CsvBindByName
    private String specialty;
    @CsvBindByName
    private String competence;

    public Employee() {
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
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
        final Employee other = (Employee) obj;
        if (!Objects.equals(this.specialty, other.specialty)) {
            return false;
        }
        if (!Objects.equals(this.competence, other.competence)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Employee{" + "specialty=" + specialty + ", competence=" + competence + '}';
    }
    
    
}
