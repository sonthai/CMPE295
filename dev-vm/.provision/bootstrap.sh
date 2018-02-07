#!/usr/bin/env bash

echo "Installing maven, git, curl, and java8 ..."
sudo apt-get -y install maven git curl
sudo apt-get install -y default-jdk

echo "Installing tomcat8..."
sudo apt-get -y install tomcat8 tomcat8-docs tomcat8-examples tomcat8-admin

echo "Installing Apache Cassandra..."
echo "deb http://www.apache.org/dist/cassandra/debian 311x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
curl https://www.apache.org/dist/cassandra/KEYS | sudo apt-key add -
sudo apt-key adv --keyserver pool.sks-keyservers.net --recv-key A278B781FE4B2BDA
sudo apt-get update
sudo apt-get install -y cassandra
echo "Starting Apache Cassandra..."
sudo sudo service cassandra start
# Stop sudo systemctl stop cassandra.service
# Enable sudo systemctl enable cassandra.service

echo "Install Zookepper..."
sudo apt-get install -y zookeeperd
netstat -ant | grep :2181

echo "Installing and Starting Kafka Server..."
curl -O http://mirrors.advancedhosters.com/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz
sudo mkdir /opt/kafka
sudo tar -xvf kafka_2.11-1.0.0.tgz -C /opt/kafka
sudo nohup /opt/kafka/kafka_2.11-1.0.0/bin/kafka-server-start.sh /opt/kafka/kafka_2.11-1.0.0/config/server.properties > /tmp/kafka.log 2>&1 &
echo "Waiting 10s for kakfta to start..."
sleep 10

echo "Testing Kafka server..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1  --partitions 1 --topic testing
echo "Listing available topic on apache kafka..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --list --zookeeper localhost:2181


#git clone https://github.com/arpitaggarwal/hello-spring.git

 