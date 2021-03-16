# file-manager
## Description  
Spring Boot application for managing files on local machine. In addition it can create backups of the files in other locations like external HDD etc. 
It stores every file in the same root directory and changes its' names to random unique strings so they don't repeat. The structure of the directories and original names of the files are stored in the database.   
The app can link multiple files with each other and display them together as collections helpful for the user. This feature is useful for linking similar files together or linking RAW photo with its improved JPG version (my personal use case). ***This feature is under development***

## Build  
Application uses Gradle. The default task is `bootRun` and the default Spring Profile is `dev`.  
To run `dev` version simply use one of the following commands:  
  `gradlew`  
  `gradlew bootRun`  
  `gradlew bootRun -Pdev`  
  `gradlew -Pdev`  
To build the app use the `build` task:  
  `gradlew build`  
  `gradlew build -Pdev`  
If you want to add other profiles like `prod`, add `application-prod.yml` to `resources` directory and use the `-Pprod` flag.

There is a `PostgreSQL` DB needed to run this application. The `dev` version uses ***locations_service*** db with the same user and password.  
The app uses `Flyway` migration tool to create DB schema and tables at app start, so there is no need to configure it. Just run the app, tables will be create automaticly.
