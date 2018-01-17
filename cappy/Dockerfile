FROM ingvarjackal/alpine-oraclejre9
WORKDIR /usr/src/use
COPY docker-entrypoint.sh /usr/src/app/docker-entrypoint.sh
RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ["/usr/src/app/docker-entrypoint.sh"]
COPY build/libs/*.jar /usr/src/use/app.jar
CMD java -jar -Xmx200M app.jar