FROM maven:3.8.5-openjdk-17-slim
COPY . /h2db/
WORKDIR /h2db
RUN mvn clean install
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/h2db-0.0.1-SNAPSHOT.jar"]