# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory to /bookingService
WORKDIR /bookingService

# Copy the current directory contents into the container at /bookingService
COPY . .

# Install Maven
RUN microdnf install -y maven

# Build the projects with Maven
RUN mvn -Dmaven.test.skip=true package



ENTRYPOINT ["java" ,"-jar","target/showService-0.0.1.jar"]
