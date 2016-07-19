# Test/POC Environment
The Docker images created by this project are intended to run in a mult-instance environment.  We utilize them in our own test environment in AWS on the following EC2 instance types: 

- Client	->	m3.medium
- Hue		->	m3.medium
- Master	->	m3.medium
- Nodes		->	m3.large

Each instance runs one of each type of container image as described below.  While these instances sizes are what we settled on for our test/POC environment, you may choose to increase the instance sizes in your environment for your scenarios.


# How to run
If you would like to understand a bit how each of these components function, see the [Apache Hadoop 2.6.0](https://hadoop.apache.org/docs/r2.6.0/index.html) documentation. 

Prior to running these containers, please ensure that between each of the instances in your test environment, the ports required by the components are accessible.  See the [CDH5.x Port mapping](http://www.cloudera.com/documentation/enterprise/5-6-x/topics/cdh_ig_ports_cdh5.html) for a listing and description of each port.

On each instance in the cluster, make an environment variable accessible which contains the IP or FQDN of the "Master" instance.  Either the IP or FQDN needs to be resolvable by all instances in the cluster.  In the steps below, the IP or FQDN for the master will be referred to as *$MASTER*


## Master Instance
### Namenode  (only 1 per cluster)
We run these components on the "Master" instance in our test environment.  Only one of these components can be run in a cluster.  While we run them on a single instance in our environment, they can be run separately on their own instances.  Just adjust the environment reference in each to point to the desired instance hosting that component.
  
    $ sudo docker run -d --name namenode -m 1524m -e NAMENODE=$MASTER --net host - p 8020:8020 -p 8022:8022 -p 50070:50070 -p 50470:50470 namenode:cdh5.6.0


### Secondary Namenode (only 1 per cluster)

	$ sudo docker run -d --name secondary-namenode -m 256m -e NAMENODE=$MASTER --net host -p 50090:50090 -p  50495:50495 secondary-namenode:cdh5.6.0


### Resource Manager (only 1 per cluster)
	$ sudo docker run -d --name resource-manager -m 1024m -e NAMENODE=$MASTER -e HISTORYSERVER=$MASTER -e RESOURCEMANAGER=$MASTER --net host -p 8032:8032 -p 8030:8030 -p 8031:8031 -p 8033:8033 -p 8088:8088 -p 8090:8090 resource-manager:cdh5.6.0


### History Server (only 1 per cluster)

	$ sudo docker run -d --name history-server -m 1024m -e NAMENODE=$MASTER -e HISTORYSERVER=$MASTER -e RESOURCEMANAGER=$MASTER --net host -p 10020:10020 -p 10033:10033 -p 13562:13562 -p  19888:19888 -p 19890:19890 history-server:cdh5.6.0








## Node Instances
We run these components on the "Nodes" instances in our test environment.  In your environment, you can run as many as you like, however, each "Node" instance must run the pair of the components below.

### Datanode  (only 1 per host, but N per cluster)

	$ sudo docker run -d -m 1024m -e NAMENODE=$MASTER --net host -p 50010:50010 -p 1004:1004 -p 50075:50075 -p 50475:50475 -p 1006:1006 -p 50020:50020 datanode:cdh5.6.0


### Node Manager (only 1 per host, but N per cluster)

	$ sudo docker run -d -e NAMENODE=$MASTER -e HISTORYSERVER=$MASTER -e RESOURCEMANAGER=$MASTER --net host -p 8041:8041 -p 8042:8042 -p 8044:8044 node-manager:cdh5.6.0










## Client Instance
We run this component on the "Client" instance in our test environment.  This container is setup to act as a gateway to the hadoop cluster, and also has the Apache Apex software pre-installed.

### Hadoop Cluster Client (N per node but external to the cluster hosts)

	$ sudo docker run -d --name hadoop-client -m 1024m -e NAMENODE=$MASTER -e HISTORYSERVER=$MASTER -e RESOURCEMANAGER=$MASTER --net host hadoop-client:cdh5.6.0



To use it, run the following to run any of the provided Hadoop example yarn jobs, after the cluster is up and stable:

	$ sudo docker exec -it hadoop-client bash
	$ cd /usr/lib/hadoop-mapreduce/
	$ yarn jar hadoop-mapreduce-examples.jar pi 16 1000   # Should produce Estimated value of Pi is 3.14250000000000000000


## Hue Instance
We run this component on the "Hue" instance in our test environment.  

### Hue

	$ sudo docker run -d --name hue -m 2048m -e NAMENODE=$MASTER -e HISTORYSERVER=$MASTER -e RESOURCEMANAGER=$MASTER -p 80:8888 hadoop-client:cdh5.6.0

Once running, browse to the UI (http://<IP of Hue instance>) and log in as hdfs:hdfs.






# Port forwarding notes
To enable access to the HDFS Web ports locally, use ssh's port forwarding capability.  For example, to access the Namenode's web port (50070), execute the following
in a linux terminal window, then it will be viewable via http://localhost:50070.


	$ ssh -nNT -L 50070:$MASTER:50070 -i key.pem <user>@$MASTER