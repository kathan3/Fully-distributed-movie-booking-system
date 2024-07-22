FROM maven:3.8.5-openjdk-17-slim
COPY . /wallet/
WORKDIR /wallet
RUN mvn clean install
ENTRYPOINT ["java","-jar","target/wallet-0.0.1-SNAPSHOT.jar"]
