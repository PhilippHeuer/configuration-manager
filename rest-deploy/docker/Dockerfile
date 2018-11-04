############################################################
# Base Image
############################################################

# Base Image
FROM openjdk:8-jre-alpine

############################################################
# Installation
############################################################

# copy files from rootfs to the container
ADD rootfs /

# installation
RUN apk add --no-cache bash

############################################################
# Execution
############################################################

# Execution
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=1", "-XX:+AlwaysPreTouch", "-XX:-TieredCompilation", "-XX:+UseStringDeduplication", "-XX:+UseG1GC", "-jar", "/app.jar" ]
