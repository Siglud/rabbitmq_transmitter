FROM ingvarjackal/alpine-oraclejre9
COPY build/libs/*.jar /usr/src/use/app.jar
WORKDIR /usr/src/use
CMD java -jar -Xmx200M app.jar