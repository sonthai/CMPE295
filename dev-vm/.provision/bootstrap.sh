#!/usr/bin/env bash
echo "************* Start Setting up ***********"
echo "Root $ROOT"
echo "******************************************"

echo "Installing maven, git, curl, and java8 ..."
sudo apt-get -y install maven git curl, awscli
sudo apt-get install -y default-jdk

echo "Installing python and package dependency ..."
python3 -V
sudo apt-get -y update
sudo apt-get install -y python3-pip python3-dev
pip3 install tensorflow
pip3 install psutil
pip3 install annoy 
pip3 install scipy 
pip3 install nltk

'''
echo "Installing tomcat8..."
sudo echo "export CATALINA_OPTS=\"-Xms512m -Xmx1024m\"" >> $ROOT/.bashrc
sudo apt-get -y install tomcat8 tomcat8-docs tomcat8-examples tomcat8-admin


echo "Install Zookepper..."
sudo apt-get install -y zookeeperd
netstat -ant | grep :2181

echo "Installing and Starting Kafka Server..."
curl -O http://mirrors.advancedhosters.com/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz
sudo mkdir /opt/kafka
sudo tar -xvf kafka_2.11-1.0.0.tgz -C /opt/kafka
sudo rm kafka_2.11-1.0.0.tgz
sudo nohup /opt/kafka/kafka_2.11-1.0.0/bin/kafka-server-start.sh /opt/kafka/kafka_2.11-1.0.0/config/server.properties > /tmp/kafka.log 2>&1 &
echo "Waiting 10s for kafka to start..."
sleep 10

echo "Testing Kafka server..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1  --partitions 1 --topic testing
echo "Listing available topic on apache kafka..."
sudo /opt/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --list --zookeeper localhost:2181

echo "Cleaning up..."
sudo rm -rf $ROOT/app; $ROOT/engine

echo "Building and deploying app-1"
sudo cp -R /mnt/app $ROOT/app
cd $ROOT/app; mvn clean install
sudo cp target/app-1.war /var/lib/tomcat8/webapps/
echo "Restarting tomcat server"
sudo service tomcat8 restart

echo "Copying engine folder ..."
cd /mnt
sudo cp -R engine/ $ROOT

echo "Create uploads folder to store uploaded images ..."
mkdir -p $ROOT/engine/uploads
sudo chown tomcat8:tomcat8 $ROOT/engine/uploads
'''
echo "Testing tensorflow set up"
sudo cp -R tests/ $ROOT
python3 tests/tftest.py
