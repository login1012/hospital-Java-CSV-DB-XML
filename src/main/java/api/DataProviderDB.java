/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

/**
 *
 * @author Liza
 */
public class DataProviderDB implements DataProvider{
    private static final Logger log = LogManager.getLogger(Main.class);
    private static DataProviderDB instance;
    Connection connect;
      
    public DataProviderDB() throws IOException, ClassNotFoundException, SQLException {
        getConnection();      
    }
     
    public static DataProviderDB getInstance(){
        try{
            if (instance == null) {
                instance = new DataProviderDB();
            }
            return instance;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return instance;
        }
  }
     
    private Connection getConnection() throws IOException, ClassNotFoundException, SQLException{
        try{
            Class.forName(ConfigurationUtil.getConfigurationEntry(Constants.DB_DRIVER));
            connect = DriverManager.getConnection(ConfigurationUtil.getConfigurationEntry(Constants.DB_URL),
            ConfigurationUtil.getConfigurationEntry(Constants.DB_USER),
            ConfigurationUtil.getConfigurationEntry(Constants.DB_PASS));
            return connect;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            throw new IOException();
    }}
     
    private RequestStatus execute(String sql)  throws IOException, ClassNotFoundException, SQLException{       
        log.debug(sql);
        try{
            try (PreparedStatement statement = connect.prepareStatement(sql)) {
                statement.executeUpdate();
            }
            getConnection().commit();
            return RequestStatus.SUCCESS;
        }catch(SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }
    
    private ResultSet getResultSet(String sql) throws IOException, ClassNotFoundException, SQLException, Exception{
        log.debug(sql);
        ResultSet set;
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            set = statement.executeQuery();
            return set; 
        }catch(SQLException e){
            log.error(e);
            return null;
        }   
     }

    public RequestStatus setDB() throws IOException, ClassNotFoundException, SQLException, SQLException{
        try{
            execute(Constants.CREATE_EMPLOYEE);
            execute(Constants.CREATE_DIAGNOSIS);
            execute(Constants.CREATE_APPOINTMENT);
            execute(Constants.CREATE_HOSPITAL);
            execute(Constants.CREATE_HOSPITALIZATION);
            execute(Constants.CREATE_MEDICALINSURANCE);
            execute(Constants.CREATE_PATIENT);
            execute(Constants.CREATE_PAYMENT);
            execute(Constants.CREATE_SURVEY);
            return RequestStatus.SUCCESS;
        }catch(IOException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

     
      @Override
    public Optional<List<Diagnosis>> viewHistory(long patientId) throws Exception {       
        if (getPatient(patientId).isPresent()){
            try{
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+getPatient(patientId).get().toString());
                ResultSet set;
                PreparedStatement statement = getConnection().prepareStatement(String.format((Constants.SELECT_HISTORY), patientId),
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                set = statement.executeQuery();
                if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
                }
                set.beforeFirst();
                List<Diagnosis> history = new ArrayList<>();
                while (set.next()) {
                            history.add(getDiagnosis(set.getInt(Constants.COLUMN_ID)).get());
                }        
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+history.toString());
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
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                    return RequestStatus.SUCCESS;
                }
                appointment.setStatus(status);
                updateAppointment(serviceId, appointment);
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.CHANGE)+appointment.toString());
                return RequestStatus.SUCCESS;
            }catch(Exception e){
            log.error(e);
            return RequestStatus.FAIL;
        }}else{
              log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.CHANGE)+diagnosis.toString()); 
                     return RequestStatus.SUCCESS;
                }
                else{
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                    return RequestStatus.SUCCESS;
                }
            }else {
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return 0;
            }
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_ALL_PAYMENTS),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            set = statement.executeQuery();            
            set.beforeFirst();
             List<Payment> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getPayment(set.getInt(Constants.COLUMN_ID)).get());
                }
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return Optional.empty();
            }    
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_ALL_DIAGNOSIS),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            set = statement.executeQuery();            
            List<Diagnosis> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getDiagnosis(set.getInt(Constants.COLUMN_ID)).get());
                }
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
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) +
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
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+months);
            if(months<1){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRONG)+months);
                return Optional.empty();
            }    
            
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_ALL_DIAGNOSIS),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return Optional.empty();
                } 
            set.beforeFirst();
             List<Diagnosis> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getDiagnosis(set.getInt(Constants.COLUMN_ID)).get());
                }          
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + 
                        item.getKey() + " - " + item.getValue());
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
    public RequestStatus discharged(long hospitalizationId) throws Exception {
       if (getHospitalization(hospitalizationId).isPresent()){
        try{
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ORIGINAL)+hospitalizationId);
            Hospitalization hospitalization = getHospitalization(hospitalizationId).get();
            if(hospitalization.getStatus()==HospitalizationStatus.DISCHARGED){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ALREADY_DONE));
                return RequestStatus.SUCCESS;
            }
            hospitalization.setStatus(HospitalizationStatus.DISCHARGED);
            editHospitalization(hospitalizationId, hospitalization);
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospitalizationId);
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
                ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_ALL_HOSPITALIZATION),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            set = statement.executeQuery();
            
              List<Hospitalization> hospitalizations = new ArrayList<>();
                while (set.next()) {
                            hospitalizations.add(getHospitalization(set.getInt(Constants.COLUMN_ID)).get());
                }          
                List<Hospitalization> list = hospitalizations
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
                        }
                    } 
                    if (ward==0){
                        return wards.size()+1;
                    }
                    return ward;
                }else{
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FULL_HOSPITAL));
                    return 0;
                }
            }catch(Exception e){
                log.error(e);
                return 0;
            }
        }else{
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                   log.error(ConfigurationUtil.getConfigurationEntry(Constants.FULL_HOSPITAL));
                   return RequestStatus.SUCCESS;
               }          
            }else{
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SIGNATURE));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.STATUS_NOT));
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
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return -1;
        }
        try{
            ResultSet set;
                PreparedStatement statement = getConnection().prepareStatement(String.format((Constants.SELECT_ALL_EMPLOYEE)),
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                set = statement.executeQuery();              
                List<Employee> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getEmployee(set.getInt(Constants.COLUMN_ID)).get());
                }        
            List<Long> specialists = new ArrayList<>();
            list.stream().filter(item -> (item.getSpecialty()
                    .equals(specialization)))
                    .forEach(item -> specialists.add(item.getId()));
            if (specialists.isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + doctor + " " + amount);
            }
            int min = Collections.min(schedule.values());
            long id=-1;
            for(Map.Entry<Long, Integer> item : schedule.entrySet()){
                if((item.getValue()==min)){
                    id = item.getKey();
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED) + id);
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
                 ResultSet set;
                PreparedStatement statement = getConnection().prepareStatement(String.format((Constants.SELECT_ALL_APPOINTMENT)),
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                set = statement.executeQuery();              
                List<Appointment> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getAppointment(set.getInt(Constants.COLUMN_ID)).get());
                }
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.RESULT) + item.getKey() + "." + Month.of(currentMonth+1) + " - " + item.getValue());
            }   
            return Optional.of(date);
        } log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));  
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
                 ResultSet set;
                PreparedStatement statement = getConnection().prepareStatement(String.format((Constants.SELECT_ALL_APPOINTMENT)),
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                set = statement.executeQuery();              
                List<Appointment> list = new ArrayList<>();
                while (set.next()) {
                            list.add(getAppointment(set.getInt(Constants.COLUMN_ID)).get());
                }
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
                    log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+nextDay+" "+date.get(nextDay));
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
                                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.ACQUIRED)+nextDay+" "+date.get(nextDay));               
                                return Optional.of(date);
                            }
                        }
                    }else{                   
                        log.debug(nextDay + " " + currentMonth + " " 
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
            if (execute(String.format((Constants.INSERT_EMPLOYEE), 
                name, phone, specialty, competence))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextEmployee()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;       
           } catch (IOException e) {
                log.error(e);
               return RequestStatus.FAIL;
        }
    }
    
    @Override
    public Optional<Employee> getEmployee(long id) throws Exception {
        log.debug(id);       
        try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_EMPLOYEE, id));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Employee employee = new Employee();            
            employee.setId(set.getLong(Constants.COLUMN_ID));
            employee.setName(set.getString(Constants.COLUMN_NAME));
            employee.setPhone(set.getLong(Constants.COLUMN_PHONE));
            employee.setSpecialty(set.getString(Constants.COLUMN_SPECIALTY));
            employee.setCompetence(set.getString(Constants.COLUMN_COMPETENCE));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(employee);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }
     }

    @Override
    public RequestStatus updateEmployee(long id, Employee employee) throws Exception {      
       log.debug(id);
        try {
            if (getEmployee(employee.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        if (employee.getName().isEmpty()|String.valueOf(employee.getPhone()).isEmpty()|employee.getSpecialty().isEmpty()
                |employee.getCompetence().isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }        
        
        if(execute(String.format((Constants.UPDATE_EMPLOYEE), 
                employee.getName(), employee.getPhone(), employee.getSpecialty(), employee.getCompetence(), id))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + employee.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteEmployee(long id) throws Exception {
        log.debug(id);
        try{
            if(getEmployee(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_EMPLOYEE), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createPatient(String name, long phone, String passport, String allergy) throws IOException, ClassNotFoundException, SQLException, Exception {
        try {
            if (name.isEmpty()|String.valueOf(phone).isEmpty()|passport.isEmpty()|allergy.isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (!checkString(name)||!checkString(allergy)){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG));
                return RequestStatus.FAIL;
            }           
            if (execute(String.format((Constants.INSERT_PATIENT),
                name, phone, passport, allergy))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextPatient()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;       
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public Optional<Patient> getPatient(long id) throws Exception {
        log.debug(id);       
        try {            
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_PATIENT, id));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Patient patient = new Patient();
            patient.setId(set.getLong(Constants.COLUMN_ID));
            patient.setName(set.getString(Constants.COLUMN_NAME));
            patient.setPhone(set.getLong(Constants.COLUMN_PHONE));
            patient.setPassport(set.getString(Constants.COLUMN_PASSPORT));
            patient.setAllergy(set.getString(Constants.COLUMN_ALLERGY));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(patient);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }
   }

    @Override
    public RequestStatus updatePatient(Patient patient) throws Exception {
       log.debug(patient.getId());
        try {
             if (getPatient(patient.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        if (patient.getName().isEmpty()|String.valueOf(patient.getPhone()).isEmpty()|patient.getPassport().isEmpty()
                |patient.getAllergy().isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
       
        if((execute(String.format((Constants.UPDATE_PATIENT), 
                patient.getName(), patient.getPhone(), patient.getPassport(), patient.getAllergy(), patient.getId())))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + patient.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deletePatient(long id) throws Exception {
         log.debug(id);
        try{
            if(getPatient(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_PATIENT), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = getCalendar(date);
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if (execute(String.format((Constants.INSERT_SURVEY),
                patientId, doctorId, ServiceStatus.MEDICALTEST.ordinal(),
                calendar.get(DAY_OF_MONTH), calendar.get(MONTH), calendar.get(YEAR), calendar.get(HOUR), calendar.get(MINUTE),
                type, report))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextSurvey()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }
    }

    @Override
    public Optional<Survey> getSurvey(long id) throws Exception {
        log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_SURVEY, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Survey survey = new Survey();
            survey.setId(resultSet.getLong(Constants.COLUMN_ID));
            survey.setPatientId(resultSet.getLong(Constants.COLUMN_PATIENTID));
            survey.setDoctorId(resultSet.getLong(Constants.COLUMN_DOCTORID));
            survey.setDay(resultSet.getInt(Constants.COLUMN_DAY));        
            survey.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));        
            survey.setYear(resultSet.getInt(Constants.COLUMN_YEAR));    
            survey.setHour(resultSet.getInt(Constants.COLUMN_HOUR));    
            survey.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));    
            survey.setStatus(ServiceStatus.values()[resultSet.getInt(Constants.COLUMN_STATUS)]);
            survey.setType(resultSet.getString(Constants.COLUMN_TYPE));
            survey.setReport(resultSet.getString(Constants.COLUMN_REPORT));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(survey);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }        
    }

    @Override
    public RequestStatus updateSurvey(Survey survey) throws Exception {
         log.debug(survey.getId());
        try {
            if (getSurvey(survey.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(survey.getPatientId()).isEmpty()|String.valueOf(survey.getDoctorId()).isEmpty()
                  |String.valueOf(survey.getDay()).isEmpty()|String.valueOf(survey.getMonth()).isEmpty()|
                  String.valueOf(survey.getYear()).isEmpty()|String.valueOf(survey.getHour()).isEmpty()|
                  String.valueOf(survey.getMinute()).isEmpty()|
                String.valueOf(survey.getStatus()).isEmpty()|String.valueOf(survey.getType()).isEmpty()|
                     String.valueOf(survey.getReport()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getPatient(survey.getPatientId()).isEmpty()|getEmployee(survey.getDoctorId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, survey.getDay());
            calendar.set(MONTH, survey.getMonth());
            calendar.set(YEAR, survey.getYear());
            calendar.set(HOUR, survey.getHour());
            calendar.set(MINUTE, survey.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
        if(execute(String.format((Constants.UPDATE_SURVEY), 
                survey.getPatientId(), survey.getDoctorId(), survey.getStatus().ordinal(), survey.getDay(), survey.getMonth(), survey.getYear(), 
                survey.getHour(), survey.getMinute(), survey.getType(), survey.getReport(), survey.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + survey.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }       
    }

    @Override
    public RequestStatus deleteSurvey(long id) throws Exception {
        log.debug(id);
        try{
            if(getSurvey(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_SURVEY), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
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
            if (execute(String.format((Constants.INSERT_APPOINTMENT),
                patientId, doctorId, status.ordinal(),
                calendar.get(DAY_OF_MONTH), calendar.get(MONTH), calendar.get(YEAR), calendar.get(HOUR), calendar.get(MINUTE),
                specialization))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextAppointment()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }}
       
    @Override
    public Optional<Appointment> getAppointment(long id) throws Exception {
       log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_APPOINTMENT, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Appointment appointment = new Appointment();
            appointment.setId(resultSet.getLong(Constants.COLUMN_ID));
            appointment.setPatientId(resultSet.getLong(Constants.COLUMN_PATIENTID));
            appointment.setDoctorId(resultSet.getLong(Constants.COLUMN_DOCTORID));
            appointment.setDay(resultSet.getInt(Constants.COLUMN_DAY));        
            appointment.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));        
            appointment.setYear(resultSet.getInt(Constants.COLUMN_YEAR));    
            appointment.setHour(resultSet.getInt(Constants.COLUMN_HOUR));    
            appointment.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));    
            appointment.setStatus(ServiceStatus.values()[resultSet.getInt(Constants.COLUMN_STATUS)]);
            appointment.setSpecialization(resultSet.getString(Constants.COLUMN_SPECIALIZATION));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(appointment);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }        
    }

    @Override
    public RequestStatus updateAppointment(long id, Appointment appointment) throws Exception {
        log.debug(appointment.getId());
        try {
            if (getAppointment(appointment.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(appointment.getPatientId()).isEmpty()|String.valueOf(appointment.getDoctorId()).isEmpty()
                  |String.valueOf(appointment.getDay()).isEmpty()|String.valueOf(appointment.getMonth()).isEmpty()|
                  String.valueOf(appointment.getYear()).isEmpty()|String.valueOf(appointment.getHour()).isEmpty()|
                  String.valueOf(appointment.getMinute()).isEmpty()|
                String.valueOf(appointment.getStatus()).isEmpty()| appointment.getStatus()==ServiceStatus.MEDICALTEST|
                     String.valueOf(appointment.getSpecialization()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getPatient(appointment.getPatientId()).isEmpty()|getEmployee(appointment.getDoctorId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, appointment.getDay());
            calendar.set(MONTH, appointment.getMonth());
            calendar.set(YEAR, appointment.getYear());
            calendar.set(HOUR, appointment.getHour());
            calendar.set(MINUTE, appointment.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
        if(execute(String.format((Constants.UPDATE_APPOINTMENT), 
                appointment.getPatientId(), appointment.getDoctorId(), appointment.getStatus().ordinal(), appointment.getDay(), appointment.getMonth(), appointment.getYear(), 
                appointment.getHour(), appointment.getMinute(), appointment.getSpecialization(), appointment.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + appointment.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }       
    }

    @Override
    public RequestStatus deleteAppointment(long id) throws Exception {
       log.debug(id);
        try{
            if(getAppointment(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_APPOINTMENT), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createDiagnosis(boolean signature, long patientId, long serviceId, 
            String diagnosisInf, String medicine, String medicalTests) throws Exception {
         try {
           if (String.valueOf(signature).isEmpty()|String.valueOf(patientId).isEmpty()|
                   String.valueOf(serviceId).isEmpty()||diagnosisInf.isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getPatient(patientId).isEmpty()|getAppointment(serviceId).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }            
            if (execute(String.format((Constants.INSERT_DIAGNOSIS),
               signature, serviceId, getAppointment(serviceId).get().getDay(),
                getAppointment(serviceId).get().getMonth(),
                 getAppointment(serviceId).get().getYear(),
                  getAppointment(serviceId).get().getHour(),
                   getAppointment(serviceId).get().getMinute(),
                   patientId, diagnosisInf, medicine, medicalTests))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextDiagnosis()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }        
    }

   @Override
    public Optional<Diagnosis> getDiagnosis(long id) throws Exception {
        log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_DIAGNOSIS, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setId(resultSet.getLong(Constants.COLUMN_ID));
            diagnosis.setPatientId(resultSet.getLong(Constants.COLUMN_PATIENTID));
            diagnosis.setDay(resultSet.getInt(Constants.COLUMN_DAY));        
            diagnosis.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));        
            diagnosis.setYear(resultSet.getInt(Constants.COLUMN_YEAR));    
            diagnosis.setHour(resultSet.getInt(Constants.COLUMN_HOUR));    
            diagnosis.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));             
            diagnosis.setSignature(resultSet.getBoolean(Constants.COLUMN_SIGNATURE));
            diagnosis.setServiceId(resultSet.getLong(Constants.COLUMN_SERVICEID));
            diagnosis.setDiagnosis(resultSet.getString(Constants.COLUMN_DIAGNOSISINF));
            diagnosis.setMedicine(resultSet.getString(Constants.COLUMN_MEDICINE));
            diagnosis.setMedicalTests(resultSet.getString(Constants.COLUMN_MEDICALTESTS));           
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(diagnosis);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editDiagnosis(Diagnosis diagnosis) throws Exception {     
        log.debug(diagnosis.getId());
        try {
            if (getDiagnosis(diagnosis.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(diagnosis.getPatientId()).isEmpty()|String.valueOf(diagnosis.getServiceId()).isEmpty()
                  |String.valueOf(diagnosis.getDay()).isEmpty()|String.valueOf(diagnosis.getMonth()).isEmpty()|
                  String.valueOf(diagnosis.getYear()).isEmpty()|String.valueOf(diagnosis.getHour()).isEmpty()|
                  String.valueOf(diagnosis.getMinute()).isEmpty()|
                String.valueOf(diagnosis.getDiagnosis()).isEmpty()|String.valueOf(diagnosis.isSignature()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getPatient(diagnosis.getPatientId()).isEmpty()|getAppointment(diagnosis.getServiceId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, diagnosis.getDay());
            calendar.set(MONTH, diagnosis.getMonth());
            calendar.set(YEAR, diagnosis.getYear());
            calendar.set(HOUR, diagnosis.getHour());
            calendar.set(MINUTE, diagnosis.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.UPDATE_DIAGNOSIS), 
                diagnosis.isSignature(), diagnosis.getServiceId(), diagnosis.getDay(), 
                diagnosis.getMonth(), diagnosis.getYear(),
                 diagnosis.getHour(),diagnosis.getMinute(),
                 diagnosis.getPatientId(), diagnosis.getDiagnosis(), diagnosis.getMedicine(), 
                diagnosis.getMedicalTests(), diagnosis.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + diagnosis.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }       
    }

    @Override
    public RequestStatus deleteDiagnosis(long id) throws Exception {
        log.debug(id);
        try{
            if(getDiagnosis(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_DIAGNOSIS), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
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
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }            
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, getAppointment(serviceId).get().getDay());
            calendar.set(MONTH, getAppointment(serviceId).get().getMonth());
            calendar.set(YEAR, getAppointment(serviceId).get().getYear());
            calendar.set(HOUR, getAppointment(serviceId).get().getHour());
            calendar.set(MINUTE, getAppointment(serviceId).get().getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if (execute(String.format((Constants.INSERT_PAYMENT),
                signature, serviceId, getAppointment(serviceId).get().getDay(),
                getAppointment(serviceId).get().getMonth(),
                 getAppointment(serviceId).get().getYear(),
                  getAppointment(serviceId).get().getHour(),
                   getAppointment(serviceId).get().getMinute(), number, price))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextPayment()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }       
    }

    @Override
    public Optional<Payment> getPayment(long id) throws Exception {
       log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_PAYMENT, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Payment payment = new Payment();
            payment.setId(resultSet.getLong(Constants.COLUMN_ID));
            payment.setSignature(resultSet.getBoolean(Constants.COLUMN_SIGNATURE));
            payment.setServiceId(resultSet.getLong(Constants.COLUMN_SERVICEID));
            payment.setDay(resultSet.getInt(Constants.COLUMN_DAY));        
            payment.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));        
            payment.setYear(resultSet.getInt(Constants.COLUMN_YEAR));    
            payment.setHour(resultSet.getInt(Constants.COLUMN_HOUR));    
            payment.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));
            payment.setNumber(resultSet.getLong(Constants.COLUMN_NUMBER));
            payment.setPrice(resultSet.getInt(Constants.COLUMN_PRICE));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(payment);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }        
    }

    @Override
    public RequestStatus editPayment(Payment payment) throws Exception {
       log.debug(payment.getId());
        try {
            if (getPayment(payment.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(payment.isSignature()).isEmpty()|String.valueOf(payment.getServiceId()).isEmpty()
                  |String.valueOf(payment.getDay()).isEmpty()|String.valueOf(payment.getMonth()).isEmpty()|
                  String.valueOf(payment.getYear()).isEmpty()|String.valueOf(payment.getHour()).isEmpty()|
                  String.valueOf(payment.getMinute()).isEmpty()|
                String.valueOf(payment.getNumber()).isEmpty()|String.valueOf(payment.getPrice()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getAppointment(payment.getServiceId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, payment.getDay());
            calendar.set(MONTH, payment.getMonth());
            calendar.set(YEAR, payment.getYear());
            calendar.set(HOUR, payment.getHour());
            calendar.set(MINUTE, payment.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.UPDATE_PAYMENT), 
                payment.isSignature(), payment.getServiceId(), payment.getDay(), 
                payment.getMonth(), payment.getYear(),
                payment.getHour(), payment.getMinute(), payment.getNumber(), payment.getPrice(),
                payment.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + payment.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }              
}

    @Override
    public RequestStatus deletePayment(long id) throws Exception {
          log.debug(id);
        try{
            if(getPayment(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_PAYMENT), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createMedicalInsurance(boolean signature, long serviceId, int reimbursement, long number) throws Exception {
         try {
            if (String.valueOf(signature).isEmpty()|String.valueOf(serviceId).isEmpty()
                    |String.valueOf(number).isEmpty()|String.valueOf(reimbursement).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
            if (getAppointment(serviceId).isEmpty()||
                   getAppointment(serviceId).get().getStatus()!=ServiceStatus.APPOINTED){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }            
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, getAppointment(serviceId).get().getDay());
            calendar.set(MONTH, getAppointment(serviceId).get().getMonth());
            calendar.set(YEAR, getAppointment(serviceId).get().getYear());
            calendar.set(HOUR, getAppointment(serviceId).get().getHour());
            calendar.set(MINUTE, getAppointment(serviceId).get().getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if (execute(String.format((Constants.INSERT_MEDICALINSURANCE),
                signature, serviceId, getAppointment(serviceId).get().getDay(),
                getAppointment(serviceId).get().getMonth(),
                 getAppointment(serviceId).get().getYear(),
                  getAppointment(serviceId).get().getHour(),
                   getAppointment(serviceId).get().getMinute(), number, reimbursement))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextMedicalInsurance()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }
    }
    @Override
    public Optional<MedicalInsurance> getMedicalInsurance(long id) throws Exception {
         log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_MEDICALINSURANCE, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            MedicalInsurance medicalInsurance = new MedicalInsurance();
            medicalInsurance.setId(resultSet.getLong(Constants.COLUMN_ID));
            medicalInsurance.setSignature(resultSet.getBoolean(Constants.COLUMN_SIGNATURE));
            medicalInsurance.setServiceId(resultSet.getLong(Constants.COLUMN_SERVICEID));
            medicalInsurance.setDay(resultSet.getInt(Constants.COLUMN_DAY));        
            medicalInsurance.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));        
            medicalInsurance.setYear(resultSet.getInt(Constants.COLUMN_YEAR));    
            medicalInsurance.setHour(resultSet.getInt(Constants.COLUMN_HOUR));    
            medicalInsurance.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));
            medicalInsurance.setNumber(resultSet.getLong(Constants.COLUMN_NUMBER));
            medicalInsurance.setReimbursement(resultSet.getInt(Constants.COLUMN_REIMBURSEMENT));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(medicalInsurance);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }        
    }

    @Override
    public RequestStatus editMedicalInsurance(MedicalInsurance medicalInsurance) throws Exception {
    log.debug(medicalInsurance.getId());
        try {
            if (getMedicalInsurance(medicalInsurance.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(medicalInsurance.isSignature()).isEmpty()|String.valueOf(medicalInsurance.getServiceId()).isEmpty()
                  |String.valueOf(medicalInsurance.getDay()).isEmpty()|String.valueOf(medicalInsurance.getMonth()).isEmpty()|
                  String.valueOf(medicalInsurance.getYear()).isEmpty()|String.valueOf(medicalInsurance.getHour()).isEmpty()|
                  String.valueOf(medicalInsurance.getMinute()).isEmpty()|
                String.valueOf(medicalInsurance.getNumber()).isEmpty()|String.valueOf(medicalInsurance.getReimbursement()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getAppointment(medicalInsurance.getServiceId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, medicalInsurance.getDay());
            calendar.set(MONTH, medicalInsurance.getMonth());
            calendar.set(YEAR, medicalInsurance.getYear());
            calendar.set(HOUR, medicalInsurance.getHour());
            calendar.set(MINUTE, medicalInsurance.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.UPDATE_MEDICALINSURANCE), 
                medicalInsurance.isSignature(), medicalInsurance.getServiceId(), medicalInsurance.getDay(), 
                medicalInsurance.getMonth(), medicalInsurance.getYear(),
                medicalInsurance.getHour(), medicalInsurance.getMinute(), medicalInsurance.getNumber(),
                medicalInsurance.getReimbursement(), medicalInsurance.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + medicalInsurance.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }              
}
    @Override
    public RequestStatus deleteMedicalInsurance(long id) throws Exception {
        log.debug(id);
        try{
            if(getPayment(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_MEDICALINSURANCE), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public RequestStatus createHospitalization(boolean signature, long diagnosisId, long hospitalId, int ward, HospitalizationStatus status) throws Exception {
        try {
            if (String.valueOf(signature).isEmpty()|String.valueOf(diagnosisId).isEmpty()|String.valueOf(hospitalId).isEmpty()
                    |String.valueOf(status).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getDiagnosis(diagnosisId).isEmpty()|getHospital(hospitalId).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }                        
            if (execute(String.format((Constants.INSERT_HOSPITALIZATION),
                signature, diagnosisId, hospitalId, ward,
                getDiagnosis(diagnosisId).get().getDay(),
                getDiagnosis(diagnosisId).get().getMonth(),
                 getDiagnosis(diagnosisId).get().getYear(),
                  getDiagnosis(diagnosisId).get().getHour(),
                   getDiagnosis(diagnosisId).get().getMinute(), status.ordinal()))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextAppointment()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.SUCCESS;
        }      
    }

    @Override
    public Optional<Hospitalization> getHospitalization(long id) throws Exception {
        log.debug(id);       
        try {
            ResultSet resultSet;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_HOSPITALIZATION, id));
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Hospitalization hospitalization = new Hospitalization();
            hospitalization.setId(resultSet.getLong(Constants.COLUMN_ID));
            hospitalization.setSignature(resultSet.getBoolean(Constants.COLUMN_SIGNATURE));
            hospitalization.setServiceId(resultSet.getLong(Constants.COLUMN_SERVICEID));
            hospitalization.setDay(resultSet.getInt(Constants.COLUMN_DAY));
            hospitalization.setMonth(resultSet.getInt(Constants.COLUMN_MONTH));
            hospitalization.setYear(resultSet.getInt(Constants.COLUMN_YEAR));
            hospitalization.setHour(resultSet.getInt(Constants.COLUMN_HOUR));
            hospitalization.setMinute(resultSet.getInt(Constants.COLUMN_MINUTE));
            hospitalization.setHospitalId(resultSet.getLong(Constants.COLUMN_HOSPITAL));
            hospitalization.setWard(resultSet.getInt(Constants.COLUMN_WARD));
            hospitalization.setStatus(HospitalizationStatus.values()[resultSet.getInt(Constants.COLUMN_STATUS)]);
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(hospitalization);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }
      
    }

    @Override
    public RequestStatus editHospitalization(long id, Hospitalization hospitalization) throws Exception {
        log.debug(hospitalization.getId());
        try {
            if (getHospitalization(hospitalization.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;}
            
             if (String.valueOf(hospitalization.isSignature()).isEmpty()|String.valueOf(hospitalization.getServiceId()).isEmpty()
                     |String.valueOf(hospitalization.getHospitalId()).isEmpty()
                    |String.valueOf(hospitalization.getStatus()).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
                return RequestStatus.FAIL;
            }
             if (getDiagnosis(hospitalization.getServiceId()).isEmpty()|getHospital(hospitalization.getHospitalId()).isEmpty()){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }            
             
            Calendar calendar = Calendar.getInstance();
            calendar.set(DATE, hospitalization.getDay());
            calendar.set(MONTH, hospitalization.getMonth());
            calendar.set(YEAR, hospitalization.getYear());
            calendar.set(HOUR, hospitalization.getHour());
            calendar.set(MINUTE, hospitalization.getMinute());
            if (checkDate(calendar)==RequestStatus.FAIL){
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.UPDATE_HOSPITALIZATION), 
                hospitalization.isSignature(), hospitalization.getServiceId(), hospitalization.getDay(), 
                hospitalization.getMonth(), hospitalization.getYear(),
                hospitalization.getHour(), hospitalization.getMinute(), hospitalization.getHospitalId(), 
                hospitalization.getWard(),
                hospitalization.getStatus().ordinal(), hospitalization.getId()))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospitalization.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteHospitalization(long id) throws Exception {
         log.debug(id);
        try{
            if(getHospitalization(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
            if(execute(String.format((Constants.DELETE_HOSPITALIZATION), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
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
            if (execute(String.format((Constants.INSERT_HOSPITAL),
                name, wardsNumber))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + (getNextHospital()));
                return RequestStatus.SUCCESS;
            }else return RequestStatus.FAIL;       
        } catch (IOException e) {
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    @Override
    public Optional<Hospital> getHospital(long id) throws Exception {
        log.debug(id);       
        try {          
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SELECT_HOSPITAL, id));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return Optional.empty();
            }   
            Hospital hospital = new Hospital();
            hospital.setId(set.getLong(Constants.COLUMN_ID));
            hospital.setName(set.getString(Constants.COLUMN_NAME));
            hospital.setWardsNumber(set.getInt(Constants.COLUMN_WARDSNUMBER));
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.FOUND));
            return Optional.of(hospital);
        } catch(NoSuchElementException e){
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus editHospital(Hospital hospital) throws Exception {
        log.debug(hospital.getId());
        try {
             if (getHospital(hospital.getId()).isEmpty()){
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
            return RequestStatus.FAIL;
        }
        if (hospital.getName().isEmpty()|String.valueOf(hospital.getWardsNumber()).isEmpty()) {
            log.error(ConfigurationUtil.getConfigurationEntry(Constants.NULL_PARAM));
            return RequestStatus.FAIL;
        }
        if((execute(String.format((Constants.UPDATE_HOSPITAL), 
                hospital.getName(),hospital.getWardsNumber(), hospital.getId())))
                ==RequestStatus.SUCCESS){
            log.debug(ConfigurationUtil.getConfigurationEntry(Constants.WRITTEN) + hospital.getId());
            return RequestStatus.SUCCESS;
        } else return RequestStatus.FAIL;
    } catch (IOException e) {
        log.error(e);
        return RequestStatus.FAIL;
    }
    }

    @Override
    public RequestStatus deleteHospital(long id) throws Exception {
        log.debug(id);
        try{
            if(getHospital(id).isEmpty()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return RequestStatus.FAIL;
            }
                if(execute(String.format((Constants.DELETE_HOSPITAL), id))==RequestStatus.SUCCESS){
                log.debug(ConfigurationUtil.getConfigurationEntry(Constants.DELETED));
                return RequestStatus.SUCCESS;
            }else
            return RequestStatus.FAIL;
        }catch(IOException | ClassNotFoundException | SQLException e){
            log.error(e);
            return RequestStatus.FAIL;
        }
    }

    public Calendar getCalendar(String date) throws ParseException{
        try{
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy hh:mm");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(format.parse(date));
            return calendar;
            }catch(ParseException e){
                log.error(Constants.WRONG_PARSE);
                return null;
            }
        }
    
    public long getNextSurvey() throws IOException, Exception{
         try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.SURVEY_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
     }
    public long getNextEmployee() throws IOException, Exception{
        try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.EMPLOYEE_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }     
    public long getNextPatient() throws IOException, Exception{
       try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.PATIENT_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    public long getNextAppointment() throws IOException, Exception{
         try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.APPOINTMENT_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    public long getNextDiagnosis() throws IOException, Exception{
        try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.DIAGNOSIS_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    public long getNextPayment() throws IOException, Exception{
        try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.PAYMENT_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    public long getNextMedicalInsurance() throws IOException, Exception{
         try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.MEDICALINSURANCE_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    public long getNextHospitalization() throws IOException, Exception{
        try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.HOSPITALIZATION_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }   
    public long getNextHospital() throws IOException, Exception{
         try {
            ResultSet set;
            PreparedStatement statement = getConnection().prepareStatement(String.format(Constants.HOSPITAL_ID));
            set = statement.executeQuery();
            if (!set.next()){
                log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                return 1;
            }
            return set.getLong("MAXID");
            } catch (SQLException e) {
                log.error(e);
                return 0;
            }
    }
    
    public boolean setUp() throws Exception {
        try{
            if (getEmployee(0).isEmpty()){
                execute(String.format((Constants.SETUP_EMPLOYEE),
                0, Constants.NAME, Constants.PHONE, Constants.SPECIALTY, Constants.COMPETENCE));
            }
            if (getPatient(0).isEmpty()){
                execute(String.format((Constants.SETUP_PATIENT),
                0,Constants.NAME, Constants.PHONE, Constants.PASSPORT, Constants.ALLERGY));
            }
            if (getSurvey(0).isEmpty()){
                Calendar calendar = Calendar.getInstance();
                do{
                    calendar.add(Calendar.DATE, 1);
                }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
                calendar.set(HOUR, 11);
                calendar.set(MINUTE, 00);
                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("dd.MM.yyyy hh:mm");
               execute(String.format((Constants.SETUP_SURVEY),
                0, 0, 0, ServiceStatus.MEDICALTEST.ordinal(), calendar.get(DAY_OF_MONTH),  calendar.get(MONTH), calendar.get(YEAR),
                calendar.get(HOUR), calendar.get(MINUTE), Constants.TYPE,Constants.REPORT));                
            }
            if (getAppointment(0).isEmpty()){
                Calendar calendar = Calendar.getInstance();
                do{
                    calendar.add(Calendar.DATE, 1);
                }while(calendar.get(DAY_OF_WEEK)==1|calendar.get(DAY_OF_WEEK)==7);
                calendar.set(HOUR, 11);
                calendar.set(MINUTE, 00);
                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("dd.MM.yyyy hh:mm");              
               execute(String.format((Constants.SETUP_APPOINTMENT),
                0, 0, 0, ServiceStatus.APPOINTED.ordinal(), calendar.get(DAY_OF_MONTH),  calendar.get(MONTH), calendar.get(YEAR),
                calendar.get(HOUR), calendar.get(MINUTE), Constants.SPECIALIZATION));  
            }
            if (getDiagnosis(0).isEmpty()){
                Appointment appointment = getAppointment(0).get();
                execute(String.format((Constants.SETUP_DIAGNOSIS),
                0, false, ServiceStatus.APPOINTED.ordinal(), 
                appointment.getDay(),
                appointment.getMonth(),
                appointment.getYear(),              
                appointment.getHour(),
                appointment.getMinute(),
                0, Constants.DIAGNOSIS,
                Constants.MEDICINE, Constants.MEDICAL_TESTS));                
            }
            if (getHospital(0).isEmpty()){
                execute(String.format((Constants.SETUP_HOSPITAL),
                0, Constants.NAME, Constants.WARDS)); 
            }
            if(getHospitalization(0).isEmpty()){                        
                Diagnosis diagnosis = getDiagnosis(0).get();
                execute(String.format((Constants.SETUP_HOSPITALIZATION),
                0, false, 0,
                diagnosis.getDay(),
                diagnosis.getMonth(),
                diagnosis.getYear(),              
                diagnosis.getHour(),
                diagnosis.getMinute(),
                0, 1, HospitalizationStatus.HOSPITALIZED.ordinal()));
            }
            if (getMedicalInsurance(0).isEmpty()){
                Appointment appointment = getAppointment(0).get();
                execute(String.format((Constants.SETUP_MEDICALINSURANCE),
                0, false, 0,
                appointment.getDay(),
                appointment.getMonth(),
                appointment.getYear(),              
                appointment.getHour(),
                appointment.getMinute(),
                Constants.NUMBER, Constants.REIMBURSEMENT));
            }
            if (getPayment(0).isEmpty()){
               Appointment appointment = getAppointment(0).get();
                execute(String.format((Constants.SETUP_PAYMENT),
                0, false, 0,
                appointment.getDay(),
                appointment.getMonth(),
                appointment.getYear(),              
                appointment.getHour(),
                appointment.getMinute(),
                Constants.NUMBER, Constants.PRICE));
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
