FROM maven:3.8.5-openjdk-17-slim
COPY . /user/
WORKDIR /user
RUN mvn clean install
ENTRYPOINT ["java","-jar","target/user-0.0.1-SNAPSHOT.jar"]
