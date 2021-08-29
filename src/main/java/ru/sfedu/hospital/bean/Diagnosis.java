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
public class Diagnosis extends Documents{
    @CsvBindByName
    private long patientId;
    @CsvBindByName
    private String diagnosis;
    @CsvBindByName
    private String medicine;
    @CsvBindByName
    private String medicalTests;

    public Diagnosis() {
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getMedicalTests() {
        return medicalTests;
    }

    public void setMedicalTests(String medicalTests) {
        this.medicalTests = medicalTests;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Diagnosis other = (Diagnosis) obj;
        if (this.patientId != other.patientId) {
            return false;
        }
        if (!Objects.equals(this.diagnosis, other.diagnosis)) {
            return false;
        }
        if (!Objects.equals(this.medicine, other.medicine)) {
            return false;
        }
        if (!Objects.equals(this.medicalTests, other.medicalTests)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Diagnosis{" + "patientId=" + patientId + ", diagnosis=" + diagnosis + ", medicine=" + medicine + ", medicalTests=" + medicalTests + '}';
    }

    
}
