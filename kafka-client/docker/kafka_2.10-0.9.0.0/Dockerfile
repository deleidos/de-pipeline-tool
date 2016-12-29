FROM ubuntu:14.04.4

RUN apt-get -y install curl software-properties-common
RUN mkdir -p /opt/kafka/kafka_2.10-0.9.0.0
# RUN curl -SL -o /opt/kafka/kafka_2.10-0.9.0.0.tgz http://apache.go-parts.com/kafka/0.9.0.0/kafka_2.10-0.9.0.0.tgz
COPY kafka_2.10-0.9.0.0.tgz /opt/kafka/

RUN tar -C /opt/kafka/kafka_2.10-0.9.0.0 -xvzf /opt/kafka/kafka_2.10-0.9.0.0.tgz --strip 1

RUN useradd kafka -m
RUN echo "kafka:kafka"|chpasswd
RUN adduser kafka sudo

RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections
RUN apt-get -y install oracle-java8-installer

RUN perl -pi -e 's/zookeeper.connect=localhost:2181/zookeeper.connect=10.168.186.23:2181/g' /opt/kafka/kafka_2.10-0.9.0.0/config/server.properties
RUN perl -pi -e 's/#host.name=localhost/host.name=10.168.186.23/g' /opt/kafka/kafka_2.10-0.9.0.0/config/server.properties
RUN perl -pi -e 's/#advertised.host.name=<hostname routable by clients>/advertised.host.name=10.168.186.23/g' /opt/kafka/kafka_2.10-0.9.0.0/config/server.properties
RUN perl -pi -e 's/#advertised.port=<port accessible by clients>/advertised.port=9092/g' /opt/kafka/kafka_2.10-0.9.0.0/config/server.properties
RUN perl -pi -e 's/log.retention.hours=168/log.retention.hours=4/g' /opt/kafka/kafka_2.10-0.9.0.0/config/server.properties

RUN chown -R kafka:kafka /opt/kafka

EXPOSE 9092

USER kafka
CMD ["/opt/kafka/kafka_2.10-0.9.0.0/bin/kafka-server-start.sh", "/opt/kafka/kafka_2.10-0.9.0.0/config/server.properties"]
