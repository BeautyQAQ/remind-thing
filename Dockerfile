FROM adoptopenjdk/openjdk11
VOLUME /tmp
ADD remind-thing-1.0-SNAPSHOT.jar remind-thing-1.0-SNAPSHOT.jar
#COPY ./target/remind-thing-1.0-SNAPSHOT.jar remind-thing-1.0-SNAPSHOT.jar
#ENTRYPOINT ["java","-jar","/remind-thing-1.0-SNAPSHOT.jar", "&"]
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/remind-thing-1.0-SNAPSHOT.jar"]