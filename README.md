# Leidos DigitalEdge Pipeline Tool

## About

## Overview

The DigitalEdge Pipeline Tool is an Apache Apex-based data ingestion and processing framework. Data is read from input sources, parsed into records, mapped via a schema, optionally enriched, and persisted to a data store.

The tool allows the user to create, deploy, manage and monitor systems using an intuitive graphical interface.

## Supported Apache Apex Operators

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

# Installation

## Prerequisites

* A pre-configured Hadoop cluster with Apache Apex installed including a name node with dtCli installed. See instructions under the hadoop sub-project.
* A MongoDB instance. See instructions below for populating Apex operator metadata.

### Hadoop Apex Cluster

The hadoop project contains a Docker-based DevOps process for deploying an Apex-enabled hadoop cluster. [See the hadoop README file for instructions](hadoop/README.md).

### Apex Pipeline Operator Metadata

In order for the Pipeline UI Tool to work, operator metadata must be installed into the MongoDB instance. The operator metadata is used to generically populate the available Apex operator data in the UI. A sample tool for populating data is located in the de-framework-service OperatorMetaDataLoader class in the package: 
(https://github.com/deleidos/de-pipeline-tool/tree/master/de-framework-service/src/main/java/com/deleidos/framework/service/tools)

The OperatorMetaDataLoader class can be executed from any machine that has port access to the MongoDB instance. Simply set a MONGODB_HOSTNAME environment variable on that machine, and the loader will populate the data into MongoDB from the JSON file.

The default operator metadata is located in the following JSON file:
(https://github.com/deleidos/de-pipeline-tool/blob/master/de-framework-service/src/main/resources/operator_metadata.json)

The OperatorMetaDataLoader class can be executed from any machine that has port access to the MongoDB instance. Simply set a MONGODB_HOSTNAME environment variable on that machine, and the loader will populate the data into MongoDB from the JSON file.

## Installing from Docker Containers

In addition to the Hadoop Apex cluster and MongoDB, there are three applications that need to be installed. 

* DE Pipeline UI Tool (https://hub.docker.com/r/adamscottv/de-pipeline-tool/)
* DE Framework Service (https://hub.docker.com/r/adamscottv/de-framework-service/)
* DE Manager Service (https://hub.docker.com/r/adamscottv/de-manager-service/)

The three application containers are described in more detail below.

### DE Pipeline UI Tool 

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
* VIRTUAL_HOST - The hostname or IP of the instance running the Framework Service container.

### DE Framework Service

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
* MONGODB_HOSTNAME - The hostname or IP of the MongoDB instance.
* MANAGER_SERVICE_HOSTNAME - The hostname or IP of Hadoop client node running the Manager Service container.
* HADOOP_NAME_NODE_HOSTNAME - The hostname or IP of a Hadoop name node.

The /opt/apex-deployment volume that is mapped must be populated with the pre-compiled Apex operator jar files. To facilitate this process, a set of default operator jars are provided:
(https://s3.amazonaws.com/apex-deployment/apex-deployment.tar.gz)

Unzip this file under /opt on the same instance that is to run the de-framework-service container (do this before running the container so the volume can be mapped).

### DE Manager Service

The Manager Service container is installed on the client node of the Hadoop cluster. The Manager Service receives pipeline system management requests from the Framework Service and executes them locally using the Apache Apex dtCli client tool.

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

The following environment variables must be configured on the instance running the container:
* NAMENODE - The hostname or IP of a name node of the Hadoop cluster.
* RESOURCEMANAGER - The hostname or IP of the resource manager node of the Hadoop cluster.
* HISTORYSERVER - The hostname or IP of the history server node of the Hadoop cluster.

## Security and Firewall Access

* The Framework Service instance must be open to the web app instance on port 8080.
* The Hadoop cluster must be open to the application server instance on ports 22 and 8088.
* Data stores must be open to the Hadoop cluster on their ports: 9300 (Elasticsearch), 27017 (MongoDB)
* If the Redis Dimensional Enrichment operator is being used, the Hadoop cluster must be open to port 6379.
