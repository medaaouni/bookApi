# Use an official OpenJDK runtime as a parent image
#FROM openjdk:17-jdk-slim
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
#COPY target/your-application.jar app.jar
COPY target/*.jar app.jar


# Expose the port your app runs on
EXPOSE 8080

# Set environment variables for the profile and sensitive data
ENV SPRING_PROFILES_ACTIVE=dev
ENV DB_PASSWORD=defaultpassword

# Run the application
#ENTRYPOINT: The command specified in ENTRYPOINT cannot be overridden by docker run arguments.
#However, you can override it using the --entrypoint flag in the docker run command.
#CMD: The command specified in CMD can be overridden by providing arguments in the docker run command.
#When both ENTRYPOINT and CMD are used, CMD provides default arguments to the ENTRYPOINT.
#ENTRYPOINT ["java", "-jar"]
#CMD ["app.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]


