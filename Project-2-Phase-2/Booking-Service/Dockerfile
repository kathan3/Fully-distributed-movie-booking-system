FROM maven:3.8.5-openjdk-17-slim
COPY . /bookings/
WORKDIR /bookings
EXPOSE 8080
RUN mvn compile
ENTRYPOINT ["mvn","exec:java", "-Dexec.mainClass=com.example.QuickstartApp"]