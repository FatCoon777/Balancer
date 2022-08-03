FROM openjdk:16
COPY ./build/libs/Balancer-0.0.1-SNAPSHOT.jar/ /home
WORKDIR /home
ENTRYPOINT java -jar Balancer-0.0.1-SNAPSHOT.jar