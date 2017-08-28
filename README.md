# Leidos DigitalEdge Pipeline Tool

## About

### Overview

The DigitalEdge Pipeline Tool is an Apache Apex-based data ingestion and processing framework. Data is read from input sources, parsed into records, mapped via a schema, optionally enriched, and persisted to a data store.

The tool allows the user to create, deploy, manage and monitor systems using an intuitive graphical interface.

### Supported Apache Apex Operators

Support input sources:
* S3

Supported input data formats:
* JSON
* CSV
* XML

Supported Dimensional Enrichment caches:
* Redis

Supported data mapping formats:
* SchemaWizard

Supported output data stores:
* MongoDB
* Elasticsearch
* Redis
* REST services
* Kafka

## Installation

### Prerequisites

* A pre-configured Hadoop cluster with Apache Apex installed including a name node with dtCli installed. See instructions under the hadoop sub-project.
* A MongoDB instance. See instructions below for populating Apex operator metadata.

The hadoop project contains a Docker-based DevOps process for deploying an Apex-enabled hadoop cluster. [See the hadoop README file for instructions](hadoop/README.md).

### Installing from Docker Containers

In addition to the Hadoop Apex cluster and MongoDB, there are three applications that need to be installed. 

* DE Pipeline UI Tool (https://hub.docker.com/r/adamscottv/de-pipeline-tool/)
* DE Framework Service (https://hub.docker.com/r/adamscottv/de-framework-service/)
* DE Manager Service (https://hub.docker.com/r/adamscottv/de-manager-service/)

The three application containers are described in more detail below.

# DE Pipeline UI Tool 

The Pipeline UI Tool is a graphical user interface that is used to create, deploy, and manage pipelines and their associated operators.

The Pipeline UI Tool container may be run with Docker Compose as shown below:

```
de-pipeline-tool:
  image: adamscottv/de-pipeline-tool:latest
  environment:
    - VIRTUAL_HOST
  ports:
    - "80:8080"
  expose:
    - "80"
  mem_limit: 128M
```

The following environment variables must be configured on the instance running the container:

# DE Framework Service

The Framework Service is the middle tier for the Pipeline UI Tool and provides WebSocket-based service functionality to create and manage pipeline systems.

The Framework Service container may be run with Docker Compose as shown below:

```
de-framework-service:
  container_name: de-framework-service
  image: adamscottv/de-framework-service:latest
  volumes:
    - "/opt/apex-deployment:/opt/apex-deployment:rw"
  ports:
    - "80:8080"
    - "1514:1514/udp"
  environment:
    - MONGODB_HOSTNAME
    - MANAGER_SERVICE_HOSTNAME
    - HADOOP_NAME_NODE_HOSTNAME
```

The following environment variables must be configured on the instance running the container:
*

# DE Manager Service

The Manager Service container is installed on the client node of the Hadoop cluster. The Manager Service receives pipeline system management requests from the Framework Service and executes them locally using the Apache Apex DTCLI client tool.

The Manager Service container may be run with Docker Compose as shown below:

```
de-manager-service:
    container_name: de-manager-service
    image: "adamscottv/de-manager-service:latest"
    ports: 
      - "8080:8080"
    environment:
      - NAMENODE
      - RESOURCEMANAGER
      - HISTORYSERVER
    stdin_open: true
```	

### Security and Firewall Access

* The Framework Service instance must be open to the web app instance on port 8080.
* The Hadoop cluster must be open to the application server instance on ports 22 and 8088.
* Data stores must be open to the Hadoop cluster on their ports: 9300 (Elasticsearch), 27017 (MongoDB)
* If the Redis Dimensional Enrichment operator is being used, the Hadoop cluster must be open to port 6379.
