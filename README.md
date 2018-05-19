# CMPE295
### Set up dev environment
- Download and install [Vagrant](https://www.vagrantup.com/downloads.html)
- How to use vagrant
  + Start vagrant: vagrant up
  + Reload provision: vagrant provision OR vagrant reload --provision (provision script is under dev-vm/.provision directory)
  + Destroy vm: vagrant destroy


### Project structure
- app folder: contains the backend services written in Spring framework.
- engine/scripts folder: contains images vector folder and python script to perform the recommendation.
- jetson tx2 folder: contains pooler (web UI) and sender (python script used for object detection).
- mobile_app/DRS: contains the android application.
- aws-install.sh: automated scripted used to set up and deploy application on AWS.
- dev-vm folder: contains the code to create the dev environment using vagrant
