FROM adoptopenjdk/openjdk17
VOLUME /tmp
ADD remind-thing-1.0-SNAPSHOT.jar remind-thing-1.0-SNAPSHOT.jar
#COPY ./target/how_article.jar how_article.jar
#ENTRYPOINT ["java","-jar","/how_article.jar", "&"]
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/remind-thing-1.0-SNAPSHOT.jar"]