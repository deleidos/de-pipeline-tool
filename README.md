# Leidos DigitalEdge Pipeline Tool

## 

# Installation
 

## Server Configuration

The DigitalEdge Pipeline Tool runs in an embedded Jetty server. Installation and configured of an application is not required; the build produces a runnable jar.

Configuration is controlled by a single file in the analytics-config project. There are two versions, one for normal operation, and one for testing:

*src/main/resources/analytics_config.json
*src/test/resources/analytics_config.json

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
* The hostname of the Hadoop node where your Apex cluster is running. _Note: Requires Apache Apex dtCli to be installed._
* The username used to log in to the Hadoop Apache Apex node.
* The path to the PEM key file used to log in to the Hadoop Apache Apex node. This file must be present on the machine running the application server.

The other configuration values can be set for testing purposes but are not used by the server during runtime. In the future, there will be separate configurations for testing and runtime.

## Web App Configuration

The web app must be configured with the hostname of the backend jetty web socket server. 

TODO