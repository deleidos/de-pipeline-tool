FROM java:openjdk-8-jdk

ADD de-framework-service-0.0.1-SNAPSHOT-dist.tar.gz /tmp/

RUN rm -fv /tmp/de-framework-service-0.0.1-SNAPSHOT-dist.tar.gz

WORKDIR /tmp/de-framework-service

EXPOSE 8080
EXPOSE 1514/udp

CMD ["./start_service.sh"]