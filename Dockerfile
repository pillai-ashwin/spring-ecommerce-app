# Use the official Jenkins LTS image from Docker Hub
FROM jenkins/jenkins:lts

# Switch to the root user to install additional packages
USER root

# Install Maven and other dependencies needed to build a Spring Boot project
RUN apt-get update && apt-get install -y maven

# Switch back to the Jenkins user
USER jenkins