/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital;

import ru.sfedu.hospital.bean.Appointment;
import ru.sfedu.hospital.bean.Diagnosis;
import ru.sfedu.hospital.bean.Employee;
import ru.sfedu.hospital.bean.Hospital;
import ru.sfedu.hospital.bean.Hospitalization;
import ru.sfedu.hospital.bean.MedicalInsurance;
import ru.sfedu.hospital.bean.Patient;
import ru.sfedu.hospital.bean.Payment;
import ru.sfedu.hospital.bean.Survey;
import ru.sfedu.hospital.enums.HospitalizationStatus;
import ru.sfedu.hospital.enums.ServiceStatus;

/**
 *
 * @author Liza
 */
public class TestBase{
    
    public Hospitalization createHospitalization(long id, boolean signature, long serviceId, long hospitalId, int ward, HospitalizationStatus status){
       Hospitalization hospitalization = new Hospitalization();
       hospitalization.setId(id);
       hospitalization.setSignature(signature);
       hospitalization.setServiceId(serviceId);
       hospitalization.setHospitalId(hospitalId);
       hospitalization.setWard(ward);
       hospitalization.setStatus(status);
       return hospitalization;
    } 
    public MedicalInsurance createMedicalInsurance(long id, boolean signature, long serviceId, int reimbursement, long number){
            MedicalInsurance medicalInsurance = new MedicalInsurance();
            medicalInsurance.setId(id);
            medicalInsurance.setSignature(signature);           
            medicalInsurance.setServiceId(serviceId);
            medicalInsurance.setNumber(number);
            medicalInsurance.setReimbursement(reimbursement);
            return(medicalInsurance);
    }
    public Payment createPayment(long id, boolean signature, long serviceId, long number, int price){
            Payment payment = new Payment();
            payment.setId(id);
            payment.setSignature(signature);           
            payment.setServiceId(serviceId);
            payment.setNumber(number);
            payment.setPrice(price);
            return payment;
    }
    public Diagnosis createDiagnosis(long id, boolean signature, long serviceId, String diagnosisInf, String medicine, String medicalTests, boolean recommendations){
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setId(id);
            diagnosis.setServiceId(serviceId);
            diagnosis.setSignature(signature);
            diagnosis.setDiagnosis(diagnosisInf);
            diagnosis.setMedicine(medicine);
            diagnosis.setMedicalTests(medicalTests);
        return diagnosis;
    }
    public Appointment createAppointment(long id, long patientId, long doctorId, int year, int month, int day, int hour, int minute, ServiceStatus status, String specialization){
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setPatientId(patientId);
        appointment.setDoctorId(doctorId);
        appointment.setYear(year);
        appointment.setMonth(month);
        appointment.setDay(day);
        appointment.setHour(hour);
        appointment.setMinute(minute);
        appointment.setStatus(status);
        appointment.setSpecialization(specialization);
        return appointment;
    }       
    public Survey createSurvey(long id, long patientId, long doctorId, int year, int month, int day, int hour, int minute, ServiceStatus status, String type, String report){
        Survey survey = new Survey();
        survey.setId(id);
        survey.setPatientId(patientId);
        survey.setDoctorId(doctorId);
        survey.setYear(year);
        survey.setMonth(month);
        survey.setDay(day);
        survey.setHour(hour);
        survey.setMinute(minute);
        survey.setStatus(status);
        survey.setType(type);
        survey.setReport(report);
        return survey;
    }
    public Patient createPatient(long id, String name, long phone, String passport, String allergy){
        Patient patient = new Patient();
        patient.setId(id);
        patient.setName(name);
        patient.setPhone(phone);
        patient.setPassport(passport);
        patient.setAllergy(allergy);
        return patient;        
    }
    public Employee createEmployee(long id, String name, long phone, String specialty, String copmpetence){
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setPhone(phone);
        employee.setSpecialty(specialty);
        employee.setCompetence(copmpetence);
        return employee;
    }
    public Hospital createHospital(long id, String name, int wardsNumber){
        Hospital hospital = new Hospital();
        hospital.setId(id);
        hospital.setName(name);
        hospital.setWardsNumber(wardsNumber);
        return hospital;
    }
}
