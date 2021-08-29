/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospital;

import api.DataProvider;
import api.DataProviderCSV;
import api.DataProviderDB;
import api.DataProviderXML;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.hospital.enums.RequestStatus;
import ru.sfedu.hospital.enums.ServiceStatus;
import ru.sfedu.hospital.utils.ConfigurationUtil;

/**
 *
 * @author Liza
 */
public class Main {
    private static Logger log = LogManager.getLogger(Main.class);
    
    private static DataProvider getDataProvider(String msg) throws IOException, ClassNotFoundException, SQLException, Exception{
        switch (msg) {
          case Constants.DATA_PROVIDER_CSV:
            return new DataProviderCSV();
          case Constants.DATA_PROVIDER_XML:
            return new DataProviderXML();
          case Constants.DATA_PROVIDER_JDBC:
            DataProviderDB provider = DataProviderDB.getInstance();
            provider.setDB();
            provider.setUp();
            return provider;
        }
    throw new NullPointerException(ConfigurationUtil.getConfigurationEntry(Constants.NO_PROVIDER));
  }
       
    static public void main(String[] args) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, ClassNotFoundException, Exception{
        try{
            if (args.length==0){
                log.info(ConfigurationUtil.getConfigurationEntry(Constants.HELP));
                return;
            }
            
            DataProvider dataProvider = getDataProvider(args[0]);
            
            if (args[1].equals(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_CREATE_EMPLOYEE))) {
                if (args.length == 6) {
                  if (dataProvider.createEmployee(args[2], Long.parseLong(args[3]), args[4], args[5])==RequestStatus.SUCCESS) {
                    log.info(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_CREATE_EMPLOYEE_SUCCESS));
                      } else log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG));
                } else log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG_AMOUNT));
            }           
            else if (args[1].equals(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_CREATE_PATIENT))) {
                if (args.length == 6) {
                   if (dataProvider.createPatient(args[2], Long.parseLong(args[3]), args[4], args[5])==RequestStatus.SUCCESS) {
                        log.info(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_CREATE_PATIENT_SUCCESS));
                    } else log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG));
                } else log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG_AMOUNT));                
            }
            else if (args[1].equals(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_GET_HOSPITAL))) {
                if (args.length == 3) {
                    if (dataProvider.getHospital(Long.parseLong(args[2])).isPresent()) {
                        log.info(ConfigurationUtil.getConfigurationEntry(Constants.METHOD_NAME_GET_HOSPITAL_SUCCESS));
                        log.info(dataProvider.getHospital(Long.parseLong(args[2])).toString());
                    } else log.info(ConfigurationUtil.getConfigurationEntry(Constants.NO_SUCH));
                } else log.error(ConfigurationUtil.getConfigurationEntry(Constants.WRONG_AMOUNT));               
            }
            else log.error(ConfigurationUtil.getConfigurationEntry(Constants.NO_METHOD));          
        } catch (IOException | NullPointerException | NumberFormatException e) {
        log.error(e);
    }
}}


