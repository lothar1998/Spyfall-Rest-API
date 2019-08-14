FROM openjdk:8u222
ADD build/libs/Project-X-0.0.1-SNAPSHOT.jar .
EXPOSE 10000
CMD java -jar Project-X-0.0.1-SNAPSHOT.jar