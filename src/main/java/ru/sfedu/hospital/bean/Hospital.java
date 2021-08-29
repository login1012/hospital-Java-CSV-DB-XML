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
public class Hospital {
    @CsvBindByName
    private long id;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private int wardsNumber;

    public Hospital() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWardsNumber() {
        return wardsNumber;
    }

    public void setWardsNumber(int wardsNumber) {
        this.wardsNumber = wardsNumber;
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
        final Hospital other = (Hospital) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.wardsNumber != other.wardsNumber) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Hospital{" + "id=" + id + ", name=" + name + ", wardsNumber=" + wardsNumber + '}';
    }

}
