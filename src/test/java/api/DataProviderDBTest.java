/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.hospital.Constants;
import ru.sfedu.hospital.TestBase;
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
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.YEAR;
import ru.sfedu.hospital.Main;

/**
 *
 * @author Liza
 *@throws java.lang.Exception */
public class DataProviderDBTest extends TestBase{
    private static final DataProviderDB dataProvider = DataProviderDB.getInstance();
    
    /**
     * Filling in test data.
     * @throws java.lang.Exception
     */
    @Before
    public void setUpData()throws Exception{
        dataProvider.setDB();
        dataProvider.setUp();
    }

    
    /**
     * Successful test of viewHistory method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testViewHistorySuccess() throws Exception {
        List<Diagnosis> list = dataProvider.viewHistory(0).get();
        Assert.assertEquals(Constants.DIAGNOSIS, list.get(0).getDiagnosis());
    }
    /**
     * Failed test of viewHistory method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testViewHistoryFail() throws Exception {
        Assert.assertEquals(dataProvider.viewHistory(-1), Optional.empty());
    }
    /**
     * Successful test of changeServiceStatus method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testChangeServiceStatusSuccess() throws Exception {
        Assert.assertEquals(dataProvider.changeServiceStatus(0, ServiceStatus.APPOINTED), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of changeServiceStatus method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testChangeServiceStatusFail() throws Exception {
        Assert.assertEquals(dataProvider.changeServiceStatus(-1, ServiceStatus.PAID), RequestStatus.FAIL);
    }
    /**
     * Successful test of confirmValid method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testConfirmValidSuccess() throws Exception {
       Assert.assertEquals(dataProvider.confirmValid(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of confirmValid method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testConfirmValidFail() throws Exception {
       Assert.assertEquals(dataProvider.confirmValid(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of calculateIncome method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testCalculateIncomeSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.calculateIncome(1), 0);
    }
    /**
     * Failed test of calculateIncome method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testCalculateIncomeFail() throws Exception {
        Assert.assertEquals(0, dataProvider.calculateIncome(0));
    }
    
    /**
     * Successful test of dynamicsCalculation method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testDynamicsCalculationSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.dynamicsCalculation(Constants.DIAGNOSIS, 1), Optional.empty());
    }
    /**
     * Failed test of dynamicsCalculation method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testDynamicsCalculationFail() throws Exception {
         Assert.assertEquals(dataProvider.dynamicsCalculation(Constants.DIAGNOSIS, 0), Optional.empty());
    }
    /**
     * Successful test of getStatistics method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetStatisticsSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.getStatistics(1), Optional.empty());
    }
    /**
     * Failed test of getStatistics method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetStatisticsFail() throws Exception {
        Assert.assertEquals(dataProvider.getStatistics(0), Optional.empty());
    }
    /**
     * Successful test of discharged method, of class DataProviderDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testDischargedSuccess() throws Exception {
         Assert.assertEquals(dataProvider.discharged(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of discharged method, of class DataProviderDB.
     *@throws java.lang.Exception */
    @Test
    public void testDischargedFail() throws Exception {
         Assert.assertEquals(dataProvider.discharged(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of setWard method, of class DataProviderDB.
     *@throws java.lang.Exception */
    @Test
    public void testSetWardSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.setWard(0), -1);
    }
    /**
     * Failed test of setWard method, of class DataProviderDB.
     *@throws java.lang.Exception *@throws java.lang.Exception */
    @Test
    public void testSetWardFail() throws Exception {
        Assert.assertEquals(0, dataProvider.setWard(-1));
    }
    /**
     * Successful test of payTheBill method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testPayTheBillSuccess() throws Exception {
          Assert.assertEquals(dataProvider.payTheBill(0, 777, 1000), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of payTheBill method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testPayTheBillFail() throws Exception {
          Assert.assertEquals(dataProvider.payTheBill(-1, 0, 0), RequestStatus.FAIL);
    }
    /**
     * Successful test of confirmInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testConfirmInsuranceSuccess() throws Exception {
      dataProvider.changeServiceStatus(0, ServiceStatus.APPOINTED);
      Assert.assertEquals(dataProvider.confirmInsurance(0, 888, 500), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of confirmInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testConfirmInsuranceFail() throws Exception {
      Assert.assertEquals(dataProvider.confirmInsurance(-1, 0, 0), RequestStatus.FAIL);
    }       
    /**
     * Successful test of hospitalize method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testHospitalizeSuccess() throws Exception {
        dataProvider.confirmValid(0);
        Assert.assertEquals(dataProvider.hospitalize(0, 0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of hospitalize method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testHospitalizeFail() throws Exception {
        Assert.assertEquals(dataProvider.hospitalize(-1, 0), RequestStatus.FAIL);
    }
    /**
     * Successful test of registerService method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testRegisterServiceSuccess() throws Exception {
        Assert.assertEquals(dataProvider.registerService(0, TypeOfPayment.PAYMENT, 1234, 1000), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of registerService method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testRegisterServiceFail() throws Exception {
        dataProvider.changeServiceStatus(0, ServiceStatus.PAID);
        Assert.assertEquals(dataProvider.registerService(0, TypeOfPayment.INSURANCE, 1234, 1000), RequestStatus.FAIL);
    }
    
    /**
     * Successful test of autoSelection method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testAutoSelectionSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.autoSelection(Constants.SPECIALTY), -1);
    }
    /**
     * Failed test of autoSelection method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testAutoSelectionFail() throws Exception {
         Assert.assertEquals(-1, dataProvider.autoSelection(Constants.NONE));
    }
    
    /**
     * Successful test of findDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void getAmountOfAppointmentsSuccessful () throws Exception {
        Assert.assertNotEquals(dataProvider.getAmountOfAppointments(0), Optional.empty());
    }
    /**
     * Failed test of findDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void getAmountOfAppointmentsFail () throws Exception {
        Assert.assertEquals(dataProvider.getAmountOfAppointments(-1), Optional.empty());
    }
    
     /**
     * Successful test of findDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testFindDateSuccess() throws Exception {
        Assert.assertNotEquals(dataProvider.findDate(0), Optional.empty());
    }

    /**
     * Failed test of findDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testFindDateFail() throws Exception {
        Assert.assertEquals(dataProvider.findDate(-1), Optional.empty());
    }
    
    
    
    /**
     * Successful test of createEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateEmployeeSuccess() throws Exception {
         Assert.assertEquals(dataProvider.createEmployee(Constants.NAME, Constants.PHONE, Constants.SPECIALTY, Constants.COMPETENCE),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception
     */
    @Test
    public void testCreateEmployeeFail() throws Exception {
        Assert.assertNotEquals(dataProvider.createEmployee(Constants.NONE, Constants.PHONE, Constants.SPECIALTY, Constants.COMPETENCE),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetEmployeeSuccess() throws Exception {
        Optional<Employee> optionalEmployee = dataProvider.getEmployee(0);
        Assert.assertTrue(optionalEmployee.isPresent());
    }
    /**
     * Failed test of getEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetEmployeeFail() throws Exception {
        Optional<Employee> optionalEmployee = dataProvider.getEmployee(-1);
        Assert.assertFalse(optionalEmployee.isPresent());
    }

    /**
     * Successful test of updateEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateEmployeeSuccess() throws Exception {
        Optional<Employee> optionalEmployee = dataProvider.getEmployee(0);
        optionalEmployee.get().setCompetence(Constants.COMPETENCE);
        Assert.assertEquals(dataProvider.updateEmployee(0, optionalEmployee.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateEmployeeFail() throws Exception {
        Optional<Employee> optionalEmployee = dataProvider.getEmployee(0);
        optionalEmployee.get().setName(Constants.NONE);
        Assert.assertEquals(dataProvider.updateEmployee(0, optionalEmployee.get()), RequestStatus.FAIL);
    }
    
     /**
     * Successful test of createPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreatePatientSuccess() throws Exception {
        Assert.assertEquals(dataProvider.createPatient(Constants.NAME, Constants.PHONE, 
                Constants.PASSPORT, Constants.ALLERGY),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreatePatientFail() throws Exception {
        Assert.assertNotEquals(dataProvider.createPatient(Constants.NONE, Constants.PHONE, 
                  Constants.PASSPORT, Constants.ALLERGY),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetPatientSuccess() throws Exception {
        Optional<Patient> optionalPatient = dataProvider.getPatient(0);
        Assert.assertTrue(optionalPatient.isPresent());
    }
    /**
     * Failed test of getPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetPatientFail() throws Exception {
        Optional<Patient> optionalPatient = dataProvider.getPatient(-1);
        Assert.assertFalse(optionalPatient.isPresent());
    }

    /**
     * Successful test of updatePatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdatePatientSuccess() throws Exception {
        Optional<Patient> optionalPatient = dataProvider.getPatient(0);
        optionalPatient.get().setAllergy(Constants.ALLERGY);
        Assert.assertEquals(dataProvider.updatePatient(optionalPatient.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updatePatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdatePatientFail() throws Exception {
        Optional<Patient> optionalPatient = dataProvider.getPatient(0);
        optionalPatient.get().setAllergy(Constants.NONE);
        Assert.assertEquals(dataProvider.updatePatient(optionalPatient.get()), RequestStatus.FAIL);
    }
   
         /**
     * Successful test of createSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception
     */
    @Test
    public void testCreateSurveySuccess() throws Exception {
        Calendar calendar = Calendar.getInstance();
        do{
            calendar.add(Calendar.DATE, 1);
        }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
        calendar.set(HOUR, 11);
        calendar.set(MINUTE, 00);
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm");
        String date=(format.format(calendar.getTime()));
        Assert.assertEquals(dataProvider.createSurvey(0, 0,
                date, Constants.TYPE,Constants.REPORT),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateSurveyFail() throws Exception {
        Calendar calendar = Calendar.getInstance();
        do{
            calendar.add(Calendar.DATE, 1);
        }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
        calendar.set(HOUR, 11);
        calendar.set(MINUTE, 00);
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm");
        String date=(format.format(calendar.getTime()));
        Assert.assertNotEquals(dataProvider.createSurvey(-1, 0,
                date, Constants.TYPE,Constants.REPORT),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetSurveySuccess() throws Exception {
        Optional<Survey> optionalSurvey = dataProvider.getSurvey(0);
        Assert.assertTrue(optionalSurvey.isPresent());
    }
    /**
     * Failed test of getSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetSurveyFail() throws Exception {
        Optional<Survey> optionalSurvey = dataProvider.getSurvey(-1);
        Assert.assertFalse(optionalSurvey.isPresent());
    }

    /**
     * Successful test of updateSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateSurveySuccess() throws Exception {
        Optional<Survey> optionalSurvey = dataProvider.getSurvey(0);
        optionalSurvey.get().setReport(Constants.REPORT);
        Assert.assertEquals(dataProvider.updateSurvey(optionalSurvey.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateSurveyFail() throws Exception {
        Optional<Survey> optionalSurvey = dataProvider.getSurvey(0);
        optionalSurvey.get().setType(Constants.NONE);
        Assert.assertEquals(dataProvider.updateSurvey(optionalSurvey.get()), RequestStatus.FAIL);
    }
    
    /**
     * Successful test of createAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateAppointmentSuccess() throws Exception {
        Calendar calendar = Calendar.getInstance();
        do{
            calendar.add(Calendar.DATE, 1);
        }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
        calendar.set(HOUR, 11);
        calendar.set(MINUTE, 00);
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm");
        String date=(format.format(calendar.getTime()));
        Assert.assertEquals(dataProvider.createAppointment(0, 0, ServiceStatus.APPOINTED,
               date, Constants.SPECIALIZATION),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateAppointmentFail() throws Exception {
        Calendar calendar = Calendar.getInstance();
        do{
            calendar.add(Calendar.DATE, 1);
        }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
        calendar.set(HOUR, 11);
        calendar.set(MINUTE, 00);
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy hh:mm");
        String date=(format.format(calendar.getTime()));
         Assert.assertNotEquals(dataProvider.createAppointment(0, 0, ServiceStatus.APPOINTED,
                date, Constants.NONE),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetAppointmentSuccess() throws Exception {
        Optional<Appointment> optionalAppointment = dataProvider.getAppointment(0);
        Assert.assertTrue(optionalAppointment.isPresent());
    }
    /**
     * Failed test of getAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetAppointmentFail() throws Exception {
        Optional<Appointment> optionalAppointment = dataProvider.getAppointment(-1);
        Assert.assertFalse(optionalAppointment.isPresent());
    }

    /**
     * Successful test of updateAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateAppointmentSuccess() throws Exception {
        Optional<Appointment> optionalAppointment = dataProvider.getAppointment(0);
        optionalAppointment.get().setSpecialization(Constants.SPECIALIZATION);
        Assert.assertEquals(dataProvider.updateAppointment(0, optionalAppointment.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateAppointmentFail() throws Exception {
        Optional<Appointment> optionalAppointment = dataProvider.getAppointment(0);
        optionalAppointment.get().setSpecialization(Constants.NONE);
        Assert.assertNotEquals(dataProvider.updateAppointment(0, optionalAppointment.get()), RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of createDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateDiagnosisSuccess() throws Exception {
        Assert.assertEquals(dataProvider.createDiagnosis(false, 0, 0, Constants.DIAGNOSIS,
                Constants.MEDICINE, Constants.MEDICAL_TESTS),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateDiagnosisFail() throws Exception {
         Assert.assertNotEquals(dataProvider.createDiagnosis(false, 0, 0, Constants.NONE,
                Constants.MEDICINE, Constants.MEDICAL_TESTS),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception
     */
    @Test
    public void testGetDiagnosisSuccess() throws Exception {
        Optional<Diagnosis> optionalDiagnosis = dataProvider.getDiagnosis(0);
        Assert.assertTrue(optionalDiagnosis.isPresent());
    }
    /**
     * Failed test of getDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetDiagnosisFail() throws Exception {
        Optional<Diagnosis> optionalDiagnosis = dataProvider.getDiagnosis(-1);
        Assert.assertFalse(optionalDiagnosis.isPresent());
    }

    /**
     * Successful test of updateDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testEditDiagnosisSuccess() throws Exception {
        Optional<Diagnosis> optionalDiagnosis = dataProvider.getDiagnosis(0);
        optionalDiagnosis.get().setDiagnosis(Constants.DIAGNOSIS);
        Assert.assertEquals(dataProvider.editDiagnosis(optionalDiagnosis.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateDiagnosisFail() throws Exception {
        Optional<Diagnosis> optionalDiagnosis = dataProvider.getDiagnosis(0);
        optionalDiagnosis.get().setDiagnosis(Constants.NONE);
        Assert.assertNotEquals(dataProvider.editDiagnosis(optionalDiagnosis.get()), RequestStatus.SUCCESS);
    }
    
    
    /**
     * Successful test of createHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateHospitalSuccess() throws Exception {
        Assert.assertEquals(dataProvider.createHospital(Constants.NAME, Constants.WARDS),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateHospitalFail() throws Exception {
         Assert.assertNotEquals(dataProvider.createHospital(Constants.NONE, Constants.WARDS),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetHospitalSuccess() throws Exception {
        Optional<Hospital> optionalHospital = dataProvider.getHospital(0);
        Assert.assertTrue(optionalHospital.isPresent());
    }
    /**
     * Failed test of getHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetHospitalFail() throws Exception {
        Optional<Hospital> optionalHospital = dataProvider.getHospital(-1);
        Assert.assertFalse(optionalHospital.isPresent());
    }

    /**
     * Successful test of updateHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testEditHospitalSuccess() throws Exception {
        Optional<Hospital> optionalHospital = dataProvider.getHospital(0);
        optionalHospital.get().setName(Constants.NAME);
        Assert.assertEquals(dataProvider.editHospital(optionalHospital.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateHospitalFail() throws Exception {
        Optional<Hospital> optionalHospital = dataProvider.getHospital(0);
        optionalHospital.get().setName(Constants.NONE);
        Assert.assertNotEquals(dataProvider.editHospital(optionalHospital.get()), RequestStatus.SUCCESS);
    }

    /**
     * Successful test of createHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateHospitalizationSuccess() throws Exception {
        Assert.assertEquals(dataProvider.createHospitalization(false, 0, 0, 1, HospitalizationStatus.HOSPITALIZED),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateHospitalizationFail() throws Exception {
         Assert.assertNotEquals(dataProvider.createHospitalization(false, -1, 0, 1, HospitalizationStatus.HOSPITALIZED),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetHospitalizationSuccess() throws Exception {
        Optional<Hospitalization> optionalHospitalization = dataProvider.getHospitalization(0);
        Assert.assertTrue(optionalHospitalization.isPresent());
    }
    /**
     * Failed test of getHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetHospitalizationFail() throws Exception {
        Optional<Hospitalization> optionalHospitalization = dataProvider.getHospitalization(-1);
        Assert.assertFalse(optionalHospitalization.isPresent());
    }

    /**
     * Successful test of updateHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testEditHospitalizationSuccess() throws Exception {
        Optional<Hospitalization> optionalHospitalization = dataProvider.getHospitalization(0);
        optionalHospitalization.get().setHospitalId(0);
        Assert.assertEquals(dataProvider.editHospitalization(0, optionalHospitalization.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateHospitalizationFail() throws Exception {
       Optional<Hospitalization> optionalHospitalization = dataProvider.getHospitalization(0);
        optionalHospitalization.get().setHospitalId(-1);
        Assert.assertNotEquals(dataProvider.editHospitalization(0, optionalHospitalization.get()), RequestStatus.SUCCESS);
    }
       
    /**
     * Successful test of createMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateMedicalInsuranceSuccess() throws Exception {
        dataProvider.changeServiceStatus(0, ServiceStatus.APPOINTED);
        Assert.assertEquals(dataProvider.createMedicalInsurance(false, 0, 1000, 1234),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreateMedicalInsuranceFail() throws Exception {
         Assert.assertNotEquals(dataProvider.createMedicalInsurance(false, -1, 1000, 1234),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetMedicalInsuranceSuccess() throws Exception {
        Optional<MedicalInsurance> optionalMedicalInsurance = dataProvider.getMedicalInsurance(0);
        Assert.assertTrue(optionalMedicalInsurance.isPresent());
    }
    /**
     * Failed test of getMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetMedicalInsuranceFail() throws Exception {
        Optional<MedicalInsurance> optionalMedicalInsurance = dataProvider.getMedicalInsurance(-1);
        Assert.assertFalse(optionalMedicalInsurance.isPresent());
    }

    /**
     * Successful test of updateMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testEditMedicalInsuranceSuccess() throws Exception {
        Optional<MedicalInsurance> optionalMedicalInsurance = dataProvider.getMedicalInsurance(0);
        optionalMedicalInsurance.get().setReimbursement(1100);
        Assert.assertEquals(dataProvider.editMedicalInsurance(optionalMedicalInsurance.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updateMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdateMedicalInsuranceFail() throws Exception {
        Optional<MedicalInsurance> optionalMedicalInsurance = dataProvider.getMedicalInsurance(0);
        optionalMedicalInsurance.get().setServiceId(-1);
        Assert.assertNotEquals(dataProvider.editMedicalInsurance(optionalMedicalInsurance.get()), RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of createPayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreatePaymentSuccess() throws Exception {
        dataProvider.changeServiceStatus(0, ServiceStatus.APPOINTED);
        Assert.assertEquals(dataProvider.createPayment(false, 0, 1000, 1234),RequestStatus.SUCCESS);
    }
    
    /**
     * Failed test of createPayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCreatePaymentFail() throws Exception {
         Assert.assertNotEquals(dataProvider.createPayment(false, -1, 1000, 1234),RequestStatus.SUCCESS);
    }
    
    /**
     * Successful test of getPayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetPaymentSuccess() throws Exception {
        Optional<Payment> optionalPayment = dataProvider.getPayment(0);
        Assert.assertTrue(optionalPayment.isPresent());
    }
    /**
     * Failed test of getPayment method, of class DataProviderDB.
     *@throws java.lang.Exception
     */
    @Test
    public void testGetPaymentFail() throws Exception {
        Optional<Payment> optionalPayment = dataProvider.getPayment(-1);
        Assert.assertFalse(optionalPayment.isPresent());
    }

    /**
     * Successful test of updatePayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testEditPaymentSuccess() throws Exception {
        Optional<Payment> optionalPayment = dataProvider.getPayment(0);
        optionalPayment.get().setServiceId(0);
        Assert.assertEquals(dataProvider.editPayment(optionalPayment.get()), RequestStatus.SUCCESS);
    }

    /**
     * Failed test of updatePayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testUpdatePaymentFail() throws Exception {
        Optional<Payment> optionalPayment = dataProvider.getPayment(0);
        optionalPayment.get().setServiceId(-1);
        Assert.assertNotEquals(dataProvider.editPayment(optionalPayment.get()), RequestStatus.SUCCESS);
    }
    
    
    /**
     * Successful test of deletePayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeletePaymentSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deletePayment(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deletePayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeletePaymentFail() throws Exception {
        Assert.assertEquals(dataProvider.deletePayment(-1), RequestStatus.FAIL);
    }
    
    /**
     * Successful test of deleteEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteEmployeeSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteEmployee(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteEmployeeFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteEmployee(-1), RequestStatus.FAIL);
    }
     /**
     * Successful test of deletePatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeletePatientSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deletePatient(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deletePatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeletePatientFail() throws Exception {
        Assert.assertEquals(dataProvider.deletePatient(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of deleteSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteSurveySuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteSurvey(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteSurveyFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteSurvey(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of deleteAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteAppointmentSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteAppointment(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteAppointmentFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteAppointment(-1), RequestStatus.FAIL);
    }
    
    /**
     * Successful test of deleteDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteDiagnosisSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteDiagnosis(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteDiagnosisFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteDiagnosis(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of deleteHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteHospitalSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteHospital(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteHospitalFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteHospital(-1), RequestStatus.FAIL);
    }
     /**
     * Successful test of deleteHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteHospitalizationSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteHospitalization(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteHospitalizationFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteHospitalization(-1), RequestStatus.FAIL);
    }
    /**
     * Successful test of deleteMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteMedicalInsuranceSuccess() throws Exception {
        Assert.assertEquals(dataProvider.deleteMedicalInsurance(0), RequestStatus.SUCCESS);
    }
    /**
     * Failed test of deleteMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testDeleteMedicalInsuranceFail() throws Exception {
        Assert.assertEquals(dataProvider.deleteMedicalInsurance(-1), RequestStatus.FAIL);
    }

    /**
     * Successful test of getCalendar method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetCalendarSuccess() throws Exception {
        Assert.assertEquals(Constants.YEAR, dataProvider.getCalendar(Constants.DATE).get(YEAR));
    }
    /**
     * Failed test of getCalendar method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetCalendarFail() throws Exception {
         Assert.assertEquals(null, dataProvider.getCalendar(Constants.WRONG_CALENDAR));
    }
    
    /**
     * Successful test of getNextSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextSurveySuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextSurvey());
    }
    /**
     * Failed test of getNextSurvey method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextSurveyFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextSurvey());
    }
    /**
     * Successful test of getNextEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextEmployeeSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextEmployee());
    }
    /**
     * Failed test of getNextEmployee method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextEmployeeFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextEmployee());
    }
    /**
     * Successful test of getNextPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextPatientSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextPatient());
    }
    /**
     * Failed test of getNextPatient method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextPatientFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextPatient());
    }
    /**
     * Successful test of getNextAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextAppointmentSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextAppointment());
    }
    /**
     * Failed test of getNextAppointment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextAppointmentFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextAppointment());
    }
    /**
     * Successful test of getNextDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextDiagnosisSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextDiagnosis());
    }
    /**
     * Failed test of getNextDiagnosis method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextDiagnosisFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextDiagnosis());
    }
    /**
     * Successful test of getNextPayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextPaymentSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextPayment());
    }
    /**
     * Failed test of getNextPayment method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextPaymentFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextPayment());
    }
    /**
     * Successful test of getNextMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextMedicalInsuranceSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextMedicalInsurance());
    }
    /**
     * Failed test of getNextMedicalInsurance method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextMedicalInsuranceFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextMedicalInsurance());
    }
    /**
     * Successful test of getNextHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextHospitalizationSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextHospitalization());
    }
    /**
     * Failed test of getNextHospitalization method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextHospitalizationFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextHospitalization());
    }
    /**
     * Successful test of getNextHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextHospitalSuccess() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextHospital());
    }
    /**
     * Failed test of getNextHospital method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testGetNextHospitalFail() throws Exception {
        Assert.assertNotEquals(-1, dataProvider.getNextHospital());
    }
    /**
     * Successful test of setUp method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testSetUpSuccess() throws Exception {
        Assert.assertEquals(true, dataProvider.setUp());
    }
    /**
     * Failed test of setUp method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testSetUpFail() throws Exception {
         Assert.assertEquals(true, dataProvider.setUp());
    }
    /**
     * Successful test of checkDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCheckDateSuccess() throws Exception {
        Calendar calendar = Calendar.getInstance();
        do{
            calendar.add(Calendar.DATE, 1);
        }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
        calendar.set(HOUR, Constants.WORK_START_HOURS);
        calendar.set(MINUTE, 00);
        Assert.assertEquals(RequestStatus.SUCCESS, dataProvider.checkDate(calendar));
    }
    /**
     * Failed test of checkDate method, of class DataProviderDB.
     *@throws java.lang.Exception 
     */
    @Test
    public void testCheckDateFail() throws Exception {
        Calendar currentDate = Calendar.getInstance();
        Assert.assertEquals(RequestStatus.FAIL, dataProvider.checkDate(currentDate));
    }
}

