/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospital.Constants;
import ru.sfedu.hospital.Main;
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
import ru.sfedu.hospital.utils.ConfigurationUtil;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.hospital.utils.WrapperXML;
/**
 *
 * @author Liza
 */
public class DataProviderXML implements DataProvider{

    private static final Logger log = LogManager.getLogger(Main.class);

    public DataProviderXML() {
        try {
            log.debug(new File(ConfigurationUtil.getConfigurationEntry(Constants.XMLPATH)).mkdirs());
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    @Override
    public Optional<List<Diagnosis>> viewHistory(long patientId) throws Exception {       
        if (getPatient(patientId).isPresent()){
            try{
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+getPatient(patientId).get().toString());
                List<Diagnosis> diagnosisList = readFromXml(Diagnosis.class);
                List <Diagnosis> history = diagnosisList.stream()
                        .filter(item -> (item.getPatientId()==patientId))
                        .collect(Collectors.toList());
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+history.toString());
                return Optional.of(history);
            }catch(IOException e){
                log.error(e);
                return Optional.empty();
            }
        }else{
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }   
    }

    @Override
    public RequestStatus changeServiceStatus(long serviceId, ServiceStatus status) throws Exception {
         if (getAppointment(serviceId).isPresent()){
             try{
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+getAppointment(serviceId).get().toString());
                Appointment appointment = getAppointment(serviceId).get();
                if(appointment.getStatus()==status){
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                    return RequestStatus.SUCCESS;
                }
                appointment.setStatus(status);
                updateAppointment(serviceId, appointment);
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.CHANGE)+appointment.toString());
                return RequestStatus.SUCCESS;
            }catch(Exception e){
            log.error(e);
            return RequestStatus.FAIL;
        }}else{
              log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
              return RequestStatus.FAIL;
         }
         
    }

    @Override
    public RequestStatus confirmValid(long id) throws Exception {
        try{
            if (getDiagnosis(id).isPresent()){
                Diagnosis diagnosis = getDiagnosis(id).get();    
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+diagnosis.toString());            
                if (!diagnosis.isSignature()){
                    diagnosis.setSignature(true);
                    editDiagnosis(diagnosis);
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.CHANGE)+diagnosis.toString()); 
                     return RequestStatus.SUCCESS;
                }
                else{
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                    return RequestStatus.SUCCESS;
                }
            }else {
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        }catch(Exception e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public int calculateIncome(int months) throws Exception {
        try{
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+months);
            if(months<1){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return 0;
            }    
            List<Payment> list = readFromXml(Payment.class);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(MONTH);
            int currentYear = calendar.get(YEAR);
            List<Payment> calcList = list.stream()
                    .filter(item -> ((currentMonth-item.getMonth()<months)&(item.getYear()==currentYear)))
                    .collect(Collectors.toList());
            List<Integer> payment = new ArrayList<>();
            calcList.stream().forEach(item -> payment.add(item.getPrice()));
            int sum;
            sum=payment.stream().mapToInt(Integer::intValue).sum();
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.RESULT)+sum);
            return sum;
        }catch(IOException e){
            log.error(e);
            throw new Exception();
        }
    }

    @Override
    public Optional<Map<Integer, Long>> dynamicsCalculation(String diagnosis, int months) throws Exception {
        try{
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+diagnosis+" "+months);
            if(months<1){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return Optional.empty();
            }    
            List<Diagnosis> list = readFromXml(Diagnosis.class);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(MONTH);
            int currentYear = calendar.get(YEAR);
            List<Diagnosis> statList = list.stream()
                    .filter(item -> ((currentMonth-item.getMonth()<=months)&(item.getYear()==currentYear))
                            &item.getDiagnosis().equals(diagnosis))
                    .collect(Collectors.toList());
            Map<Integer, Long> statMap = statList.stream()
             .collect(Collectors.groupingBy(Diagnosis::getMonth, Collectors.counting()));      
            if(!statMap.isEmpty()){
                for(Map.Entry<Integer, Long> item : statMap.entrySet()){
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) +
                            Month.of(item.getKey()+1) + " - " + item.getValue());
                } 
                return Optional.of(statMap);
            }
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_STATISTICS));
            return Optional.empty();
        }catch(IOException e){
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Map<String, Long>> getStatistics(int months) throws Exception {
         try{
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+months);
            if(months<1){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return Optional.empty();
            }    
            List<Diagnosis> list = readFromXml(Diagnosis.class);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(MONTH);
            int currentYear = calendar.get(YEAR);
             List<Diagnosis> statList = list.stream()
                    .filter(item -> ((currentMonth-item.getMonth()<months)&&(item.getYear()==currentYear)))
                    .collect(Collectors.toList());
            Map<String, Long> statMap = statList.stream()
             .collect(Collectors.groupingBy(Diagnosis::getDiagnosis, Collectors.counting()));             
           if(!statMap.isEmpty()){
               for(Map.Entry<String, Long> item : statMap.entrySet()){
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + 
                        item.getKey() + " - " + item.getValue());
                }  
                return Optional.of(statMap);
            }
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.NO_STATISTICS));
            return Optional.empty();
        }catch(IOException e){
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus discharged(long hospitalizationId) throws Exception {
        if (getHospitalization(hospitalizationId).isPresent()){
        try{
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+hospitalizationId);
            Hospitalization hospitalization = getHospitalization(hospitalizationId).get();
            if(hospitalization.getStatus()==HospitalizationStatus.DISCHARGED){
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                return RequestStatus.SUCCESS;
            }
            hospitalization.setStatus(HospitalizationStatus.DISCHARGED);
            editHospitalization(hospitalizationId, hospitalization);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospitalizationId);
            return RequestStatus.SUCCESS;
        }catch(Exception e){
            log.error(e);
            throw new Exception();
        }} else return RequestStatus.FAIL;
    }

    @Override
    public int setWard(long hospitalId) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+hospitalId);
        if(getHospital(hospitalId).isPresent()){
            try{ 
                Hospital hospital = getHospital(hospitalId).get();
                int ward=0;
                List<Hospitalization> list = readFromXml(Hospitalization.class)
                        .stream()
                        .filter(item -> (item.getHospitalId()==hospitalId)
                                &item.getStatus()==HospitalizationStatus.HOSPITALIZED)
                        .collect(Collectors.toList()); 
                List<Integer> wardsFull = new ArrayList<>();
                list.stream().forEach(item -> wardsFull.add(item.getWard()));  
                List<Integer> wards = wardsFull.stream().distinct().collect(Collectors.toList());
                int amountOfWards = hospital.getWardsNumber(); 
                if (wards.isEmpty()){
                    return 1;
                }
                if(wards.size()!=amountOfWards){
                    wards.stream().sorted();
                    for(int i=-1;i==wards.size()-1;i++){                   
                        if ((wards.get(i+1)-wards.get(i))>1){
                            ward=wards.get(i)+1;
                        //available=wards.stream().
                        //filter(item -> item==(el+1))
                        //.collect(Collectors.toList());
                        }
                    } 
                    if (ward==0){
                        log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED) + (wards.size()+1));
                        return wards.size()+1;
                    }
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED) + ward);
                    return ward;
                }else{
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.FULL_HOSPITAL));
                    return 0;
                }
            }catch(Exception e){
                log.error(e);
                return 0;
            }
        }else{
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return 0;
        }           
    }
     
    @Override
    public RequestStatus payTheBill(long serviceId, long number, int price) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)
                +serviceId+" "+number+" "+price);
        try{
            if (getAppointment(serviceId).isPresent()&&
                    getAppointment(serviceId).get().getStatus()==ServiceStatus.APPOINTED){
                createPayment(true, serviceId, number, price);
                if (changeServiceStatus(serviceId, ServiceStatus.PAID)==RequestStatus.FAIL){
                    return RequestStatus.FAIL;
                }
                return RequestStatus.SUCCESS;
            }else{
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        }catch(Exception e){
            log.error(e);
            return RequestStatus.FAIL;
        }            
    }

    @Override
    public RequestStatus confirmInsurance(long serviceId, long number, int insurance) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)
                +serviceId+" "+number+" "+insurance);
         try{
            if (getAppointment(serviceId).isPresent()&&
                    getAppointment(serviceId).get().getStatus()==ServiceStatus.APPOINTED){
                createPayment(true, serviceId, number, insurance);
                if (changeServiceStatus(serviceId, ServiceStatus.CONFIRMED)==RequestStatus.FAIL){
                    return RequestStatus.FAIL;
                }
                return RequestStatus.SUCCESS;
            }else{
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        }catch(Exception e){
            log.error(e);
            return RequestStatus.FAIL;
        }            
    }

    @Override
    public RequestStatus hospitalize(long hospitalId, long diagnosisId) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)
                +hospitalId+" "+diagnosisId);
        if (getDiagnosis(diagnosisId).isPresent()&getHospital(hospitalId).isPresent()){
            Diagnosis diagnosis = getDiagnosis(diagnosisId).get();
            if (diagnosis.isSignature()){ 
               if (setWard(hospitalId)!=0){
                    createHospitalization(false, diagnosisId, hospitalId, setWard(hospitalId), HospitalizationStatus.HOSPITALIZED);
                    return RequestStatus.SUCCESS;
               }else{
                   log.info(ConfigurationUtil.getConfigurationEntry(Constants.FULL_HOSPITAL));
                   return RequestStatus.SUCCESS;
               }          
            }else{
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SIGNATURE));
                return RequestStatus.FAIL;
            }
        }else{
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
             return RequestStatus.FAIL;
        }        
    }

    @Override
    public RequestStatus registerService(long serviceId, TypeOfPayment typeOfPayment, long number, int price) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)
                +serviceId+" "+typeOfPayment+" "+number+" "+price);
        try{
            if(getAppointment(serviceId).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(getAppointment(serviceId).get().getStatus()==ServiceStatus.APPOINTED){
                switch (typeOfPayment){
                    case PAYMENT:
                        payTheBill(serviceId, number, price);
                        break;
                    case INSURANCE:
                        confirmInsurance(serviceId, number, price);
                        break;
                }
            return RequestStatus.SUCCESS;
            }else{
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.STATUS_NOT));
                return RequestStatus.FAIL;
            }
        }catch(Exception e){
            log.error(e);
             return RequestStatus.FAIL;          
        }
    }
    
    @Override
    public long autoSelection(String specialization) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+specialization);
        if(specialization.isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return -1;
        }
        try{
            List<Employee> list = readFromXml(Employee.class);
            List<Long> specialists = new ArrayList<>();
            list.stream().filter(item -> (item.getSpecialty()
                    .equals(specialization)))
                    .forEach(item -> specialists.add(item.getId()));
            if (specialists.isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return -1;
            }
            Map <Long, Integer> schedule = new HashMap<>();
            Integer amount;
            List<Long> doctors = specialists.stream().distinct().collect(Collectors.toList());
            Collection<Integer> values;
            for(long doctor : doctors){
                values = getAmountOfAppointments(doctor).get().values();
                values.removeIf(item -> item==0);
                amount=values.size();
                schedule.put(doctor, amount);
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + doctor + " " + amount);
            }
            int min = Collections.min(schedule.values());
            long id=-1;
            for(Map.Entry<Long, Integer> item : schedule.entrySet()){
                if((item.getValue()==min)){
                    id = item.getKey();
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED) + id);
                    return id;
                }
            }
            return id;
        }catch(Exception e){
            log.error(e);
            return -1;
        }
    }
    
    @Override
    public Optional<Map<Integer, Integer>> getAmountOfAppointments(long doctorId) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+doctorId);
        try{
            if (getEmployee(doctorId).isPresent()){
            List<Appointment> list = readFromXml(Appointment.class);
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(DAY_OF_MONTH);
            int nextDay = currentDay+1;
            int currentMonth = calendar.get(MONTH);
            int currentYear = calendar.get(YEAR);
            List<Integer> hours = new ArrayList<>();
            Map<Integer, Integer> date = new HashMap<>();
            
            while (nextDay<=(currentDay+7)){
                hours.clear();
                for(Appointment check : list){
                    if (check.getDay()==nextDay&check.getMonth()==currentMonth
                            &check.getYear()==currentYear&check.getDoctorId()==doctorId){
                        hours.add(check.getHour());
                    }
                }
                date.put(nextDay, hours.size());
                nextDay++;
            }
            for(Map.Entry<Integer, Integer> item : date.entrySet()){
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + item.getKey() + "." + Month.of(currentMonth+1) + " - " + item.getValue());
            }   
            return Optional.of(date);
        } log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));  
          return Optional.empty();
        }catch(IOException e){
            log.error(e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Map<Integer, Integer>> findDate(long doctorId) throws Exception {
        log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+doctorId);
        try{
            if (getEmployee(doctorId).isPresent()){
            List<Appointment> list = readFromXml(Appointment.class);
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(DAY_OF_MONTH);
            int nextDay = currentDay+1;
            int currentMonth = calendar.get(MONTH);
            int currentYear = calendar.get(YEAR);
            List<Integer> hours = new ArrayList<>();
            Map<Integer, Integer> date = new HashMap<>();
            
            while (nextDay<=(currentDay+7)){
                hours.clear();
                for(Appointment check : list){
                    if (check.getDay()==nextDay&check.getMonth()==currentMonth
                            &check.getYear()==currentYear&check.getDoctorId()==doctorId){
                        hours.add(check.getHour());
                    }
                }
                if(hours.isEmpty()){
                    date.put(nextDay, Constants.WORK_START_HOURS);
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+nextDay+" "+date.get(nextDay));
                    return Optional.of(date);
                }
                if(hours.size()!=Constants.WORK_HOURS){
                        hours.stream().sorted().distinct();
                        for(int item : hours){
                             for(int i=Constants.WORK_START_HOURS;
                                     i<Constants.WORK_START_HOURS+Constants.WORK_HOURS;i++){
                                if(item==i){
                                    continue;
                                }
                                date.put(nextDay, item);
                                log.info(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+nextDay+" "+date.get(nextDay));               
                                return Optional.of(date);
                            }
                        }
                    }else{                   
                        log.info(nextDay + " " + currentMonth + " " 
                                + ConfigurationUtil.getConfigurationEntry(Constants.FULL_DAY));                   
                    }
            nextDay++;   
            }
            return Optional.empty();
        } log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));  
          return Optional.empty();
        }catch(IOException e){
            log.error(e);
           return Optional.empty();
        }}

    @Override
    public RequestStatus createEmployee(String name, long phone, String specialty, String competence) throws Exception {
        try {
            if (name.isEmpty()|String.valueOf(phone).isEmpty()|specialty.isEmpty()|competence.isEmpty()) {
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (!checkString(name)||!checkString(specialty)||!checkString(competence)){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG));
                return RequestStatus.FAIL;
            }
            Employee employee = new Employee();
            employee.setId(getNextEmployee());
            employee.setName(name);
            employee.setPhone(phone);
            employee.setSpecialty(specialty);
            employee.setCompetence(competence);
            writeToXml(employee);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + employee.getId());
            return RequestStatus.SUCCESS;
           } catch (IOException e) {
                log.error(e);
               return RequestStatus.FAIL;
        }
    }
    @Override
    public Optional<Employee> getEmployee(long id) throws Exception {
        log.debug(id);
        List<Employee> list=readFromXml(Employee.class);
        try{
            Employee employee = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(employee);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
     }

    @Override
    public RequestStatus updateEmployee(long id, Employee employee) throws Exception { 
        log.debug(id);
        try {
        if (employee.getName().isEmpty()|String.valueOf(employee.getPhone()).isEmpty()|employee.getSpecialty().isEmpty()
                |employee.getCompetence().isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        List<Employee> list = readFromXml(Employee.class);
        //log.debug(list.get(32));
        Optional<Employee> optional = list.stream()
                .filter(item -> item.getId() == employee.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(employee);
        writeToXml(Employee.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + employee.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteEmployee(long id) throws Exception {
        log.debug(id);
        try{
            if(getEmployee(id).isPresent()){
            List<Employee> list = readFromXml(Employee.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Employee.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createPatient(String name, long phone, String passport, String allergy) throws IOException, Exception {
        try {
            if (name.isEmpty()|String.valueOf(phone).isEmpty()|passport.isEmpty()|allergy.isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (!checkString(name)||!checkString(allergy)){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG));
                return RequestStatus.FAIL;
            }
            Patient patient = new Patient();
            patient.setId(getNextPatient());
            patient.setName(name);
            patient.setPhone(phone);
            patient.setPassport(passport);
            patient.setAllergy(allergy);
            writeToXml(patient);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + patient.getId());
            return RequestStatus.SUCCESS;
            } catch (IOException e) {
                log.error(e);
                return RequestStatus.SUCCESS;
            }
    }

    @Override
    public Optional<Patient> getPatient(long id) throws Exception {
        log.debug(id);
        List<Patient> list=readFromXml(Patient.class);
        try{
            Patient patient = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(patient);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
   }

    @Override
    public RequestStatus updatePatient(Patient patient) throws Exception {
        log.debug(patient.getId());
        try {
        if (patient.getName().isEmpty()|String.valueOf(patient.getPhone()).isEmpty()|patient.getPassport().isEmpty()
                |patient.getAllergy().isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        List<Patient> list = readFromXml(Patient.class);
        //log.debug(list.get(32));
        Optional<Patient> optional = list.stream()
                .filter(item -> item.getId() == patient.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(patient);
        writeToXml(Patient.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + patient.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deletePatient(long id) throws Exception {
        log.debug(id);
        try{
            if(getPatient(id).isPresent()){
            List<Patient> list = readFromXml(Patient.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Patient.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createSurvey(long patientId, long doctorId, String date, String type, String report) throws Exception {
        try {
            if (String.valueOf(patientId).isEmpty()|String.valueOf(doctorId).isEmpty()|String.valueOf(date).isEmpty()|
                String.valueOf(type).isEmpty()|String.valueOf(report).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getPatient(patientId).isEmpty()|getEmployee(doctorId).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = getCalendar(date);
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            Survey survey = new Survey();
            survey.setId(getNextSurvey());
            survey.setPatientId(patientId);
            survey.setDoctorId(doctorId);           
            survey.setDay(calendar.get(DAY_OF_MONTH));        
            survey.setMonth(calendar.get(MONTH));        
            survey.setYear(calendar.get(YEAR));    
            survey.setHour(calendar.get(HOUR));    
            survey.setMinute(calendar.get(MINUTE));    
            survey.setStatus(ServiceStatus.MEDICALTEST);
            survey.setType(type);
            survey.setReport(report);
            writeToXml(survey);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + survey.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }
    }

    @Override
    public Optional<Survey> getSurvey(long id) throws Exception {
        log.debug(id);
        List<Survey> list=readFromXml(Survey.class);
        try{
            Survey survey = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(survey);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus updateSurvey(Survey survey) throws Exception {
        log.debug(survey.getId());
        try {
        if (String.valueOf(survey.getPatientId()).isEmpty()|String.valueOf(survey.getDoctorId()).isEmpty()
                |String.valueOf(survey.getStatus()).isEmpty()
                |String.valueOf(survey.getMonth()).isEmpty()
                |survey.getType().isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        List<Survey> list = readFromXml(Survey.class);
        //log.debug(list.get(32));
        Optional<Survey> optional = list.stream()
                .filter(item -> item.getId() == survey.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(survey);
        writeToXml(Survey.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + survey.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteSurvey(long id) throws Exception {
        log.debug(id);
        try{
            if(getSurvey(id).isPresent()){
            List<Survey> list = readFromXml(Survey.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Survey.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createAppointment(long patientId, long doctorId, ServiceStatus status, String date, String specialization) throws Exception {
       try {
            if (String.valueOf(patientId).isEmpty()|String.valueOf(doctorId).isEmpty()|String.valueOf(date).isEmpty()|
                status!=ServiceStatus.APPOINTED|String.valueOf(specialization).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (doctorId==-5){
                doctorId=autoSelection(specialization);
            }
            if (getPatient(patientId).isEmpty()|getEmployee(doctorId).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            if (date.equals("no")){
                Integer day = findDate(doctorId).get().keySet().iterator().next();
                Integer hour = findDate(doctorId).get().get(day);
                calendar.set(DAY_OF_MONTH, day);
                calendar.set(HOUR, hour);
                calendar.set(MINUTE, 0);
            }else
                calendar=getCalendar(date);           
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            Appointment appointment = new Appointment();
            appointment.setPatientId(patientId);
            appointment.setDoctorId(doctorId);
            appointment.setDay(calendar.get(DAY_OF_MONTH));        
            appointment.setMonth(calendar.get(MONTH));        
            appointment.setYear(calendar.get(YEAR));    
            appointment.setHour(calendar.get(HOUR));    
            appointment.setMinute(calendar.get(MINUTE));    
            appointment.setStatus(status);
            appointment.setSpecialization(specialization); 
            writeToXml(appointment);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + appointment.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public Optional<Appointment> getAppointment(long id) throws Exception { 
        log.debug(id);
        try{
            List<Appointment> list=readFromXml(Appointment.class);
            Appointment appointment = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(appointment);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus updateAppointment(long id, Appointment appointment) throws Exception {
        log.debug(id);
        try {
         if (String.valueOf(appointment.getPatientId()).isEmpty()|String.valueOf(appointment.getDoctorId()).isEmpty()
                  |String.valueOf(appointment.getDay()).isEmpty()|String.valueOf(appointment.getMonth()).isEmpty()|
                  String.valueOf(appointment.getYear()).isEmpty()|String.valueOf(appointment.getHour()).isEmpty()|
                  String.valueOf(appointment.getMinute()).isEmpty()|
                String.valueOf(appointment.getStatus()).isEmpty()|appointment.getStatus()==ServiceStatus.MEDICALTEST|
                     String.valueOf(appointment.getSpecialization()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
         if (getPatient(appointment.getPatientId()).isEmpty()|getEmployee(appointment.getDoctorId()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        List<Appointment> list = readFromXml(Appointment.class);
        Optional<Appointment> optional = list.stream()
                .filter(item -> item.getId() == appointment.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(appointment);
        writeToXml(Appointment.class, list, true);
         log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + appointment.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus deleteAppointment(long id) throws Exception {
        log.debug(id);
        try{
            if(getAppointment(id).isPresent()){
            List<Appointment> list = readFromXml(Appointment.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Appointment.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createDiagnosis(boolean signature, long patientId, long serviceId, 
            String diagnosisInf, String medicine, String medicalTests) throws Exception {
        try {
            if (String.valueOf(serviceId).isEmpty()|diagnosisInf.isEmpty()|
                    String.valueOf(signature).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getPatient(patientId).isEmpty()|getAppointment(serviceId).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }            
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setId(getNextDiagnosis());
            diagnosis.setPatientId(patientId);
            diagnosis.setServiceId(serviceId);
            diagnosis.setSignature(signature);
            diagnosis.setDiagnosis(diagnosisInf);
            diagnosis.setMedicine(medicine);
            diagnosis.setMedicalTests(medicalTests);
            diagnosis.setYear(getAppointment(serviceId).get().getYear());
            diagnosis.setMonth(getAppointment(serviceId).get().getMonth());
            diagnosis.setDay(getAppointment(serviceId).get().getDay());
            diagnosis.setHour(getAppointment(serviceId).get().getHour());
            diagnosis.setMinute(getAppointment(serviceId).get().getMinute());
            writeToXml(diagnosis);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + diagnosis.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

   @Override
    public Optional<Diagnosis> getDiagnosis(long id) throws Exception {
        log.debug(id);
        List<Diagnosis> list=readFromXml(Diagnosis.class);
        try{
            Diagnosis diagnosis = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(diagnosis);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editDiagnosis(Diagnosis diagnosis) throws Exception {
        log.debug(diagnosis.getId());
        try {
        if (String.valueOf(diagnosis.getPatientId()).isEmpty()|String.valueOf(diagnosis.getServiceId()).isEmpty()
                |String.valueOf(diagnosis.getDiagnosis()).isEmpty()) 
        {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        if (getPatient(diagnosis.getPatientId()).isEmpty()|getAppointment(diagnosis.getServiceId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        List<Diagnosis> list = readFromXml(Diagnosis.class);
        Optional<Diagnosis> optional = list.stream()
                .filter(item -> item.getId() == diagnosis.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(diagnosis);
        writeToXml(Diagnosis.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + diagnosis.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus deleteDiagnosis(long id) throws Exception {
        log.debug(id);
         try{
            if(getDiagnosis(id).isPresent()){
            List<Diagnosis> list = readFromXml(Diagnosis.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Diagnosis.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createPayment(boolean signature, long serviceId, long number, int price) throws Exception {
        try {
            if (String.valueOf(signature).isEmpty()|String.valueOf(serviceId).isEmpty()
                    |String.valueOf(number).isEmpty()|String.valueOf(price).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getAppointment(serviceId).isEmpty()||
                   getAppointment(serviceId).get().getStatus()!=ServiceStatus.APPOINTED){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Payment payment = new Payment();
            payment.setId(getNextPayment());
            payment.setSignature(signature);           
            payment.setServiceId(serviceId);
            payment.setNumber(number);
            payment.setPrice(price);
            payment.setYear(getAppointment(serviceId).get().getYear());
            payment.setMonth(getAppointment(serviceId).get().getMonth());
            payment.setDay(getAppointment(serviceId).get().getDay());
            payment.setHour(getAppointment(serviceId).get().getHour());
            payment.setMinute(getAppointment(serviceId).get().getMinute());
            writeToXml(payment);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + payment.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
        
    }

    @Override
    public Optional<Payment> getPayment(long id) throws Exception {
        log.debug(id);
        List<Payment> list=readFromXml(Payment.class);
        try{
            Payment payment = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(payment);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editPayment(Payment payment) throws Exception {
        log.debug(payment.getId());
        try {
        if (String.valueOf(payment.getServiceId()).isEmpty()|String.valueOf(payment.getNumber()).isEmpty()
                |String.valueOf(payment.getPrice()).isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
         if (getAppointment(payment.getServiceId()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        List<Payment> list = readFromXml(Payment.class);
        //log.debug(list.get(32));
        Optional<Payment> optional = list.stream()
                .filter(item -> item.getId() == payment.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(payment);
        writeToXml(Payment.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + payment.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }}

    @Override
    public RequestStatus deletePayment(long id) throws Exception {
        log.debug(id);
         try{
            if(getPayment(id).isPresent()){
            List<Payment> list = readFromXml(Payment.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Payment.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createMedicalInsurance(boolean signature, long serviceId, int reimbursement, long number) throws Exception {
        try {
            if (String.valueOf(serviceId).isEmpty()|String.valueOf(number).isEmpty()|String.valueOf(reimbursement).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getAppointment(serviceId).isEmpty()||
                   getAppointment(serviceId).get().getStatus()!=ServiceStatus.APPOINTED){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            MedicalInsurance medicalInsurance = new MedicalInsurance();
            medicalInsurance.setId(getNextMedicalInsurance());
            medicalInsurance.setSignature(signature);           
            medicalInsurance.setServiceId(serviceId);
            medicalInsurance.setNumber(number);
            medicalInsurance.setReimbursement(reimbursement);
            medicalInsurance.setYear(getAppointment(serviceId).get().getYear());
            medicalInsurance.setMonth(getAppointment(serviceId).get().getMonth());
            medicalInsurance.setDay(getAppointment(serviceId).get().getDay());
            medicalInsurance.setHour(getAppointment(serviceId).get().getHour());
            medicalInsurance.setMinute(getAppointment(serviceId).get().getMinute());
            writeToXml(medicalInsurance);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + medicalInsurance.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }
    @Override
    public Optional<MedicalInsurance> getMedicalInsurance(long id) throws Exception {
        log.debug(id);
        List<MedicalInsurance> list=readFromXml(MedicalInsurance.class);
        try{
            MedicalInsurance medicalInsurance = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(medicalInsurance);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editMedicalInsurance(MedicalInsurance medicalInsurance) throws Exception {
        log.debug(medicalInsurance.getId());
    try{
    if (String.valueOf(medicalInsurance.getServiceId()).isEmpty()|String.valueOf(medicalInsurance.getNumber()).isEmpty()
            |String.valueOf(medicalInsurance.getReimbursement()).isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        if (getAppointment(medicalInsurance.getServiceId()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
        List<MedicalInsurance> list = readFromXml(MedicalInsurance.class);
        //log.debug(list.get(32));
        Optional<MedicalInsurance> optional = list.stream()
                .filter(item -> item.getId() == medicalInsurance.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(medicalInsurance);
        writeToXml(MedicalInsurance.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + medicalInsurance.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
}
    @Override
    public RequestStatus deleteMedicalInsurance(long id) throws Exception {
        log.debug(id);
        try{
            if(getMedicalInsurance(id).isPresent()){
            List<MedicalInsurance> list = readFromXml(MedicalInsurance.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(MedicalInsurance.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createHospitalization(boolean signature, long diagnosisId, long hospitalId, int ward, HospitalizationStatus status) throws Exception {
        try {
            if (String.valueOf(diagnosisId).isEmpty()|String.valueOf(hospitalId).isEmpty()|String.valueOf(ward).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getDiagnosis(diagnosisId).isEmpty()|getHospital(hospitalId).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Hospitalization hospitalization = new Hospitalization();
            hospitalization.setId(getNextHospitalization());
            hospitalization.setSignature(signature);           
            hospitalization.setServiceId(diagnosisId);
            hospitalization.setHospitalId(hospitalId);
            hospitalization.setWard(ward);
            hospitalization.setStatus(status);
            hospitalization.setYear(getDiagnosis(diagnosisId).get().getYear());
            hospitalization.setMonth(getDiagnosis(diagnosisId).get().getMonth());
            hospitalization.setDay(getDiagnosis(diagnosisId).get().getDay());
            hospitalization.setHour(getDiagnosis(diagnosisId).get().getHour());
            hospitalization.setMinute(getDiagnosis(diagnosisId).get().getMinute());
            writeToXml(hospitalization);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospitalization.getId());
            return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public Optional<Hospitalization> getHospitalization(long id) throws Exception {
        log.debug(id);
        List<Hospitalization> list=readFromXml(Hospitalization.class);
        try{
            Hospitalization hospitalization = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(hospitalization);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editHospitalization(long id, Hospitalization hospitalization) throws Exception {
        log.debug(hospitalization.getId());
        try {
        if (String.valueOf(hospitalization.getServiceId()).isEmpty()|String.valueOf(hospitalization.getHospitalId()).isEmpty()
                |String.valueOf(hospitalization.getWard()).isEmpty()
                |String.valueOf(hospitalization.getStatus()).isEmpty())
        {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        if (getDiagnosis(hospitalization.getServiceId()).isEmpty()|getHospital(hospitalization.getHospitalId()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
        }
        List<Hospitalization> list = readFromXml(Hospitalization.class);
        Optional<Hospitalization> optional = list.stream()
                .filter(item -> item.getId() == hospitalization.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(hospitalization);
        writeToXml(Hospitalization.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospitalization.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus deleteHospitalization(long id) throws Exception {
        log.debug(id);
        try{
            if(getHospitalization(id).isPresent()){
            List<Hospitalization> list = readFromXml(Hospitalization.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Hospitalization.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createHospital(String name, int wardsNumber) throws Exception {
            try {
            if (name.isEmpty()|String.valueOf(wardsNumber).isEmpty()) {
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
       Hospital hospital = new Hospital();
       hospital.setId(getNextHospital());
       hospital.setName(name);
       hospital.setWardsNumber(wardsNumber);
       writeToXml(hospital);
       log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospital.getId());
        return RequestStatus.SUCCESS;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }
    }

    @Override
    public Optional<Hospital> getHospital(long id) throws Exception {
        log.debug(id);
        List<Hospital> list=readFromXml(Hospital.class);
        try{
            Hospital hospital = list.stream()
                .filter(el->el.getId()==id)
                .findFirst().get();
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(hospital);
        } catch(NoSuchElementException e){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editHospital(Hospital hospital) throws Exception {
        log.debug(hospital.getId());
        try{
        if (hospital.getName().isEmpty()|String.valueOf(hospital.getWardsNumber()).isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        List<Hospital> list = readFromXml(Hospital.class);
        //log.debug(list.get(32));
        Optional<Hospital> optional = list.stream()
                .filter(item -> item.getId() == hospital.getId())
                .findFirst();
        if (optional.isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        list.remove(optional.get());
        list.add(hospital);
        writeToXml(Hospital.class, list, true);
        log.info(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospital.getId());
        return RequestStatus.SUCCESS;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteHospital(long id) throws Exception {
        log.debug(id);
        try{
            if(getHospital(id).isPresent()){
            List<Hospital> list = readFromXml(Hospital.class);
            list.removeIf(item -> item.getId() == id);
            writeToXml(Hospital.class, list, true);
            log.info(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
            return RequestStatus.SUCCESS;
            }
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }catch (Exception e){
        log.error(e);
        return RequestStatus.FAIL;
        }
    }


    private <T> boolean writeToXml(Class<?> tClass, List<T> object, boolean overwrite) {
        List<T> fileObjectList;
        if (!overwrite) {
            fileObjectList = (List<T>) readFromXml(tClass);
            fileObjectList.addAll(object);
        }
        else {
            fileObjectList = new ArrayList<>(object);
        }
        try {
            File file = new File(ConfigurationUtil.getConfigurationEntry(Constants.XMLPATH)
                    + tClass.getSimpleName().toLowerCase()
                    + ConfigurationUtil.getConfigurationEntry(Constants.XMLEXTENSION));
            if (!file.exists()){
                file.createNewFile();
            }
            Serializer serializer = new Persister();
            WrapperXML<T> WrapperXML = new WrapperXML<>();
            WrapperXML.setList(fileObjectList);
            serializer.write(WrapperXML, file);
            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }
    private <T> boolean writeToXml(T object) {
        try {
            if (object == null) {
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return false;
            }
            return writeToXml(object.getClass(), Collections.singletonList(object), false);
        } catch (IOException e) {
            log.error(e);
            return false;
        }
    }
    private <T> List<T> readFromXml(Class<T> tClass) {
        try {
            File file = new File(ConfigurationUtil.getConfigurationEntry(Constants.XMLPATH)
                    + tClass.getSimpleName().toLowerCase()
                    + ConfigurationUtil.getConfigurationEntry(Constants.XMLEXTENSION));
            if (!file.exists()){
                file.createNewFile();
            }
            Serializer serializer = new Persister();
            WrapperXML<T> WrapperXML = new WrapperXML<>();
            WrapperXML = serializer.read(WrapperXML.getClass(), file);
            if (WrapperXML.getList() == null) WrapperXML.setList(new ArrayList<>());
            return WrapperXML.getList();
        } catch (Exception e) {
            log.error(e);
            return new ArrayList<>();
        }
    }
       
    public Calendar getCalendar(String date) throws ParseException, IOException{
        try{
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy hh:mm");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(format.parse(date));
            return calendar;
            }catch(ParseException e){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG_PARSE));
                return null;
            }
        }
    
    public long getNextSurvey() throws IOException{
        List<Survey> objectList = readFromXml(Survey.class);
        if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextEmployee() throws IOException{
        List<Employee> objectList = readFromXml(Employee.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }     
    public long getNextPatient() throws IOException{
        List<Patient> objectList = readFromXml(Patient.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextAppointment() throws IOException{
        List<Appointment> objectList = readFromXml(Appointment.class);
        if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextDiagnosis() throws IOException{
        List<Diagnosis> objectList = readFromXml(Diagnosis.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextPayment() throws IOException{
        List<Payment> objectList = readFromXml(Payment.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextMedicalInsurance() throws IOException{
        List<MedicalInsurance> objectList = readFromXml(MedicalInsurance.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }
    public long getNextHospitalization() throws IOException{
        List<Hospitalization> objectList = readFromXml(Hospitalization.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }   
    public long getNextHospital() throws IOException{
        List<Hospital> objectList = readFromXml(Hospital.class);
         if (objectList.isEmpty()) return 1;
        List<Long> idList = new ArrayList<>();
        objectList.stream().forEach(item -> idList.add(item.getId()));
        Long id = idList.stream().max(Long::compare).get()+1;
        return id;
    }

    public boolean setUp() throws Exception {
        try{
            if (getEmployee(0).isEmpty()){
                Employee employee = new Employee();
                employee.setId(0);
                employee.setName(Constants.NAME);
                employee.setPhone(Constants.PHONE);
                employee.setSpecialty(Constants.SPECIALTY);
                employee.setCompetence(Constants.COMPETENCE);
                writeToXml(employee);
            }
            if (getPatient(0).isEmpty()){
                Patient patient = new Patient();
                patient.setId(0);
                patient.setName(Constants.NAME);
                patient.setPhone(Constants.PHONE);
                patient.setPassport(Constants.PASSPORT);
                patient.setAllergy(Constants.ALLERGY);
                writeToXml(patient);
            }
            if (getSurvey(0).isEmpty()){
                Survey survey = new Survey();
                survey.setId(0);
                survey.setPatientId(0);
                survey.setDoctorId(0);
                Calendar calendar = Calendar.getInstance();
                do{
                    calendar.add(Calendar.DATE, 1);
                }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
                calendar.set(HOUR, 11);
                calendar.set(MINUTE, 00);
                survey.setDay(calendar.get(DAY_OF_MONTH));        
                survey.setMonth(calendar.get(MONTH));        
                survey.setYear(calendar.get(YEAR));    
                survey.setHour(calendar.get(HOUR));    
                survey.setMinute(calendar.get(MINUTE));    
                survey.setStatus(ServiceStatus.MEDICALTEST);
                survey.setType(Constants.TYPE);
                survey.setReport(Constants.REPORT);
                writeToXml(survey);
            }
            if (getAppointment(0).isEmpty()){
                Appointment appointment = new Appointment();
                Calendar calendar = Calendar.getInstance();
                do{
                    calendar.add(Calendar.DATE, 1);
                }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
                calendar.set(HOUR, 11);
                calendar.set(MINUTE, 00);
                appointment.setId(0);
                appointment.setPatientId(0);
                appointment.setDoctorId(0);
                appointment.setDay(calendar.get(DAY_OF_MONTH));        
                appointment.setMonth(calendar.get(MONTH));        
                appointment.setYear(calendar.get(YEAR));    
                appointment.setHour(calendar.get(HOUR));    
                appointment.setMinute(calendar.get(MINUTE));    
                appointment.setStatus(ServiceStatus.APPOINTED);
                appointment.setSpecialization(Constants.SPECIALIZATION); 
                writeToXml(appointment);
            }
            if (getDiagnosis(0).isEmpty()){
                Diagnosis diagnosis = new Diagnosis();
                diagnosis.setId(0);
                diagnosis.setPatientId(0);
                diagnosis.setServiceId(0);
                diagnosis.setSignature(false);
                diagnosis.setDiagnosis(Constants.DIAGNOSIS);
                diagnosis.setMedicine(Constants.MEDICINE);
                diagnosis.setMedicalTests(Constants.MEDICAL_TESTS);
                diagnosis.setYear(getAppointment(0).get().getYear());
                diagnosis.setMonth(getAppointment(0).get().getMonth());
                diagnosis.setDay(getAppointment(0).get().getDay());
                diagnosis.setHour(getAppointment(0).get().getHour());
                diagnosis.setMinute(getAppointment(0).get().getMinute());
                writeToXml(diagnosis);
            }
            if (getHospital(0).isEmpty()){
                Hospital hospital = new Hospital();
                hospital.setId(0);
                hospital.setName(Constants.NAME);
                hospital.setWardsNumber(Constants.WARDS);
                writeToXml(hospital);
            }
            if(getHospitalization(0).isEmpty()){
                Hospitalization hospitalization = new Hospitalization();
                hospitalization.setId(0);
                hospitalization.setSignature(false);           
                hospitalization.setServiceId(0);
                hospitalization.setHospitalId(0);
                hospitalization.setWard(1);
                hospitalization.setStatus(HospitalizationStatus.HOSPITALIZED);
                hospitalization.setYear(getDiagnosis(0).get().getYear());
                hospitalization.setMonth(getDiagnosis(0).get().getMonth());
                hospitalization.setDay(getDiagnosis(0).get().getDay());
                hospitalization.setHour(getDiagnosis(0).get().getHour());
                hospitalization.setMinute(getDiagnosis(0).get().getMinute());
                writeToXml(hospitalization);
            }
            if (getMedicalInsurance(0).isEmpty()){
                MedicalInsurance medicalInsurance = new MedicalInsurance();
                medicalInsurance.setId(0);
                medicalInsurance.setSignature(false);           
                medicalInsurance.setServiceId(0);
                medicalInsurance.setNumber(123);
                medicalInsurance.setReimbursement(1000);
                medicalInsurance.setYear(getAppointment(0).get().getYear());
                medicalInsurance.setMonth(getAppointment(0).get().getMonth());
                medicalInsurance.setDay(getAppointment(0).get().getDay());
                medicalInsurance.setHour(getAppointment(0).get().getHour());
                medicalInsurance.setMinute(getAppointment(0).get().getMinute());
            writeToXml(medicalInsurance);
            }
            if (getPayment(0).isEmpty()){
               Payment payment = new Payment();
                payment.setId(0);
                payment.setSignature(false);           
                payment.setServiceId(0);
                payment.setNumber(456);
                payment.setPrice(1100);
                payment.setYear(getAppointment(0).get().getYear());
                payment.setMonth(getAppointment(0).get().getMonth());
                payment.setDay(getAppointment(0).get().getDay());
                payment.setHour(getAppointment(0).get().getHour());
                payment.setMinute(getAppointment(0).get().getMinute());
                writeToXml(payment);
            }
            return true;
        }catch(Exception e){
            log.error(e);
            return false;
        }
    }
    
    public RequestStatus checkDate(Calendar calendar) throws Exception{
        Calendar currentDate = Calendar.getInstance();
        if (calendar.get(YEAR)==currentDate.get(YEAR)
                &calendar.get(MONTH)==currentDate.get(MONTH)
                &currentDate.get(DAY_OF_MONTH)-calendar.get(DAY_OF_MONTH)<8
                &currentDate.get(DAY_OF_MONTH)!=calendar.get(DAY_OF_MONTH)
                &calendar.get(MINUTE)==0
                &(calendar.get(HOUR)>=Constants.WORK_START_HOURS
                |calendar.get(HOUR)<(Constants.WORK_START_HOURS+Constants.WORK_HOURS))
                &(calendar.get(DAY_OF_WEEK)!=1
                &calendar.get(DAY_OF_WEEK)!=7)){
                return RequestStatus.SUCCESS;
        }else{
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG_DATE));
            return RequestStatus.FAIL;
        }       
    }
    
    public boolean checkString(String string) throws Exception{
        Pattern pattern = Pattern.compile ("[a-zA-Z\\s]+");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
