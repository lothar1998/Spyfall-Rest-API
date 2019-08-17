FROM openjdk:8u222
ADD build/libs/Project-X-0.0.1-SNAPSHOT.jar .
ENV X_PROFILE prod
EXPOSE 9090
CMD java -jar -Dspring.profiles.active=$X_PROFILE Project-X-0.0.1-SNAPSHOT.jar