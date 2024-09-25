FROM openjdk:17-jdk

EXPOSE 8081

COPY build/libs/CloudStorage-0.0.1-SNAPSHOT.jar app.jar

#ADD src/main/resources/db/changelog/db.changelog-master.yaml /liquibase/changelog/
ADD src/main/resources/application.properties src/main/resources/application.properties
#ADD src/main/resources/db/changelog/migrations/002-schema.sql src/main/resources/db/changelog/migrations/002-schema.sql
#ADD src/main/resources/db/changelog/migrations/003-schema.sql src/main/resources/db/changelog/migrations/003-schema.sql
#ADD src/main/resources/db/changelog/migrations/004-schema.sql src/main/resources/db/changelog/migrations/004-schema.sql
#ADD src/main/resources/db/changelog/migrations/005-schema.sql src/main/resources/db/changelog/migrations/005-schema.sql

ENTRYPOINT ["java", "-jar", "app.jar"]
