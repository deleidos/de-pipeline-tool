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


## Installation

### Requirements

* A pre-configured Hadoop cluster with Apex Apex installed including a name node with dtCli installed.
* A MongoDB instance.
* An application server instance with Java 8 installed.
* A web app instance.

The hadoop project contains a Docker-based DevOps process for deploying an Apex-enabled hadoop cluster. [See the hadoop README file for instructions](hadoop/README.md).

### Security and Firewall Access

* The application server instance must be open to the web app instance on port 8080.
* The Hadoop cluster must be open to the application server instance on ports 22 and 8088.
* Data stores must be open to the Hadoop cluster on their ports: 9300 (Elasticsearch), 27017 (MongoDB)
* If the Redis Dimensional Enrichment operator is being used, the Hadoop cluster must be open to port 6379.

### Server Configuration

The DigitalEdge Pipeline Tool runs in an embedded Jetty server. Installation and configured of an application is not required; the build produces a runnable jar.

Configuration is controlled by a single file in the analytics-config project. There are two versions, one for normal operation, and one for testing:

* src/main/resources/analytics_config.json
* src/test/resources/analytics_config.json

Example configuration file:

``` json
{
	"server_port": "8080",
	"api_plugins": [
		"com.deleidos.framework.service.FrameworkServiceDataApiPlugin"
	],
	"elasticsearch_cluster_name": "YourClusterName",
	"elasticsearch_hostnames": [
		"your.es.hostname1",
		"your.es.hostname2"
	],
	"mongodb_hostname": "your.mongodb.hostname",
	"apex_hostname": "your.apex.name.node.hostname",
	"apex_host_username": "your.apex.host.username",
	"apex_key_file_path": "your/path/to/apex/host/pem/key/file",
	"redis_hostname" : "your.redis.hostname"
}
```

At a minimum, you will need to provide:
* Your MongoDB hostname where DE metadata and system definitions will be stored.
* The hostname of a Hadoop node where your Apex cluster is running. _Note: Requires Apache Apex dtCli to be installed on the node._
* The username used to log in to the Hadoop Apache Apex node.
* The path to the PEM key file used to log in to the Hadoop Apache Apex node. This file must be present on the machine running the application server.

The other configuration values can be set for unit testing purposes but are not used by the server during runtime. In the future, there will be separate configurations for testing and runtime.

### Web App Configuration

The web app must be configured with the hostname of the backend jetty web socket server in the file app/config.json. 

``` json
{
    "hostname": "ws://your.app.server.name/analytics"
}
```
### Operator Configuration

Operators are configured to log to syslog using the UDP protocol. This feature enables to logs to be viewed by the user in real time. 

Configuration is controlled by a single file in the de-operator-common project:

* src/main/resources/operator_config.json

Example configuration file:

``` json
{
	"syslog_udp_hostname" : "your.syslog.udp.hostname",
    "syslog_udp_port" : "your.syslog.udp.port"
}
```