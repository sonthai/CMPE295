#!/usr/bin/env bash
echo "************* Start Setting up ***********"
export ROOT="/home/ubuntu"
export CMPE295="$ROOT/CMPE295"
echo "Root $ROOT"
echo "******************************************"

echo "Installing maven, git, curl, and java8 ..."
#sudo apt-get -y install git curl
sudo apt -y install maven  
sudo apt -y install awscli
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

echo "Installing tomcat8..."
#sudo echo "export CATALINA_OPTS=\"-Xms512m -Xmx1024m\"" >> $ROOT/.bashrc
sudo apt-get -y install tomcat8 tomcat8-docs tomcat8-examples tomcat8-admin

echo "Downloading kafka jar file ...."
curl -O http://mirrors.advancedhosters.com/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz
mkdir $ROOT/kafka
tar -xvf kafka_2.11-1.0.0.tgz -C $ROOT/kafka
rm $CMPE295/kafka_2.11-1.0.0.tgz
#echo "export KAFKA_HEAP_OPTS=\"-Xms500m -Xmx500m\"" >> $ROOT/.bashrc
#source $ROOT/.bashrc
echo "KAFKA HEAP OPTS: $KAFKA_HEAP_OPTS. Sleep 10s"
sleep 10

echo "Starting zookeeper server..."
nohup $ROOT/kafka/kafka_2.11-1.0.0/bin/zookeeper-server-start.sh $ROOT/kafka/kafka_2.11-1.0.0/config/zookeeper.properties > $ROOT/kafka/zookeeper.log 2>&1 &
echo "Waiting 10s for zookeeper to start..."
sleep 10

netstat -ant | grep :2181
echo "Starting kafka server..."
nohup $ROOT/kafka/kafka_2.11-1.0.0/bin/kafka-server-start.sh $ROOT/kafka/kafka_2.11-1.0.0/config/server.properties > $ROOT/kafka/kafka.log 2>&1 &
echo "Waiting 10s for kafka to start..."
sleep 10

echo "Testing Kafka server..."
sudo $ROOT/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1  --partitions 1 --topic testing
echo "Listing available topic on apache kafka..."
sudo $ROOT/kafka/kafka_2.11-1.0.0/bin/kafka-topics.sh --list --zookeeper localhost:2181


echo "Building and deploying app-1"
cd $CMPE295/app; mvn clean install
sudo cp target/app-1.war /var/lib/tomcat8/webapps/
echo "Restarting tomcat server"
sudo service tomcat8 restart


echo "Create uploads folder to store uploaded images ..."
mkdir -p $CMPE295/engine/uploads
sudo chown tomcat8:tomcat8 $CMPE295/engine/uploads

echo "Testing tensorflow set up"
python3 $CMPE295/tests/tftest.py
