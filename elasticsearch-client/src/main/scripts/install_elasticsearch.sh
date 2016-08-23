#!/bin/bash

if [ $# -lt 1 ]
  then
    echo "Must provide Elasticsearch Cluster Name as argument"
    exit
fi

sudo swapoff -a
CLUSTER_NAME=$1
PUBLIC_IP=`curl -s http://169.254.169.254/latest/meta-data/public-ipv4`
PUBLIC_DNS=`curl http://169.254.169.254/latest/meta-data/public-hostname`
# grab half the available memory for ES HEAP
ES_HEAP_VAL=`free -g | awk '/^Mem:/{print $2/2}' | awk '{print int($1+0.5)}'`

cd ~
sudo add-apt-repository ppa:openjdk-r/ppa -y
sudo apt-get update
sudo apt-get install openjdk-8-jdk -y --force-yes
wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.7.1.deb
sudo dpkg -i elasticsearch-1.7.1.deb

sudo mkdir /usr/share/elasticsearch/data
sudo chown elasticsearch:elasticsearch -R /usr/share/elasticsearch/
cd /usr/share/elasticsearch/

sudo bin/plugin -install elasticsearch/elasticsearch-cloud-aws/2.7.1
#http://localhost:9200/_plugin/kopf/
sudo bin/plugin -install lmenezes/elasticsearch-kopf/2.0
#http://localhost:9200/_plugin/HQ/
sudo bin/plugin -install royrusso/elasticsearch-HQ
sudo bin/plugin -install elasticsearch/marvel/latest

sudo mv /etc/elasticsearch/elasticsearch.yml /etc/elasticsearch/elasticsearch.yml.bak
sudo mv /etc/default/elasticsearch /etc/default/elasticsearch.bak

cd ~
ESConfig=$(cat<<EOF
# paths
path.data: "/usr/share/elasticsearch/data"

# additional configuration
bootstrap.mlockall: true
indices.fielddata.cache.size: "30%"
indices.cache.filter.size: "30%"

# AWS discovery
cloud.aws.access_key: "AKIAIBGS6H6F4MHBCXPA"
cloud.aws.secret_key: "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"

plugin.mandatory: "cloud-aws"

cluster.name: "__CLUSTER_NAME__"

node.name: "__PUBLIC_IP__"

discovery.type: "ec2"
discovery.ec2.groups: "es.cluster"
#discovery.ec2.ping_timeout: "30s"
discovery.ec2.availability_zones: "us-east-1c"
cloud.aws.region: "us-east"

discovery.zen.ping.multicast.enabled: false

network.publish_host: "__PUBLIC_DNS__"
EOF
)
sudo echo "$ESConfig" > elasticsearch.yml
sudo mv elasticsearch.yml /etc/elasticsearch

sudo sed -i s/__PUBLIC_IP__/$PUBLIC_IP/g /etc/elasticsearch/elasticsearch.yml
sudo sed -i s/__PUBLIC_DNS__/$PUBLIC_DNS/g /etc/elasticsearch/elasticsearch.yml
sudo sed -i s/__CLUSTER_NAME__/$CLUSTER_NAME/g /etc/elasticsearch/elasticsearch.yml

ESSettings=$(cat<<EOF
ES_HEAP_SIZE=__ES_HEAP_VAL__g
MAX_LOCKED_MEMORY=unlimited
EOF
)
sudo echo "$ESSettings" > elasticsearch
sudo mv elasticsearch /etc/default

sudo sed -i s/__ES_HEAP_VAL__/$ES_HEAP_VAL/g /etc/default/elasticsearch

sudo update-rc.d elasticsearch defaults
sudo service elasticsearch restart