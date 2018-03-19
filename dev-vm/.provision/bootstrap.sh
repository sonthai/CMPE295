#!/usr/bin/env bash

echo "Installing maven, git, curl, and java8 ..."
sudo apt-get -y install maven git curl
sudo apt-get install -y default-jdk

echo "Installing python and package dependency ..."
python3 -V
sudo apt-get upgrade
sudo apt-get install -y python3-pip python3-dev
pip3 install tensorflow
pip3 install tensorflow-gpu
pip3 install psutil

echo "Installing tomcat8..."
sudo echo "export CATALINA_OPTS=\"-Xms512m -Xmx1024m\"" >> /home/vagrant/.bashrc
sudo apt-get -y install tomcat8 tomcat8-docs tomcat8-examples tomcat8-admin

echo "Installing Apache Cassandra..."
echo "deb http://www.apache.org/dist/cassandra/debian 311x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list
curl https://www.apache.org/dist/cassandra/KEYS | sudo apt-key add -
sudo apt-key adv --keyserver pool.sks-keyservers.net --recv-key A278B781FE4B2BDA
sudo apt-get update
sudo apt-get install -y cassandra
echo "Starting Apache Cassandra..."
sudo sudo service cassandra start

echo "Install Zookepper..."
sudo apt-get install -y zookeeperd
netstat -ant | grep :2181

echo "Installing and Starting Kafka Server..."
curl -O http://mirrors.advancedhosters.com/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz
sudo mkdir /opt/kafka
sudo tar -xvf kafka_2.11-1.0.0.tgz -C /opt/kafka
sudo rm kafka_2.11-1.0.0.tgz
sudo nohup /opt/kafka/kafka_2.11-1.0.0/bin/kafka-server-start.sh /opt/kafka/kafka_2.11-1.0.0/config/server.properties > /tmp/kafka.log 2>&1 &
echo "Waiting 10s for kakfta to start..."
sleep 10

echo "Testing Kafka server..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1  --partitions 1 --topic testing
echo "Listing available topic on apache kafka..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --list --zookeeper localhost:2181

echo "Cleaning up..."
sudo rm -rf /home/vagrant/app; /home/vagrant/scripts

echo "Building and deploying app-1"
sudo cp -R /mnt/app /home/vagrant/app
cd /home/vagrant/app; mvn clean install
sudo cp target/app-1.war /var/lib/tomcat8/webapps/
echo "Restarting tomcat server"
sudo service tomcat8 restart

echo "Copying classify image scripts to scripts folder ..."
cd /mnt
sudo cp -R scripts/classify_images.py /home/vagrant/

echo "Create uploads folder to store uploaded image ..."
mkdir /home/vagrant/uploads
sudo chown tomcat8:tomcat8 /home/vagrant/uploads