/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the updateor.
 */
package api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import ru.sfedu.hospital.enums.RequestStatus;
import ru.sfedu.hospital.enums.ServiceStatus;
import ru.sfedu.hospital.enums.TypeOfPayment;

/**
 * Interface implemented by DataProviders
 * @author Liza
 */
public interface DataProvider {
    
    /**
     * View patient history by ID
     * @param patientId
     * @return List of Documents
     * @throws java.lang.Exception
     */
     Optional<List<Diagnosis>> viewHistory(long patientId)throws Exception;  
    
     /**
     * Change service status
     * @param serviceId
     * @param status
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus changeServiceStatus(long serviceId, ServiceStatus status)throws Exception;
    
    /**
     * Confirm that the document is valid
     * @param documentId
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus confirmValid(long documentId)throws Exception;
    
    /**
     * Calculate the profit
     * @param months
     * @return The amount of profit
     * @throws java.lang.Exception
     */
    int calculateIncome(int months)throws Exception;

    
     /**
     * Dynamics calculation 
     * @param diagnosis
     * @param months
     * @return The number of cases in each of the last n months
     * @throws java.lang.Exception
     */
    Optional<Map<Integer, Long>> dynamicsCalculation (String diagnosis, int months)throws Exception;  
    
    /**
     * Make a rating of diseases in n months 
     * @param months
     * @return List of 10 common diseases
     * @throws java.lang.Exception
     */
    Optional<Map<String, Long>> getStatistics (int months)throws Exception; 
    
    /**
     * Discharged
     * @param hospitalizationId
     * @return Change hospitalization status
     * @throws java.lang.Exception
     */
    RequestStatus discharged (long hospitalizationId)throws Exception; 
 
    /**
     * Set a ward
     * @param hospitalId
     * @return Determine the number of the free ward
     * @throws java.lang.Exception
     */
    int setWard (long hospitalId)throws Exception;
    
       
    /**
     * Appoint recommended examinations
     * @param serviceId
     * @param number
     * @param price
     * @return Payment infromation
     * @throws java.lang.Exception
     */
    RequestStatus payTheBill (long serviceId, long number, int price)throws Exception;
         

    /**
     * Confirm medical insurance 
     * @param serviceId
     * @param number
     * @param insurance
     * @return MedicalInsurance
     * @throws java.lang.Exception
     */
    RequestStatus confirmInsurance (long serviceId, long number, int insurance)throws Exception; 
    
    /**
     * Hospitalize a patient
     * @param hospitalId
     * @param diagnosisId
     * @return Hospitalization information
     * @throws java.lang.Exception
     */
    RequestStatus hospitalize (long hospitalId, long diagnosisId)throws Exception;
    
    /**
     * Change service status and create payment documents
     * @param serviceId
     * @param typeOfPayment
     * @param number
     * @param price
     * @return MedicalInsurance
     * @throws java.lang.Exception
     */
    RequestStatus registerService (long serviceId, TypeOfPayment typeOfPayment, long number, int price)throws Exception;
    
    /**
     * Autosearch for a competent doctor
     * @param specialization
     * @return List of id of competent doctors
     * @throws java.lang.Exception
     */
    long autoSelection(String specialization)throws Exception;
    
    /**
     * Counting the number of doctor's appointments
     * @param doctorId
     * @return Listing the numbers of appointments per day
     * @throws java.lang.Exception
     */
    public Optional<Map<Integer, Integer>> getAmountOfAppointments(long doctorId) throws Exception;
    
    /**
     * Autosearch for a date of appointment
     * @param doctorId
     * @return Available dates
     * @throws java.lang.Exception
     */
    Optional<Map<Integer, Integer>> findDate(long doctorId)throws Exception;
     
 
    /*Employee*/
    
    /**
     * Employee creation method
     * @param name
     * @param phone
     * @param specialty
     * @param competence
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus createEmployee(String name, long phone, String specialty, String competence)throws Exception;
    
    /**
     * Employee selection method
     * 
     * @param id
     * @return Employee object
     * @throws java.lang.Exception
     */
    Optional<Employee> getEmployee(long id)throws Exception;
    
    /**
     * Employee update method
     * 
     * @param id
     * @param employee id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus updateEmployee(long id, Employee employee)throws Exception;
    
    /**
     * Employee delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteEmployee(long id)throws Exception;
    
    /*Patient*/
    
    /**
     * Patient creation method
     * @param name
     * @param phone
     * @param passport
     * @param allergy
     * @return Patient object
     * @throws java.lang.Exception
     */
    RequestStatus createPatient(String name, long phone, String passport, String allergy)throws Exception;
    
     /**
     * Patient selection method
     * 
     * @param id
     * @return Patient object
     * @throws java.lang.Exception
     */
    Optional<Patient> getPatient(long id)throws Exception;
    
    /**
     * Patient update method
     * 
     * @param patient
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus updatePatient(Patient patient)throws Exception;
    
    /**
     * Patient delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deletePatient(long id)throws Exception;
    
    /*Survey*/
        
    /**
     * Survey creation method
     * @param patientId
     * @param doctorId
     * @param date
     * @param type
     * @param report
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus createSurvey(long patientId, long doctorId, String date, String type, String report)throws Exception;
    
    /**
     * Survey selection method
     * 
     * @param id
     * @return Survey object
     * @throws java.lang.Exception
     */
    Optional<Survey> getSurvey(long id)throws Exception;
    
    /**
     * Survey update method
     * 
     * @param survey
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus updateSurvey(Survey survey)throws Exception;
    
    /**
     * Survey delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteSurvey(long id)throws Exception;
    
    /*Appointment*/    
    
    /**
     * Appointment creation method
     * @param patientId
     * @param doctorId
     * @param specialization
     * @param status
     * @param date
     * @return Appointment object
     * @throws java.lang.Exception
     */
    RequestStatus createAppointment(long patientId, long doctorId, ServiceStatus status, String date,String specialization)throws Exception;
    
    /**
     * Appointment selection method
     * 
     * @param id
     * @return Appointment object
     * @throws java.lang.Exception
     */
    Optional <Appointment> getAppointment(long id)throws Exception;
    
    /**
     * Appointment update method
     * 
     * @param id
     * @param appointment
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus updateAppointment(long id, Appointment appointment)throws Exception;
    
    /**
     * Appointment delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteAppointment(long id)throws Exception;
    
     /*Diagnosis*/
    
    /**
     * Diagnosis creation method
     * @param signature
     * @param patientId
     * @param serviceId
     * @param diagnosisInf
     * @param medicine
     * @param MedicalTests
     * @return Diagnosis object
     * @throws java.lang.Exception
     */
    RequestStatus createDiagnosis(boolean signature, long patientId, long serviceId, String diagnosisInf, 
            String medicine, String MedicalTests)throws Exception;
            
    /**
     * Diagnosis selection method
     * 
     * @param id
     * @return Diagnosis object
     * @throws java.lang.Exception
     */
    Optional<Diagnosis> getDiagnosis(long id)throws Exception;
    
    /**
     * Diagnosis edit method
     * 
     * @param diagnosis
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus editDiagnosis(Diagnosis diagnosis)throws Exception;
    
    /**
     * Diagnosis delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteDiagnosis(long id)throws Exception;
    
    /*Payment*/
    
    /**
     * Diagnosis creation method
     * @param signature
     * @param serviceId
     * @param number
     * @param price
     * @return Payment object
     * @throws java.lang.Exception
     */
    RequestStatus createPayment(boolean signature, long serviceId, long number, int price)throws Exception;
    
     /**
     * Payment selection method
     * 
     * @param id
     * @return Payment object
     * @throws java.lang.Exception
     */
    Optional<Payment> getPayment(long id)throws Exception;
    
    /**
     * Payment edit method
     * 
     * @param payment
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus editPayment(Payment payment)throws Exception;
    
    /**
     * Payment delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deletePayment(long id)throws Exception;
    
     /*MedicalInsurance*/
    
    /**
     * Diagnosis creation method
     * @param signature
     * @param serviceId
     * @param reimbursement
     * @param number
     * @return Appointment object
     * @throws java.lang.Exception
     */
    RequestStatus createMedicalInsurance(boolean signature, long serviceId, int reimbursement, long number)throws Exception;
    
    /**
     * MedicalInsurance selection method
     * 
     * @param id
     * @return MedicalInsurance object
     * @throws java.lang.Exception
     */
    Optional<MedicalInsurance> getMedicalInsurance(long id)throws Exception;
    
    /**
     * MedicalInsurance edit method
     * 
     * @param medicalInsurance
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus editMedicalInsurance(MedicalInsurance medicalInsurance)throws Exception;
    
    /**
     * MedicalInsurance delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteMedicalInsurance(long id)throws Exception;
    
    /*Hospitalization*/
    
    /**
     * Hospitalization creation method
     * @param signature
     * @param diagnosisId
     * @param hospitalId
     * @param ward
     * @param status
     * @return Hoapitalization object
     * @throws java.lang.Exception
     */
    RequestStatus createHospitalization(boolean signature, long diagnosisId, long hospitalId, int ward, HospitalizationStatus status)throws Exception;
    
    /**
     * Hospitalization selection method
     * 
     * @param id
     * @return Hospitalization object
     * @throws java.lang.Exception
     */
    Optional<Hospitalization> getHospitalization(long id)throws Exception;
    
    /**
     * Hospitalization edit method
     * 
     * @param id
     * @param hospitalization
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus editHospitalization(long id, Hospitalization hospitalization)throws Exception;
    
    /**
     * Hospitalization delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteHospitalization(long id)throws Exception;
    
    /*Hospital*/
    
    /**
     * Hospital creation method
     * @param name
     * @param wardsNumber
     * @return Hospital object
     * @throws java.lang.Exception
     */
    RequestStatus createHospital(String name, int wardsNumber)throws Exception;
    
    /**
     * Hospital selection method
     * 
     * @param id
     * @return Hospital object
     * @throws java.lang.Exception
     */
    Optional<Hospital> getHospital(long id)throws Exception;
    
    /**
     * Hospital edit method
     * 
     * @param hospital
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus editHospital(Hospital hospital)throws Exception;
    
    /**
     * Hospital delete method
     * 
     * @param id
     * @return RequestStatus
     * @throws java.lang.Exception
     */
    RequestStatus deleteHospital(long id)throws Exception;
    
    
}
