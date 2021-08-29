/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital;

/**
 *
 * @author Liza
 */
public class Constants {  
    public static final String NO_PROVIDER="no_provider";    
    public static final String NO_METHOD="no_method";    
    public static final String ACQUIRED="acquired_info";    
    public static final String DIAGNOSIS="Neurosis";    
    public static final String MEDICINE="Sedatives";    
    public static final String MEDICAL_TESTS="";    
    public static final String ALLERGY = "Penicillin"; 
    public static final String ALREADY_DONE="already_done";
    public static final String CHANGE="change_info";    
    public static final String CLOSED="closed";    
    public static final String COMPETENCE = "Something"; 
    public static final String CSVEXTENSION="csv_extension";
    public static final String CSVPATH="csv_path";
    public static final String DATA_PROVIDER_CSV = "DataProviderCSV";
    public static final String DATA_PROVIDER_JDBC = "DataProviderDB";
    public static final String DATA_PROVIDER_XML = "DataProviderXML";
    public static final String DATE = "01.01.2021 11:00"; 
    public static final String WRONG_CALENDAR = "01/01/2021 11:00"; 
    public static final int YEAR = 2021; 
    public static final String DB_DRIVER="db_driver";    
    public static final String DB_PASS="db_pass";    
    public static final String DB_URL="db_url";    
    public static final String DB_USER="db_user";    
    public static final String DEBUG="debug";
    public static final String DELETED="deleted";
    public static final String EMPTY="empty_object";
    public static final String ENV_CONST="source";
    public static final String ERROR="error";
    public static final String FOUND="found";
    public static final String FULL_DAY="full_day";    
    public static final String FULL_HOSPITAL="full_hospital";    
    public static final String INFO="info";
    public static final String METHOD_NAME_CREATE_EMPLOYEE = "method_name.create_employee";
    public static final String METHOD_NAME_CREATE_EMPLOYEE_SUCCESS = "method_name.create_employee_success";
    public static final String METHOD_NAME_CREATE_PATIENT = "method_name.create_patient";
    public static final String METHOD_NAME_CREATE_PATIENT_SUCCESS = "method_name.create_patient_success";
    public static final String METHOD_NAME_GET_HOSPITAL = "method_name.get_hospital";
    public static final String METHOD_NAME_GET_HOSPITAL_SUCCESS = "method_name.get_hospital_success";
    public static final String NAME = "Name";
    public static final String NONE = "";
    public static final String NOT_ENOUGH="not_enough";    
    public static final String WRONG_AMOUNT="wrong_amount";    
    public static final String NO_SIGNATURE="no_signature";    
    public static final String NO_SUCH="no_such";
    public static final String NO_STATISTICS="no_statistics";
    public static final String NULL_PARAM="null_param";
    public static final long NUMBER=1234;
    public static final long PRICE=1000;
    public static final long REIMBURSEMENT=1100;
    public static final String ORIGINAL="original_info";    
    public static final String PASSPORT = "07AB"; 
    public static final String HELP = "help"; 
    public static final String REPORT = "Health";
    public static final String RESULT="result";    
    public static final String SPECIALIZATION = "Heartache";
    public static final String SPECIALTY = "Anything";
    public static final String STATUS_NOT="status_not";
    public static final String SYSTEMP="systemp";
    public static final String TYPE = "Ultrasound"; 
    public static final int WARDS = 5; 
    public static final String WRITTEN = "written";
    public static final String WRONG = "wrong_parameters";
    public static final String WRONG_PARSE="wrong_parse";
    public static final String WRONG_DATE="wrong_date";
    public static final String WRONG_STATUS="wrong_status";
    public static final String XMLEXTENSION="xml_extension";
    public static final String XMLPATH="xml_path";
    public static final int TEST_CONST=33;
    public static final int WORK_HOURS=6;
    public static final int WORK_START_HOURS=10;
    public static final long PHONE = 7900123;
    
    public static final String SELECT_HISTORY="SELECT id FROM DIAGNOSIS WHERE patientId='%d'";  
    
    public static final String SELECT_ALL_PAYMENTS="SELECT id FROM PAYMENT";  
    public static final String SELECT_ALL_APPOINTMENT="SELECT * FROM APPOINTMENT";  
    public static final String SELECT_ALL_DIAGNOSIS="SELECT id FROM DIAGNOSIS";  
    public static final String SELECT_ALL_HOSPITALIZATION="SELECT id FROM HOSPITALIZATION";  
    public static final String SELECT_ALL_EMPLOYEE="SELECT id FROM EMPLOYEE";  
    
    
    public static final String CREATE_APPOINTMENT="CREATE TABLE IF NOT EXISTS APPOINTMENT(id serial NOT NULL, patientId bigint NOT NULL, doctorId bigint NOT NULL, status int NOT NULL, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, specialization character(255) NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_DIAGNOSIS="CREATE TABLE IF NOT EXISTS DIAGNOSIS( id serial NOT NULL, signature boolean NOT NULL, serviceId bigint NOT NULL, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, patientId bigint NOT NULL, diagnosisInf character(255) NOT NULL, medicine character(255), medicalTests character(255), PRIMARY KEY (id))";    
    public static final String CREATE_EMPLOYEE="CREATE TABLE IF NOT EXISTS EMPLOYEE(id serial NOT NULL, name character(255) NOT NULL, phone bigint NOT NULL, specialty character(255) NOT NULL, competence character(255) NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_HOSPITAL="CREATE TABLE IF NOT EXISTS HOSPITAL(id serial NOT NULL, name character(255) NOT NULL, wardsNumber integer NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_HOSPITALIZATION="CREATE TABLE IF NOT EXISTS HOSPITALIZATION(id serial NOT NULL, signature boolean NOT NULL, serviceId bigint NOT NULL, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, hospitalId bigint NOT NULL, ward integer, status integer, PRIMARY KEY (id))";    
    public static final String CREATE_MEDICALINSURANCE="CREATE TABLE IF NOT EXISTS MEDICALINSURANCE(id serial NOT NULL, signature boolean NOT NULL, serviceId bigint NOT NULL, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, number bigint NOT NULL, reimbursement integer NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_PATIENT="CREATE TABLE IF NOT EXISTS PATIENT(id serial NOT NULL, name character(255) NOT NULL, phone bigint NOT NULL, passport character(255) NOT NULL, allergy character(255) NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_PAYMENT="CREATE TABLE IF NOT EXISTS PAYMENT(id serial NOT NULL, signature boolean NOT NULL, serviceId bigint NOT NULL, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, number bigint NOT NULL, price integer NOT NULL, PRIMARY KEY (id))";    
    public static final String CREATE_SURVEY="CREATE TABLE IF NOT EXISTS SURVEY(id serial NOT NULL, patientId bigint NOT NULL, doctorId bigint NOT NULL, status int, day integer NOT NULL, month integer NOT NULL, year integer NOT NULL, hour integer NOT NULL, minute integer NOT NULL, type character (255) NOT NULL, report character(255) NOT NULL, PRIMARY KEY (id))";    
    
    public static final String INSERT_APPOINTMENT="INSERT INTO APPOINTMENT (patientId, doctorId, status, day, month, year, hour, minute, specialization) VALUES (%d, %d, %d, %d, %d, %d, %d, %d, '%s')";    
    public static final String INSERT_DIAGNOSIS="INSERT INTO DIAGNOSIS (signature, serviceId, day, month, year, hour, minute, patientId, diagnosisInf, medicine, medicalTests) VALUES (%b, %d, %d, %d, %d, %d, %d, %d, '%s', '%s', '%s')";    
    public static final String INSERT_EMPLOYEE="INSERT INTO EMPLOYEE (name, phone, specialty, competence) VALUES ('%s', %d, '%s', '%s')";    
    public static final String INSERT_HOSPITAL="INSERT INTO HOSPITAL (name, wardsNumber) VALUES('%s', %d)";   
    public static final String INSERT_HOSPITALIZATION="INSERT INTO HOSPITALIZATION (signature, serviceId, day, month, year, hour, minute, hospitalId, ward, status) VALUES(%b, %d, %d, %d, %d, %d, %d, %d, %d, %d)";   
    public static final String INSERT_MEDICALINSURANCE="INSERT INTO MEDICALINSURANCE (signature, serviceId, day, month, year, hour, minute, number, reimbursement) VALUES(%b, %d, %d, %d, %d, %d, %d, %d, %d)";    
    public static final String INSERT_PATIENT="INSERT INTO PATIENT (name, phone, passport, allergy) VALUES ('%s', %d, '%s', '%s')";   
    public static final String INSERT_PAYMENT="INSERT INTO PAYMENT (signature, serviceId, day, month, year, hour, minute, number, price) VALUES(%b, %d, %d, %d, %d, %d, %d, %d, %d)";  
    public static final String INSERT_SURVEY="INSERT INTO SURVEY (patientId, doctorId, status, day, month, year, hour, minute, type, report) VALUES (%d, %d, %d, %d, %d, %d, %d, %d, '%s', '%s')";   
    
    public static final String SELECT_APPOINTMENT="SELECT * FROM APPOINTMENT WHERE id='%d'";    
    public static final String SELECT_DIAGNOSIS="SELECT * FROM DIAGNOSIS WHERE id='%d'";    
    public static final String SELECT_EMPLOYEE="SELECT * FROM EMPLOYEE WHERE id='%d'";    
    public static final String SELECT_HOSPITAL="SELECT * FROM HOSPITAL WHERE id='%d'";    
    public static final String SELECT_HOSPITALIZATION="SELECT * FROM HOSPITALIZATION WHERE id='%d'";    
    public static final String SELECT_MEDICALINSURANCE="SELECT * FROM MEDICALINSURANCE WHERE id='%d'";    
    public static final String SELECT_PATIENT="SELECT * FROM PATIENT WHERE id='%d'";    
    public static final String SELECT_PAYMENT="SELECT * FROM PAYMENT WHERE id='%d'";    
    public static final String SELECT_SURVEY="SELECT * FROM SURVEY WHERE id='%d'";    
    
    public static final String UPDATE_APPOINTMENT="UPDATE APPOINTMENT SET patientId = %d, doctorId = %d, status =%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, specialization='%s' where id=%d";     
    public static final String UPDATE_DIAGNOSIS="UPDATE DIAGNOSIS SET signature=%b, serviceId=%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, patientId=%d, diagnosisInf='%s', medicine='%s', medicalTests='s'";     
    public static final String UPDATE_EMPLOYEE="UPDATE EMPLOYEE SET name = '%s', phone = %d, specialty = '%s', competence = '%s' where id=%d";    
    public static final String UPDATE_HOSPITALIZATION="UPDATE HOSPITALIZATION SET signature = %b, serviceId=%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, hospitalId=%d, ward=%d, status=%d where id=%d";    
    public static final String UPDATE_HOSPITAL="UPDATE HOSPITAL SET name='%s', wardsNumber=%d";   
    public static final String UPDATE_MEDICALINSURANCE="UPDATE MEDICALINSURANCE SET signature = %b, serviceId=%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, number=%d, reimbursement=%d where id=%d"; 
    public static final String UPDATE_PATIENT="UPDATE PATIENT SET name = '%s', phone = %d, passport = '%s', allergy = '%s' where id=%d";    
    public static final String UPDATE_PAYMENT="UPDATE PAYMENT SET signature = %b, serviceId=%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, number=%d, price=%d where id=%d";    
    public static final String UPDATE_SURVEY="UPDATE SURVEY SET patientId = %d, doctorId = %d, status =%d, day=%d, month=%d, year=%d, hour=%d, minute=%d, type='%s', report='%s' where id=%d";    

    public static final String DELETE_APPOINTMENT = "DELETE FROM APPOINTMENT WHERE id=%d";
    public static final String DELETE_DIAGNOSIS = "DELETE FROM DIAGNOSIS WHERE id=%d";
    public static final String DELETE_EMPLOYEE = "DELETE FROM EMPLOYEE WHERE id=%d";
    public static final String DELETE_HOSPITALIZATION = "DELETE FROM HOSPITALIZATION WHERE id=%d";
    public static final String DELETE_HOSPITAL = "DELETE FROM HOSPITAL WHERE id=%d";
    public static final String DELETE_MEDICALINSURANCE = "DELETE FROM MEDICALINSURANCE WHERE id=%d";
    public static final String DELETE_PATIENT = "DELETE FROM PATIENT WHERE id=%d";
    public static final String DELETE_PAYMENT = "DELETE FROM PAYMENT WHERE id=%d";
    public static final String DELETE_SURVEY = "DELETE FROM SURVEY WHERE id=%d";
   
    public static final String APPOINTMENT_ID = "SELECT max(id) as MAXID FROM (SELECT id from APPOINTMENT)";
    public static final String SURVEY_ID = "SELECT max(id) as MAXID FROM (SELECT id from SURVEY)";
    public static final String DIAGNOSIS_ID = "SELECT max(id) as MAXID FROM (SELECT id from DIAGNOSIS)";
    public static final String EMPLOYEE_ID = "SELECT max(id) as MAXID FROM (SELECT id from EMPLOYEE)";
    public static final String HOSPITALIZATION_ID = "SELECT max(id) as MAXID FROM (SELECT id from HOSPITALIZATION)";
    public static final String HOSPITAL_ID = "SELECT max(id) as MAXID FROM (SELECT id from HOSPITAL)";
    public static final String MEDICALINSURANCE_ID = "SELECT max(id) as MAXID FROM (SELECT id from MEDICALINSURANCE)";
    public static final String PATIENT_ID = "SELECT max(id) as MAXID FROM (SELECT id from PATIENT)";
    public static final String PAYMENT_ID = "SELECT max(id) as MAXID FROM (SELECT id from PAYMENT)";
  
    public static final String COLUMN_ID = "id";    
    public static final String COLUMN_NAME = "name";    
    public static final String COLUMN_PHONE = "phone";    
    public static final String COLUMN_SPECIALTY = "specialty";    
    public static final String COLUMN_COMPETENCE = "competence";   
    public static final String COLUMN_PASSPORT = "passport";   
    public static final String COLUMN_ALLERGY = "allergy";   
    public static final String COLUMN_PATIENTID = "patientId";   
    public static final String COLUMN_DOCTORID = "doctorId";   
    public static final String COLUMN_DAY = "day";   
    public static final String COLUMN_MONTH = "month";   
    public static final String COLUMN_YEAR = "year";   
    public static final String COLUMN_HOUR = "hour";   
    public static final String COLUMN_MINUTE = "minute";   
    public static final String COLUMN_STATUS = "status";   
    public static final String COLUMN_TYPE = "type";   
    public static final String COLUMN_REPORT = "report";   
    public static final String COLUMN_SPECIALIZATION = "specialization";   
    public static final String COLUMN_WARDSNUMBER = "wardsNumber";   
    public static final String COLUMN_SIGNATURE = "signature";   
    public static final String COLUMN_SERVICEID = "serviceId";   
    public static final String COLUMN_DIAGNOSISINF = "diagnosisInf";   
    public static final String COLUMN_MEDICINE = "medicine";   
    public static final String COLUMN_MEDICALTESTS = "medicalTests"; 
    public static final String COLUMN_NUMBER = "number"; 
    public static final String COLUMN_PRICE = "price"; 
    public static final String COLUMN_REIMBURSEMENT= "reimbursement"; 
    public static final String COLUMN_HOSPITAL= "hospitalId"; 
    public static final String COLUMN_WARD= "ward"; 
    
    public static final String SETUP_APPOINTMENT="INSERT INTO APPOINTMENT (id, patientId, doctorId, status, day, month, year, hour, minute, specialization) VALUES (%d, %d, %d, %d, %d, %d, %d, %d, %d, '%s')";    
    public static final String SETUP_DIAGNOSIS="INSERT INTO DIAGNOSIS (id, signature, serviceId, day, month, year, hour, minute, patientId, diagnosisInf, medicine, medicalTests) VALUES (%d, %b, %d, %d, %d, %d, %d, %d, %d, '%s', '%s', '%s')";    
    public static final String SETUP_EMPLOYEE="INSERT INTO EMPLOYEE (id, name, phone, specialty, competence) VALUES (%d, '%s', %d, '%s', '%s')";    
    public static final String SETUP_HOSPITAL="INSERT INTO HOSPITAL (id, name, wardsNumber) VALUES(%d, '%s', %d)";   
    public static final String SETUP_HOSPITALIZATION="INSERT INTO HOSPITALIZATION (id, signature, serviceId, day, month, year, hour, minute, hospitalId, ward, status) VALUES(%d, %b, %d, %d, %d, %d, %d, %d, %d, %d, %d)";   
    public static final String SETUP_MEDICALINSURANCE="INSERT INTO MEDICALINSURANCE (id, signature, serviceId, day, month, year, hour, minute, number, reimbursement) VALUES(%d, %b, %d, %d, %d, %d, %d, %d, %d, %d)";    
    public static final String SETUP_PATIENT="INSERT INTO PATIENT (id, name, phone, passport, allergy) VALUES (%d, '%s', %d, '%s', '%s')";   
    public static final String SETUP_PAYMENT="INSERT INTO PAYMENT (id, signature, serviceId, day, month, year, hour, minute, number, price) VALUES(%d, %b, %d, %d, %d, %d, %d, %d, %d, %d)";  
    public static final String SETUP_SURVEY="INSERT INTO SURVEY (id, patientId, doctorId, status, day, month, year, hour, minute, type, report) VALUES (%d, %d, %d, %d, %d, %d, %d, %d, %d, '%s', '%s')";   
    
}
