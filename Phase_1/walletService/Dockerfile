# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory to /walletapp
WORKDIR /walletService

# Copy the current directory contents into the container at /walletService
COPY . .

# Install Maven
RUN microdnf install -y maven

# Build the projects with Maven
RUN mvn clean install


ENTRYPOINT ["java" ,"-jar","target/walletService-0.0.1.jar"]
