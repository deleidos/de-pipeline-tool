# Hadoop Containers

This project provides a set of Docker images  which form a CDH 5.6.x Hadoop Cluster with Yarn enabled.  The container images are meant to be run on a multi-instance environment (on-prem or in the cloud) with each instance having adequate resources (CPU, RAM, Disk, etc...) in a test/POC (Proof of Concept) environment.  Using these container images in a production environment is not recommended.


## Pre-reqs
This is a Maven project which helps facilitate the automated creation of the Docker images via the simple invocation of `mvn install`.  In doing so, it will run through the Docker build process for each of the node types in a Hadoop/Yarn cluster building Docker images for each type.  As such, the requirements for doing so are as follows:

- Docker 1.7+
- Maven 3.3+
- Password-less sudo  (used to perform docker build .... commands.  If not setup, you will simply be prompted along the way.)
- 1GB of RAM
- A minimum of 10GB of Disk space
- External connectivity to pull required software as part of the install of each component


## Install
The following describes how to create the Hadoop w/ Yarn Docker container images.

### Building

To build the Hadoop w/ Yarn Docker container images you need to have a working environment as described above.

To start the process of building the container images, invoke:

    $ mvn clean install
    
This will kick off the creation of each of the Docker images for the Hadoop cluster w/ Yarn node types (Namenode, Datanode, Resource manager, etc...).  Depending on your machine's resources, this process can take up to 15 minutes.  At it's completion, you should have the following Docker images ready to go.

- datanode:cdh5.6.0
- hadoop-client:cdh5.6.0   (Pre-installed Apache Apex v3.3.0-incubating)
- history-server:cdh5.6.0
- hue:cdh5.6.0
- namenode:cdh5.6.0
- node-manager:cdh5.6.0
- resource-manager:cdh5.6.0
- secondary-namenode:cdh5.6.0

For convenience, all of the above Docker images are available under the [deleidos](https://hub.docker.com/u/deleidos/) organization.


### Running the Cluster
[See Running.](./Running.md)




