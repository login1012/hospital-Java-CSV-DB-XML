	Сигнатура запуска:
java "-Dconfig.path=.\src\main\resources\test.properties" "-Dlog4j.configurationFile=log4j2.properties" -jar hospital.jar

java "-Dconfig.path=.\src\main\resources\test.properties""-Dlog4j.configurationFile=log4j2x.properties" -jar hospital.jar

<DataProviderCSV/DataProviderXML/DataProviderDB> 
<createEmployee <name><phone><specialty><competence>/
createPatient <name><phone><passport><allergy>/
getHospital <hospitalId>/>

  	Описание: Создание работника
java "-Dconfig.path=.\src\main\resources\test.properties" "-Dlog4j.configurationFile=log4j2.properties" -jar hospital.jar DataProviderCSV createEmployee "Debby Rayan" 5647385 Therapist Cold

 	Описание: Создание пациента
java "-Dconfig.path=.\src\main\resources\test.properties" "-Dlog4j.configurationFile=log4j2.properties" -jar hospital.jar DataProviderXML createPatient Megan 3755637 AK8272 Milk

  	Описание: Получение информации о больнице
java "-Dconfig.path=.\src\main\resources\test.properties" "-Dlog4j.configurationFile=log4j2.properties" -jar hospital.jar DataProviderDB getHospital 0